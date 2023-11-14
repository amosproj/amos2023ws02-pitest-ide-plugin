// SPDX-License-Identifier: MIT
// SPDX-FileCopyrightText: 2023

package com.amos.pitmutationmate.pitmutationmate

import com.intellij.execution.process.ProcessHandler
import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.editor.Document
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.ui.Messages
import com.intellij.psi.PsiDocumentManager

class RunConfigurationAction : AnAction() {
    private lateinit var className: String

    fun init(cn: String) {
        className = cn
    }

    override fun actionPerformed(e: AnActionEvent) {
        println("RunConfiguratorAction actionPerformed for class $className")
        val project: com.intellij.openapi.project.Project? = e.project
        val gradleTaskExecutor = GradleTaskExecutor()
        if (project != null) {
            project.basePath?.let { gradleTaskExecutor.executeTask(it, "", "pitest") }
        }
    }

    override fun update(e: AnActionEvent) {
        println("RunConfiguratorAction update")
        super.update(e)
    }

    override fun getActionUpdateThread(): ActionUpdateThread {
        println("RunConfiguratorAction getActionUpdateThread")
        return super.getActionUpdateThread()
    }
}
