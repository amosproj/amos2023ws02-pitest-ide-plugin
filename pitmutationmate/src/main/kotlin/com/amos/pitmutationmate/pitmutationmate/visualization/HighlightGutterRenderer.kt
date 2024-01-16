// SPDX-License-Identifier: MIT
// SPDX-FileCopyrightText: 2023 Tim Herzig <tim.herzig@hotmail.com>

package com.amos.pitmutationmate.pitmutationmate.visualization

import com.amos.pitmutationmate.pitmutationmate.actions.AnnotationAction
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.editor.markup.GutterIconRenderer
import com.intellij.openapi.editor.markup.RangeHighlighter
import com.intellij.ui.JBColor
import java.awt.Component
import java.awt.Graphics
import javax.swing.Icon

class HighlightGutterRenderer(val color: String) : GutterIconRenderer() {

    override fun equals(other: Any?): Boolean {
        if (other is HighlightGutterRenderer) {
            return other.color == this.color
        }
        return false
    }

    override fun hashCode(): Int {
        return color.hashCode()
    }

    object GutterHighlighter {
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

    override fun getIcon(): Icon {
        return when (this.color) {
            "red" -> {
                BarIcon(JBColor.RED)
            }
            "yellow" -> {
                BarIcon(JBColor.YELLOW)
            }
            else -> {
                BarIcon(JBColor.GREEN)
            }
        }
    }

    override fun getClickAction(): AnAction {
        return AnnotationAction()
    }

    override fun getTooltipText(): String {
        return "Click to disable MutationMate annotations"
    }

    override fun getAlignment(): Alignment {
        return Alignment.LEFT
    }

    private class BarIcon(val color: JBColor) : Icon {
        override fun paintIcon(c: Component?, g: Graphics, x: Int, y: Int) {
            g.color = this.color
            g.fillRect(x, y, iconWidth, iconHeight)
        }

        override fun getIconWidth(): Int {
            return 5
        }

        override fun getIconHeight(): Int {
            return 16
        }
    }
}
