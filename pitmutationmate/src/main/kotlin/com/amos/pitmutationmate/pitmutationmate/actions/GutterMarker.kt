// SPDX-License-Identifier: MIT
// SPDX-FileCopyrightText: 2023 Brianne Oberson <brianne.oberson@gmail.com>

package com.amos.pitmutationmate.pitmutationmate.actions

import com.amos.pitmutationmate.pitmutationmate.icons.Icons
import com.intellij.execution.lineMarker.RunLineMarkerContributor
import com.intellij.psi.PsiClass
import com.intellij.psi.PsiElement
import org.jetbrains.kotlin.psi.KtClass
import javax.swing.Icon

class GutterMarker : RunLineMarkerContributor() {
    override fun getInfo(psielement: PsiElement): Info? {
        val gutterIcon: Icon = Icons.RunButton
        if (psielement.context is PsiClass && psielement.text.equals("class")) {
            val toolTipProvider: (PsiElement) -> String = { _ -> "Run PIT MutationMate on '${(psielement.context as PsiClass).name}'" }
            val fqn = (psielement.context as PsiClass).qualifiedName
            val action: Array<GutterAction?> = arrayOf(fqn?.let { GutterAction(it) })
            return Info(gutterIcon, action, toolTipProvider)
        }
        if (psielement.context is KtClass && psielement.text.equals("class")) {
            val toolTipProvider: (PsiElement) -> String = { _ -> "Run PIT MutationMate on '${(psielement.context as KtClass).name}'" }
            val fqn = (psielement.context as KtClass).fqName.toString()
            val action: Array<GutterAction?> = arrayOf(GutterAction(fqn))
            return Info(gutterIcon, action, toolTipProvider)
        }
        return null
    }
}
