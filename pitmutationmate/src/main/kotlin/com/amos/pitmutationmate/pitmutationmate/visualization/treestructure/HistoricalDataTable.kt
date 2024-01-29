// SPDX-FileCopyrightText: 2023 2023
//
// SPDX-License-Identifier: MIT

package com.amos.pitmutationmate.pitmutationmate.visualization.treestructure

import com.amos.pitmutationmate.pitmutationmate.reporting.XMLParser
import com.amos.pitmutationmate.pitmutationmate.services.ReportPathGeneratorService
import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import com.intellij.ui.treeStructure.treetable.TreeTable
import com.squareup.wire.internal.newMutableList
import java.awt.GridLayout
import java.io.File
import java.nio.file.Path
import javax.swing.JPanel
import javax.swing.JScrollPane
import kotlin.io.path.exists

class HistoricalDataTable(project: Project) : JPanel() {

    private var rootNode: DataNode = DataNode(
        "",
        "",
        "",
        "",
        "",
        mutableListOf()
    )
    private var alreadyFoundData: MutableList<String> = newMutableList()
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

    private fun gatherHistoricData(
        coverageReports: MutableList<XMLParser.CoverageReport>,
        packageReports: MutableList<XMLParser.CoverageReport>,
        totalReports: MutableList<XMLParser.CoverageReport>,
        directory: File
    ) {
        val maxNumberOfLastReports = 3

        if (directory.exists() && directory.isDirectory) {
            val parser = XMLParser()
            val files = directory.listFiles()

            val sortedFiles = files?.sortedByDescending { it.lastModified() }
            val mostRecentFiles = sortedFiles?.take(maxNumberOfLastReports)

            if (mostRecentFiles != null) {
                for (file in mostRecentFiles) {
                    val mutationReportPath = Path.of(file.path + "/mutations.xml")
                    val coverageReportPath = Path.of(file.path + "/coverageInformation.xml")

                    if (mutationReportPath.exists() && coverageReportPath.exists()) {
                        val resultData = parser.loadResultsFromXmlReport(mutationReportPath.toString(), coverageReportPath.toString())
                        coverageReports.addAll(resultData.coverageReports)
                        packageReports.addAll(resultData.packageReports)
                        resultData.totalResult?.let { totalReports.add(it) }
                    }
                }
            }
        }
    }

    private fun createDataStructure(project: Project): DataNode {
        deleteTree(rootNode)
        val coverageReports: MutableList<XMLParser.CoverageReport> = newMutableList()
        val packageReports: MutableList<XMLParser.CoverageReport> = newMutableList()
        val totalReports: MutableList<XMLParser.CoverageReport> = newMutableList()
        val directory = File(project.service<ReportPathGeneratorService>().getArchivePath().toString())

        gatherHistoricData(coverageReports, packageReports, totalReports, directory)

        val totalReport = totalReports.first()

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
