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
        return if (this.color == "light-pink") {
            LightPinkBarIcon()
        }else if (this.color == "dark-pink") {
            DarkPinkBarIcon()
        }else if (this.color == "light-green") {
            LightGreenBarIcon()
        }else {
            DarkGreenBarIcon()
        }
    }

    override fun getAlignment(): Alignment {
        return Alignment.LEFT
    }

    private class LightPinkBarIcon : Icon {
        override fun paintIcon(c: Component?, g: Graphics, x: Int, y: Int) {
            g.setColor(Color(252, 218, 217))
            g.fillRect(x, y, iconWidth, iconHeight)
        }

        override fun getIconWidth(): Int {
            return 10
        }

        override fun getIconHeight(): Int {
            return 16
        }
    }
    private class DarkPinkBarIcon : Icon {
        override fun paintIcon(c: Component?, g: Graphics, x: Int, y: Int) {
            g.setColor(Color(253, 161, 159))
            g.fillRect(x, y, iconWidth, iconHeight)
        }

        override fun getIconWidth(): Int {
            return 10
        }

        override fun getIconHeight(): Int {
            return 16
        }
    }
    private class LightGreenBarIcon : Icon {
        override fun paintIcon(c: Component?, g: Graphics, x: Int, y: Int) {
            g.setColor(Color(215, 255, 214))
            g.fillRect(x, y, iconWidth, iconHeight)
        }

        override fun getIconWidth(): Int {
            return 10
        }

        override fun getIconHeight(): Int {
            return 16
        }
    }
    private class DarkGreenBarIcon : Icon {
        override fun paintIcon(c: Component?, g: Graphics, x: Int, y: Int) {
            g.setColor(Color(161, 255, 161))
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
