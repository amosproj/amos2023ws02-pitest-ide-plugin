// SPDX-License-Identifier: MIT
// SPDX-FileCopyrightText: 2023 Lennart Heimbs, Brianne Oberson

package com.amos.pitmutationmate.pitmutationmate.configuration

import com.amos.pitmutationmate.pitmutationmate.execution.GradleTaskExecutor
import com.amos.pitmutationmate.pitmutationmate.execution.MavenTaskExecutor
import com.amos.pitmutationmate.pitmutationmate.services.PluginCheckerService
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
import com.intellij.openapi.options.SettingsEditor
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.Messages
import org.jetbrains.annotations.NotNull
import org.jetbrains.kotlin.idea.configuration.isMavenized

class RunConfiguration(
    project: Project,
    factory: ConfigurationFactory?,
    name: String?
) : RunConfigurationBase<RunConfigurationOptions?>(project, factory, name) {
    private val logger: Logger = Logger.getInstance(RunConfiguration::class.java)

    override fun getOptions(): RunConfigurationOptions {
        return super.getOptions() as RunConfigurationOptions
    }

    var isDefault: Boolean
        get() = options.isDefault
        set(isDefault) {
            options.isDefault = isDefault
        }

    var taskName: String?
        get() = options.taskName
        set(taskName) {
            options.taskName = taskName
        }

    var gradleExecutable: String?
        get() = options.gradleExecutable
        set(gradleExecutable) {
            options.gradleExecutable = gradleExecutable
        }

    var buildType: String?
        get() = options.buildType
        set(buildType) {
            options.buildType = buildType
        }

    var verbose: Boolean
        get() = options.verbose
        set(verbose) {
            options.verbose = verbose
        }

    var classFQN: String?
        get() = options.classFQN
        set(classFQN) {
            options.classFQN = classFQN
        }

    override fun getConfigurationEditor(): SettingsEditor<out RunConfiguration> {
        val settingsEditor = com.amos.pitmutationmate.pitmutationmate.configuration.SettingsEditor()
        settingsEditor.checkDefault(this)
        return settingsEditor
    }

    override fun getState(
        executor: Executor,
        environment: ExecutionEnvironment
    ): RunProfileState? {
        val pluginChecker = project.service<PluginCheckerService>()
        val errorMessage = pluginChecker.getErrorMessage(withHeader = false)
        if (errorMessage != null) {
            Messages.showErrorDialog(project, errorMessage, PluginCheckerService.ERROR_MESSAGE_TITLE)
            return null
        }

        return object : CommandLineState(environment) {
            @NotNull
            @Throws(ExecutionException::class)
            override fun startProcess(): ProcessHandler {
                if (project.isMavenized) {
                    logger.debug("MutationMateRunConfiguration: executing maven task.")
                    val mavenTaskExecutor = MavenTaskExecutor()
                    return mavenTaskExecutor.executeTask(project, options)
                }
                logger.debug("MutationMateRunConfiguration: executing gradle task.")
                val gradleTaskExecutor = GradleTaskExecutor()
                return gradleTaskExecutor.executeTask(project, options)
            }
        }
    }
}
