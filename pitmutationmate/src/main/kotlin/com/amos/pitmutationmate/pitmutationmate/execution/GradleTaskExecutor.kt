// SPDX-License-Identifier: MIT
// SPDX-FileCopyrightText: 2023 Lennart Heimbs

package com.amos.pitmutationmate.pitmutationmate.execution

import com.amos.pitmutationmate.pitmutationmate.configuration.RunConfigurationOptions
import com.intellij.execution.configurations.GeneralCommandLine
import java.io.File
import java.nio.file.Path

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
        options: RunConfigurationOptions,
        projectDir: String,
        reportDir: Path,
        port: Int
    ): GeneralCommandLine {
        val commandLine = GeneralCommandLine()
        var gradleExecutable: String? = options.gradleExecutable
        var taskName: String? = PITEST_TASK_NAME

        if (!options.taskName.isNullOrEmpty()) {
            taskName = options.taskName
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

        commandLine.addParameters(getPitestOverrideParameters(options.classFQN, port, reportDir, options.verbose))

        commandLine.workDirectory = File(projectDir)
        return commandLine
    }

    private fun getPitestOverrideParameters(classFQN: String?, port: Int, reportDir: Path, verbose: Boolean): List<String> {
        val parameters = mutableListOf<String>()
        if (!classFQN.isNullOrEmpty()) {
            parameters.add("-Dpitmutationmate.override.targetClasses=$classFQN")
        }
        parameters.add("-Dpitmutationmate.override.outputFormats=XML,report-coverage")
        parameters.add("-Dpitmutationmate.override.addCoverageListenerDependency=io.github.amosproj:coverage-reporter:1.1")
        parameters.add("-Dpitmutationmate.override.verbose=$verbose")
        parameters.add("-Dpitmutationmate.override.port=$port")
        parameters.add("-Dpitmutationmate.override.reportDir=${reportDir.toAbsolutePath()}")
        return parameters
    }
}
