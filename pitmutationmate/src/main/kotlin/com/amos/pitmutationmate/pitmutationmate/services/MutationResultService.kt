// SPDX-License-Identifier: MIT
// SPDX-FileCopyrightText: 2023 Lennart Heimbs

package com.amos.pitmutationmate.pitmutationmate.services

import com.amos.pitmutationmate.pitmutationmate.reporting.XMLParser
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import java.io.File
import kotlin.io.path.exists

@Service(Service.Level.PROJECT)
class MutationResultService(private val project: Project) {

    private var lastMutationResult: XMLParser.ResultData? = null

    fun getMutationResult(): XMLParser.ResultData? {
        val reportPathGenerator = project.service<ReportPathGeneratorService>()
        val mutationReportPath = reportPathGenerator.getReportMutationsFile()
        val coverageReportPath = reportPathGenerator.getReportCoverageFile()

        if (mutationReportPath.exists() && coverageReportPath.exists()) {
            val parser = XMLParser()
            return parser.loadResultsFromXmlReport(mutationReportPath.toString(), coverageReportPath.toString())
        }
        return null
    }

    fun updateLastMutationResult(): XMLParser.ResultData? {
        lastMutationResult = getMutationResult()
        return lastMutationResult
    }

    fun getHistoricMutationResults(): XMLParser.ResultData{
        val maxNumberOfRecentReportsToLookUp = 3
        val directory = project.service<ReportPathGeneratorService>().getArchivePath().toFile()

        return getHistoricMutationResults(maxNumberOfRecentReportsToLookUp, directory)
    }

    private fun getHistoricMutationResults(maxNumberOfRecentReportsToLookUp: Int, directory: File): XMLParser.ResultData {
        val result: XMLParser.ResultData = XMLParser.ResultData()

        if (directory.exists() && directory.isDirectory) {
            val parser = XMLParser()
            val files = directory.listFiles()

            val sortedFiles = files?.sortedByDescending { it.lastModified() }
            val mostRecentFiles = sortedFiles?.take(maxNumberOfRecentReportsToLookUp)

            if (mostRecentFiles != null) {
                for (file in mostRecentFiles) {
                    val service = project.service<ReportPathGeneratorService>()
                    val mutationReportPath = service.getMutationInformationPath(file.path)
                    val coverageReportPath = service.getCoverageInformationPath(file.path)

                    if (mutationReportPath.exists() && coverageReportPath.exists()) {
                        val resultData = parser.loadResultsFromXmlReport(mutationReportPath.toString(), coverageReportPath.toString())
                        result.coverageReports.addAll(resultData.coverageReports)
                        result.packageReports.addAll(resultData.packageReports)
                        result.totalResult = resultData.totalResult
                    }
                }
            }
        }
        return result
    }
}
