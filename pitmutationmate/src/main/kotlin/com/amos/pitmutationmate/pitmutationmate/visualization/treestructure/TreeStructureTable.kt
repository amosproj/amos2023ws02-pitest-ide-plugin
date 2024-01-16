// SPDX-FileCopyrightText: 2023 2023
//
// SPDX-License-Identifier: MIT

package com.amos.pitmutationmate.pitmutationmate.visualization.treestructure

import com.amos.pitmutationmate.pitmutationmate.reporting.XMLParser
import com.intellij.ui.ColoredTreeCellRenderer
import com.intellij.ui.treeStructure.treetable.TreeTable
import java.awt.GridLayout
import javax.swing.JPanel
import javax.swing.JScrollPane
import javax.swing.JTree

class TreeStructureTable : JPanel() {

    init {
        layout = GridLayout(0, 1)
        val treeTableModel = TreeTableModel(createDataStructure())
        val treeTable = TreeTable(treeTableModel)
        treeTable.setRootVisible(true)
        treeTable.apply {
            tree.apply {
                showsRootHandles = true
                isRootVisible = true
//                cellRenderer = CellRenderer()
            }
        }
        treeTable.tableHeader.reorderingAllowed = false
        treeTable.tableHeader.resizingAllowed = false
        this.add(JScrollPane(treeTable))
        setSize(1000, 800)
    }
    companion object {
        const val ID = "PackageBreakdown"
        const val TITLE = "Package Breakdown"
        private var rootNode: MutableList<DataNode> = ArrayList()
        private fun createDataStructure(): DataNode {
            // TODO: replace with real coverage reports
            val coverageReport1: XMLParser.CoverageReport = XMLParser.CoverageReport(
                fileName = "myfilename1",
                packageName = "mypackagename1",
                mutatedClass = "mymutatedclass1",
                lineCoveragePercentage = 0,
                lineCoverageTextRatio = "0/19",
                mutationCoveragePercentage = 0,
                mutationCoverageTextRatio = "0/19",
                testStrengthPercentage = 0,
                testStrengthTextRatio = "0/19",
                numberOfClasses = 1
            )
            val coverageReport2: XMLParser.CoverageReport = XMLParser.CoverageReport(
                fileName = "myfilename2",
                packageName = "mypackagename2",
                mutatedClass = "mymutatedclass2",
                lineCoveragePercentage = 0,
                lineCoverageTextRatio = "0/19",
                mutationCoveragePercentage = 0,
                mutationCoverageTextRatio = "0/19",
                testStrengthPercentage = 0,
                testStrengthTextRatio = "0/19",
                numberOfClasses = 2
            )
            val coverageReport3: XMLParser.CoverageReport = XMLParser.CoverageReport(
                fileName = "myfilename3",
                packageName = "mypackagename2",
                mutatedClass = "mymutatedclass3",
                lineCoveragePercentage = 0,
                lineCoverageTextRatio = "0/19",
                mutationCoveragePercentage = 0,
                mutationCoverageTextRatio = "0/19",
                testStrengthPercentage = 0,
                testStrengthTextRatio = "0/19",
                numberOfClasses = 2
            )
            var coverageReports: List<XMLParser.CoverageReport> = emptyList()
            coverageReports = coverageReports.plus(coverageReport1)
            coverageReports = coverageReports.plus(coverageReport2)
            coverageReports = coverageReports.plus(coverageReport3)

            // iterate over reports and add them to data node structure
            for (report in coverageReports) {
                var packageNode = rootNode.find { it.name == report.packageName }
                if (packageNode != null) {
                    packageNode.children = packageNode.children?.plus(DataNode(report.fileName, report.numberOfClasses, report.lineCoverageTextRatio, report.mutationCoverageTextRatio, report.testStrengthTextRatio, emptyList()))
                } else {
                    packageNode = DataNode(report.packageName, report.numberOfClasses, report.lineCoverageTextRatio, report.mutationCoverageTextRatio, report.testStrengthTextRatio, emptyList())
                    packageNode.children = packageNode.children?.plus(DataNode(report.fileName, report.numberOfClasses, report.lineCoverageTextRatio, report.mutationCoverageTextRatio, report.testStrengthTextRatio, emptyList()))
                    rootNode = rootNode.plus(packageNode).toMutableList()
                }
            }
            return DataNode("All", 0, "", "", "", rootNode)
        }
    }
}

class CellRenderer : ColoredTreeCellRenderer() {
    override fun customizeCellRenderer(
        tree: JTree,
        value: Any?,
        selected: Boolean,
        expanded: Boolean,
        leaf: Boolean,
        row: Int,
        hasFocus: Boolean
    ) {
        append("liam")
    }
}
