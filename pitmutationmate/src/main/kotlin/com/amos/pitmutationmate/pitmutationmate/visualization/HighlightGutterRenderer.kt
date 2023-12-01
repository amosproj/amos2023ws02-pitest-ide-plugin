// SPDX-License-Identifier: MIT
// SPDX-FileCopyrightText: 2023 Tim Herzig <tim.herzig@hotmail.com>

import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.editor.markup.GutterIconRenderer
import com.intellij.openapi.editor.markup.RangeHighlighter
import com.intellij.psi.PsiElement
import java.awt.Color
import java.awt.Component
import java.awt.Graphics
import javax.swing.Icon


class HighlightGutterRenderer(color: String): GutterIconRenderer() {
    private val toolTip = "PITest run"
    val toolTipProvider: (PsiElement) -> String = { _ -> toolTip }
    val color: String = color
    override fun equals(other: Any?): Boolean {
        TODO("Not yet implemented")
    }

    override fun hashCode(): Int {
        TODO("Not yet implemented")
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
        return if (this.color == "red") {
            RedBarIcon()
        }else if (this.color == "green") {
            GreenBarIcon()
        }else {
            YellowBarIcon()
        }
    }

    override fun getAlignment(): Alignment {
        return Alignment.LEFT
    }


    private class RedBarIcon : Icon {
        override fun paintIcon(c: Component?, g: Graphics, x: Int, y: Int) {
            g.setColor(Color.RED)
            g.fillRect(x, y, iconWidth, iconHeight)
        }

        override fun getIconWidth(): Int {
            return 10
        }

        override fun getIconHeight(): Int {
            return 16
        }
    }

    private class GreenBarIcon : Icon {
        override fun paintIcon(c: Component?, g: Graphics, x: Int, y: Int) {
            g.setColor(Color.GREEN)
            g.fillRect(x, y, iconWidth, iconHeight)
        }

        override fun getIconWidth(): Int {
            return 10
        }

        override fun getIconHeight(): Int {
            return 16
        }
    }

    private class YellowBarIcon : Icon {
        override fun paintIcon(c: Component?, g: Graphics, x: Int, y: Int) {
            g.setColor(Color.YELLOW)
            g.fillRect(x, y, iconWidth, iconHeight)
        }

        override fun getIconWidth(): Int {
            return 10
        }

        override fun getIconHeight(): Int {
            return 16
        }
    }
}
