// SPDX-License-Identifier: MIT
// SPDX-FileCopyrightText: 2023 Lennart Heimbs <lennart@heimbs.me>

package com.amos.pitmutationmate.pitmutationmate.execution

import com.amos.pitmutationmate.pitmutationmate.configuration.RunConfigurationOptions
import com.amos.pitmutationmate.pitmutationmate.services.ReportPathGeneratorService
import com.amos.pitmutationmate.pitmutationmate.services.UdpMessagingServer
import com.intellij.execution.configurations.GeneralCommandLine
import com.intellij.execution.process.ProcessHandler
import com.intellij.execution.process.ProcessHandlerFactory
import com.intellij.execution.process.ProcessTerminatedListener
import com.intellij.openapi.components.service
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.project.Project
import java.nio.file.Path

abstract class BasePitestExecutor {
    private val log: Logger = Logger.getInstance(BasePitestExecutor::class.java)

    fun executeTask(
        project: Project,
        options: RunConfigurationOptions
    ): ProcessHandler {
        val messagingServer = project.service<UdpMessagingServer>()
        messagingServer.startServer(options.classFQN) // Start the UDP server

        val reportPathGenerator = project.service<ReportPathGeneratorService>()
        reportPathGenerator.setBuildType(options.buildType)
        val reportDir = reportPathGenerator.getReportPath()

        val commandLine = buildCommandLine(options, project.basePath!!, reportDir, messagingServer.port)
        log.debug("BasePitestExecutor: executeTask: commandLine: $commandLine")
        val processHandler = createProcessHandler(commandLine)
        processHandler.addProcessListener(ExecutionDoneProcessListener(project, options.classFQN))
        ProcessTerminatedListener.attach(processHandler)
        return processHandler
    }

    abstract fun buildCommandLine(
        options: RunConfigurationOptions,
        projectDir: String,
        reportDir: Path,
        port: Int
    ): GeneralCommandLine

    private fun createProcessHandler(commandLine: GeneralCommandLine): ProcessHandler {
        return ProcessHandlerFactory.getInstance().createColoredProcessHandler(commandLine)
    }
}
