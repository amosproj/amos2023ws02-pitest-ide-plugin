// SPDX-License-Identifier: MIT
// SPDX-FileCopyrightText: 2023 Lennart Heimbs

package com.amos.pitmutationmate.pitmutationmate.services

import com.amos.pitmutationmate.pitmutationmate.reporting.XMLParser
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import java.nio.file.Path
import kotlin.io.path.exists

@Service(Service.Level.PROJECT)
class MutationResultService(private val project: Project) {

    private var lastMutationResult: XMLParser.ResultData? = null
    private var lastHistoricResult: XMLParser.ResultData? = null

    fun getMutationResult(): XMLParser.ResultData? {
        if (lastMutationResult == null) {
            updateLastMutationResult()
        }
        return lastMutationResult
    }

    fun updateLastMutationResult() {
        val reportPathGenerator = project.service<ReportPathGeneratorService>()
        val mutationReportPath = reportPathGenerator.getReportMutationsFile()
        val coverageReportPath = reportPathGenerator.getReportCoverageFile()

        if (mutationReportPath.exists() && coverageReportPath.exists()) {
            val parser = XMLParser()
            lastMutationResult = parser.loadResultsFromXmlReport(mutationReportPath.toString(), coverageReportPath.toString())
        }
    }

    fun getHistoricMutationResults(): XMLParser.ResultData? {
        if (lastHistoricResult == null) {
            updateHistoricMutationResults()
        }
        return lastHistoricResult
    }

    fun updateHistoricMutationResults() {
        val directory = project.service<ReportPathGeneratorService>().getArchivePath().toFile()
        val result: XMLParser.ResultData = XMLParser.ResultData()

        if (directory.exists() && directory.isDirectory) {
            val parser = XMLParser()
            val files = directory.listFiles()

            val sortedFiles = files?.sortedByDescending { it.lastModified() }

            if (sortedFiles != null) {
                for (file in sortedFiles) {
                    val service = project.service<ReportPathGeneratorService>()
                    val mutationReportPath = service.getReportMutationsFile(Path.of(file.path))
                    val coverageReportPath = service.getReportCoverageFile(Path.of(file.path))

                    if (mutationReportPath.exists() && coverageReportPath.exists()) {
                        val resultData = parser.loadResultsFromXmlReport(mutationReportPath.toString(), coverageReportPath.toString())
                        result.coverageReports.addAll(resultData.coverageReports)
                        result.packageReports.addAll(resultData.packageReports)
                        result.totalResult = resultData.totalResult
                    }
                }
            }
        }
        lastHistoricResult = result
    }
}
