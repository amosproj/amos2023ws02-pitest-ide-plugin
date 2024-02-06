// SPDX-License-Identifier: MIT
// SPDX-FileCopyrightText: 2023

package com.amos.pitmutationmate.pitmutationmate.ui

import com.intellij.openapi.ui.TextFieldWithBrowseButton
import com.intellij.ui.components.JBTextField
import com.intellij.uiDesigner.core.GridConstraints
import com.intellij.uiDesigner.core.GridLayoutManager
import com.intellij.util.ui.UIUtil
import java.awt.Dimension
import javax.swing.JComponent
import javax.swing.JLabel
import javax.swing.JPanel

class RunConfigFormBuilder {

    private var lineCount: Int = 0
    private var components = ArrayList<JComponent>()
    private var constraints = ArrayList<GridConstraints>()

    fun addLabeledComponent(labelText: String, component: JComponent): RunConfigFormBuilder {
        val label = createLabelForComponent(labelText, component)
        return addLabeledComponent(label, component)
    }

    private fun getFill(component: JComponent): Int {
        return if (component is JBTextField || component is TextFieldWithBrowseButton) {
            GridConstraints.FILL_HORIZONTAL
        } else {
            GridConstraints.FILL_NONE
        }
    }

    private fun getSizePolicy(component: JComponent): Int {
        return if (component is JBTextField || component is TextFieldWithBrowseButton) {
            GridConstraints.SIZEPOLICY_WANT_GROW
        } else {
            GridConstraints.SIZEPOLICY_FIXED
        }
    }

    private fun addLabeledComponent(label: JComponent, component: JComponent): RunConfigFormBuilder {
        val c = GridConstraints()

        c.row = lineCount
        c.column = 0
        c.rowSpan = 1
        c.colSpan = 1
        c.anchor = GridConstraints.ANCHOR_WEST
        c.fill = GridConstraints.FILL_NONE
        c.hSizePolicy = GridConstraints.SIZEPOLICY_FIXED
        c.vSizePolicy = GridConstraints.SIZEPOLICY_FIXED

        components.add(label)
        constraints.add(c)

        components.add(component)
        constraints.add(
            GridConstraints(
                c.row,
                1,
                c.rowSpan,
                c.colSpan,
                c.anchor,
                getFill(component),
                getSizePolicy(component),
                c.vSizePolicy,
                null,
                getPreferredSize(component),
                null
            )
        )

        lineCount++
        return this
    }

    private fun getPreferredSize(component: JComponent): Dimension? {
        return if (component is JBTextField) Dimension(150, -1) else null
    }

    fun buildPanel(): JPanel {
        val panel = JPanel(GridLayoutManager(lineCount, 2))
        for (i in 0 until components.size) {
            panel.add(components[i], constraints[i])
        }
        return panel
    }

    companion object {
        fun createFormBuilder(): RunConfigFormBuilder {
            return RunConfigFormBuilder()
        }

        private fun createLabelForComponent(labelText: String, component: JComponent): JLabel {
            val label = JLabel(UIUtil.replaceMnemonicAmpersand(labelText))
            label.labelFor = component
            return label
        }
    }
}
