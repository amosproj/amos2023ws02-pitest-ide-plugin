// SPDX-License-Identifier: MIT
// SPDX-FileCopyrightText: 2023 Brianne Oberson <brianne.oberson@gmail.com>

package com.amos.pitmutationmate.pitmutationmate.actions

import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys

class GutterAction(private val fqn: String) : RunConfigurationAction() {
    override fun actionPerformed(e: AnActionEvent) {
        updateAndExecuteRunConfig(fqn, e.project!!)
    }
}
