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
    private var treeTable: TreeTable
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

    private fun createDataStructure(project: Project): DataNode {
        deleteTree(rootNode)

        val bundledReports = project.service<MutationResultService>().getHistoricMutationResults()
        val coverageReports = bundledReports.coverageReports
        val packageReports = bundledReports.packageReports
        val totalReport = bundledReports.totalResult

        if (totalReport != null) {
            rootNode = createReportDataNode("All", totalReport, mutableListOf())!!
            // iterate over reports and add them to data node structure
            for (report in coverageReports) {
                var packageNode = rootNode.children.find { it.name == report.packageName }
                if (packageNode != null) {
                    createReportDataNode(report.fileName, report)?.let { packageNode!!.children.add(it) }
                } else {
                    val packageReport = packageReports.find { it.packageName == report.packageName }
                    if (packageReport != null) {
                        packageNode = createReportDataNode(packageReport.packageName, packageReport)
                        if (packageNode != null) {
                            createReportDataNode(report.fileName, report)?.let { packageNode.children.add(it) }
                        }
                        if (packageNode != null) {
                            rootNode.children.add(packageNode)
                        }
                    }
                }
            }
        } else {
            rootNode = DataNode("No test run", "found in history!", "Try to rerun", " pitest", "or restart the IDE", mutableListOf())
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
    ): DataNode? {
        if (!alreadyFoundData.contains(name)) {
            alreadyFoundData.add(name)
            return DataNode(
                name,
                report.numberOfClasses.toString(),
                report.lineCoverageTextRatio,
                report.mutationCoverageTextRatio,
                report.testStrengthTextRatio,
                children
            )
        }
        return null
    }
}
