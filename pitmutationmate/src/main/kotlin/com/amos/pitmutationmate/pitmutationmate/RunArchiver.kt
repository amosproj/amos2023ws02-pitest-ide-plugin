package com.amos.pitmutationmate.pitmutationmate
import com.intellij.openapi.project.Project
import java.io.File
import java.lang.Exception
import java.nio.file.Paths


class RunArchiver (packageName: String, project: Project) {
    private val pn: String = packageName
    private val pwd: String? = project.basePath?.let { Paths.get(it).toAbsolutePath().toString() }
    private val rootPiTestDirectoryPathName: String = "$pwd/build/reports/pitest"
    private val reportDirectory: File = File("$rootPiTestDirectoryPathName/${this.pn}")

    fun archiveRun () {
        println("Archiving $reportDirectory")
        var archiveDirectory = File("$rootPiTestDirectoryPathName/history/${this.pn}")
        print("Archive directory $archiveDirectory exists: ${archiveDirectory.exists()}")
        if (!archiveDirectory.exists()) { // && archiveDirectory.isDirectory) {
            val success = archiveDirectory.mkdirs()

            if (!success) {
                throw Exception("error creating archive directory")
            }

            print("Created $archiveDirectory")
        }

        val index: Int = archiveDirectory.listFiles()!!.size + 1

        archiveDirectory = File("${archiveDirectory.path}/$index")
        val success = archiveDirectory.mkdir()

        if (!success) {
            throw  Exception("error creating archive directory")
        }

        for (file in this.reportDirectory.listFiles()!!) {
            val destinationFile = File("${archiveDirectory.path}/${file.name}")
            try {
                file.copyTo(destinationFile, overwrite = true)
                println("Report ${file.path} saved successfully")
            } catch (e: Exception) {
                println("Error saving report")
            }
        }
    }
}