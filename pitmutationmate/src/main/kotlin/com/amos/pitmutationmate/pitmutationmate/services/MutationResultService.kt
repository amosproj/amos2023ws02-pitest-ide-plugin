// SPDX-License-Identifier: MIT
// SPDX-FileCopyrightText: 2023 Lennart Heimbs

package com.amos.pitmutationmate.pitmutationmate.services

import com.amos.pitmutationmate.pitmutationmate.reporting.XMLParser
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
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
            val ret = parser.loadResultsFromXmlReport(mutationReportPath.toString(), coverageReportPath.toString())
            return ret
        }
        return null
    }

    fun updateLastMutationResult(): XMLParser.ResultData? {
        lastMutationResult = getMutationResult()
        return lastMutationResult
    }
}
