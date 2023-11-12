// SPDX-License-Identifier: MIT
// SPDX-FileCopyrightText: 2023

package com.amos.pitmutationmate.pitmutationmate

import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.editor.Document
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.ui.Messages
import com.intellij.pom.Navigatable
import org.apache.tools.ant.Project


class RunConfiguration : AnAction() {
    override fun actionPerformed(e: AnActionEvent) {
        println("RunConfigure Action Performed")

        var project: com.intellij.openapi.project.Project? = e.project
        var message: StringBuilder = StringBuilder(e.presentation.text + " Selected!")
        var selElement: Editor? = e.getData(CommonDataKeys.EDITOR_EVEN_IF_INACTIVE) //NAVIGATABLE)
        if (selElement != null) {
            var selDocument: Document? = selElement.document
            
            message.append("\nSelected Element: ").append(selElement)
        }
        var title: String = e.presentation.description
        Messages.showMessageDialog(
            project,
            message.toString(),
            title,
            Messages.getInformationIcon()
        )
    }

    override fun update(e: AnActionEvent) {
        println("update")
        super.update(e)
    }

    override fun getActionUpdateThread(): ActionUpdateThread {
        println("getActionUpdateThread")
        return super.getActionUpdateThread()
    }
}

