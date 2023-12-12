// SPDX-License-Identifier: MIT
// SPDX-FileCopyrightText: 2023 Brianne Oberson <brianne.oberson@gmail.com>

package com.amos.pitmutationmate.pitmutationmate.actions

import com.amos.pitmutationmate.pitmutationmate.configuration.RunConfiguration
import com.amos.pitmutationmate.pitmutationmate.configuration.RunConfigurationType
import com.amos.pitmutationmate.pitmutationmate.reporting.XMLListener
import com.intellij.execution.ExecutorRegistry
import com.intellij.execution.ProgramRunnerUtil
import com.intellij.execution.RunManager
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import java.nio.file.Paths

abstract class RunConfigurationAction : AnAction() {
    fun updateAndExecuteRunConfig(classFQN: String?, project: Project, editor: Editor?) {
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
    }
}
