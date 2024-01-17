// SPDX-License-Identifier: MIT
// SPDX-FileCopyrightText: 2023 Lennart Heimbs <lennart@heimbs.me>

package com.amos.pitmutationmate.pitmutationmate.execution

import com.amos.pitmutationmate.pitmutationmate.services.MutationResultService
import com.amos.pitmutationmate.pitmutationmate.services.ReportPathGeneratorService
import com.amos.pitmutationmate.pitmutationmate.services.UdpMessagingServer
import com.amos.pitmutationmate.pitmutationmate.ui.ToolWindowFactory
import com.intellij.execution.configurations.GeneralCommandLine
import com.intellij.execution.process.ProcessAdapter
import com.intellij.execution.process.ProcessEvent
import com.intellij.execution.process.ProcessHandler
import com.intellij.execution.process.ProcessHandlerFactory
import com.intellij.execution.process.ProcessTerminatedListener
import com.intellij.openapi.components.service
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowManager
import java.nio.file.Path

abstract class BasePitestExecutor {

    private val log: Logger = Logger.getInstance(BasePitestExecutor::class.java)

    fun executeTask(
        project: Project,
        executable: String?,
        overrideTaskName: String?,
        classFQN: String?
    ): ProcessHandler {
        val messagingServer = project.service<UdpMessagingServer>()
        messagingServer.startServer(classFQN) // Start the UDP server

        val reportDir = project.service<ReportPathGeneratorService>().getReportPath()

        val commandLine = buildCommandLine(executable, overrideTaskName, project.basePath!!, classFQN, reportDir, messagingServer.port)
        log.debug("BasePitestExecutor: executeTask: commandLine: $commandLine")
        val processHandler = createProcessHandler(commandLine)
        processHandler.addProcessListener(object : ProcessAdapter() {
            override fun processTerminated(event: ProcessEvent) {
                log.debug("BasePitestExecutor: executeTask: processTerminated: event: $event")
                messagingServer.stopServer() // Stop the UDP server
                // update tool window with latest result data
                val toolWindow: ToolWindow? = ToolWindowManager.getInstance(project).getToolWindow(ToolWindowFactory.ID)
                // safe and get latest pitest results and update report toolWindow with it
                val coverageReport = project.service<MutationResultService>().updateLastMutationResult()?.coverageReports?.first()
                if (toolWindow != null) {
                    ToolWindowFactory.Util.updateReport(toolWindow, coverageReport)
                }
            }
        })
        ProcessTerminatedListener.attach(processHandler)
        return processHandler
    }

    abstract fun buildCommandLine(
        executable: String?,
        overrideTaskName: String?,
        projectDir: String,
        classFQN: String?,
        reportDir: Path,
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
