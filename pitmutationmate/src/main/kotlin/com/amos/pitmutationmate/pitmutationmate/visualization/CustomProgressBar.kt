// SPDX-FileCopyrightText: 2023 2023
//
// SPDX-License-Identifier: MIT

package com.amos.pitmutationmate.pitmutationmate.visualization

import com.intellij.ui.JBColor
import com.intellij.util.ui.UIUtil
import java.awt.FontMetrics
import java.awt.Graphics
import javax.swing.JComponent

internal class CustomProgressBar(private val coveragePercentage: Int, private val ratioText: String) : JComponent() {
    private val font = UIUtil.getLabelFont()
    private val barWidth = 150
    private val barHeight = 20
    private val spaceBetweenTextAndBar = 5

    override fun getWidth(): Int {
        return barWidth + getTextWidth(ratioText) + spaceBetweenTextAndBar
    }

    override fun getHeight(): Int {
        return barHeight
    }

    override fun paintComponent(g: Graphics) {
        super.paintComponent(g)

        val greenWidth = (coveragePercentage * barWidth) / 100
        val redWidth = barWidth - greenWidth

        // add space between ratio Text and the bar
        val ratioTextStartX = barWidth + spaceBetweenTextAndBar

        drawText(g, ratioText, ratioTextStartX)

        // Draw green segment
        g.color = JBColor.GREEN
        g.fillRect(1, 1, greenWidth, barHeight - 2)

        // Draw red segment
        g.color = JBColor.RED
        g.fillRect(greenWidth + 1, 1, redWidth - 2, barHeight - 2)

        val percentageText = "$coveragePercentage%"
        drawText(g, percentageText, (barWidth - getTextWidth(percentageText)) / 2)
    }

    private fun getTextWidth(text: String): Int {
        return getFontMetrics(font).stringWidth(text)
    }

    private fun drawText(g: Graphics, text: String, x: Int): Int {
        g.font = font

        val fontMetrics: FontMetrics = g.fontMetrics
        val textWidth: Int = fontMetrics.stringWidth(text)
        val textHeight: Int = fontMetrics.height

        val y = (barHeight - textHeight) / 2 + fontMetrics.ascent

        g.color = JBColor.BLACK // Set the color for the text
        g.drawString(text, x, y)

        return textWidth
    }
}
