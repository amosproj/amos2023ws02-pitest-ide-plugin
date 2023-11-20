<<<<<<< HEAD:pitmutationmate/src/main/kotlin/com/amos/pitmutationmate/pitmutationmate/RunConfigurationMarker.kt
// SPDX-FileCopyrightText: 2023 2023
//
// SPDX-License-Identifier: MIT

package com.amos.pitmutationmate.pitmutationmate
=======
// SPDX-License-Identifier: MIT
// SPDX-FileCopyrightText: 2023

package com.amos.pitmutationmate.pitmutationmate.actions
>>>>>>> main:pitmutationmate/src/main/kotlin/com/amos/pitmutationmate/pitmutationmate/actions/GutterMarker.kt

import com.intellij.execution.lineMarker.RunLineMarkerContributor
import com.intellij.icons.AllIcons
import com.intellij.psi.PsiElement
import com.intellij.psi.util.elementType
import javax.swing.Icon


class GutterMarker : RunLineMarkerContributor() {
    override fun getInfo(element: PsiElement): Info? {
        val gutterIcon: Icon = AllIcons.General.ArrowRight
        val toolTip = "Run PIT MutationMate"
        val runActions: Array<ToolMenuAction> = arrayOf(ToolMenuAction())
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
