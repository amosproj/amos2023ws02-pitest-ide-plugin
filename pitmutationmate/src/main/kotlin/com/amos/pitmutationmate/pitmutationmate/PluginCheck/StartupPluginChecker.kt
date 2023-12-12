// SPDX-License-Identifier: MIT
// SPDX-FileCopyrightText: 2023

package com.amos.pitmutationmate.pitmutationmate.PluginCheck

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.startup.ProjectActivity
import com.intellij.openapi.ui.Messages
import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.psi.PsiManager
import org.apache.commons.io.IOUtils
import org.codehaus.groovy.ast.builder.AstBuilder
import java.io.File
import java.io.FileInputStream

class StartupPluginChecker : ProjectActivity {

    override suspend fun execute(project: Project) {
        ApplicationManager.getApplication().invokeLater {
            checkGroovyBuildFile(project)
            checkKotlinBuildFile(project)
        }
    }

    private fun checkKotlinBuildFile(project: Project) {
        val buildFileName = "build.gradle.kts"
        val kotlinBuildFile = File(project.basePath + "/$buildFileName")
        if(kotlinBuildFile.exists()) {
            val virtualFile = LocalFileSystem.getInstance().findFileByIoFile(kotlinBuildFile)
            if (virtualFile != null) {
                val psiFile = PsiManager.getInstance(project).findFile(virtualFile)
                val pluginCheckerKotlin = PluginCheckerKotlin()
                psiFile?.node?.psi?.accept(pluginCheckerKotlin)
                val pitestPluginText = "id(\"info.solidsoft.pitest\") version \"1.15.0\""
                val companionPluginText = "id(\"io.github.amosproj.pitmutationmate.override\") version \"1.0\""
                throwErrorMessage(pluginCheckerKotlin.pitestPluginAvailable,
                    pluginCheckerKotlin.companionPluginAvailable,
                    buildFileName,
                    project,
                    pitestPluginText,
                    companionPluginText)
            }
        }
    }

    private fun checkGroovyBuildFile(project: Project) {
        val buildFileName = "build.gradle"
        val groovyBuildFile = File(project.basePath + "/$buildFileName")
        if(groovyBuildFile.exists()) {
            val builder = AstBuilder()
            val nodes = builder.buildFromString(IOUtils.toString(FileInputStream(groovyBuildFile), "UTF-8"))
            val pluginCheckerGroovy = PluginCheckerGroovy()
            for(node in nodes) {
                node.visit(pluginCheckerGroovy)
            }
            val pitestPluginText = "id 'info.solidsoft.pitest' version '1.15.0'"
            val companionPluginText = "id 'io.github.amosproj.pitmutationmate.override' version '1.0'"
            throwErrorMessage(pluginCheckerGroovy.pitestPluginAvailable,
                pluginCheckerGroovy.companionPluginAvailable,
                buildFileName,
                project,
                pitestPluginText,
                companionPluginText)
        }
    }

    private fun throwErrorMessage(
        pitestPluginAvailable: Boolean,
        companionPluginAvailable: Boolean,
        buildFileName: String,
        project: Project,
        pitestPluginText: String,
        companionPluginText: String
    ) {
        var errorMessage = ""
        if (!pitestPluginAvailable) {
            errorMessage += String.format(
                ERROR_MESSAGE_PITEST_PLUGIN_MISSING,
                buildFileName,
                pitestPluginText
            )
        }
        if (!companionPluginAvailable) {
            errorMessage += String.format(
                ERROR_MESSAGE_COMPANION_PLUGIN_MISSING,
                buildFileName,
                companionPluginText
            )
        }
        if (errorMessage.isNotEmpty()) {
            Messages.showErrorDialog(project, errorMessage, ERROR_MESSAGE_TITLE)
        }
    }

    companion object {
        private const val ERROR_MESSAGE_TITLE = "Plugins for PITMutationPlugin are missing"
        private const val ERROR_MESSAGE_PITEST_PLUGIN_MISSING = "The pitest gradle Plugin is missing.\n" +
                                                                "Please add a Gradle Pitest Plugin to the %s file like the following:\n" +
                                                                "%s\nAnd see the pitest docs for missing configurations of pitest\n\n"
        private const val ERROR_MESSAGE_COMPANION_PLUGIN_MISSING = "The Companion Plugin is missing.\n" +
                                                                   "Please add the following line to your %s file:\n" +
                                                                   "%s\n\n"
    }
}
