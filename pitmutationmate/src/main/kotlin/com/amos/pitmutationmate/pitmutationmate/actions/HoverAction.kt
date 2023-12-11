// SPDX-License-Identifier: MIT
// SPDX-FileCopyrightText: 2023 Tim Herzig <tim.herzig@hotmail.com>

package com.amos.pitmutationmate.pitmutationmate.actions

import com.intellij.codeInsight.hint.HintManager
import com.intellij.codeInsight.hint.HintManagerImpl
import com.intellij.codeInsight.hint.HintUtil
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.event.EditorMouseEvent
import com.intellij.openapi.editor.event.EditorMouseMotionListener
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiDocumentManager
import com.intellij.psi.PsiFile
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.ui.LightweightHint
import com.intellij.util.ui.accessibility.AccessibleContextUtil
import java.awt.Point
import javax.swing.JComponent

class HoverAction(editor: Editor) : EditorMouseMotionListener {
    private var editor: Editor = editor;
    fun hoverActionExample(editor: Editor) {
        this.editor = editor;
        this.editor.addEditorMouseMotionListener(this)
    }

    override fun mouseMoved(e: EditorMouseEvent) {
        val project: Project = this.editor.project ?: return
        val psiFile: PsiFile = PsiDocumentManager.getInstance(project).getPsiFile(this.editor.document) ?: return

        val offset: Int = editor.caretModel.offset
        PsiTreeUtil.findElementOfClassAtOffset(psiFile, offset, psiFile.javaClass, false)
        val line = editor.caretModel.visualPosition.getLine() + 1
        showHoverMessage(this.editor, "PiTest: offset: $offset, line: $line")
    }

    fun showHoverMessage(editor: Editor, message: String) {
        val hintManager: HintManagerImpl = HintManagerImpl.getInstanceImpl()
        val label: JComponent = HintUtil.createInformationLabel(message, null, null, null)
        AccessibleContextUtil.setName(label, "Message")
        val hint: LightweightHint = LightweightHint(label)
        val point: Point = HintManagerImpl.getHintPosition(hint, editor, editor.caretModel.visualPosition, 1)
        val flags: Int = HintManager.HIDE_BY_ANY_KEY or HintManager.HIDE_BY_TEXT_CHANGE or HintManager.HIDE_BY_SCROLLING
        hintManager.showEditorHint(hint, editor, point, flags, 0, true, 1)
    }
}
