// SPDX-License-Identifier: MIT
// SPDX-FileCopyrightText: 2023 Tim Herzig <tim.herzig@hotmail.com>

package com.amos.pitmutationmate.pitmutationmate

import com.amos.pitmutationmate.pitmutationmate.services.ReportPathGeneratorService
import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import java.io.File
import java.lang.Exception
import java.nio.file.Path
import java.nio.file.Paths

class RunArchiver(packageName: String, project: Project) {
    private val pn: String = packageName
    private val reportPathGeneratorService = project.service<ReportPathGeneratorService>()
    private val reportDirectory: File = File(reportPathGeneratorService.getReportPath().toString())

    fun archiveRun() {
        println("Archiving $reportDirectory")
        var archiveDirectory = Paths.get(reportPathGeneratorService.getArchivePath().toString(), this.pn).toFile()
        if (!archiveDirectory.exists()) {
            val success = archiveDirectory.mkdirs()

            if (!success) {
                throw Exception("error creating archive directory")
            }

            print("Created $archiveDirectory")
        }

        val index: Int = archiveDirectory.listFiles()!!.size + 1

        archiveDirectory = Paths.get(archiveDirectory.path, index.toString()).toFile()
        val success = archiveDirectory.mkdir()

        if (!success) {
            throw Exception("error creating archive directory")
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
