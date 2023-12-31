// SPDX-License-Identifier: MIT
// SPDX-FileCopyrightText: 2023 Tim Herzig <tim.herzig@hotmail.com>

package com.amos.pitmutationmate.pitmutationmate.actions

import com.amos.pitmutationmate.pitmutationmate.reporting.XMLParser
import com.intellij.codeInsight.hint.HintManager
import com.intellij.codeInsight.hint.HintManagerImpl
import com.intellij.codeInsight.hint.HintUtil
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.event.EditorMouseEvent
import com.intellij.openapi.editor.event.EditorMouseListener
import com.intellij.openapi.editor.event.EditorMouseMotionListener
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiDocumentManager
import com.intellij.psi.PsiFile
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.ui.LightweightHint
import com.intellij.util.ui.accessibility.AccessibleContextUtil
import java.awt.Point
import javax.swing.JComponent

class HoverAction(private val editor: Editor, private val result: XMLParser.ResultData) {
    fun addHoverAction() {
        this.editor.addEditorMouseListener(MouseClick())
//        TODO: Decide on which action suits the plugin the best --> when choice is made refactor to only use one
//        this.editor.addEditorMouseMotionListener(MouseMotion())
    }

    inner class MouseMotion : EditorMouseMotionListener {
        override fun mouseMoved(event: EditorMouseEvent) {
            showHoverMessage(event.mouseEvent.point)
        }
    }

    inner class MouseClick : EditorMouseListener {
        override fun mouseClicked(event: EditorMouseEvent) {
            showHoverMessage(event.mouseEvent.point)
        }
    }

    private fun buildHoverMessage(point: Point): String? {
        val project: Project = this.editor.project ?: return null
        val psiFile: PsiFile = PsiDocumentManager.getInstance(project).getPsiFile(this.editor.document) ?: return null

        val offset: Int = this.editor.visualPositionToOffset(this.editor.xyToVisualPosition(point))
        val line: Int = this.editor.yToVisualLine(point.y) + 1
        PsiTreeUtil.findElementOfClassAtOffset(psiFile, offset, psiFile.javaClass, false)

        for (r in this.result.mutationResults) {
            if (r.lineNumber == line) {
                val color: String = if (r.detected) "dark-green" else "dark-pink"
                return "PiTest: selected offset: $offset, selected line: $line \n" +
                    "The color of this line is $color"
            }
        }

        return null
    }

    fun showHoverMessage(point: Point) {
        val message: String = buildHoverMessage(point) ?: return
        val hintManager: HintManagerImpl = HintManagerImpl.getInstanceImpl()
        val label: JComponent = HintUtil.createInformationLabel(message, null, null, null)
        AccessibleContextUtil.setName(label, "PiTest")
        val hint = LightweightHint(label)
        val p: Point = HintManagerImpl.getHintPosition(hint, this.editor, this.editor.xyToVisualPosition(point), 1)
        val flags: Int = HintManager.HIDE_BY_ANY_KEY or HintManager.HIDE_BY_TEXT_CHANGE or HintManager.HIDE_BY_SCROLLING
        hintManager.showEditorHint(hint, this.editor, p, flags, 0, true, 1)
    }
}
