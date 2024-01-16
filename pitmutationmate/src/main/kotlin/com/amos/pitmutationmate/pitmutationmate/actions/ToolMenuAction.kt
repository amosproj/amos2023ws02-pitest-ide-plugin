// SPDX-License-Identifier: MIT
// SPDX-FileCopyrightText: 2023 Brianne Oberson <brianne.oberson@gmail.com>

package com.amos.pitmutationmate.pitmutationmate.actions

import com.amos.pitmutationmate.pitmutationmate.services.PluginCheckerService
import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.components.service

class ToolMenuAction : RunConfigurationAction() {
    override fun actionPerformed(e: AnActionEvent) {
        updateAndExecuteRunConfig("", e.project!!)
    }

    override fun getActionUpdateThread(): ActionUpdateThread {
        return ActionUpdateThread.BGT
    }

    override fun update(e: AnActionEvent) {
        val pluginError = e.project?.service<PluginCheckerService>()?.getErrorMessage() != null
        e.presentation.isEnabled = pluginError
    }
}
