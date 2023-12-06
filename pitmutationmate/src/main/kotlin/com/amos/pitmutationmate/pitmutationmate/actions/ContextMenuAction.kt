// SPDX-License-Identifier: MIT
// SPDX-FileCopyrightText: 2023 Brianne Oberson <brianne.oberson@gmail.com>

package com.amos.pitmutationmate.pitmutationmate.actions

import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiClass
import com.intellij.psi.PsiElement

class ContextMenuAction : RunConfigurationAction() {
    override fun actionPerformed(e: AnActionEvent) {
        val editor = e.getData(CommonDataKeys.EDITOR)
        val psiFile = e.getData(CommonDataKeys.PSI_FILE)
        println(e.place)
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
        }

//        println("ContextMenuAction: actionPerformed for file $psiFile")
//        val psiClasses = (psiFile as PsiClassOwner).classes
//        val fqns = mutableListOf<String>()
//        for (psiClass in psiClasses) {
//            val fqn = psiClass.qualifiedName
//            if (fqn != null) {
//                fqns.add(fqn)
//                println("ContextMenuAction: detected class '$fqn'")
//            }
//        }


//        updateAndExecuteRunConfig(fqns.first(), e.project!!, editor)
    }

    override fun update(e: AnActionEvent) {
        val file: VirtualFile? = e.dataContext.getData("virtualFile") as VirtualFile?
        val shouldEnable: Boolean = checkCondition(file)
        e.presentation.isEnabled = shouldEnable
    }

    private fun checkCondition(file: VirtualFile?): Boolean {
        return file != null && (file.name.endsWith(".java") || file.name.endsWith(".kt"))
    }

    private fun findEnclosingClass(psiElement: PsiElement?): PsiClass? {
        var currentElement: PsiElement? = psiElement
        while (currentElement != null && currentElement !is PsiClass) {
            currentElement = currentElement.parent
        }
        return currentElement as? PsiClass
    }
}
