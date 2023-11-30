// SPDX-License-Identifier: MIT
// SPDX-FileCopyrightText: 2023 Lennart Heimbs

package com.amos.pitmutationmate.pitmutationmate.configuration

import com.amos.pitmutationmate.pitmutationmate.execution.GradleTaskExecutor
import com.amos.pitmutationmate.pitmutationmate.execution.MavenTaskExecutor
import com.intellij.execution.ExecutionException
import com.intellij.execution.Executor
import com.intellij.execution.configurations.*
import com.intellij.execution.process.ProcessHandler
import com.intellij.execution.runners.ExecutionEnvironment
import com.intellij.openapi.externalSystem.ExternalSystemModulePropertyManager
import com.intellij.openapi.module.ModuleManager
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
                val buildSystem = getBuildSystem()
                if (buildSystem == null) {
                    throw ExecutionException("Could not determine build system.")
                } else if (buildSystem == "Maven") {
                    println("MutationMateRunConfiguration: executing maven task.")
                    val mavenTaskExecutor = MavenTaskExecutor()
                    return mavenTaskExecutor.executeTask(projectDir, gradleExecutable, taskName, classFQN)
                }
                println("MutationMateRunConfiguration: executing gradle task.")
                val gradleTaskExecutor = GradleTaskExecutor()
                return gradleTaskExecutor.executeTask(projectDir, gradleExecutable, taskName, classFQN)
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
        println("MutationMateRunConfiguration: buildSystem is '$buildSystem'.")
        return buildSystem
    }

    private fun getBuildSystemForModule(module: com.intellij.openapi.module.Module): String? {
        val manager = ExternalSystemModulePropertyManager.getInstance(module)
        return manager.getExternalSystemId()
    }
}
