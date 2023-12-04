// SPDX-FileCopyrightText: 2023 2023
//
// SPDX-License-Identifier: MIT

package com.amos.pitmutationmate.pitmutationmate.visualization

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

class LatestPiTestReport : JPanel() {

    init {
        val lineCoverageBar = CustomProgressBar(30, "1/5")
        val mutationCoverageBar = CustomProgressBar(50, "3000/30000")
        val testStrengthBar = CustomProgressBar(93, "200/2000")

        val data = arrayOf(arrayOf(getLabel("Class Name"), getLabel("Test.java")),
                    arrayOf(getLabel("Line Coverage"), lineCoverageBar),
                    arrayOf(getLabel("Mutation Coverage"), mutationCoverageBar),
                    arrayOf(getLabel("Test Strength"), testStrengthBar))

        val columnNames = arrayOf("Pit Test Coverage Report", "")
        val model = DefaultTableModel(data, columnNames)
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
        table.columnModel.getColumn(1).cellRenderer = CustomProgressBarRenderer()

        layout = BorderLayout()

        add(JBScrollPane(table))
    }

    private fun getLabel(text: String) : JBLabel {
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
            } else if(value is JBLabel) {
                return value
            } else {
                return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column)
            }
        }
    }
}
