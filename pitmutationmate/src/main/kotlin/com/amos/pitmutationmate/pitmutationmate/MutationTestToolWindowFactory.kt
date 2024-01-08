// SPDX-License-Identifier: MIT
// SPDX-FileCopyrightText: 2023
package com.amos.pitmutationmate.pitmutationmate

import com.amos.pitmutationmate.pitmutationmate.reporting.XMLParser
import com.amos.pitmutationmate.pitmutationmate.visualization.BarGraph
import com.amos.pitmutationmate.pitmutationmate.visualization.LineGraph
import com.amos.pitmutationmate.pitmutationmate.visualization.PiTestClassReport
import com.amos.pitmutationmate.pitmutationmate.visualization.PiTestReports
import com.amos.pitmutationmate.pitmutationmate.visualization.treetable.JTreeTable
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.content.ContentFactory

internal class MutationTestToolWindowFactory : ToolWindowFactory, DumbAware {
    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        // TODO: fetch most recent results to display (e.g. when opening up the editor and previous Pitest runs are saved)

        val coverageReport = ContentFactory.getInstance().createContent(PiTestReports(), "Reports", false)
        val table = ContentFactory.getInstance().createContent(JTreeTable(), "Mutationtest Coverage", false)
        val lineChart = ContentFactory.getInstance().createContent(LineGraph(), "Line Chart", false)
        val barChart = ContentFactory.getInstance().createContent(BarGraph(), "Bar Chart", false)

        toolWindow.contentManager.addContent(coverageReport)
        toolWindow.contentManager.addContent(table)
        toolWindow.contentManager.addContent(lineChart)
        toolWindow.contentManager.addContent(barChart)

        updateReport(toolWindow, null)
    }

    fun updateReport(toolWindow: ToolWindow, newCoverageReport: XMLParser.CoverageReport?) {
        val report = if (newCoverageReport != null) {
            PiTestClassReport(newCoverageReport)
        } else {
            null
        }
        val reportWindow = toolWindow.contentManager.findContent("Reports").component

        if (reportWindow is PiTestReports) {
            if (report != null) {
                reportWindow.addReport(report)
            }
            reportWindow.visualizeReports()
        }
    }
}
