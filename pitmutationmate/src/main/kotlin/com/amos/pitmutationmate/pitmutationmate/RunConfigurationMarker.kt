// SPDX-FileCopyrightText: 2023 2023
//
// SPDX-License-Identifier: MIT

package com.amos.pitmutationmate.pitmutationmate

import com.intellij.execution.lineMarker.RunLineMarkerContributor
import com.intellij.icons.AllIcons
import com.intellij.psi.PsiElement
import com.intellij.psi.util.elementType
import javax.swing.Icon


class RunConfigurationMarker : RunLineMarkerContributor() {
    override fun getInfo(element: PsiElement): Info? {
        val gutterIcon: Icon = AllIcons.General.ArrowRight
        val toolTip = "Run Pit Mutation Mate"
        val runActions: Array<RunConfigurationAction> = arrayOf(RunConfigurationAction())
        for (runConfig in runActions) {
            runConfig.init(element.text.toString())
        }
        val toolTipProvider: (PsiElement) -> String = { _ -> toolTip }

        if (element.elementType.toString() == "CLASS") {
            return Info(gutterIcon, runActions, toolTipProvider)
        }

        return null
    }
}
