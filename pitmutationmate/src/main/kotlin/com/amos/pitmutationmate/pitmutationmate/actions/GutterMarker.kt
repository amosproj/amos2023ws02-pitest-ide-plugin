// SPDX-License-Identifier: MIT
// SPDX-FileCopyrightText: 2023 Brianne Oberson <brianne.oberson@gmail.com>

package com.amos.pitmutationmate.pitmutationmate.actions

import com.intellij.execution.lineMarker.RunLineMarkerContributor
import com.intellij.icons.AllIcons
import com.intellij.psi.PsiClass
import com.intellij.psi.PsiElement
import org.jetbrains.kotlin.psi.KtClass
import javax.swing.Icon

class GutterMarker : RunLineMarkerContributor() {
    override fun getInfo(psielement: PsiElement): Info? {
        val gutterIcon: Icon = AllIcons.General.ArrowRight
        if (psielement is PsiClass) {
            println(psielement)
            val toolTipProvider: (PsiElement) -> String = { _ -> "Run PIT MutationMate on '${psielement.name}'" }
            val fqn = psielement.qualifiedName
            val action: Array<GutterAction?> = arrayOf(fqn?.let { GutterAction(it) })
            return Info(gutterIcon, action, toolTipProvider)
        }
        if (psielement is KtClass) {
            println(psielement)
            val toolTipProvider: (PsiElement) -> String = { _ -> "Run PIT MutationMate on '${psielement.name}'" }
            val fqn = psielement.fqName.toString()
            val action: Array<GutterAction?> = arrayOf(GutterAction(fqn))
            return Info(gutterIcon, action, toolTipProvider)
        }
        return null
    }
}
