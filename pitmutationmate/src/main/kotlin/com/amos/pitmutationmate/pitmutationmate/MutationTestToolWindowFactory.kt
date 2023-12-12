// SPDX-License-Identifier: MIT
// SPDX-FileCopyrightText: 2023
package com.amos.pitmutationmate.pitmutationmate

import com.amos.pitmutationmate.pitmutationmate.reporting.XMLParser
import com.amos.pitmutationmate.pitmutationmate.visualization.BarGraph
import com.amos.pitmutationmate.pitmutationmate.visualization.LatestPiTestReport
import com.amos.pitmutationmate.pitmutationmate.visualization.LineGraph
import com.amos.pitmutationmate.pitmutationmate.visualization.treetable.JTreeTable
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.components.JBLabel
import com.intellij.ui.content.ContentFactory
import javax.swing.JPanel

internal class MutationTestToolWindowFactory : ToolWindowFactory, DumbAware {
    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        // TODO: fetch most recent results to display (e.g. when opening up the editor and previous Pitest runs are saved)
        val lastCoverageReport: XMLParser.CoverageReport? = null
        val latestPiTestReport = if (lastCoverageReport == null) {
            ContentFactory.getInstance().createContent(displayErrorMessage(), "Latest Result", false)
        } else {
            ContentFactory.getInstance().createContent(LatestPiTestReport(lastCoverageReport), "Latest Result", false)
        }
        val table = ContentFactory.getInstance().createContent(JTreeTable(), "Mutationtest Coverage", false)
        val lineChart = ContentFactory.getInstance().createContent(LineGraph(), "Line Chart", false)
        val barChart = ContentFactory.getInstance().createContent(BarGraph(), "Bar Chart", false)

        toolWindow.contentManager.addContent(latestPiTestReport)
        toolWindow.contentManager.addContent(table)
        toolWindow.contentManager.addContent(lineChart)
        toolWindow.contentManager.addContent(barChart)
    }

    fun updateReport(toolWindow: ToolWindow, newCoverageReport: XMLParser.CoverageReport) {
        toolWindow.contentManager.findContent("Latest Result").component = LatestPiTestReport(newCoverageReport)
    }

    private fun displayErrorMessage(): JPanel {
        val panel = JPanel()

        // Displaying an error message in the panel
        val errorMessage = "No results to display yet."
        val errorLabel = JBLabel(errorMessage)
        panel.add(errorLabel)

        return panel
    }
}
