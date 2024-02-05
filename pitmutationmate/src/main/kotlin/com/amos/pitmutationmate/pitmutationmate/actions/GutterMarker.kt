// SPDX-License-Identifier: MIT
// SPDX-FileCopyrightText: 2023 Brianne Oberson <brianne.oberson@gmail.com>

package com.amos.pitmutationmate.pitmutationmate.actions

import com.amos.pitmutationmate.pitmutationmate.icons.Icons
import com.amos.pitmutationmate.pitmutationmate.services.TestEnvCheckerService
import com.intellij.execution.lineMarker.RunLineMarkerContributor
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiClass
import com.intellij.psi.PsiElement
import org.jetbrains.kotlin.psi.KtClass
import javax.swing.Icon

class GutterMarker : RunLineMarkerContributor() {
    private val gutterIcon: Icon = Icons.RunButton

    override fun getInfo(psielement: PsiElement): Info? {
        val project = psielement.project

        if (psielement.parent is PsiClass && psielement.text.equals("class")) {
            if (project.service<TestEnvCheckerService>().isPsiTestClass(psielement.parent as PsiClass)) {
                return null
            }
            val toolTipProvider: (PsiElement) -> String = { _ -> "Run PIT MutationMate on '${(psielement.parent as PsiClass).name}'" }
            val fqn = (psielement.parent as PsiClass).qualifiedName

            return Info(gutterIcon, toolTipProvider, getAction(project, fqn))
        }
        if (psielement.parent is KtClass && psielement.text.equals("class")) {
            if (project.service<TestEnvCheckerService>().isKtTestClass(psielement.parent as KtClass)) {
                return null
            }
            val toolTipProvider: (PsiElement) -> String = { _ -> "Run PIT MutationMate on '${(psielement.parent as KtClass).name}'" }
            val fqn = (psielement.parent as KtClass).fqName.toString()
            return Info(gutterIcon, toolTipProvider, getAction(project, fqn))
        }
        return null
    }

    private fun getAction(project: Project, fqdn: String?): AnAction {
        return object : AnAction("Run PIT MutationMate", "Initializes a PiTest run on the selected class", gutterIcon) {
            override fun actionPerformed(e: AnActionEvent) {
                RunConfigurationActionRunner.updateAndExecuteRunConfig(fqdn, project)
            }
        }
    }
}
