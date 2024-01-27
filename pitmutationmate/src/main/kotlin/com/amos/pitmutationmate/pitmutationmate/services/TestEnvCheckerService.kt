// SPDX-License-Identifier: MIT
// SPDX-FileCopyrightText: 2023

package com.amos.pitmutationmate.pitmutationmate.services

import com.amos.pitmutationmate.pitmutationmate.utils.Utils
import com.intellij.openapi.components.Service
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiClass
import com.intellij.psi.PsiClassOwner
import org.jetbrains.kotlin.psi.KtClass
import java.io.File

@Service(Service.Level.PROJECT)
class TestEnvCheckerService(private val project: Project) {

    fun isTestFile(file: File): Boolean {
        val psiFile = Utils.getPsiFileFromPath(project, file.path)
        val psiClasses = (psiFile as PsiClassOwner).classes

        for (psiClass in psiClasses) {
            if (isPsiTestClass(psiClass)) {
                return true
            }
        }
        return false
    }

    // TODO This should be adjusted with a more thorough test
    fun isPsiTestClass(psiClass: PsiClass): Boolean {
        val fqn = psiClass.qualifiedName.toString()
        return fqn.endsWith("Test")
    }

    fun isKtTestClass(ktClass: KtClass): Boolean {
        val fqn = ktClass.fqName.toString()
        return fqn.endsWith("Test")
    }
}
