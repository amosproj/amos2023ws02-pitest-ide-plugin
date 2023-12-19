// SPDX-License-Identifier: MIT
// SPDX-FileCopyrightText: 2023 Lennart Heimbs <lennart@heimbs.me>

package com.amos.pitmutationmate.pitmutationmate.execution

import com.intellij.execution.configurations.GeneralCommandLine
import java.io.File
import java.nio.file.Path

class MavenTaskExecutor : BasePitestExecutor() {
    private var taskName: String = "org.pitest:pitest-maven:mutationCoverage"
    private var mavenExecutable: String = "mvn"

    override fun buildCommandLine(
        executable: String?,
        overrideTaskName: String?,
        projectDir: String,
        classFQN: String?,
        reportDir: Path,
        port: Int
    ): GeneralCommandLine {
        val commandLine = GeneralCommandLine()

        if (!executable.isNullOrEmpty()) {
            this.mavenExecutable = executable!!
        }

        if (!overrideTaskName.isNullOrEmpty()) {
            this.taskName = overrideTaskName!!
        }

        commandLine.exePath = mavenExecutable
        commandLine.addParameters(this.taskName)
        commandLine.addParameters(getPitestOverrideParameters(classFQN))

        commandLine.workDirectory = File(projectDir)
        return commandLine
    }

    private fun getPitestOverrideParameters(classFQN: String?): List<String> {
        val parameters = mutableListOf<String>()
        if (!classFQN.isNullOrEmpty()) {
            parameters.add("-DtargetClasses=$classFQN")
        }
        parameters.add("-Dverbose=true")
        return parameters
    }
}
