// SPDX-License-Identifier: MIT
// SPDX-FileCopyrightText: 2023 Lennart Heimbs

package com.amos.pitmutationmate.pitmutationmate.configuration

import com.amos.pitmutationmate.pitmutationmate.execution.GradleTaskExecutor
import com.amos.pitmutationmate.pitmutationmate.execution.MavenTaskExecutor
import com.amos.pitmutationmate.pitmutationmate.services.PluginCheckerService
import com.amos.pitmutationmate.pitmutationmate.ui.ToolWindowFactory
import com.intellij.execution.ExecutionException
import com.intellij.execution.Executor
import com.intellij.execution.configurations.CommandLineState
import com.intellij.execution.configurations.ConfigurationFactory
import com.intellij.execution.configurations.RunConfiguration
import com.intellij.execution.configurations.RunConfigurationBase
import com.intellij.execution.configurations.RunProfileState
import com.intellij.execution.process.ProcessHandler
import com.intellij.execution.runners.ExecutionEnvironment
import com.intellij.openapi.components.service
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.externalSystem.ExternalSystemModulePropertyManager
import com.intellij.openapi.module.ModuleManager
import com.intellij.openapi.options.SettingsEditor
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.Messages
import com.intellij.openapi.wm.ToolWindowManager
import org.jetbrains.annotations.NotNull

class RunConfiguration(
    project: Project,
    factory: ConfigurationFactory?,
    name: String?
) : RunConfigurationBase<RunConfigurationOptions?>(project, factory, name) {
    private val log: Logger = Logger.getInstance(RunConfiguration::class.java)

    override fun getOptions(): RunConfigurationOptions {
        return super.getOptions() as RunConfigurationOptions
    }

    var taskName: String?
        get() = options.taskName
        set(taskName) {
            options.taskName = taskName
            log.debug("MutationMateRunConfiguration: taskName was updated to '$taskName'.")
        }

    var gradleExecutable: String?
        get() = options.gradleExecutable
        set(gradleExecutable) {
            options.gradleExecutable = gradleExecutable
            log.debug("MutationMateRunConfiguration: gradleExecutable was updated to '$gradleExecutable'.")
        }

    var classFQN: String?
        get() = options.classFQN
        set(classFQN) {
            options.classFQN = classFQN
            log.debug("MutationMateRunConfiguration: classFQN was updated to '$classFQN'.")
        }

    override fun getConfigurationEditor(): SettingsEditor<out RunConfiguration> {
        return com.amos.pitmutationmate.pitmutationmate.configuration.SettingsEditor()
    }

    override fun getState(
        executor: Executor,
        environment: ExecutionEnvironment
    ): RunProfileState? {
        val pluginChecker = project.service<PluginCheckerService>()
        val errorMessage = pluginChecker.getErrorMessage(withHeader = false)
        val toolWindow = ToolWindowManager.getInstance(project).getToolWindow(ToolWindowFactory.ID)
        if (errorMessage != null) {
            if (toolWindow != null) {
                ToolWindowFactory.Util.initiateWithConfigError(errorMessage, toolWindow)
            }
            Messages.showErrorDialog(project, errorMessage, PluginCheckerService.ERROR_MESSAGE_TITLE)
            return null
        }
        if (toolWindow != null) {
            ToolWindowFactory.Util.initiateWithData(toolWindow)
        }

        return object : CommandLineState(environment) {
            @NotNull
            @Throws(ExecutionException::class)
            override fun startProcess(): ProcessHandler {
                val buildSystem = getBuildSystem()
                if (buildSystem == null) {
                    throw ExecutionException("Could not determine build system.")
                } else if (buildSystem == "Maven") {
                    log.debug("MutationMateRunConfiguration: executing maven task.")
                    val mavenTaskExecutor = MavenTaskExecutor()
                    return mavenTaskExecutor.executeTask(project, gradleExecutable, taskName, classFQN)
                }
                log.debug("MutationMateRunConfiguration: executing gradle task.")
                val gradleTaskExecutor = GradleTaskExecutor()
                return gradleTaskExecutor.executeTask(project, gradleExecutable, taskName, classFQN)
            }
        }
    }

    fun getBuildSystem(): String? {
        var buildSystem: String? = null
        val modules = ModuleManager.getInstance(project).modules
        for (module in modules) {
            val moduleBuildSystem = getBuildSystemForModule(module)
            if (moduleBuildSystem != null) {
                buildSystem = moduleBuildSystem
            }
        }
        log.debug("MutationMateRunConfiguration: buildSystem is '$buildSystem'.")
        return buildSystem
    }

    private fun getBuildSystemForModule(module: com.intellij.openapi.module.Module): String? {
        val manager = ExternalSystemModulePropertyManager.getInstance(module)
        return manager.getExternalSystemId()
    }
}
