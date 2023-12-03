// SPDX-License-Identifier: MIT
// SPDX-FileCopyrightText: 2023 Lennart Heimbs <lennart@heimbs.me>

package com.amos.pitmutationmate.pitmutationmate.execution

import com.intellij.execution.configurations.GeneralCommandLine
import com.intellij.execution.process.ProcessHandler
import com.intellij.execution.process.ProcessHandlerFactory
import com.intellij.execution.process.ProcessTerminatedListener

abstract class BasePitestExecutor {

    fun executeTask(
        projectDir: String,
        executable: String?,
        taskName: String?,
        classFQN: String?
    ): ProcessHandler {
        println("BasePitestExecutor: executeTask")
        val commandLine = buildCommandLine(executable, taskName, projectDir, classFQN)
        println("BasePitestExecutor: executeTask: commandLine: $commandLine")
        val processHandler = createProcessHandler(commandLine)
        ProcessTerminatedListener.attach(processHandler)
        return processHandler
    }

    abstract fun buildCommandLine(
        executable: String?,
        taskName: String?,
        projectDir: String,
        classFQN: String?
    ): GeneralCommandLine

    private fun createProcessHandler(commandLine: GeneralCommandLine): ProcessHandler {
        return ProcessHandlerFactory.getInstance().createColoredProcessHandler(commandLine)
    }

    protected fun String?.isNullOrEmpty(): Boolean {
        @Suppress("VerboseNullabilityAndEmptiness")
        return this == null || this.isEmpty()
    }
}
