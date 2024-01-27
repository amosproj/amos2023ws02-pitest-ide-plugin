// SPDX-License-Identifier: MIT
// SPDX-FileCopyrightText: 2023 Brianne Oberson <brianne.oberson@gmail.com>

package com.amos.pitmutationmate.pitmutationmate.actions

import com.amos.pitmutationmate.pitmutationmate.icons.Icons
import com.amos.pitmutationmate.pitmutationmate.services.TestEnvCheckerService
import com.intellij.execution.lineMarker.RunLineMarkerContributor
import com.intellij.openapi.components.service
import com.intellij.psi.PsiClass
import com.intellij.psi.PsiElement
import org.jetbrains.kotlin.psi.KtClass
import javax.swing.Icon

class GutterMarker : RunLineMarkerContributor() {
    override fun getInfo(psielement: PsiElement): Info? {
        val gutterIcon: Icon = Icons.RunButton
        val project = psielement.project
        if (psielement.parent is PsiClass && psielement.text.equals("class")) {
            if (project.service<TestEnvCheckerService>().isPsiTestClass(psielement.parent as PsiClass)) {
                return null
            }
            val toolTipProvider: (PsiElement) -> String = { _ -> "Run PIT MutationMate on '${(psielement.parent as PsiClass).name}'" }
            val fqn = (psielement.parent as PsiClass).qualifiedName
            val action: Array<GutterAction?> = arrayOf(fqn?.let { GutterAction(it) })
            return Info(gutterIcon, action, toolTipProvider)
        }
        if (psielement.parent is KtClass && psielement.text.equals("class")) {
            if (project.service<TestEnvCheckerService>().isKtTestClass(psielement.parent as KtClass)) {
                return null
            }
            val toolTipProvider: (PsiElement) -> String = { _ -> "Run PIT MutationMate on '${(psielement.parent as KtClass).name}'" }
            val fqn = (psielement.parent as KtClass).fqName.toString()
            val action: Array<GutterAction?> = arrayOf(GutterAction(fqn))
            return Info(gutterIcon, action, toolTipProvider)
        }
        return null
    }
}
