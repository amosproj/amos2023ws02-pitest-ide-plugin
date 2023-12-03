// SPDX-License-Identifier: MIT
// SPDX-FileCopyrightText: 2023 Brianne Oberson <brianne.oberson@gmail.com>

package com.amos.pitmutationmate.pitmutationmate.actions

import com.intellij.openapi.actionSystem.AnActionEvent

class ToolMenuAction : RunConfigurationAction() {
    override fun actionPerformed(e: AnActionEvent) {
        updateAndExecuteRunConfig("", e.project!!)
    }
}
