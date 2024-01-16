// SPDX-License-Identifier: MIT
// SPDX-FileCopyrightText: 2024 Lennart Heimbs

package com.amos.pitmutationmate.pitmutationmate.activity

import com.amos.pitmutationmate.pitmutationmate.buildSystem.BuildSystemUtils
import com.amos.pitmutationmate.pitmutationmate.services.PluginCheckerService
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.service
import com.intellij.openapi.diagnostic.thisLogger
import com.intellij.openapi.project.Project
import com.intellij.openapi.startup.ProjectActivity

class LaunchActivity : ProjectActivity {
    override suspend fun execute(project: Project) {
        ApplicationManager.getApplication().runReadAction {
            val pluginChecker = project.service<PluginCheckerService>()
            val buildFiles = BuildSystemUtils.getProjectBuildFiles(project)
            thisLogger().debug("Detected buildFiles to analyze with PluginCheckerService on project open: $buildFiles")
            ApplicationManager.getApplication().invokeLater { pluginChecker.checkPlugins(buildFiles) }
        }
    }
}
