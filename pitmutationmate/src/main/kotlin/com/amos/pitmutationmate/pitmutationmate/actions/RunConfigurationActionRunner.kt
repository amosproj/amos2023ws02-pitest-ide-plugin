// SPDX-License-Identifier: MIT
// SPDX-FileCopyrightText: 2023 Brianne Oberson <brianne.oberson@gmail.com>

package com.amos.pitmutationmate.pitmutationmate.actions

import com.amos.pitmutationmate.pitmutationmate.configuration.RunConfiguration
import com.amos.pitmutationmate.pitmutationmate.configuration.RunConfigurationType
import com.intellij.execution.ExecutorRegistry
import com.intellij.execution.ProgramRunnerUtil
import com.intellij.execution.RunManager
import com.intellij.openapi.project.Project

class RunConfigurationActionRunner {
    companion object {
        private const val DEFAULT_RUN_CONFIG_NAME = "Default"
        fun updateAndExecuteRunConfig(classFQN: String?, project: Project) {
            val executor = ExecutorRegistry.getInstance().getExecutorById("Run")

            val runManager = RunManager.getInstance(project)

            var runConfig = runManager.findConfigurationByName(DEFAULT_RUN_CONFIG_NAME)
            if (runConfig == null) {
                runConfig = runManager.createConfiguration(DEFAULT_RUN_CONFIG_NAME, RunConfigurationType::class.java)
                (runConfig.configuration as RunConfiguration).isDefault = true
            }
            runConfig.configuration.let {
                val rc = it as RunConfiguration
                if (classFQN != null) {
                    // Adds a star at the end of each ClassFQN so every inner class is included in the pitest task
                    rc.classFQN = classFQN.split(",").joinToString(separator = ",") { classIt -> "$classIt*" }
                }
            }
            runManager.addConfiguration(runConfig)
            runManager.selectedConfiguration = runConfig

            ProgramRunnerUtil.executeConfiguration(runConfig, executor!!)
        }
    }
}
