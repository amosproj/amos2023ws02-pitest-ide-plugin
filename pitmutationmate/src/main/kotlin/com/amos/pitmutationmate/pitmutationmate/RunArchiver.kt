// SPDX-License-Identifier: MIT
// SPDX-FileCopyrightText: 2023 Tim Herzig <tim.herzig@hotmail.com>

package com.amos.pitmutationmate.pitmutationmate

import com.amos.pitmutationmate.pitmutationmate.services.ReportPathGeneratorService
import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import java.io.File
import java.nio.file.Path

class RunArchiver(project: Project) {

    private val reportPathGeneratorService = project.service<ReportPathGeneratorService>()
    private val reportDirectory = File(reportPathGeneratorService.getReportPath().toString())

    fun archiveRun() {
        println("Archiving $reportDirectory")
        var archiveDirectory = Path.of(reportPathGeneratorService.getArchivePath().toString()).toFile()
        if (!archiveDirectory.exists()) {
            val success = archiveDirectory.mkdirs()
            if (!success) {
                throw Exception("error creating archive directory")
            }
            print("Created $archiveDirectory")
        }

        val index = archiveDirectory.listFiles()!!.size + 1
        archiveDirectory = Path.of(archiveDirectory.path, index.toString()).toFile()
        val success = archiveDirectory.mkdir()

        if (!success) {
            throw Exception("error creating directory to archive history Number $index")
        }

        for (file in this.reportDirectory.listFiles()!!) {
            val destinationFile = Paths.get(archiveDirectory.path.toString(), file.name).toFile()
            try {
                file.copyTo(destinationFile, overwrite = true)
                println("Report ${file.path} saved successfully")
            } catch (e: Exception) {
                println("ErrorDialog saving report")
            }
        }
    }
}
