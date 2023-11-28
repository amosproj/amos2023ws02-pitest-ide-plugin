package com.amos.pitmutationmate.pitmutationmate
import com.intellij.openapi.project.Project
import java.io.File
import java.lang.Exception
import java.nio.file.Path
import java.nio.file.Paths


class RunArchiver(packageName: String, project: Project) {
    private val pn: String = packageName
    private val pwd: String? = project.basePath?.let { Paths.get(it).toAbsolutePath().toString() }
    private val rootPiTestDirectoryPathName: Path? = pwd?.let { Paths.get(it, "build", "reports", "pitest") }
    private val reportDirectory: File = File(Paths.get(rootPiTestDirectoryPathName.toString(), this.pn).toString())

    fun archiveRun() {
        println("Archiving $reportDirectory")
        var archiveDirectory = Paths.get(rootPiTestDirectoryPathName.toString(), "history", this.pn).toFile()
        if (!archiveDirectory.exists()) { // && archiveDirectory.isDirectory) {
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
                println("Error saving report")
            }
        }
    }
}