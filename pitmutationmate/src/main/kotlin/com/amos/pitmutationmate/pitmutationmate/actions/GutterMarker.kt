// SPDX-License-Identifier: MIT
// SPDX-FileCopyrightText: 2023 Brianne Oberson <brianne.oberson@gmail.com>

package com.amos.pitmutationmate.pitmutationmate.actions

import com.intellij.execution.lineMarker.RunLineMarkerContributor
import com.intellij.icons.AllIcons
import com.intellij.psi.PsiClass
import com.intellij.psi.PsiElement
import javax.swing.Icon


class GutterMarker : RunLineMarkerContributor() {
    override fun getInfo(psielement: PsiElement): Info? {
        if (psielement is PsiClass) {
            val gutterIcon: Icon = AllIcons.General.ArrowRight
            val fqn = psielement.qualifiedName
            val toolTip = "Run PIT MutationMate on '$fqn'"
            val toolTipProvider: (PsiElement) -> String = { _ -> toolTip }
            val action: Array<GutterAction?> = arrayOf(fqn?.let { GutterAction(it) })
            return Info(gutterIcon, action, toolTipProvider)
        }
        return null
    }
}
