// SPDX-License-Identifier: MIT
// SPDX-FileCopyrightText: 2023 Brianne Oberson

package com.amos.pitmutationmate.pitmutationmate.actions

import com.amos.pitmutationmate.pitmutationmate.editor.PluginState
import com.intellij.codeInsight.daemon.DaemonCodeAnalyzer
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent

class AnnotationAction : AnAction() {
    override fun actionPerformed(e: AnActionEvent) {
        PluginState.isAnnotatorEnabled = !PluginState.isAnnotatorEnabled

        // Trigger a reanalysis of the file to refresh the annotations
        // by using the DaemonCodeAnalyzer
        val project = e.project
        DaemonCodeAnalyzer.getInstance(project).restart()
    }
}
