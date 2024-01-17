// SPDX-License-Identifier: MIT
// SPDX-FileCopyrightText: 2023 Brianne Oberson <brianne.oberson@gmail.com>, Tim Herzig <tim.herzig@hotmail.com>

package com.amos.pitmutationmate.pitmutationmate.actions

import com.amos.pitmutationmate.pitmutationmate.services.PluginCheckerService
import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.components.service
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.psi.PsiClass
import com.intellij.psi.PsiClassOwner
import com.intellij.psi.PsiDirectory
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiManager
import org.jetbrains.kotlin.psi.KtClass
import java.io.File

class ContextMenuAction : RunConfigurationAction() {
    private val logger = Logger.getInstance(ContextMenuAction::class.java)

    private fun updateAndExecuteForFile(psiFileArray: Array<PsiFile>, project: Project) {
        var classFQNs: String = ""
        for (psiFile in psiFileArray) {
            logger.info("ContextMenuAction: actionPerformed in ProjectViewPopup for file $psiFile")
            val psiClasses = (psiFile as PsiClassOwner).classes
            for (psiClass in psiClasses) {
                val fqn = psiClass.qualifiedName
                if (fqn != null) {
                    if (!fqn.endsWith("Test")) {
                        classFQNs = if (classFQNs != "") {
                            "$classFQNs,$fqn"
                        } else {
                            fqn
                        }
                    }
                }
            }
        }
        logger.info("ContextMenuAction: selected classes are $classFQNs.")
        updateAndExecuteRunConfig(classFQNs, project)
    }

    private fun getPsiFileFromPath(project: Project, filePath: String): PsiFile? {
        return LocalFileSystem.getInstance().findFileByPath(filePath)
            ?.let { PsiManager.getInstance(project).findFile(it) }
    }

    private fun actionEditorPopup(e: AnActionEvent) {
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

    private fun actionProjectViewPopupFile(e: AnActionEvent) {
        val psiFile = e.getData(CommonDataKeys.PSI_FILE)

        if (psiFile != null) {
            updateAndExecuteForFile(arrayOf(psiFile), e.project!!)
        }
    }

    private fun actionProjectViewPopupDir(e: AnActionEvent) {
        val psiElement = e.getData(CommonDataKeys.PSI_ELEMENT)
        var psiFileArray: Array<PsiFile> = emptyArray()
        if (psiElement != null) {
            if (psiElement is PsiDirectory) {
                val path = psiElement.virtualFile.path.toString()
                val directory = File(path)

                directory.walk()
                    .filter { it.isFile && (it.extension == "kt" || it.extension == "java") }
                    .forEach { getPsiFileFromPath(e.project!!, it.toString())?.let { it1 -> psiFileArray += it1 } }
            }
        }

        updateAndExecuteForFile(psiFileArray, e.project!!)
    }

    override fun actionPerformed(e: AnActionEvent) {
        if (e.place == "EditorPopup") {
            actionEditorPopup(e)
        } else if (e.place == "ProjectViewPopup") {
            if (e.getData(CommonDataKeys.PSI_ELEMENT).toString().startsWith("PsiDirectory")) {
                actionProjectViewPopupDir(e)
            } else {
                actionProjectViewPopupFile(e)
            }
        }
    }

    override fun update(e: AnActionEvent) {
        val pluginError = e.project?.service<PluginCheckerService>()?.getErrorMessage()
        if (pluginError != null) {
            e.presentation.isEnabled = false
            return
        }
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
        if (e.place == "ProjectViewPopup" && e.getData(CommonDataKeys.PSI_ELEMENT).toString().startsWith("PsiDirectory")) {
            val psiElement = e.getData(CommonDataKeys.PSI_ELEMENT)
            if (psiElement is PsiDirectory) {
                val directory: File = File(psiElement.virtualFile.path.toString())
                var returnValue: Boolean = false
                directory.walk()
                    .filter { it.isFile && (it.extension == "kt" || it.extension == "java") }
                    .forEach { if (!it.name.contains("Test")) { returnValue = true } }
                return returnValue
            }
            return false
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
