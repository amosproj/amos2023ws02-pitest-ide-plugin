package com.amos.pitmutationmate.pitmutationmate

import com.intellij.execution.lineMarker.ExecutorAction
import com.intellij.execution.lineMarker.RunLineMarkerContributor
import com.intellij.icons.AllIcons
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.psi.PsiElement
import com.intellij.psi.util.elementType
import javax.swing.Icon

class RunConfigurationMarker : RunLineMarkerContributor() {
    override fun getInfo(element: PsiElement): Info? {
        if (element.elementType.toString() == "CLASS") {
            val gutterIcon: Icon = AllIcons.General.ArrowRight
            val toolTip = "Run Pit Mutation Mate"
            val toolTipProvider: (PsiElement) -> String = { _ -> toolTip }
            val runConfigAction = RunConfigurationAction()
            runConfigAction.init(element.text.toString())
            return Info(gutterIcon, arrayOf(runConfigAction), toolTipProvider)
        }

        return null
    }
}