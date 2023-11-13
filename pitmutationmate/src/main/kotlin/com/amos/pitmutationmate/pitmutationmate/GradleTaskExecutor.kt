// SPDX-License-Identifier: MIT
// SPDX-FileCopyrightText: 2023 Lennart Heimbs <lennart@heimbs.me>

package com.amos.pitmutationmate.pitmutationmate

import com.intellij.execution.configurations.GeneralCommandLine
import com.intellij.execution.process.ProcessHandler
import com.intellij.execution.process.ProcessHandlerFactory
import com.intellij.execution.process.ProcessTerminatedListener
import com.intellij.openapi.util.SystemInfo
import java.io.File

class GradleTaskExecutor {

    private var taskName: String = "pitest"
    private var gradleExecutable: String? = null
    private val windowsGradleExecutable = "gradlew.bat"
    private val unixGradleExecutable = "gradlew"

    fun executeTask(
        projectDir: String,
        gradleExecutable: String?,
        taskName: String?
    ): ProcessHandler {
        val commandLine = buildCommandLine(gradleExecutable, taskName, projectDir)
        val processHandler = createProcessHandler(commandLine)
        ProcessTerminatedListener.attach(processHandler)
        return processHandler
    }

    private fun buildCommandLine(
        gradleExecutable: String?,
        taskName: String?,
        projectDir: String
    ): GeneralCommandLine {
        val commandLine = GeneralCommandLine()

        this.gradleExecutable = gradleExecutable
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

        commandLine.workDirectory = File(projectDir)
        return commandLine
    }

    private fun createProcessHandler(commandLine: GeneralCommandLine): ProcessHandler {
        return ProcessHandlerFactory.getInstance().createColoredProcessHandler(commandLine)
    }

    private fun String?.isNullOrEmpty(): Boolean {
        @Suppress("VerboseNullabilityAndEmptiness")
        return this == null || this.isEmpty()
    }
}
