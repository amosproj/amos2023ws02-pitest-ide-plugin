// SPDX-License-Identifier: MIT
// SPDX-FileCopyrightText: 2023

package com.amos.pitmutationmate.pitmutationmate.visualization

import com.amos.pitmutationmate.pitmutationmate.reporting.XMLParser
import com.intellij.ui.components.JBLabel
import com.intellij.ui.components.JBScrollPane
import com.intellij.ui.table.JBTable
import com.intellij.util.ui.JBUI
import com.intellij.util.ui.UIUtil
import javaslang.Tuple
import java.awt.BorderLayout
import java.awt.Component
import java.awt.Dimension
import javax.swing.JComponent
import javax.swing.JPanel
import javax.swing.JTable
import javax.swing.ListSelectionModel
import javax.swing.table.DefaultTableCellRenderer
import javax.swing.table.DefaultTableModel

class PiTestClassReport(
    private val coverageReport: XMLParser.CoverageReport = XMLParser.CoverageReport(
        lineCoveragePercentage = 0,
        lineCoverageTextRatio = "",
        mutationCoveragePercentage = 0,
        mutationCoverageTextRatio = "",
        testStrengthPercentage = 0,
        testStrengthTextRatio = ""
    )
) : JPanel() {

    private var height: Int = 0

    fun largeRenderer() {
        this.removeAll() // To assure an empty block
        val lineCoverageBar = CustomProgressBar(this.coverageReport.lineCoveragePercentage, this.coverageReport.lineCoverageTextRatio)
        val mutationCoverageBar = CustomProgressBar(this.coverageReport.mutationCoveragePercentage, this.coverageReport.mutationCoverageTextRatio)
        val testStrengthBar = CustomProgressBar(this.coverageReport.testStrengthPercentage, this.coverageReport.testStrengthTextRatio)

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
        table.tableHeader.font = UIUtil.getLabelFont()

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
        this.height = (table.rowCount + 2) * table.rowHeight
    }

    fun smallRenderer() {
        this.removeAll() // To assure an empty block
        val lineCoverageBar = CustomProgressBar(this.coverageReport.lineCoveragePercentage, this.coverageReport.lineCoverageTextRatio)

        val data = arrayOf(
            arrayOf(getLabel("Class Name"), getLabel("Test.java")),
            arrayOf(getLabel("Line Coverage"), lineCoverageBar)
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
        table.tableHeader.font = UIUtil.getLabelFont()

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
        this.height = (table.rowCount + 2) * table.rowHeight
    }

    fun getCustomHeight(): Int {
        return this.height
    }

    private fun getLabel(text: String): JBLabel {
        val label = JBLabel(text)
        label.font = UIUtil.getLabelFont()
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

class PiTestReports : JPanel() {
    private var reports: ArrayList<PiTestClassReport> = ArrayList()

    fun addReport(report: PiTestClassReport) {
        this.reports.add(report)
    }

    private fun displayErrorMessage(): JPanel {
        val panel = JPanel()

        // Displaying an error message in the panel
        val errorMessage = "No results to display yet."
        val errorLabel = JBLabel(errorMessage)
        panel.add(errorLabel)

        return panel
    }

    private fun getLabel(text: String): JBLabel {
        val label = JBLabel(text)
        label.font = UIUtil.getLabelFont()
        return label
    }

    fun visualizeReports() {
        this.removeAll()
        if (this.reports.isEmpty()) {
            this.add(displayErrorMessage())
            return
        }

        var rows = emptyArray<Array<out JComponent>>()
        val cols = arrayOf("Class", "Result")

        var heights = emptyArray<Int>()

        for (i in this.reports.indices) {
            if (i < 5){
                this.reports[i].largeRenderer()
            } else {
                this.reports[i].smallRenderer()
            }
            heights += this.reports[i].getCustomHeight()
            rows += arrayOf(getLabel("Test_Report"), this.reports[i])
        }

        val model = object : DefaultTableModel(rows, cols) {
            override fun isCellEditable(row: Int, column: Int): Boolean {
                return false
            }
        }

        val table = JBTable(model)

        table.columnModel.getColumn(0).cellRenderer = cellRenderer()
        table.columnModel.getColumn(1).cellRenderer = cellRenderer()

        val firstColumnWidth = table.tableHeader.getFontMetrics(table.tableHeader.font).stringWidth(" Pit Test Coverage Report ") + 5
        table.columnModel.getColumn(0).maxWidth = firstColumnWidth
        table.columnModel.getColumn(0).minWidth = firstColumnWidth
        table.columnModel.getColumn(0).preferredWidth = firstColumnWidth
        table.columnModel.getColumn(0).width = firstColumnWidth

        for (i in heights.indices) {
            table.setRowHeight(i, heights[i])
        }

        layout = BorderLayout()

        add(JBScrollPane(table))
    }

    private class cellRenderer : DefaultTableCellRenderer() {

        override fun getTableCellRendererComponent(
            table: JTable?,
            value: Any?,
            isSelected: Boolean,
            hasFocus: Boolean,
            row: Int,
            column: Int
        ): Component {
            if (value is PiTestClassReport) {
                return value
            } else if (value is JBLabel) {
                return value
            } else {
                return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column)
            }
        }
    }
}
