// SPDX-License-Identifier: MIT
// SPDX-FileCopyrightText: 2023

package com.amos.pitmutationmate.pitmutationmate.services

import com.amos.pitmutationmate.pitmutationmate.plugincheck.PluginCheckerGroovy
import com.amos.pitmutationmate.pitmutationmate.plugincheck.PluginCheckerKotlin
import com.amos.pitmutationmate.pitmutationmate.ui.ToolWindowFactory
import com.intellij.openapi.components.Service
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.diagnostic.thisLogger
import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.wm.ToolWindowManager
import com.intellij.psi.PsiManager
import com.intellij.util.ui.UIUtil
import org.apache.commons.io.IOUtils
import org.codehaus.groovy.ast.builder.AstBuilder
import java.io.ByteArrayInputStream
import java.io.InputStream

@Service(Service.Level.PROJECT)
class PluginCheckerService(private val project: Project) {
    private val logger = Logger.getInstance(this::class.java)

    private var isKotlinOrGroovy: BuildFile = BuildFile.NONE
    private var isKotlinPitestPluginAvailable = false
    private var isKotlinCompanionPluginAvailable = false
    private var isGroovyPitestPluginAvailable = false
    private var isGroovyCompanionPluginAvailable = false

    fun checkPlugins(file: VirtualFile) {
        if (!file.exists()) {
            logger.debug("File ${file.path} does not exist. Not checking for plugins.")
            return
        }

        when (file.name) {
            "build.gradle.kts" -> {
                isKotlinOrGroovy = BuildFile.KOTLIN
                checkKotlinBuildFile(file)
            }

            "build.gradle" -> {
                isKotlinOrGroovy = BuildFile.GROOVY
                checkGroovyBuildFile(file)
            }

            else -> {
                logger.debug("File ${file.path} is not a build file. Not checking for plugins.")
                return
            }
        }
        fillToolWindow()
    }

    fun getErrorMessage(withHeader: Boolean = true): String? {
        var errorMessage = ""

        if (isKotlinOrGroovy == BuildFile.NONE) {
            return null
        }

        var pitestPluginAvailable = isKotlinPitestPluginAvailable
        var companionPluginAvailable = isKotlinCompanionPluginAvailable
        var pitestPluginString = KOTLIN_PITEST_PLUGIN
        var overridePluginString = KOTLIN_OVERRIDE_PLUGIN
        var buildFileName = KOTLIN_BUILD_FILE
        if (isKotlinOrGroovy == BuildFile.GROOVY) {
            pitestPluginAvailable = isGroovyPitestPluginAvailable
            companionPluginAvailable = isGroovyCompanionPluginAvailable
            pitestPluginString = GROOVY_PITEST_PLUGIN
            overridePluginString = GROOVY_OVERRIDE_PLUGIN
            buildFileName = GROOVY_BUILD_FILE
        }

        if (!pitestPluginAvailable) {
            if (errorMessage.isNotEmpty()) errorMessage += "\n"
            errorMessage += String.format(
                ERROR_MESSAGE_PITEST_PLUGIN_MISSING,
                buildFileName,
                pitestPluginString
            )
        }
        if (!companionPluginAvailable) {
            if (errorMessage.isNotEmpty()) errorMessage += "\n"
            errorMessage += String.format(
                ERROR_MESSAGE_COMPANION_PLUGIN_MISSING,
                buildFileName,
                overridePluginString
            )
        }

        if (errorMessage.isNotEmpty()) {
            val header = if (withHeader) "<h1>Configuration error:</h1><br/>" else ""
            val font = UIUtil.getToolbarFont()
            return """<html>
                <body style="font-family: '${font.family}'; font-size: 12px; margin: 15px;">
                    $header${errorMessage.replace("\n", "<br/>")}
                </body>
            </html>
            """.trimIndent()
        }
        return null
    }

    private fun fillToolWindow() {
        val toolWindow = ToolWindowManager.getInstance(project).getToolWindow(ToolWindowFactory.ID)
        if (toolWindow != null) {
            val errorMessage = getErrorMessage()
            if (errorMessage != null) {
                ToolWindowFactory.Util.initiateWithConfigError(errorMessage, toolWindow)
            } else {
                ToolWindowFactory.Util.initiateWithData(toolWindow)
            }
        }
    }

    private fun checkKotlinBuildFile(kotlinBuildFile: VirtualFile) {
        thisLogger().debug("checking kotlin plugins for $kotlinBuildFile")
        val psiFile = PsiManager.getInstance(project).findFile(kotlinBuildFile)
        val kotlinChecker = PluginCheckerKotlin()
        psiFile?.node?.psi?.accept(kotlinChecker)

        if (!isKotlinPitestPluginAvailable) {
            isKotlinPitestPluginAvailable = kotlinChecker.pitestPluginAvailable
        }
        if (!isKotlinCompanionPluginAvailable) {
            isKotlinCompanionPluginAvailable = kotlinChecker.companionPluginAvailable
        }
    }

    private fun checkGroovyBuildFile(groovyBuildFile: VirtualFile) {
        thisLogger().debug("checking groovy plugins for $groovyBuildFile")
        val builder = AstBuilder()
        val nodes = builder.buildFromString(
            IOUtils.toString(
                getInputStreamFromVirtualFile(groovyBuildFile),
                "UTF-8"
            )
        )
        val groovyChecker = PluginCheckerGroovy()
        for (node in nodes) {
            node.visit(groovyChecker)
        }
        if (!isGroovyPitestPluginAvailable) {
            isGroovyPitestPluginAvailable = groovyChecker.pitestPluginAvailable
        }
        if (!isGroovyCompanionPluginAvailable) {
            isGroovyCompanionPluginAvailable = groovyChecker.companionPluginAvailable
        }
    }

    private fun getInputStreamFromVirtualFile(virtualFile: VirtualFile): InputStream? {
        val document = FileDocumentManager.getInstance().getDocument(virtualFile)
        return document?.let {
            ByteArrayInputStream(it.text.toByteArray())
        }
    }

    companion object {
        private const val KOTLIN_BUILD_FILE = "build.gradle.kts"
        private const val GROOVY_BUILD_FILE = "build.gradle"

        private const val KOTLIN_PITEST_PLUGIN = "id(\"info.solidsoft.pitest\") version \"x.y.z\""
        private const val KOTLIN_OVERRIDE_PLUGIN =
            "id(\"io.github.amos-pitmutationmate.pitmutationmate.override\") version \"1.0\""

        private const val GROOVY_PITEST_PLUGIN = "id 'info.solidsoft.pitest' version 'x.y.z'"
        private const val GROOVY_OVERRIDE_PLUGIN =
            "id \"io.github.amos-pitmutationmate.pitmutationmate.override\" version \"1.0\""

        const val ERROR_MESSAGE_TITLE = "Plugins for PITMutationPlugin are missing"
        private val ERROR_MESSAGE_PITEST_PLUGIN_MISSING = """<b>The pitest gradle Plugin is missing.</b>
            <p>Please add a Gradle Pitest Plugin line to your %s file:</p>
            <em>%s</em>
            <p>And see the pitest docs for missing configurations of pitest</p>
        """.trimIndent()
        private val ERROR_MESSAGE_COMPANION_PLUGIN_MISSING = """<b>The Override Companion Plugin is missing.</b>
            <p>Please add the following line to your %s file:</p>
            <em>%s</em>
        """.trimIndent()

        enum class BuildFile {
            KOTLIN, GROOVY, NONE
        }
    }
}
