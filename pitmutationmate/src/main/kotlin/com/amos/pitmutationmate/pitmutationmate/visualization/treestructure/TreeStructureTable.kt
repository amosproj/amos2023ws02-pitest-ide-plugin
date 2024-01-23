// SPDX-FileCopyrightText: 2023 2023
//
// SPDX-License-Identifier: MIT

package com.amos.pitmutationmate.pitmutationmate.visualization.treestructure

import com.amos.pitmutationmate.pitmutationmate.reporting.XMLParser
import com.amos.pitmutationmate.pitmutationmate.services.MutationResultService
import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import com.intellij.ui.treeStructure.treetable.TreeTable
import java.awt.GridLayout
import javax.swing.JPanel
import javax.swing.JScrollPane

class TreeStructureTable(project: Project) : JPanel() {

    init {
        layout = GridLayout(0, 1)
        val treeTableModel = TreeTableModel(createDataStructure(project))
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
        treeTable.tableHeader.resizingAllowed = true
        this.add(JScrollPane(treeTable))
        setSize(1000, 800)
    }
    companion object {

        const val ID = "PackageBreakdown"
        const val TITLE = "Package Breakdown"
        private var rootNode: MutableList<DataNode> = ArrayList()
        private fun createDataStructure(project: Project): DataNode {
            val coverageReports: MutableList<XMLParser.CoverageReport>? = project.service<MutationResultService>().updateLastMutationResult()?.coverageReports

            // iterate over reports and add them to data node structure
            if (coverageReports != null) {
                for (report in coverageReports) {
                    var packageNode = rootNode.find { it.name == report.packageName }
                    if (packageNode != null) {
                        packageNode.children = packageNode.children?.plus(DataNode(report.fileName, report.numberOfClasses.toString(), report.lineCoverageTextRatio, report.mutationCoverageTextRatio, report.testStrengthTextRatio, emptyList()))
                    } else {
                        packageNode = DataNode(report.packageName, "", "", "", "", emptyList())
                        packageNode.children = packageNode.children?.plus(DataNode(report.fileName, report.numberOfClasses.toString(), report.lineCoverageTextRatio, report.mutationCoverageTextRatio, report.testStrengthTextRatio, emptyList()))
                        rootNode = rootNode.plus(packageNode).toMutableList()
                    }
                }
            }
            return DataNode("All", "", "", "", "", rootNode)
        }
    }
}
