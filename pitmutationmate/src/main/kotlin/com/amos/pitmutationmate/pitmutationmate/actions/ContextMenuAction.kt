// SPDX-License-Identifier: MIT
// SPDX-FileCopyrightText: 2023

package com.amos.pitmutationmate.pitmutationmate.actions

import com.amos.pitmutationmate.pitmutationmate.RunArchiver
import com.amos.pitmutationmate.pitmutationmate.configuration.MutationMateRunConfiguration
import com.amos.pitmutationmate.pitmutationmate.configuration.MutationMateRunConfigurationType
import com.intellij.execution.ExecutorRegistry
import com.intellij.execution.ProgramRunnerUtil
import com.intellij.execution.RunManager
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiJavaFile

class ContextMenuAction : AnAction() {
    override fun actionPerformed(e: AnActionEvent) {
        val psiFile = e.getData(CommonDataKeys.PSI_FILE)
        println("ContextMenuAction: actionPerformed for file $psiFile")
        val psiClasses = (psiFile as PsiJavaFile).classes
        val fqns = mutableListOf<String>()
        for (psiClass in psiClasses) {
            val fqn = psiClass.qualifiedName
            if (fqn != null) {
                fqns.add(fqn)
                println("ContextMenuAction: detected class '$fqn'")
            }
        }
        val executor = ExecutorRegistry.getInstance().getExecutorById("Run")

        val runConfig = RunManager.getInstance(e.project!!).getConfigurationSettingsList(
            MutationMateRunConfigurationType::class.java
        ).first()

        runConfig.configuration.let {
            val rc = it as MutationMateRunConfiguration
            rc.classFQN = fqns.first()
        }

        ProgramRunnerUtil.executeConfiguration(runConfig, executor!!)

        // Testing RunArchiver TODO: remove this later
        val project: Project? = e.project
        if (project != null) {
            val ra: RunArchiver = RunArchiver("de.pfoerd.example.pitest.coffeemachine.service", project)
            ra.archiveRun()
        }
    }

    override fun update(e: AnActionEvent) {
        // Get the project and the file associated with the action event
        val file: VirtualFile? = e.dataContext.getData("virtualFile") as VirtualFile?

        // Check your condition to determine whether to enable or disable the action
        val shouldEnable: Boolean = checkCondition(file)

        // Enable or disable the action based on the condition
        e.presentation.isEnabled = shouldEnable
    }

    private fun checkCondition(file: VirtualFile?): Boolean {
        return file != null && (file.name.endsWith(".java") || file.name.endsWith(".kt"))
    }
}
