// SPDX-License-Identifier: MIT
// SPDX-FileCopyrightText: 2023 Lennart Heimbs <lennart@heimbs.me>

package com.amos.pitmutationmate.pitmutationmate.configuration

import com.amos.pitmutationmate.pitmutationmate.GradleTaskExecutor
import com.intellij.execution.ExecutionException
import com.intellij.execution.Executor
import com.intellij.execution.configurations.*
import com.intellij.execution.process.ProcessHandler
import com.intellij.execution.runners.ExecutionEnvironment
import com.intellij.openapi.options.SettingsEditor
import com.intellij.openapi.project.Project
import org.jetbrains.annotations.NotNull

class MutationMateRunConfiguration(
    project: Project,
    factory: ConfigurationFactory?,
    name: String?
) : RunConfigurationBase<MutationMateRunConfigurationOptions?>(project, factory, name) {
    private val projectDir: String

    init {
        projectDir = project.basePath!!
    }

    override fun getOptions(): MutationMateRunConfigurationOptions {
        return super.getOptions() as MutationMateRunConfigurationOptions
    }

    var taskName: String?
        get() = options.taskName
        set(taskName) {
            options.taskName = taskName
        }

    var gradleExecutable: String
        get() = options.gradleExecutable ?: ""
        set(gradleExecutable) {
            options.gradleExecutable = gradleExecutable
        }

    var classFQN: String
        get() = options.classFQN ?: ""
        set(classFQN) {
            options.classFQN = classFQN
            println("MutationMateRunConfiguration: classFQN was updated to '$classFQN'.")
        }

    override fun getConfigurationEditor(): SettingsEditor<out RunConfiguration> {
        return MutationMateSettingsEditor()
    }

    override fun getState(
        executor: Executor,
        environment: ExecutionEnvironment
    ): RunProfileState {
        return object : CommandLineState(environment) {
            @NotNull
            @Throws(ExecutionException::class)
            override fun startProcess(): ProcessHandler {
                val gradleTaskExecutor = GradleTaskExecutor()
                return gradleTaskExecutor.executeTask(projectDir, gradleExecutable, taskName, classFQN)
            }
        }
    }
}
