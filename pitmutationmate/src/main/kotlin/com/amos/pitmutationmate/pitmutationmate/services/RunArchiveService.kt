// SPDX-License-Identifier: MIT
// SPDX-FileCopyrightText: 2023 Tim Herzig <tim.herzig@hotmail.com>

package com.amos.pitmutationmate.pitmutationmate.services

import com.intellij.openapi.components.Service
import com.intellij.openapi.components.service
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.project.Project
import java.nio.file.Path

@Service(Service.Level.PROJECT)
class RunArchiveService(private val project: Project) {
    private val log: Logger = Logger.getInstance(RunArchiveService::class.java)

    fun archiveRun() {
        val reportPathGeneratorService = project.service<ReportPathGeneratorService>()
        val reportDirectory = reportPathGeneratorService.getReportPath().toFile()
        log.info("Archiving $reportDirectory")
        var archiveDirectory = Path.of(reportPathGeneratorService.getArchivePath().toString()).toFile()
        if (!archiveDirectory.exists()) {
            if (!archiveDirectory.mkdirs()) {
                throw Exception("error creating archive directory $archiveDirectory")
            }
            log.info("Created $archiveDirectory")
        }

        val index = archiveDirectory.listFiles()!!.size + 1
        archiveDirectory = Path.of(archiveDirectory.path, index.toString()).toFile()
        if (!archiveDirectory.mkdir()) {
            throw Exception("error creating archive directory $archiveDirectory")
        }
        log.info("Created $archiveDirectory")

        if (reportDirectory.listFiles() == null) {
            throw Exception("The last pitest run didn't generate any report files!")
        }
        reportDirectory.copyRecursively(archiveDirectory)
    }
}
