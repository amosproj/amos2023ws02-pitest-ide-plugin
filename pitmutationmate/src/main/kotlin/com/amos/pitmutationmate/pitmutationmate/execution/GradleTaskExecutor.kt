// SPDX-License-Identifier: MIT
// SPDX-FileCopyrightText: 2023 Lennart Heimbs

package com.amos.pitmutationmate.pitmutationmate.execution

import com.intellij.execution.configurations.GeneralCommandLine
import com.intellij.openapi.util.SystemInfo
import java.io.File

class GradleTaskExecutor : BasePitestExecutor() {
    private var taskName: String = "pitest"
    private var gradleExecutable: String? = null
    private val windowsGradleExecutable = "gradlew.bat"
    private val unixGradleExecutable = "./gradlew"

    override fun buildCommandLine(
        executable: String?,
        taskName: String?,
        projectDir: String,
        classFQN: String?
    ): GeneralCommandLine {
        val commandLine = GeneralCommandLine()

        this.gradleExecutable = executable
        if (!taskName.isNullOrEmpty()) {
            this.taskName = taskName!!
        }

        if (SystemInfo.isWindows) {
            if (this.gradleExecutable.isNullOrEmpty()) {
                this.gradleExecutable = windowsGradleExecutable
            }
            commandLine.exePath = "cmd"
            commandLine.addParameters("/c", this.gradleExecutable, this.taskName)
        } else {
            if (this.gradleExecutable.isNullOrEmpty()) {
                this.gradleExecutable = unixGradleExecutable
            }
            commandLine.exePath = "/usr/bin/env"
            commandLine.addParameters("sh", this.gradleExecutable, this.taskName)
        }

        commandLine.addParameters(getPitestOverrideParameters(classFQN))

        commandLine.workDirectory = File(projectDir)
        return commandLine
    }

    private fun getPitestOverrideParameters(classFQN: String?): List<String> {
        val parameters = mutableListOf<String>()
        if (!classFQN.isNullOrEmpty()) {
            parameters.add("-Dpitmutationmate.override.targetClasses=$classFQN")
        }
        parameters.add("-Dpitmutationmate.override.verbose=true")
        return parameters
    }
}
