// SPDX-License-Identifier: MIT
// SPDX-FileCopyrightText: 2023 Brianne Oberson <brianne.oberson@gmail.com>, Tim Herzig <tim.herzig@hotmail.com>

package com.amos.pitmutationmate.pitmutationmate.actions

import com.amos.pitmutationmate.pitmutationmate.services.PluginCheckerService
import com.amos.pitmutationmate.pitmutationmate.services.TestEnvCheckerService
import com.amos.pitmutationmate.pitmutationmate.utils.Utils
import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.components.service
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiClass
import com.intellij.psi.PsiClassOwner
import com.intellij.psi.PsiDirectory
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import java.io.File
import org.jetbrains.kotlin.psi.KtClass

class ContextMenuAction : RunConfigurationAction() {
    private val logger = Logger.getInstance(ContextMenuAction::class.java)

    private fun updateAndExecuteForFile(psiFileArray: Array<PsiFile>, project: Project) {
        val testEnvChecker = project.service<TestEnvCheckerService>()
        var classFQNs = ""
        for (psiFile in psiFileArray) {
            logger.info("ContextMenuAction: actionPerformed in ProjectViewPopup for file $psiFile")
            val psiClasses = (psiFile as PsiClassOwner).classes
            for (psiClass in psiClasses) {
                if (!testEnvChecker.isPsiTestClass(psiClass)) {
                    val fqn = psiClass.qualifiedName
                    if (fqn != null) {
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
            } else if (selectedClass is KtClass) {
                classFQN = selectedClass.fqName.toString()
            }

            logger.info("ContextMenuAction: selected class is $classFQN.")
            updateAndExecuteRunConfig(classFQN, e.project!!)
        }
    }

    private fun actionProjectViewPopupDir(e: AnActionEvent, psiDirectory: PsiDirectory) {
        var psiFileArray: Array<PsiFile> = emptyArray()
        val path = psiDirectory.virtualFile.path
        val directory = File(path)

        directory.walk()
            .filter { it.isFile && (it.extension == "java" || it.extension == "kt") }
            .forEach { Utils.getPsiFileFromPath(e.project!!, it.toString())?.let { it1 -> psiFileArray += it1 } }

        updateAndExecuteForFile(psiFileArray, e.project!!)
    }

    override fun actionPerformed(e: AnActionEvent) {
        val psiElement = e.getData(CommonDataKeys.PSI_ELEMENT)
        if (e.place == "EditorPopup") {
            actionEditorPopup(e)
        } else if (e.place == "ProjectViewPopup") {
            when (psiElement) {
                is PsiDirectory -> {
                    actionProjectViewPopupDir(e, psiElement)
                }

                is PsiFile -> { // This also covers KtFiles
                    updateAndExecuteForFile(arrayOf(psiElement), e.project!!)
                }

                is KtClass -> {
                    updateAndExecuteRunConfig(psiElement.fqName.toString(), e.project!!)
                }

                is PsiClass -> {
                    updateAndExecuteRunConfig(psiElement.qualifiedName, e.project!!)
                }
            }
        }
    }

    override fun update(e: AnActionEvent) {
        val pluginError = e.project?.service<PluginCheckerService>()?.getErrorMessage()
        if (pluginError != null) {
            e.presentation.isEnabled = false
            return
        }
        e.presentation.isEnabled = shouldEnablePitRun(e)
    }

    override fun getActionUpdateThread(): ActionUpdateThread {
        return ActionUpdateThread.BGT
    }

    private fun shouldEnablePitRun(e: AnActionEvent): Boolean {
        val project = e.project ?: return false
        val testEnvChecker = project.service<TestEnvCheckerService>()
        val psiFile = e.getData(CommonDataKeys.PSI_FILE)
        var psiElement = e.getData(CommonDataKeys.PSI_ELEMENT)

        val validFile = psiFile != null && (psiFile.name.endsWith(".java") || psiFile.name.endsWith(".kt"))
        if (e.place == "EditorPopup") {
            val editor = e.getData(CommonDataKeys.EDITOR)
            psiElement = psiFile?.findElementAt(editor?.caretModel!!.offset)
            val validClass = (findEnclosingClass(psiElement) != null)
            return validFile && validClass && !testEnvChecker.isTestFile(File(psiFile!!.virtualFile.path))
        }
        if (e.place == "ProjectViewPopup" && psiElement is PsiDirectory) {
            val directory = File(psiElement.virtualFile.path)
            var returnValue = false
            directory.walk()
                .filter { it.isFile && (it.extension == "kt" || it.extension == "java") }
                .forEach { if (!testEnvChecker.isTestFile(it)) { returnValue = true } }
            return returnValue
        }
        return validFile && !testEnvChecker.isTestFile(File(psiFile!!.virtualFile.path))
    }

    private fun findEnclosingClass(psiElement: PsiElement?): PsiElement? {
        var currentElement: PsiElement? = psiElement
        while (currentElement != null && currentElement !is PsiClass && currentElement !is KtClass) {
            currentElement = currentElement.parent
        }
        return currentElement
    }
}
