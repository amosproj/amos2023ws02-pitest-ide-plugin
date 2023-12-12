// SPDX-License-Identifier: MIT
// SPDX-FileCopyrightText: 2023 Lennart Heimbs

package com.amos.pitmutationmate.pitmutationmate.execution

import com.intellij.execution.configurations.GeneralCommandLine
import java.io.File

class GradleTaskExecutor : BasePitestExecutor() {
    companion object {
        const val PITEST_TASK_NAME = "pitest"
        const val WINDOWS_SHELL_EXECUTABLE = "cmd"
        const val WINDOWS_FIRST_PARAMETER = "/c"
        const val WINDOWS_GRADLE_EXECUTABLE = "gradlew.bat"
        const val UNIX_SHELL_EXECUTABLE = "/usr/bin/env"
        const val UNIX_FIRST_PARAMETER = "sh"
        const val UNIX_GRADLE_EXECUTABLE = "./gradlew"
    }
    private var systemInfoProvider: SystemInfoProvider = SystemInfo()

    override fun buildCommandLine(
        executable: String?,
        overrideTaskName: String?,
        projectDir: String,
        classFQN: String?,
        port: Int
    ): GeneralCommandLine {
        val commandLine = GeneralCommandLine()
        var gradleExecutable: String? = executable
        var taskName: String? = PITEST_TASK_NAME

        if (!overrideTaskName.isNullOrEmpty()) {
            taskName = overrideTaskName
        }

        if (systemInfoProvider.isWindows()) {
            if (gradleExecutable.isNullOrEmpty()) {
                gradleExecutable = WINDOWS_GRADLE_EXECUTABLE
            }
            commandLine.exePath = WINDOWS_SHELL_EXECUTABLE
            commandLine.addParameters(WINDOWS_FIRST_PARAMETER, gradleExecutable, taskName)
        } else {
            if (gradleExecutable.isNullOrEmpty()) {
                gradleExecutable = UNIX_GRADLE_EXECUTABLE
            }
            commandLine.exePath = UNIX_SHELL_EXECUTABLE
            commandLine.addParameters(UNIX_FIRST_PARAMETER, gradleExecutable, taskName)
        }

        commandLine.addParameters(getPitestOverrideParameters(classFQN, port))

        commandLine.workDirectory = File(projectDir)
        return commandLine
    }

    private fun getPitestOverrideParameters(classFQN: String?, port: Int): List<String> {
        val parameters = mutableListOf<String>()
        if (!classFQN.isNullOrEmpty()) {
            parameters.add("-Dpitmutationmate.override.targetClasses=$classFQN")
        }
        parameters.add("-Dpitmutationmate.override.verbose=true")
        parameters.add("-Dpitmutationmate.override.port=$port")
        return parameters
    }
}
