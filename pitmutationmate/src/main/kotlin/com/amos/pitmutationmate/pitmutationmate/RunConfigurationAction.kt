// SPDX-FileCopyrightText: 2023
// SPDX-FileCopyrightText: 2023 2023
//
// SPDX-License-Identifier: MIT

package com.amos.pitmutationmate.pitmutationmate

import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.editor.markup.GutterIconRenderer
import com.intellij.openapi.editor.markup.RangeHighlighter
import com.intellij.openapi.ui.MessageType
import com.intellij.openapi.ui.popup.Balloon
import com.intellij.openapi.ui.popup.JBPopupFactory
import com.intellij.ui.awt.RelativePoint
import org.jetbrains.annotations.NotNull
import java.awt.Color
import java.awt.Component
import java.awt.Graphics
import java.awt.Point
import javax.swing.Icon


class RunConfigurationAction : AnAction() {
    private lateinit var className: String

    fun init(cn: String) {
        className = cn
    }

    override fun actionPerformed(e: AnActionEvent) {
        println("RunConfiguratorAction actionPerformed for class $className")
        val project: com.intellij.openapi.project.Project? = e.project
        val gradleTaskExecutor = GradleTaskExecutor()
        var editor: Any? = e.getData(CommonDataKeys.EDITOR)
        println(editor)
        if (project != null && editor != null) {
            project.basePath?.let { gradleTaskExecutor.executeTask(it, "", "pitest") }
            println("HALLo TEST")
            if (editor is Editor) {
                println("EDITOR TEST")
                GutterIconExample.addRedBar(editor, 11)
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


    class RedBarGutterRenderer : GutterIconRenderer() {
        override fun equals(other: Any?): Boolean {
            TODO("Not yet implemented")
        }

        override fun hashCode(): Int {
            TODO("Not yet implemented")
        }

        @NotNull
        override fun getIcon(): Icon {
            return RED_BAR_ICON
        }

        @NotNull
        override fun getAlignment(): Alignment {
            return Alignment.LEFT // Adjust alignment as needed
        }

        override fun getTooltipText(): String? {
            return "Test Text for Highlight"
        }

        override fun getClickAction(): AnAction? {
            println("clicked")

            return ShowTextAction("Clicked Test Text to show")
        }

        private class ShowTextAction(private val text: String) : AnAction() {
            override fun actionPerformed(e: AnActionEvent) {
                val editor : Editor? = e.getData(CommonDataKeys.EDITOR)
                if(editor != null){
                    showTextBalloon(editor, text)
                }
            }

            private fun showTextBalloon(editor: Editor?, text: String) {
                println("HALLOO" + text)
                val balloon = JBPopupFactory.getInstance()
                    .createHtmlTextBalloonBuilder(text, MessageType.INFO, null)
                    .setFillColor(editor?.colorsScheme?.defaultBackground ?: Color.GRAY)
                    .setHideOnAction(true)
                    .setHideOnFrameResize(true)
                    .setHideOnKeyOutside(true)
                    .setHideOnClickOutside(true)
                    .createBalloon()
                var va = editor?.scrollingModel?.visibleArea
                var x = va?.centerX ?: 0
                var y = va?.centerY ?: 0
                val point = RelativePoint(editor?.contentComponent!!, Point(x.toInt(), y.toInt()))
                balloon.show(point, Balloon.Position.below)
            }
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

        companion object {
            private val RED_BAR_ICON: Icon = RedBarIcon()
        }
    }


    object GutterIconExample {
        fun addRedBar(editor: Editor, lineNumber: Int) {
            val markupModel = editor.markupModel
            val redBarGutterRenderer = RedBarGutterRenderer()
            markupModel.removeAllHighlighters()
            val rangeHighlighter: RangeHighlighter = markupModel.addLineHighlighter(
                TextAttributesKey.createTextAttributesKey("RED_BAR_TEXT_ATTRIBUTES"),
                lineNumber,
                0
            )
            rangeHighlighter.gutterIconRenderer = redBarGutterRenderer
        }
    }

}
