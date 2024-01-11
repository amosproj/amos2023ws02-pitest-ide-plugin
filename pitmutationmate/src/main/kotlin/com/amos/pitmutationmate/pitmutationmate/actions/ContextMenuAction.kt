// SPDX-License-Identifier: MIT
// SPDX-FileCopyrightText: 2023 Brianne Oberson <brianne.oberson@gmail.com>

package com.amos.pitmutationmate.pitmutationmate.actions

import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.diagnostic.Logger
import com.intellij.psi.PsiClass
import com.intellij.psi.PsiClassOwner
import com.intellij.psi.PsiElement
import org.jetbrains.kotlin.psi.KtClass

class ContextMenuAction : RunConfigurationAction() {
    private val logger = Logger.getInstance(ContextMenuAction::class.java)

    fun actionEditorPopup(e: AnActionEvent) {
        val editor = e.getData(CommonDataKeys.EDITOR)
        val psiFile = e.getData(CommonDataKeys.PSI_FILE)

        logger.info("ContextMenuAction: actionPerformed in EditorPopup for file $psiFile")
        val psiElement = psiFile?.findElementAt(editor?.caretModel!!.offset)
        val selectedClass = findEnclosingClass(psiElement)
        if (selectedClass != null) {
            var classFQN = ""
            if (selectedClass is PsiClass) {
                classFQN = selectedClass.qualifiedName.toString()
            }
            if (selectedClass is KtClass) {
                classFQN = selectedClass.fqName.toString()
            }

            logger.info("ContextMenuAction: selected class is $classFQN.")
            updateAndExecuteRunConfig(classFQN, e.project!!)
        }
    }

    fun actionProjectViewPopupFile(e: AnActionEvent) {
        val editor = e.getData(CommonDataKeys.EDITOR)
        val psiFile = e.getData(CommonDataKeys.PSI_FILE)

        logger.info("ContextMenuAction: actionPerformed in ProjectViewPopup for file $psiFile")
        val psiClasses = (psiFile as PsiClassOwner).classes
        var classFQNs: String = ""
        for (psiClass in psiClasses) {
            val fqn = psiClass.qualifiedName
            if (fqn != null) {
                classFQNs = if (classFQNs != "") {
                    "$classFQNs,$fqn"
                } else {
                    fqn
                }
            }
        }
        logger.info("ContextMenuAction: selected classes are $classFQNs.")
        updateAndExecuteRunConfig(classFQNs, e.project!!)
    }

    fun actionProjectViewPopupDir(e: AnActionEvent) {

    }

    override fun actionPerformed(e: AnActionEvent) {
        if (e.place == "EditorPopup") {
            actionEditorPopup(e)
        }else if (e.place == "ProjectViewPopup") {
            if (e.getData(CommonDataKeys.PSI_ELEMENT).toString().startsWith("PsiDirectory")) {
                actionProjectViewPopupDir(e)
            }else {
                actionProjectViewPopupFile(e)
            }
        }
    }

    override fun update(e: AnActionEvent) {
        val shouldEnable: Boolean = checkCondition(e)
        e.presentation.isEnabled = shouldEnable
    }

    override fun getActionUpdateThread(): ActionUpdateThread {
        return ActionUpdateThread.BGT
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
        if (e.place == "ProjectViewPopup") {
            if (e.getData(CommonDataKeys.PSI_ELEMENT).toString().startsWith("PsiDirectory")) {
                return true
            }
        }
        return validFile
    }

    private fun findEnclosingClass(psiElement: PsiElement?): PsiElement? {
        var currentElement: PsiElement? = psiElement
        while (currentElement != null && currentElement !is PsiClass && currentElement !is KtClass) {
            currentElement = currentElement.parent
        }
        return currentElement
    }
}
