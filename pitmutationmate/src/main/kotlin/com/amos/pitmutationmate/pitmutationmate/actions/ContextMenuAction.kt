// SPDX-License-Identifier: MIT
// SPDX-FileCopyrightText: 2023

package com.amos.pitmutationmate.pitmutationmate.actions
import com.amos.pitmutationmate.pitmutationmate.RunArchiver
import com.amos.pitmutationmate.pitmutationmate.GradleTaskExecutor
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile

class ContextMenuAction : AnAction() {
    override fun actionPerformed(e: AnActionEvent) {
        val context = e.getDataContext()
        val file: VirtualFile? = context.getData("virtualFile") as VirtualFile?
        println("ContextMenuAction actionPerformed for file $file")
        val project: Project? = e.project
        val gradleTaskExecutor = GradleTaskExecutor()
        if (project != null) {
            //        For testing run archiver
            val ra: RunArchiver = RunArchiver("de.pfoerd.example.pitest.coffeemachine.service", project)
            ra.archiveRun()
            project.basePath?.let { gradleTaskExecutor.executeTask(it, "", "pitest") }
        }
    }

    override fun update(e: AnActionEvent) {
        // Get the project and the file associated with the action event
        val project: Project? = e.project
        val file: VirtualFile? = e.getDataContext().getData("virtualFile") as VirtualFile?

        // Check your condition to determine whether to enable or disable the action
        val shouldEnable: Boolean = checkCondition(file)

        // Enable or disable the action based on the condition
        e.presentation.isEnabled = shouldEnable
    }

    private fun checkCondition(file: VirtualFile?): Boolean {
        return file != null && (file.name.endsWith(".java") || file.name.endsWith(".kt"))
    }
}
