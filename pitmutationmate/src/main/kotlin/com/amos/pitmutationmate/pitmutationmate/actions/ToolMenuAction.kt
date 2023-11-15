// SPDX-License-Identifier: MIT
// SPDX-FileCopyrightText: 2023

package com.amos.pitmutationmate.pitmutationmate.actions

import com.amos.pitmutationmate.pitmutationmate.GradleTaskExecutor
import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.project.Project

class ToolMenuAction : AnAction() {
    private lateinit var className: String

    fun init(cn: String) {
        className = cn
    }

    override fun actionPerformed(e: AnActionEvent) {
        println("RunConfiguratorAction actionPerformed for whole project")
        val project: Project? = e.project
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
