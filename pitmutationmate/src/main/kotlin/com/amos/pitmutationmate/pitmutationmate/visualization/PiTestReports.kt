// SPDX-License-Identifier: MIT
// SPDX-FileCopyrightText: 2023

package com.amos.pitmutationmate.pitmutationmate.visualization

import com.amos.pitmutationmate.pitmutationmate.reporting.XMLParser
import com.intellij.ui.components.JBLabel
import com.intellij.ui.components.JBScrollPane
import com.intellij.ui.table.JBTable
import com.intellij.util.ui.JBUI
import com.intellij.util.ui.UIUtil
import java.awt.BorderLayout
import java.awt.Component
import java.awt.Dimension
import javax.swing.JComponent
import javax.swing.JPanel
import javax.swing.JTable
import javax.swing.ListSelectionModel
import javax.swing.table.DefaultTableCellRenderer
import javax.swing.table.DefaultTableModel

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
        } else if (value is CustomProgressBar) {
            return value
        } else if (value is JBLabel) {
            return value
        } else {
            return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column)
        }
    }
}

fun createTable(data: Array<Array<out JComponent>>, columnNames: Array<String>): JBTable {
    val model = object : DefaultTableModel(data, columnNames) {
        override fun isCellEditable(row: Int, column: Int): Boolean {
            return false
        }
    }

    val table = JBTable(model)

//    table.setRowHeight(lineCoverageBar.height + 2)
    table.tableHeader.reorderingAllowed = false
    table.tableHeader.resizingAllowed = false
    table.tableHeader.font = UIUtil.getLabelFont()

    table.border = JBUI.Borders.empty()
    table.setShowGrid(false)
    table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION)
    table.columnSelectionAllowed = false
    table.intercellSpacing = Dimension(0, 0)

    val firstColumnWidth = table.tableHeader.getFontMetrics(table.tableHeader.font).stringWidth(" Pit Test Coverage Report ") + 10
    table.columnModel.getColumn(0).maxWidth = firstColumnWidth
    table.columnModel.getColumn(0).minWidth = firstColumnWidth
    table.columnModel.getColumn(0).preferredWidth = firstColumnWidth
    table.columnModel.getColumn(0).width = firstColumnWidth

    table.columnModel.getColumn(0).cellRenderer = cellRenderer()
    table.columnModel.getColumn(1).cellRenderer = cellRenderer()

    return table
}

fun getLabel(text: String): JBLabel {
    val label = JBLabel(text)
    label.font = UIUtil.getLabelFont()
    return label
}

class PiTestClassReport(
    private val coverageReport: XMLParser.CoverageReport = XMLParser.CoverageReport(
        fileName = "Test",
        packageName = "Test",
        mutatedClass = "Test",
        lineCoveragePercentage = 0,
        lineCoverageTextRatio = "",
        mutationCoveragePercentage = 0,
        mutationCoverageTextRatio = "",
        testStrengthPercentage = 0,
        testStrengthTextRatio = "",
        numberOfClasses = 0
    )
) : JPanel() {

    private var height: Int = 0

    fun renderer(compact: Boolean = false) {
        this.removeAll() // To assure an empty block
        val lineCoverageBar = CustomProgressBar(this.coverageReport.lineCoveragePercentage, this.coverageReport.lineCoverageTextRatio)
        val mutationCoverageBar = CustomProgressBar(this.coverageReport.mutationCoveragePercentage, this.coverageReport.mutationCoverageTextRatio)
        val testStrengthBar = CustomProgressBar(this.coverageReport.testStrengthPercentage, this.coverageReport.testStrengthTextRatio)

        val data = arrayOf(
            arrayOf(getLabel("Number of Classes"), getLabel(this.coverageReport.numberOfClasses.toString())),
            arrayOf(getLabel("Line Coverage"), lineCoverageBar),
            arrayOf(getLabel("Mutation Coverage"), mutationCoverageBar),
            arrayOf(getLabel("Test Strength"), testStrengthBar)
        )
        val columnNames = arrayOf("Pit Test Coverage Report", "")
        val table = createTable(data, columnNames)
        layout = BorderLayout()

        add(JBScrollPane(table))
        this.height = (table.rowCount + 2) * table.rowHeight
    }

    fun getCustomHeight(): Int {
        return this.height
    }
}

class PiTestReports : JPanel() {
    private var reports: ArrayList<PiTestClassReport> = ArrayList()
    private var summary: PiTestClassReport = PiTestClassReport()

    fun addReport(report: PiTestClassReport) {
        this.reports.add(report)
    }

    fun addSummary(summary: PiTestClassReport) {
        this.summary = summary
    }

    private fun displayErrorMessage(): JPanel {
        val panel = JPanel()

        // Displaying an error message in the panel
        val errorMessage = "No results to display yet."
        val errorLabel = JBLabel(errorMessage)
        panel.add(errorLabel)

        return panel
    }

    fun visualizeReports() {
        this.removeAll()
        if (this.reports.isEmpty()) {
            this.add(displayErrorMessage())
            return
        }

        var rows = emptyArray<Array<out JComponent>>()
        val colNames = arrayOf("Class", "Result")
        var heights = emptyArray<Int>()

        for (i in this.reports.indices) {
            if (i < 5) {
                this.reports[i].renderer()
                heights += this.reports[i].getCustomHeight()
                rows += arrayOf(getLabel("Placeholder Class"), this.reports[i])
            } else {
                this.summary.renderer()
                heights += this.summary.getCustomHeight()
                rows += arrayOf(getLabel("Summary"), this.summary)
                break
            }
        }

        val table = createTable(rows, colNames)

        for (i in heights.indices) {
            table.setRowHeight(i, heights[i])
        }

        layout = BorderLayout()
        add(JBScrollPane(table))
    }
}
