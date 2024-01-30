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
import javax.swing.JCheckBox
import javax.swing.JComponent
import javax.swing.JLabel
import javax.swing.JPanel

class SettingsEditor : SettingsEditor<RunConfiguration>() {
    private val myPanel: JPanel
    private val gradleTaskField: TextFieldWithHistory = TextFieldWithHistory()
    private val gradleExecutableField: TextFieldWithBrowseButton = TextFieldWithBrowseButton()
    private val targetClasses: JBTextField = JBTextField()
    private val buildTypesField: JBTextField = JBTextField()
    private val verboseCheckbox = JCheckBox("Enable Pitest verbose mode")

    init {
        gradleTaskField.text = "pitest"
        gradleExecutableField.addBrowseFolderListener(
            "Select Gradle Script",
            null,
            null,
            FileChooserDescriptorFactory.createSingleFileDescriptor()
        )
        targetClasses.emptyText.setText("com.myproj.package1.classA,com.myproj.package1.classB,com.myproj.package2.classC,...")
        buildTypesField.emptyText.setText("<none>")
        myPanel = FormBuilder.createFormBuilder()
            .addLabeledComponent("Gradle task", gradleTaskField)
            .addLabeledComponent("Gradle script", gradleExecutableField)
            .addLabeledComponent("Android build type", buildTypesField)
            .addLabeledComponent("", getBuildTypesMessage())
            .addLabeledComponent("Pitest Verbose mode", verboseCheckbox)
            .addLabeledComponent("Target classes", targetClasses)
            .addLabeledComponent("", getScopeTipMessage())
            .panel
    }

    private fun getScopeTipMessage(): JLabel {
        val multilineText = "<html>The scope should be given as a comma-separated list (no spaces!)<br/>of the fully qualified names of the desired classes to test.</html>"
        val scopeTipMessage = JLabel(multilineText)
        scopeTipMessage.font = Font(UIUtil.getLabelFont().name, UIUtil.getLabelFont().style, 12)
        return scopeTipMessage
    }

    private fun getBuildTypesMessage(): JLabel {
        val multilineText = "<html>The Android build type of which you want to use for the pitest results from.<br/>If kept empty, <c>debug</c> is tried first and last no build sub-path is used.</html>"
        val scopeTipMessage = JLabel(multilineText)
        scopeTipMessage.font = Font(UIUtil.getLabelFont().name, UIUtil.getLabelFont().style, 12)
        return scopeTipMessage
    }

    fun checkDefault(runConfiguration: RunConfiguration) {
        if (runConfiguration.isDefault) {
            targetClasses.isEditable = false
        }
    }

    override fun resetEditorFrom(runConfiguration: RunConfiguration) {
        targetClasses.text = runConfiguration.classFQN
        gradleTaskField.text = runConfiguration.taskName
        runConfiguration.gradleExecutable.also { gradleExecutableField.text = it ?: "" }
        buildTypesField.text = runConfiguration.buildType
        verboseCheckbox.isSelected = runConfiguration.verbose
    }

    override fun applyEditorTo(runConfiguration: RunConfiguration) {
        runConfiguration.classFQN = targetClasses.text
        runConfiguration.taskName = gradleTaskField.text
        runConfiguration.gradleExecutable = gradleExecutableField.text
        runConfiguration.buildType = buildTypesField.text
        runConfiguration.verbose = verboseCheckbox.isSelected
    }

    override fun createEditor(): JComponent {
        return myPanel
    }
}
