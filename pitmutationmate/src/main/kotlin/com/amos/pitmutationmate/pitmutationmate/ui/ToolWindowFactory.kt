// SPDX-License-Identifier: MIT
// SPDX-FileCopyrightText: 2023

package com.amos.pitmutationmate.pitmutationmate.ui

import com.amos.pitmutationmate.pitmutationmate.reporting.XMLParser
import com.amos.pitmutationmate.pitmutationmate.services.MutationResultService
import com.amos.pitmutationmate.pitmutationmate.services.PluginCheckerService
import com.amos.pitmutationmate.pitmutationmate.visualization.ConfigurationErrorPanel
import com.amos.pitmutationmate.pitmutationmate.visualization.PiTestClassReport
import com.amos.pitmutationmate.pitmutationmate.visualization.PiTestReports
import com.amos.pitmutationmate.pitmutationmate.visualization.treestructure.HistoricalDataTable
import com.amos.pitmutationmate.pitmutationmate.visualization.treestructure.PackageBreakdownTable
import com.amos.pitmutationmate.pitmutationmate.visualization.treestructure.TreeTableModel
import com.intellij.openapi.components.service
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.openapi.wm.ToolWindowManager
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
            val packageBreakdownTable = ContentFactory.getInstance().createContent(PackageBreakdownTable(project), PackageBreakdownTable.TITLE, false)
            val historicalDataTable = ContentFactory.getInstance().createContent(HistoricalDataTable(project), HistoricalDataTable.TITLE, false)

            toolWindow.contentManager.addContent(coverageReport)
            toolWindow.contentManager.addContent(packageBreakdownTable)
            toolWindow.contentManager.addContent(historicalDataTable)

            val reportGeneratorService = project.service<MutationResultService>()
            val newCoverageReports = reportGeneratorService.updateLastMutationResult()
            updateReport(toolWindow, newCoverageReports)
        }

        fun updateReport(toolWindow: ToolWindow, newResultData: XMLParser.ResultData?) {
            val project = toolWindow.project
            val coverageResults = newResultData?.coverageReports
            val totals = newResultData?.totalResult

            val reportWindowContent = toolWindow.contentManager.findContent(PiTestReports.TITLE) ?: return
            val reportWindow = reportWindowContent.component
            if (reportWindow is PiTestReports) {
                reportWindow.deleteReports()
                if (coverageResults != null) {
                    for (report in coverageResults) {
                        reportWindow.addReport(PiTestClassReport(report))
                    }
                }
                totals?.let { PiTestClassReport(it) }?.let { reportWindow.setSummary(it) }
                ToolWindowManager.getInstance(project).invokeLater {
                    reportWindow.visualizeReports()
                }
            }

            val treeStructureContent = toolWindow.contentManager.findContent(PackageBreakdownTable.TITLE) ?: return
            val treeStructure = treeStructureContent.component

            if (treeStructure is PackageBreakdownTable) {
                ToolWindowManager.getInstance(project).invokeLater {
                    val newRootNode = treeStructure.createDataStructure(project)
                    (treeStructure.treeTable.tableModel as TreeTableModel).updateData(newRootNode)
                }
            }

            val historicalDataContent = toolWindow.contentManager.findContent(HistoricalDataTable.TITLE) ?: return
            val historicalData = historicalDataContent.component

            if (historicalData is HistoricalDataTable) {
                ToolWindowManager.getInstance(project).invokeLater {
                    val newRootNode = historicalData.createDataStructure(project)
                    (historicalData.treeTable.tableModel as TreeTableModel).updateData(newRootNode)
                }
            }
        }
    }
}
