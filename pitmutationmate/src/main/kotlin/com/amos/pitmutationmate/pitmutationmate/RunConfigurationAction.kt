// SPDX-FileCopyrightText: 2023
// SPDX-License-Identifier: MIT

package com.amos.pitmutationmate.pitmutationmate

import HighlightGutterRenderer
import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.editor.markup.MarkupModel
import com.intellij.openapi.editor.markup.RangeHighlighter
import com.amos.pitmutationmate.pitmutationmate.RunArchiver

class RunConfigurationAction : AnAction() {
    private lateinit var className: String

    fun init(cn: String) {
        className = cn
    }

    override fun actionPerformed(e: AnActionEvent) {
        println("RunConfiguratorAction actionPerformed for class $className")
        val project: com.intellij.openapi.project.Project? = e.project
        val gradleTaskExecutor = GradleTaskExecutor()
        val editor: Any? = e.getData(CommonDataKeys.EDITOR)
        println(editor)
        if (project != null && editor != null) {
                //        For testing run archiver
            val ra: RunArchiver = RunArchiver("de.pfoerd.example.pitest.coffeemachine.service", project)
            ra.archiveRun()
            project.basePath?.let { gradleTaskExecutor.executeTask(it, "", "pitest") }
            if (editor is Editor) {
                val markupModel: MarkupModel = editor.markupModel
                markupModel.removeAllHighlighters()
                GutterIconExample.addBar(editor, "red", 10)
                GutterIconExample.addBar(editor, "green", 11)
                GutterIconExample.addBar(editor, "yellow", 12)
            }
        }
    }


    override fun update(e: AnActionEvent) {
        println("RunConfiguratorAction update")
        super.update(e)
    }

    override fun getActionUpdateThread(): ActionUpdateThread {
        println("RunConfiguratorAction getActionUpdateThread")
        return super.getActionUpdateThread()
    }

    object GutterIconExample {
        fun addBar(editor: Editor, color: String, lineNumber: Int) {
            val markupModel = editor.markupModel
            val barGutterRenderer = HighlightGutterRenderer(color)
            val rangeHighlighter: RangeHighlighter = markupModel.addLineHighlighter(
                TextAttributesKey.createTextAttributesKey("RED_BAR_TEXT_ATTRIBUTES"),
                lineNumber,
                0
            )
            rangeHighlighter.gutterIconRenderer = barGutterRenderer
        }
    }
}