// SPDX-License-Identifier: MIT
// SPDX-FileCopyrightText: 2023

package com.amos.pitmutationmate.pitmutationmate.ui

import com.amos.pitmutationmate.pitmutationmate.reporting.XMLParser
import com.amos.pitmutationmate.pitmutationmate.services.MutationResultService
import com.amos.pitmutationmate.pitmutationmate.services.PluginCheckerService
import com.amos.pitmutationmate.pitmutationmate.visualization.ConfigurationErrorPanel
import com.amos.pitmutationmate.pitmutationmate.visualization.PiTestClassReport
import com.amos.pitmutationmate.pitmutationmate.visualization.PiTestReports
import com.amos.pitmutationmate.pitmutationmate.visualization.treestructure.TreeStructureTable
import com.intellij.openapi.components.service
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.content.ContentFactory

internal class ToolWindowFactory : ToolWindowFactory, DumbAware {

    companion object {
        const val ID = "Pitest"
    }

    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        val pluginError = project.service<PluginCheckerService>().getErrorMessage()
        if (pluginError != null) {
            Util.initiateWithConfigError(pluginError, toolWindow)
        } else {
            Util.initiateWithData(toolWindow, project)
            Util.updateReport(toolWindow, null)
        }
    }

    object Util {
        fun initiateWithConfigError(errorMessage: String, toolWindow: ToolWindow) {
            toolWindow.contentManager.removeAllContents(true)
            val errorDialog = ConfigurationErrorPanel(errorMessage)
            val errorContent =
                ContentFactory.getInstance().createContent(errorDialog, ConfigurationErrorPanel.ID, false)
            toolWindow.contentManager.addContent(errorContent)
        }

        fun initiateWithData(toolWindow: ToolWindow, project: Project) {
            toolWindow.contentManager.removeAllContents(true)

            val coverageReport = ContentFactory.getInstance().createContent(PiTestReports(), PiTestReports.TITLE, false)
            val table = ContentFactory.getInstance().createContent(TreeStructureTable(project), TreeStructureTable.TITLE, false)

            toolWindow.contentManager.addContent(coverageReport)
            toolWindow.contentManager.addContent(table)

            val reportGeneratorService = project.service<MutationResultService>()
            val newCoverageReport = reportGeneratorService.updateLastMutationResult()?.coverageReports?.first()
            if (newCoverageReport != null) {
                updateReport(toolWindow, newCoverageReport)
            } else {
                updateReport(toolWindow, null)
            }
        }

        fun updateReport(toolWindow: ToolWindow, newCoverageReport: XMLParser.CoverageReport?) {
            val report = if (newCoverageReport != null) {
                PiTestClassReport(newCoverageReport)
            } else {
                null
            }
            val reportWindow = toolWindow.contentManager.findContent(PiTestReports.TITLE).component

            if (reportWindow is PiTestReports) {
                if (report != null) {
                    reportWindow.addReport(report)
                }
                reportWindow.visualizeReports()
            }
        }
    }
}
