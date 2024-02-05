// SPDX-License-Identifier: MIT
// SPDX-FileCopyrightText: 2023 Lennart Heimbs

package com.amos.pitmutationmate.pitmutationmate.visualization

import com.intellij.util.ui.UIUtil
import java.awt.BorderLayout
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.JTextPane

class ConfigurationErrorPanel(message: String?) : JPanel() {

    companion object {
        const val ID = "ErrorDialog"
        const val TITLE = "Error"
        const val DEFAULT_MESSAGE = """
            No problems found.
            Execute a mutation test to see the results.
            If there are configuration problems, they will be displayed here.
            """
    }

    init {
        updateMessage(message)
    }

    fun updateMessage(message: String?) {
        removeAll()
        val messageText = JTextPane()
        messageText.contentType = "text/html"
        messageText.isEditable = false
        messageText.alignmentX = JLabel.CENTER_ALIGNMENT
        messageText.alignmentY = JLabel.CENTER_ALIGNMENT
        messageText.background = null
        messageText.border = null
        messageText.font = UIUtil.getToolbarFont()
        messageText.text = formatMessage(message)
        add(messageText, BorderLayout.CENTER)
    }

    private fun formatMessage(message: String?): String {
        if (message.isNullOrEmpty()) {
            return formatMessage(DEFAULT_MESSAGE)
        }
        val font = UIUtil.getToolbarFont()
        val msg = """
            <html>
            <body style="font-family: '${font.family}'; font-size: 12px; margin: 15px;">
            ${message.replace("\n", "<br/>")}
            </body>
            </html>
            """
        return msg.trimIndent()
    }
}
