// SPDX-License-Identifier: MIT
// SPDX-FileCopyrightText: 2023

package com.amos.pitmutationmate.pitmutationmate.ui

import com.amos.pitmutationmate.pitmutationmate.reporting.XMLParser
import com.amos.pitmutationmate.pitmutationmate.visualization.ConfigurationErrorPanel
import com.amos.pitmutationmate.pitmutationmate.visualization.PiTestClassReport
import com.amos.pitmutationmate.pitmutationmate.visualization.PiTestReports
import com.amos.pitmutationmate.pitmutationmate.visualization.treestructure.HistoricalDataTable
import com.amos.pitmutationmate.pitmutationmate.visualization.treestructure.PackageBreakdownTable
import com.amos.pitmutationmate.pitmutationmate.visualization.treestructure.TreeTableModel
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.openapi.wm.ToolWindowManager
import com.intellij.ui.content.ContentFactory

internal class ToolWindowFactory : ToolWindowFactory {

    companion object {
        const val ID = "Pitest"
    }

    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        toolWindow.contentManager.removeAllContents(true)

        val coverageReport = ContentFactory.getInstance().createContent(PiTestReports(), PiTestReports.TITLE, false)
        val packageBreakdownTable = ContentFactory.getInstance().createContent(PackageBreakdownTable(project), PackageBreakdownTable.TITLE, false)
        val historicalDataTable = ContentFactory.getInstance().createContent(HistoricalDataTable(project), HistoricalDataTable.TITLE, false)
        val errorContent =
            ContentFactory.getInstance().createContent(ConfigurationErrorPanel(null), ConfigurationErrorPanel.TITLE, false)

        toolWindow.contentManager.addContent(coverageReport)
        toolWindow.contentManager.addContent(packageBreakdownTable)
        toolWindow.contentManager.addContent(historicalDataTable)
        toolWindow.contentManager.addContent(errorContent)
    }

    object Util {
        fun updateErrorPanel(project: Project, message: String?) {
            val toolWindow = ToolWindowManager.getInstance(project).getToolWindow(ID) ?: return

            val errorContent = toolWindow.contentManager.findContent(ConfigurationErrorPanel.TITLE)
            if (errorContent != null && errorContent.component is ConfigurationErrorPanel) {
                val errorPanel = errorContent.component as ConfigurationErrorPanel
                ToolWindowManager.getInstance(project).invokeLater {
                    errorPanel.updateMessage(message)
                }
            }
        }

        fun updateReport(project: Project, newResultData: XMLParser.ResultData?) {
            val toolWindow = ToolWindowManager.getInstance(project).getToolWindow(ID) ?: return

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
        }

        fun updateTree(project: Project) {
            val toolWindow = ToolWindowManager.getInstance(project).getToolWindow(ID) ?: return
            val treeStructureContent = toolWindow.contentManager.findContent(PackageBreakdownTable.TITLE) ?: return
            val treeStructure = treeStructureContent.component

            if (treeStructure is PackageBreakdownTable) {
                ToolWindowManager.getInstance(project).invokeLater {
                    val newRootNode = treeStructure.createDataStructure(project)
                    (treeStructure.treeTable.tableModel as TreeTableModel).updateData(newRootNode)
                }
            }
        }

        fun updateHistory(project: Project) {
            val toolWindow = ToolWindowManager.getInstance(project).getToolWindow(ID) ?: return
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
