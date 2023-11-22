package com.amos.pitmutationmate.pitmutationmate
import java.io.File
import java.lang.Exception


class RunArchiver (packageName: String) {
    private val pn: String = packageName
    private val rootPiTestDirectoryPathName: String = "./build/reports/pitest"
    private val reportDirectory: File = File("$rootPiTestDirectoryPathName/${this.pn}")

    fun archiveRun () {
        var archiveDirectory = File("$rootPiTestDirectoryPathName/history/${this.pn}")
        if (!archiveDirectory.exists() && archiveDirectory.isDirectory) {
            val success = archiveDirectory.mkdirs()

            if (!success) {
                throw Exception("error creating archive directory")
            }
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