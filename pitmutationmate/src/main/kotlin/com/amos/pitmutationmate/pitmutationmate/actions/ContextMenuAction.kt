// SPDX-License-Identifier: MIT
// SPDX-FileCopyrightText: 2023 Brianne Oberson <brianne.oberson@gmail.com>

package com.amos.pitmutationmate.pitmutationmate.actions

import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.psi.PsiClass
import com.intellij.psi.PsiClassOwner
import com.intellij.psi.PsiElement

class ContextMenuAction : RunConfigurationAction() {
    override fun actionPerformed(e: AnActionEvent) {
        val editor = e.getData(CommonDataKeys.EDITOR)
        val psiFile = e.getData(CommonDataKeys.PSI_FILE)
        if (e.place == "EditorPopup") {
            println("ContextMenuAction: actionPerformed in EditorPopup for file $psiFile")
            val psiElement = psiFile?.findElementAt(editor?.caretModel!!.offset)
            val selectedClass = findEnclosingClass(psiElement)
            if (selectedClass != null) {
                val classFQN = selectedClass.qualifiedName
                println("ContextMenuAction: selected class is $classFQN.")
                updateAndExecuteRunConfig(classFQN, e.project!!, editor)
            }
        }
        if (e.place == "ProjectViewPopup") {
            println("ContextMenuAction: actionPerformed in ProjectViewPopup for file $psiFile")
            val psiClasses = (psiFile as PsiClassOwner).classes
            var classFQNs: String = ""
            for (psiClass in psiClasses) {
                val fqn = psiClass.qualifiedName
                println("$fqn")
                if (fqn != null) {
                    classFQNs = if (classFQNs != "") {
                        "$classFQNs,$fqn"
                    } else {
                        fqn
                    }
                }
            }
            println("ContextMenuAction: selected classes are $classFQNs.")
            updateAndExecuteRunConfig(classFQNs, e.project!!, editor)
        }
    }

    override fun update(e: AnActionEvent) {
        val shouldEnable: Boolean = checkCondition(e)
        e.presentation.isEnabled = shouldEnable
    }

    private fun checkCondition(e: AnActionEvent): Boolean {
        val psiFile = e.getData(CommonDataKeys.PSI_FILE)
        val validFile = psiFile != null && (psiFile.name.endsWith(".java") || psiFile.name.endsWith(".kt"))
        if (e.place == "EditorPopup") {
            val editor = e.getData(CommonDataKeys.EDITOR)
            val psiElement = psiFile?.findElementAt(editor?.caretModel!!.offset)
            val validClass = (findEnclosingClass(psiElement) != null)
            return validFile && validClass
        }
        return validFile
    }

    private fun findEnclosingClass(psiElement: PsiElement?): PsiClass? {
        var currentElement: PsiElement? = psiElement
        while (currentElement != null && currentElement !is PsiClass) {
            currentElement = currentElement.parent
        }
        return currentElement as? PsiClass
    }
}
