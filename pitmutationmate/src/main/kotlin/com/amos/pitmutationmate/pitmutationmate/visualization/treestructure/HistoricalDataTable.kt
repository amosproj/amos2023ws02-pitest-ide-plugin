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

class HistoricalDataTable(project: Project) : JPanel() {

    private var rootNode: DataNode = DataNode(
        "",
        "",
        "",
        "",
        "",
        mutableListOf()
    )
    private var alreadyFoundData: MutableList<String> = mutableListOf()
    var treeTable: TreeTable
    companion object {
        const val ID = "HistoricalData"
        const val TITLE = "Historical Data"
    }

    init {
        layout = GridLayout(0, 1)
        val treeTableModel = TreeTableModel(createDataStructure(project))
        treeTable = TreeTable(treeTableModel)
        treeTable.setRootVisible(true)
        treeTable.apply {
            tree.apply {
                showsRootHandles = true
                isRootVisible = true
            }
        }
        treeTable.tableHeader.reorderingAllowed = false
        treeTable.tableHeader.resizingAllowed = true
        this.add(JScrollPane(treeTable))
        setSize(1000, 800)
    }

    fun createDataStructure(project: Project): DataNode {
        deleteTree(rootNode)
        alreadyFoundData.clear()

        val bundledReports = project.service<MutationResultService>().getHistoricMutationResults()
            ?: return DataNode("All", "", "", "", "", mutableListOf())

        val coverageReports = bundledReports.coverageReports
        val packageReports = bundledReports.packageReports
        val totalReport = bundledReports.totalResult

        rootNode = if (totalReport != null) {
            createReportDataNode("All", totalReport, mutableListOf())
        } else {
            DataNode("All", "", "", "", "", mutableListOf())
        }
        // iterate over reports and add them to data node structure
        for (report in coverageReports) {
            var packageNode = rootNode.children.find { it.name == report.packageName }
            if (packageNode != null) {
                if (!checkIfAlreadyFound(report.fileName)) {
                    packageNode.children.add(createReportDataNode(report.fileName, report))
                }
            } else {
                val packageReport = packageReports.find { it.packageName == report.packageName }
                if (packageReport != null) {
                    if (!checkIfAlreadyFound(packageReport.packageName)) {
                        packageNode = createReportDataNode(packageReport.packageName, packageReport)
                        if (!checkIfAlreadyFound(report.fileName)) {
                            packageNode.children.add(createReportDataNode(report.fileName, report))
                        }
                        rootNode.children.add(packageNode)
                    }
                }
            }
        }

        return rootNode
    }

    private fun deleteTree(rootNode: DataNode) {
        if (rootNode.children.isEmpty()) {
            return
        }
        for (child in rootNode.children) {
            deleteTree(child)
        }
        rootNode.children.removeAll(rootNode.children)
    }

    private fun createReportDataNode(
        name: String,
        report: XMLParser.CoverageReport,
        children: MutableList<DataNode> = mutableListOf()
    ): DataNode {
        return DataNode(
            name,
            report.numberOfClasses.toString(),
            report.lineCoverageTextRatio,
            report.mutationCoverageTextRatio,
            report.testStrengthTextRatio,
            children
        )
    }

    private fun checkIfAlreadyFound(name: String): Boolean {
        return if (!alreadyFoundData.contains(name)) {
            alreadyFoundData.add(name)
            false
        } else {
            true
        }
    }
}
