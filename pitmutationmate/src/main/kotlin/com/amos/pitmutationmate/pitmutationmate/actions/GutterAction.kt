// SPDX-License-Identifier: MIT
// SPDX-FileCopyrightText: 2023 Brianne Oberson <brianne.oberson@gmail.com>

package com.amos.pitmutationmate.pitmutationmate.actions

import com.intellij.openapi.actionSystem.AnActionEvent

class GutterAction(private val fqn: String) : RunConfigurationAction() {
    override fun actionPerformed(e: AnActionEvent) {
        runConfiguration(fqn, e.project!!)
    }
}
