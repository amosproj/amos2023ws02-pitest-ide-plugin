// SPDX-License-Identifier: MIT
// SPDX-FileCopyrightText: 2023 Brianne Oberson

package com.amos.pitmutationmate.pitmutationmate.actions

import com.amos.pitmutationmate.pitmutationmate.editor.PluginState
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.psi.PsiDocumentManager
import com.intellij.psi.PsiFile
import com.intellij.openapi.actionSystem.CommonDataKeys

class AnnotationAction: AnAction() {
    override fun actionPerformed(e: AnActionEvent) {
        PluginState.isAnnotatorEnabled = true
        val project = e.project
        val documentManager = PsiDocumentManager.getInstance(project!!)

        // Use CommonDataKeys.PSI_FILE to get the PsiFile
        val psiFile: PsiFile? = e.getData(CommonDataKeys.PSI_FILE)

        psiFile?.let {
            // Get the current editor's document and commit the changes
            documentManager.commitDocument(it.viewProvider.document)
        }
    }

}
