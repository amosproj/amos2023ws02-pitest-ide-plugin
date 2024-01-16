// SPDX-License-Identifier: MIT
// SPDX-FileCopyrightText: 2023 Lennart Heimbs

package com.amos.pitmutationmate.pitmutationmate.listener

import com.amos.pitmutationmate.pitmutationmate.buildSystem.BuildSystemUtils
import com.amos.pitmutationmate.pitmutationmate.services.PluginCheckerService
import com.intellij.openapi.components.service
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.editor.Document
import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.openapi.fileEditor.FileDocumentManagerListener
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.ProjectManager
import com.intellij.openapi.vfs.VirtualFile

class BuildFileListener : FileDocumentManagerListener {
    private val logger = Logger.getInstance(this::class.java)

    override fun beforeDocumentSaving(document: Document) {
        FileDocumentManager.getInstance().getFile(document)?.let { file ->
            ProjectManager.getInstance().openProjects.forEach { project ->
                if (isVirtualFileInProject(file, project)) {
                    logger.debug("Project: ${project.name} is being checked for plugins because it contains ${file.path}")
                    project.service<PluginCheckerService>().checkPlugins(listOf(file))
                }
            }
        }
    }

    private fun isVirtualFileInProject(file: VirtualFile, project: Project): Boolean {
        val pathMatches = file.path.startsWith(project.basePath!!)
        val isBuildFile = file.name == "build.gradle.kts" || file.name == "build.gradle"

        val projectBuildFiles = BuildSystemUtils.getProjectBuildFiles(project)
        val isBuildFileInProject = projectBuildFiles.any { it.path == file.path }

        return pathMatches && isBuildFile && isBuildFileInProject
    }
}
