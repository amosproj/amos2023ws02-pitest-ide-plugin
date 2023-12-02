// SPDX-FileCopyrightText: 2023 2023
//
// SPDX-License-Identifier: MIT

package com.amos.pitmutationmate.pitmutationmate.visualization

import com.intellij.ui.components.JBLabel
import com.intellij.ui.components.JBScrollPane
import com.intellij.ui.table.JBTable
import java.awt.*
import javax.swing.JPanel
import javax.swing.JTable
import javax.swing.table.DefaultTableCellRenderer
import javax.swing.table.DefaultTableModel

class LatestPiTestReport : JPanel() {

    init {
        val lineCoverageBar = CustomProgressBar(30, "1/5")
        val mutationCoverageBar = CustomProgressBar(50, "3000/30000")
        val testStrengthBar = CustomProgressBar(93, "200/2000")

        val title = "Latest Pit Test Coverage Report"
        val titleLabel = JBLabel(title)
        titleLabel.font = Font("Arial", Font.BOLD, 18)

        val className = "Test.java"
        val classNameLabel = JBLabel(className)
        classNameLabel.font = Font("Arial", Font.BOLD, 12)

        val data = arrayOf(arrayOf(classNameLabel, lineCoverageBar, mutationCoverageBar, testStrengthBar))
        val columnNames = arrayOf("Class Name", "Line Coverage", "Mutation Coverage", "Test Strength")
        val model = DefaultTableModel(data, columnNames)
        val table = JBTable(model)

        table.setRowHeight(lineCoverageBar.height + 1)
        table.tableHeader.reorderingAllowed = false
        table.setShowGrid(false)
        table.intercellSpacing = Dimension(0, 0)
        table.tableHeader.font = Font("Arial", Font.BOLD, 12)

        for (i in columnNames.indices) {
            val progressCol = table.columnModel.getColumn(i)
            val curr = data[0][i]

            var newWidth = -1
            if(curr is CustomProgressBar) {
                newWidth = curr.width
            } else if(curr is JBLabel) {
                val tableHeaderFont = table.tableHeader.font
                var columnNameWidth = table.tableHeader.getFontMetrics(tableHeaderFont).stringWidth(columnNames[i])
                columnNameWidth += 14 // add some padding
                newWidth = getLabelWidthDynamically(classNameLabel)
                newWidth = if (newWidth < columnNameWidth) columnNameWidth else newWidth
            }

            if(newWidth != -1) {
                progressCol.preferredWidth = newWidth + 2
                progressCol.maxWidth = newWidth + 2
                progressCol.minWidth = newWidth + 2
                progressCol.cellRenderer = CustomProgressBarRenderer()
            }
        }

        // Set up layout with FlowLayout
        layout = BorderLayout()

        // Add title label to the North region
        add(titleLabel, BorderLayout.NORTH)

        // Create a panel for the table with FlowLayout
        val tablePanel = JPanel(FlowLayout(FlowLayout.LEFT))
        tablePanel.add(JBScrollPane(table))

        // Add the table panel to the Center region
        add(tablePanel, BorderLayout.CENTER)
    }

    private fun getLabelWidthDynamically(label: JBLabel) : Int {
        val fontMetrics = label.getFontMetrics(label.font)
        return fontMetrics.stringWidth(label.text)
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
            } else if(value is JBLabel) {
                return value
            } else {
                return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column)
            }
        }
    }
}
