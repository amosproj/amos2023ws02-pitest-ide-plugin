// SPDX-License-Identifier: MIT
// SPDX-FileCopyrightText: 2023 Lennart Heimbs <lennart@heimbs.me>, Brianne Oberson

package com.amos.pitmutationmate.pitmutationmate.configuration

import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory
import com.intellij.openapi.options.SettingsEditor
import com.intellij.openapi.ui.TextFieldWithBrowseButton
import com.intellij.ui.TextFieldWithHistory
import com.intellij.ui.components.JBTextField
import com.intellij.util.ui.FormBuilder
import com.intellij.util.ui.UIUtil
import java.awt.Font
import javax.swing.JComponent
import javax.swing.JLabel
import javax.swing.JPanel

class SettingsEditor : SettingsEditor<RunConfiguration>() {
    private val myPanel: JPanel
    private val gradleTaskField: TextFieldWithHistory = TextFieldWithHistory()
    private val gradleExecutableField: TextFieldWithBrowseButton = TextFieldWithBrowseButton()
    private val targetClasses: JBTextField = JBTextField()
    private val label = JLabel("Target classes should be given as a comma-separated list (no spaces!)\nof the fully qualified names of the desired classes to test.")

    init {
        gradleTaskField.text = "pitest"
        gradleExecutableField.addBrowseFolderListener(
            "Select Gradle Script",
            null,
            null,
            FileChooserDescriptorFactory.createSingleFileDescriptor()
        )
        targetClasses.emptyText.setText("com.myproj.package1.classA,com.myproj.package1.classB,com.myproj.package2.classC,...")
        val userFont = UIUtil.getLabelFont()
        val customFont = Font(userFont.name, userFont.style, 12)
        label.font = customFont
        myPanel = FormBuilder.createFormBuilder()
            .addLabeledComponent("Gradle task", gradleTaskField)
            .addLabeledComponent("Gradle script", gradleExecutableField)
            .addLabeledComponent("Target classes", targetClasses)
            .addLabeledComponent("", getScopeTipMessage())
            .panel
    }

    private fun getScopeTipMessage(): JLabel {
        val multilineText = "<html>The scope should be given as a comma-separated list (no spaces!) of the fully qualified names<br>of the desired classes to test.</html>"
        val scopeTipMessage = JLabel(multilineText)
        scopeTipMessage.font = Font(UIUtil.getLabelFont().name, UIUtil.getLabelFont().style, 12)
        return scopeTipMessage
    }

    override fun resetEditorFrom(runConfiguration: RunConfiguration) {
        targetClasses.text = runConfiguration.classFQN
        gradleTaskField.text = runConfiguration.taskName
        runConfiguration.gradleExecutable.also { gradleExecutableField.text = it ?: "" }
    }

    override fun applyEditorTo(runConfiguration: RunConfiguration) {
        runConfiguration.classFQN = targetClasses.text
        runConfiguration.taskName = gradleTaskField.text
        runConfiguration.gradleExecutable = gradleExecutableField.text
    }

    override fun createEditor(): JComponent {
        return myPanel
    }
}
