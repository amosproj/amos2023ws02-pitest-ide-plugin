// SPDX-FileCopyrightText: 2023 2023
//
// SPDX-License-Identifier: MIT

package com.amos.pitmutationmate.pitmutationmate.visualization

import com.amos.pitmutationmate.pitmutationmate.reporting.XMLParser
import com.intellij.ui.components.JBLabel
import com.intellij.ui.components.JBScrollPane
import com.intellij.ui.table.JBTable
import com.intellij.util.ui.JBUI
import java.awt.BorderLayout
import java.awt.Component
import java.awt.Dimension
import java.awt.Font
import javax.swing.JPanel
import javax.swing.JTable
import javax.swing.ListSelectionModel
import javax.swing.table.DefaultTableCellRenderer
import javax.swing.table.DefaultTableModel

class LatestPiTestReport(
    coverageReport: XMLParser.CoverageReport = XMLParser.CoverageReport(
        lineCoveragePercentage = 0,
        lineCoverageTextRatio = "",
        mutationCoveragePercentage = 0,
        mutationCoverageTextRatio = "",
        testStrengthPercentage = 0,
        testStrengthTextRatio = ""
    )
) : JPanel() {

    init {
        val lineCoverageBar = CustomProgressBar(coverageReport.lineCoveragePercentage, coverageReport.lineCoverageTextRatio)
        val mutationCoverageBar = CustomProgressBar(coverageReport.mutationCoveragePercentage, coverageReport.mutationCoverageTextRatio)
        val testStrengthBar = CustomProgressBar(coverageReport.testStrengthPercentage, coverageReport.testStrengthTextRatio)

        val data = arrayOf(
            arrayOf(getLabel("Class Name"), getLabel("Test.java")),
            arrayOf(getLabel("Line Coverage"), lineCoverageBar),
            arrayOf(getLabel("Mutation Coverage"), mutationCoverageBar),
            arrayOf(getLabel("Test Strength"), testStrengthBar)
        )

        val columnNames = arrayOf("Pit Test Coverage Report", "")

        val model = object : DefaultTableModel(data, columnNames) {
            override fun isCellEditable(row: Int, column: Int): Boolean {
                return false
            }
        }

        val table = JBTable(model)

        table.setRowHeight(lineCoverageBar.height + 2)
        table.tableHeader.reorderingAllowed = false
        table.tableHeader.resizingAllowed = false
        table.tableHeader.font = Font("Arial", Font.BOLD, 16)

        table.border = JBUI.Borders.empty()
        table.setShowGrid(false)
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION)
        table.columnSelectionAllowed = false
        table.intercellSpacing = Dimension(0, 0)

        table.columnModel.getColumn(0).cellRenderer = CustomProgressBarRenderer()

        val firstColumnWidth = table.tableHeader.getFontMetrics(table.tableHeader.font).stringWidth(" Pit Test Coverage Report ") + 5
        table.columnModel.getColumn(0).maxWidth = firstColumnWidth
        table.columnModel.getColumn(0).minWidth = firstColumnWidth
        table.columnModel.getColumn(0).preferredWidth = firstColumnWidth
        table.columnModel.getColumn(0).width = firstColumnWidth

        table.columnModel.getColumn(1).cellRenderer = CustomProgressBarRenderer()

        layout = BorderLayout()

        add(JBScrollPane(table))
    }

    private fun getLabel(text: String): JBLabel {
        val label = JBLabel(text)
        label.font = Font("Arial", Font.BOLD, 12)
        return label
    }

    private class CustomProgressBarRenderer : DefaultTableCellRenderer() {

        override fun getTableCellRendererComponent(
            table: JTable?,
            value: Any?,
            isSelected: Boolean,
            hasFocus: Boolean,
            row: Int,
            column: Int
        ): Component {
            if (value is CustomProgressBar) {
                return value
            } else if (value is JBLabel) {
                return value
            } else {
                return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column)
            }
        }
    }
}
