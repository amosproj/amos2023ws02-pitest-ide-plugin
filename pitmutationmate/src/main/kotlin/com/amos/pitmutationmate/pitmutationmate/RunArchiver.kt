// SPDX-License-Identifier: MIT
// SPDX-FileCopyrightText: 2023 Tim Herzig <tim.herzig@hotmail.com>

package com.amos.pitmutationmate.pitmutationmate

import com.amos.pitmutationmate.pitmutationmate.services.ReportPathGeneratorService
import com.intellij.openapi.components.service
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.project.Project
import java.io.File
import java.nio.file.Path

class RunArchiver(project: Project) {

    private val reportPathGeneratorService = project.service<ReportPathGeneratorService>()
    private val reportDirectory: File = File(reportPathGeneratorService.getReportPath().toString())
    private val logger = Logger.getInstance(RunArchiver::class.java)

    fun archiveRun() {
        logger.info("Archiving $reportDirectory")
        var archiveDirectory = Path.of(reportPathGeneratorService.getArchivePath().toString()).toFile()
        if (!archiveDirectory.exists()) {
            val success = archiveDirectory.mkdirs()
            if (!success) {
                throw Exception("error creating archive directory")
            }
            logger.info("Created $archiveDirectory")
        }

        val index = archiveDirectory.listFiles()!!.size + 1
        archiveDirectory = Path.of(archiveDirectory.path, index.toString()).toFile()
        val success = archiveDirectory.mkdir()
        if (!success) {
            throw Exception("error creating directory to archive history Number $index")
        }

        if (this.reportDirectory.listFiles() == null) {
            throw Exception("The last pitest run didn't generate any report files!")
        }
        reportDirectory.copyRecursively(archiveDirectory)
    }
}
