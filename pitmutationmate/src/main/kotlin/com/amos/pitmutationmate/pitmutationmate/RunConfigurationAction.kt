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
import com.intellij.psi.PsiDocumentManager


class RunConfigurationAction : AnAction() {
    override fun actionPerformed(e: AnActionEvent) {
        println("RunConfigure Action Performed")

        var project: com.intellij.openapi.project.Project? = e.project
        var message: StringBuilder = StringBuilder(e.presentation.text + " Selected!")
        var editor: Editor? = e.getData(CommonDataKeys.EDITOR_EVEN_IF_INACTIVE) //NAVIGATABLE)
        var name: String = ""
        if (editor != null) {
            var document: Document? = editor.document
            if (document != null) {
                var psi = PsiDocumentManager.getInstance(project!!).getPsiFile(document)
                if (psi != null) {
                    message.append("\nSelected Element: ").append(psi.name)
                    name = psi.name
                }
            }
        }
        var title: String = e.presentation.description
        Messages.showMessageDialog(
            project,
            message.toString(),
            title,
            Messages.getInformationIcon()
        )
        val runConfig = RunConfiguration(name)
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

class RunConfiguration(val fileName: String) {
}
