// SPDX-License-Identifier: MIT
// SPDX-FileCopyrightText: 2023 Lennart Heimbs

package com.amos.pitmutationmate.pitmutationmate.services

import com.intellij.openapi.components.Service
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.project.Project
import java.nio.file.Path
import kotlin.io.path.exists

/**
 * A service that generates the path to the report directory.
 */
@Service(Service.Level.PROJECT)
class ReportPathGeneratorService(private val project: Project) {

    /**
     * Returns the path to the report directory.
     * @return the path to the report directory
     */
    fun getReportPath(): Path {
        val projectBasePath = project.basePath ?: ""
        if (projectBasePath.isEmpty()) {
            log.warn("Project base path is empty, using current directory as base path")
        }
        return Path.of("$projectBasePath/build/reports/pitest/test")
    }

    /**
     * Returns the path to mutations.xml.
     * @return the path to the report file
     */
    fun getReportMutationsFile(): Path {
        val path = getReportPath()
        return Path.of("$path/mutations.xml")
    }

    /**
     * Returns the path to coverage.xml.
     * @return the path to the report file
     */
    fun getReportCoverageFile(): Path {
        val path = getReportPath()
        return Path.of("$path/coverage.xml")
    }

    /**
     * Creates the report path if it does not exist yet.
     * @return the report path
     */
    fun createReportPath(reportPath: Path) : Path {
        if (!reportPath.exists()) {
            try {
                reportPath.toFile().mkdirs()
                log.info("Created report path: $reportPath")
            } catch (e: Exception) {
                log.error("Could not create report path: $reportPath")
            }
        }
        return reportPath
    }

    companion object {
        private val log: Logger = Logger.getInstance(ReportPathGeneratorService::class.java)
    }
}
