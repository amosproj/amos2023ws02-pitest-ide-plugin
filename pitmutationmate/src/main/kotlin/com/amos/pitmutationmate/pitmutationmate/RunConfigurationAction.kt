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
        val project: com.intellij.openapi.project.Project? = e.project
        val message: StringBuilder = StringBuilder(e.presentation.text + " Selected!")
        val editor: Editor? = e.getData(CommonDataKeys.EDITOR_EVEN_IF_INACTIVE) //NAVIGATABLE)
        if (editor != null) {
            val document: Document = editor.document
            val psi = PsiDocumentManager.getInstance(project!!).getPsiFile(document)
            if (psi != null) {
                message.append("\nSelected Element: ").append(psi.name)
            }
        }
        val title: String = e.presentation.description
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
