package com.amos.pitmutationmate.pitmutationmate

import com.intellij.execution.lineMarker.RunLineMarkerContributor
import com.intellij.icons.AllIcons
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.psi.PsiElement
import com.intellij.psi.util.elementType
import javax.swing.Icon


class RunConfigurationMarker : RunLineMarkerContributor() {
    override fun getInfo(element: PsiElement): Info? {
        val gutterIcon: Icon = AllIcons.General.ArrowRight
        val toolTip = "Run Pit Mutation Mate"
        val runActions: Array<AnAction> = arrayOf(RunConfigurationAction())
        val toolTipProvider: (PsiElement) -> String = { _ -> toolTip }

        if (element.elementType.toString() == "CLASS") {
            return Info(gutterIcon, runActions, toolTipProvider)
        }
        return null
    }

}