// SPDX-FileCopyrightText: 2023
// SPDX-License-Identifier: MIT

package com.amos.pitmutationmate.pitmutationmate.actions

import HighlightGutterRenderer
import com.amos.pitmutationmate.pitmutationmate.GradleTaskExecutor
import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.editor.markup.MarkupModel
import com.intellij.openapi.editor.markup.RangeHighlighter
import com.intellij.openapi.project.Project

class ToolMenuAction : AnAction() {
    private lateinit var className: String

    fun init(cn: String) {
        className = cn
    }

    override fun actionPerformed(e: AnActionEvent) {
        println("RunConfiguratorAction actionPerformed for whole project")
        val project: Project? = e.project
        val gradleTaskExecutor = GradleTaskExecutor()
        val editor: Any? = e.getData(CommonDataKeys.EDITOR)
        println(editor)
        if (project != null && editor != null) {
            project.basePath?.let { gradleTaskExecutor.executeTask(it, "", "pitest", "") }
            if (editor is Editor) {
                val markupModel: MarkupModel = editor.markupModel
                markupModel.removeAllHighlighters()
                HighlightGutterRenderer.GutterHighlighter.addBar(editor, "red", 10)
                HighlightGutterRenderer.GutterHighlighter.addBar(editor, "green", 11)
                HighlightGutterRenderer.GutterHighlighter.addBar(editor, "yellow", 12)
            }
        }
    }


    override fun update(e: AnActionEvent) {
        super.update(e)
    }

    override fun getActionUpdateThread(): ActionUpdateThread {
        return super.getActionUpdateThread()
    }
}
