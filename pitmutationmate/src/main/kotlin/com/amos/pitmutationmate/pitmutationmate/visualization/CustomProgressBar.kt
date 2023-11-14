// SPDX-FileCopyrightText: 2023 2023
//
// SPDX-License-Identifier: MIT

package com.amos.pitmutationmate.pitmutationmate.visualization

import java.awt.Color
import java.awt.Graphics
import javax.swing.JComponent
import javax.swing.JPanel
import javax.swing.JProgressBar
import javax.swing.plaf.basic.BasicProgressBarUI

internal class CustomProgressBar : JPanel() {

    private val progressBar = JProgressBar(0, 100)

    init {
        //UIManager.put("ProgressBar.background", Color.ORANGE);
        //UIManager.put("ProgressBar.foreground", Color.BLUE);
        progressBar.setStringPainted(true)
        //progressBar.setValue(0)
        progressBar.setBackground(Color.RED)
        progressBar.setForeground(Color.GREEN)
        //progressBar.setString("49%")
        progressBar.setValue(35)
        progressBar.setUI(object : BasicProgressBarUI() {
            override fun paint(g: Graphics, c: JComponent) {
                c.foreground = Color.GREEN
                c.background = Color.RED
                g.color = Color.BLACK
                super.paint(g, c)
            }
        })
        this.add(progressBar)
        //this.isVisible = true;

        //UIManager.put("nimbusOrange", Color(38, 139, 210))
    }
}
