// SPDX-License-Identifier: MIT
// SPDX-FileCopyrightText: 2023 Tim Herzig <tim.herzig@hotmail.com>

package com.amos.pitmutationmate.pitmutationmate.actions

import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.event.EditorMouseEvent
import com.intellij.openapi.editor.event.EditorMouseMotionListener
import com.intellij.openapi.editor.markup.RangeHighlighter
import com.intellij.openapi.project.Project

class HoverAction(editor: Editor, hoverHighlighter: RangeHighlighter) : EditorMouseMotionListener {
    var editor: Editor = editor;
    var hoverHighlighter: RangeHighlighter = hoverHighlighter;
    fun hoverActionExample(editor: Editor) {
        this.editor = editor;
        this.editor.addEditorMouseMotionListener(this)
    }

    override fun mouseMoved(e: EditorMouseEvent) {
        val project: Project? = this.editor.project
    }

    fun getCurrentWord() {
        return
    }

    fun getCurrentLine() {
        return
    }

    fun showHoverMessage(editor: Editor, message: String) {
        return
    }
}
