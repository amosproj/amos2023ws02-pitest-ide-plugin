// SPDX-License-Identifier: MIT
// SPDX-FileCopyrightText: 2023 Brianne Oberson <brianne.oberson@gmail.com>

package com.amos.pitmutationmate.pitmutationmate.actions

import com.amos.pitmutationmate.pitmutationmate.MutationTestToolWindowFactory
import com.amos.pitmutationmate.pitmutationmate.configuration.RunConfiguration
import com.amos.pitmutationmate.pitmutationmate.configuration.RunConfigurationType
import com.amos.pitmutationmate.pitmutationmate.reporting.XMLListener
import com.amos.pitmutationmate.pitmutationmate.reporting.XMLParser
import com.amos.pitmutationmate.pitmutationmate.services.PluginCheckerService
import com.intellij.execution.ExecutorRegistry
import com.intellij.execution.ProgramRunnerUtil
import com.intellij.execution.RunManager
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.components.service
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowManager
import java.nio.file.Paths

abstract class RunConfigurationAction : AnAction() {
    fun updateAndExecuteRunConfig(classFQN: String?, project: Project, editor: Editor?) {
        val pluginChecker = project.service<PluginCheckerService>()
        if (pluginChecker.checkGroovyBuildFile() || pluginChecker.checkKotlinBuildFile()) {
            return
        }
        val executor = ExecutorRegistry.getInstance().getExecutorById("Run")

        val runConfig = RunManager.getInstance(project).getConfigurationSettingsList(
            RunConfigurationType::class.java
        ).first()

        runConfig.configuration.let {
            val rc = it as RunConfiguration
            if (classFQN != null) {
                rc.classFQN = classFQN
            }
        }

        ProgramRunnerUtil.executeConfiguration(runConfig, executor!!)

        if (editor != null) {
            // TODO: use actual XML report directories. This currently uses a placeholder test folder
            val dir = Paths.get("build", "reports", "pitest", "test", "mutations.xml")
            val xmlListener = XMLListener(dir, editor)
            xmlListener.listen()
            val ha: HoverAction = HoverAction(editor, xmlListener.getResult())
            ha.addHoverAction()
        }

        // Update visualisation with mock results
        // TODO: replace this by real results extracted by the HTMLParser
        val toolWindow: ToolWindow? = ToolWindowManager.getInstance(project).getToolWindow("Pitest")
        val mutationTestToolWindowFactorySingleton = MutationTestToolWindowFactory()
        val coverageReport: XMLParser.CoverageReport = XMLParser.CoverageReport(
            lineCoveragePercentage = 80,
            lineCoverageTextRatio = "160/200",
            mutationCoveragePercentage = 50,
            mutationCoverageTextRatio = "100/200",
            testStrengthPercentage = 40,
            testStrengthTextRatio = "80/200"
        )
        if (toolWindow != null) {
            mutationTestToolWindowFactorySingleton.updateReport(toolWindow, coverageReport)
        }
    }
}
