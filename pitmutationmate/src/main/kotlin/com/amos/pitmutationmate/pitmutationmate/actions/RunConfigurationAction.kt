// SPDX-License-Identifier: MIT
// SPDX-FileCopyrightText: 2023 Brianne Oberson <brianne.oberson@gmail.com>

package com.amos.pitmutationmate.pitmutationmate.actions

import com.amos.pitmutationmate.pitmutationmate.configuration.RunConfiguration
import com.amos.pitmutationmate.pitmutationmate.configuration.RunConfigurationType
import com.amos.pitmutationmate.pitmutationmate.editor.PluginState
import com.amos.pitmutationmate.pitmutationmate.reporting.XMLParser
import com.amos.pitmutationmate.pitmutationmate.ui.ToolWindowFactory
import com.intellij.codeInsight.daemon.DaemonCodeAnalyzer
import com.intellij.execution.ExecutorRegistry
import com.intellij.execution.ProgramRunnerUtil
import com.intellij.execution.RunManager
import com.intellij.execution.RunnerAndConfigurationSettings
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowManager

abstract class RunConfigurationAction : AnAction() {
    fun updateAndExecuteRunConfig(classFQN: String?, project: Project) {
        val executor = ExecutorRegistry.getInstance().getExecutorById("Run")

        val runManager = RunManager.getInstance(project)
        val runConfigs = runManager.getConfigurationSettingsList(
            RunConfigurationType::class.java
        )

        val runConfig: RunnerAndConfigurationSettings = if (runConfigs.isEmpty()) {
            // create a new default run configuration
            runManager.createConfiguration("Pitest", RunConfigurationType::class.java)
        } else {
            getRunConfig(runConfigs)
        }
        runConfig.configuration.let {
            val rc = it as RunConfiguration
            if (classFQN != null) {
                rc.classFQN = classFQN
            }
        }

        ProgramRunnerUtil.executeConfiguration(runConfig, executor!!)

        // restart code highlighting upon new pitest results
        PluginState.isAnnotatorEnabled = true
        // TODO: ensure only the external annotator is rerun
        DaemonCodeAnalyzer.getInstance(project).restart()

        // Update visualisation with mock results
        // TODO: replace this by real results extracted by the HTMLParser
        val toolWindow: ToolWindow? = ToolWindowManager.getInstance(project).getToolWindow("Pitest")
        val coverageReport: XMLParser.CoverageReport = XMLParser.CoverageReport(
            classFQN.toString(),
            "Test",
            "Test",
            lineCoveragePercentage = 80,
            lineCoverageTextRatio = "160/200",
            mutationCoveragePercentage = 50,
            mutationCoverageTextRatio = "100/200",
            testStrengthPercentage = 40,
            testStrengthTextRatio = "80/200",
            numberOfClasses = 1
        )
        if (toolWindow != null) {
            ToolWindowFactory.Util.updateReport(toolWindow, coverageReport)
        }
    }

    private fun getRunConfig(runConfigs: List<RunnerAndConfigurationSettings>): RunnerAndConfigurationSettings {
        return runConfigs[0]
    }
}
