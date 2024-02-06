// SPDX-License-Identifier: MIT
// SPDX-FileCopyrightText: 2024 Lennart Heimbs

package com.amos.pitmutationmate.pitmutationmate.activity

import com.amos.pitmutationmate.pitmutationmate.services.MutationResultService
import com.amos.pitmutationmate.pitmutationmate.services.PluginCheckerService
import com.amos.pitmutationmate.pitmutationmate.ui.ToolWindowFactory
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import com.intellij.openapi.startup.ProjectActivity

class LaunchActivity : ProjectActivity {
    override suspend fun execute(project: Project) {
        project.service<PluginCheckerService>().getBuildEnvironment()

        ApplicationManager.getApplication().invokeLater {
            project.service<MutationResultService>().updateLastMutationResult()
            val coverageReport = project.service<MutationResultService>().getMutationResult()
            // also parse history data for history tab
            project.service<MutationResultService>().updateHistoricMutationResults()
            // Check if coverageResult is stored
            if (coverageReport != null) {
                ToolWindowFactory.Util.updateReport(project, coverageReport)
                ToolWindowFactory.Util.updateTree(project)
                ToolWindowFactory.Util.updateHistory(project)
            }
        }
    }
}
