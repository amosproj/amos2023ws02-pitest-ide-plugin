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
    private var kotlinPluginsAvailability = PluginsAvailability()
    private var groovyPluginsAvailability = PluginsAvailability()

    fun checkPlugins(files: Collection<VirtualFile>) {
        val kotlinPlugins = PluginsAvailability()
        val groovyPlugins = PluginsAvailability()

        // iterate over given files
        for (file in files) {
            if (!file.exists()) {
                logger.info("File ${file.path} does not exist. Not checking for plugins.")
                continue
            }

            when (file.name) {
                "build.gradle.kts" -> {
                    checkKotlinBuildFile(file, kotlinPlugins)
                    kotlinPluginsAvailability = kotlinPlugins
                    isKotlinOrGroovy = BuildFile.KOTLIN
                }

                "build.gradle" -> {
                    checkGroovyBuildFile(file, groovyPlugins)
                    groovyPluginsAvailability = groovyPlugins
                    isKotlinOrGroovy = BuildFile.GROOVY
                }

                else -> {
                    logger.info("File ${file.path} is not a build file. Not checking for plugins.")
                    continue
                }
            }
        }
        fillToolWindow()
    }

    fun getErrorMessage(withHeader: Boolean = true): String? {
        var errorMessage = ""

        val pluginsAvailability = PluginsAvailability()
        pluginsAvailability.pitestPluginAvailable = kotlinPluginsAvailability.pitestPluginAvailable || groovyPluginsAvailability.pitestPluginAvailable
        pluginsAvailability.companionPluginAvailable = kotlinPluginsAvailability.companionPluginAvailable || groovyPluginsAvailability.companionPluginAvailable

        val pitestPluginString = when (isKotlinOrGroovy) {
            BuildFile.GROOVY -> GROOVY_PITEST_PLUGIN
            else -> KOTLIN_PITEST_PLUGIN
        }
        val overridePluginString = when (isKotlinOrGroovy) {
            BuildFile.GROOVY -> GROOVY_OVERRIDE_PLUGIN
            else -> KOTLIN_OVERRIDE_PLUGIN
        }
        val buildFileName = when (isKotlinOrGroovy) {
            BuildFile.GROOVY -> GROOVY_BUILD_FILE
            else -> KOTLIN_BUILD_FILE
        }

        if (!pluginsAvailability.pitestPluginAvailable) {
            if (errorMessage.isNotEmpty()) errorMessage += "\n"
            errorMessage += String.format(
                ERROR_MESSAGE_PITEST_PLUGIN_MISSING,
                buildFileName,
                pitestPluginString
            )
        }
        if (!pluginsAvailability.companionPluginAvailable) {
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
            if (!errorMessage.isNullOrEmpty()) {
                ToolWindowFactory.Util.initiateWithConfigError(errorMessage, toolWindow)
            } else {
                ToolWindowFactory.Util.initiateWithData(toolWindow, project)
            }
        }
    }

    private fun checkKotlinBuildFile(kotlinBuildFile: VirtualFile, kotlinPlugins: PluginsAvailability) {
        thisLogger().debug("checking kotlin plugins for $kotlinBuildFile")
        val psiFile = PsiManager.getInstance(project).findFile(kotlinBuildFile)
        val kotlinChecker = PluginCheckerKotlin()
        psiFile?.node?.psi?.accept(kotlinChecker)

        if (!kotlinPlugins.pitestPluginAvailable) {
            kotlinPlugins.pitestPluginAvailable = kotlinChecker.pitestPluginAvailable
        }
        if (!kotlinPlugins.companionPluginAvailable) {
            kotlinPlugins.companionPluginAvailable = kotlinChecker.companionPluginAvailable
        }
    }

    private fun checkGroovyBuildFile(groovyBuildFile: VirtualFile, groovyPlugins: PluginsAvailability) {
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
            if (node::class.java.declaredMethods.any { it.name == "visit" }) {
                node.visit(groovyChecker)
            }
        }
        if (!groovyPlugins.pitestPluginAvailable) {
            groovyPlugins.pitestPluginAvailable = groovyChecker.pitestPluginAvailable
        }
        if (!groovyPlugins.companionPluginAvailable) {
            groovyPlugins.companionPluginAvailable = groovyChecker.companionPluginAvailable
        }
    }

    private fun getInputStreamFromVirtualFile(virtualFile: VirtualFile): InputStream? {
        val document = FileDocumentManager.getInstance().getDocument(virtualFile)
        return document?.let {
            ByteArrayInputStream(it.text.toByteArray())
        }
    }

    private class PluginsAvailability {
        var pitestPluginAvailable: Boolean = false
        var companionPluginAvailable: Boolean = false
    }

    companion object {
        private const val KOTLIN_BUILD_FILE = "build.gradle.kts"
        private const val GROOVY_BUILD_FILE = "build.gradle"

        private const val KOTLIN_PITEST_PLUGIN = "id(\"info.solidsoft.pitest\") version \"x.y.z\""
        private const val KOTLIN_OVERRIDE_PLUGIN =
            "id(\"io.github.amos-pitmutationmate.pitmutationmate.override\") version \"1.1\""

        private const val GROOVY_PITEST_PLUGIN = "id 'info.solidsoft.pitest' version 'x.y.z'"
        private const val GROOVY_OVERRIDE_PLUGIN =
            "id \"io.github.amos-pitmutationmate.pitmutationmate.override\" version \"1.1\""

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
