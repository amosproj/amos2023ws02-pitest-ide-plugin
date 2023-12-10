// SPDX-License-Identifier: MIT
// SPDX-FileCopyrightText: 2023 Lennart Heimbs <lennart@heimbs.me>

package com.amos.pitmutationmate.pitmutationmate.execution

import com.amos.pitmutationmate.pitmutationmate.services.UdpMessagingServer
import com.intellij.execution.configurations.GeneralCommandLine
import com.intellij.execution.process.ProcessAdapter
import com.intellij.execution.process.ProcessEvent
import com.intellij.execution.process.ProcessHandler
import com.intellij.execution.process.ProcessHandlerFactory
import com.intellij.execution.process.ProcessTerminatedListener
import com.intellij.openapi.components.service
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.project.Project

abstract class BasePitestExecutor {

    private val log: Logger = Logger.getInstance(BasePitestExecutor::class.java)

    fun executeTask(
        project: Project,
        executable: String?,
        taskName: String?,
        classFQN: String?
    ): ProcessHandler {
        val messagingServer = project.service<UdpMessagingServer>()
        messagingServer.startServer(classFQN) // Start the UDP server

        val commandLine = buildCommandLine(executable, taskName, project.basePath!!, classFQN, messagingServer.port)
        log.debug("BasePitestExecutor: executeTask: commandLine: $commandLine")
        val processHandler = createProcessHandler(commandLine)
        processHandler.addProcessListener(object : ProcessAdapter() {
            override fun processTerminated(event: ProcessEvent) {
                log.debug("BasePitestExecutor: executeTask: processTerminated: event: $event")
                messagingServer.stopServer() // Stop the UDP server
            }
        })
        ProcessTerminatedListener.attach(processHandler)
        return processHandler
    }

    abstract fun buildCommandLine(
        executable: String?,
        taskName: String?,
        projectDir: String,
        classFQN: String?,
        port: Int
    ): GeneralCommandLine

    private fun createProcessHandler(commandLine: GeneralCommandLine): ProcessHandler {
        return ProcessHandlerFactory.getInstance().createColoredProcessHandler(commandLine)
    }

    protected fun String?.isNullOrEmpty(): Boolean {
        @Suppress("VerboseNullabilityAndEmptiness")
        return this == null || this.isEmpty()
    }
}
