// SPDX-License-Identifier: MIT
// SPDX-FileCopyrightText: 2023 Brianne Oberson <brianne.oberson@gmail.com>

package com.amos.pitmutationmate.pitmutationmate.actions

import com.amos.pitmutationmate.pitmutationmate.configuration.RunConfiguration
import com.amos.pitmutationmate.pitmutationmate.configuration.RunConfigurationType
import com.intellij.execution.ExecutorRegistry
import com.intellij.execution.ProgramRunnerUtil
import com.intellij.execution.RunManager
import com.intellij.execution.RunnerAndConfigurationSettings
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.project.Project

abstract class RunConfigurationAction : AnAction() {
    fun updateAndExecuteRunConfig(classFQN: String?, project: Project) {
        val executor = ExecutorRegistry.getInstance().getExecutorById("Run")

        val runManager = RunManager.getInstance(project)

        val runConfig: RunnerAndConfigurationSettings = runManager.createConfiguration("Temp Pitest Config", RunConfigurationType::class.java)
        runConfig.configuration.let {
            val rc = it as RunConfiguration
            if (classFQN != null) {
                rc.classFQN = classFQN
            }
        }

        ProgramRunnerUtil.executeConfiguration(runConfig, executor!!)
    }
}
