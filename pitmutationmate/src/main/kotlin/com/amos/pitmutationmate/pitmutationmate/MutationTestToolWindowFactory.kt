// SPDX-License-Identifier: MIT
// SPDX-FileCopyrightText: 2023
package com.amos.pitmutationmate.pitmutationmate

import com.amos.pitmutationmate.pitmutationmate.visualization.CustomProgressBar
import com.amos.pitmutationmate.pitmutationmate.visualization.treetable.JTreeTable
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.content.ContentFactory


internal class MutationTestToolWindowFactory : ToolWindowFactory, DumbAware {
    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        val progressBar = ContentFactory.getInstance().createContent(CustomProgressBar(), "Progressbar", false)
        val table = ContentFactory.getInstance().createContent(JTreeTable(), "Mutationtest Coverage", false)

        toolWindow.contentManager.addContent(progressBar)
        toolWindow.contentManager.addContent(table)

    }
}
