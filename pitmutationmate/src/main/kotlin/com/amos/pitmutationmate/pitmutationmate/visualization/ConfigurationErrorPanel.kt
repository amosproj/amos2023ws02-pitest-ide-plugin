// SPDX-License-Identifier: MIT
// SPDX-FileCopyrightText: 2023 Lennart Heimbs

package com.amos.pitmutationmate.pitmutationmate.visualization

import com.intellij.util.ui.UIUtil
import java.awt.BorderLayout
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.JTextPane

class ConfigurationErrorPanel(message: String) : JPanel() {

    companion object {
        const val ID = "ErrorDialog"
    }

    init {
        layout = BorderLayout()

        val messageText = JTextPane()
        messageText.contentType = "text/html"
        messageText.isEditable = false
        messageText.alignmentX = JLabel.CENTER_ALIGNMENT
        messageText.alignmentY = JLabel.CENTER_ALIGNMENT
        messageText.background = null
        messageText.border = null
        messageText.font = UIUtil.getToolbarFont()
        messageText.text = message
        add(messageText, BorderLayout.CENTER)
    }
}
