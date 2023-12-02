// SPDX-FileCopyrightText: 2023 2023
//
// SPDX-License-Identifier: MIT

package com.amos.pitmutationmate.pitmutationmate.visualization

import java.awt.Color
import java.awt.Font
import java.awt.FontMetrics
import java.awt.Graphics
import javax.swing.JComponent

internal class CustomProgressBar(coveragePercentage: Int, ratioText: String) : JComponent() {
    private val coveragePercentage = coveragePercentage
    private val ratioText = ratioText
    private val font = Font("Arial", Font.BOLD, 12) // You can adjust the font as needed
    private val barWidth = 150
    private val barHeight = 20
    private val ratioTextStartX = 5
    private val spaceBetweenTextAndBar = 5

    override fun getWidth(): Int {
        return barWidth + getTextWidth(ratioText) + ratioTextStartX + spaceBetweenTextAndBar
    }

    override fun getHeight(): Int {
        return barHeight
    }
    override fun paintComponent(g: Graphics) {
        super.paintComponent(g)

        val greenWidth = (coveragePercentage * barWidth) / 100
        val redWidth = barWidth - greenWidth

        // add space between ratio Text and the bar
        var barStartX = getTextWidth(ratioText) + ratioTextStartX + spaceBetweenTextAndBar

        drawText(g, ratioText, ratioTextStartX)

        // Draw grey border
        g.color = Color(170, 170, 170)
        g.drawRect(barStartX, 0, barWidth - 1, barHeight - 1)

        // Draw green segment
        g.color = Color(221, 255, 221)
        g.fillRect(barStartX + 1, 1, greenWidth, barHeight - 2)

        // Draw red segment
        g.color = Color(255, 170, 170)
        g.fillRect(barStartX + greenWidth + 1, 1, redWidth - 2, barHeight - 2)

        val percentageText = "$coveragePercentage%"
        drawText(g, percentageText, barStartX + (barWidth - getTextWidth(percentageText)) / 2)
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

        g.color = Color.BLACK // Set the color for the text
        g.drawString(text, x, y)

        return textWidth
    }
}
