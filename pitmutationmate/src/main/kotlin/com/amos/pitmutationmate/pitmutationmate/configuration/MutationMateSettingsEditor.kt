// SPDX-License-Identifier: MIT
// SPDX-FileCopyrightText: 2023 Lennart Heimbs <lennart@heimbs.me>

package com.amos.pitmutationmate.pitmutationmate.configuration

import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory
import com.intellij.openapi.options.SettingsEditor
import com.intellij.openapi.ui.TextFieldWithBrowseButton
import com.intellij.ui.TextFieldWithHistory
import com.intellij.util.ui.FormBuilder
import javax.swing.JComponent
import javax.swing.JPanel

class MutationMateSettingsEditor : SettingsEditor<MutationMateRunConfiguration>() {
    private val myPanel: JPanel
    private val gradleTaskField: TextFieldWithHistory = TextFieldWithHistory()
    private val gradleExecutableField: TextFieldWithBrowseButton = TextFieldWithBrowseButton()

    init {
        gradleTaskField.text = "pitest"
        gradleExecutableField.addBrowseFolderListener("Select Gradle Script", null, null,
            FileChooserDescriptorFactory.createSingleFileDescriptor());
        myPanel = FormBuilder.createFormBuilder()
            .addLabeledComponent("Gradle task", gradleTaskField)
            .addLabeledComponent("Gradle script", gradleExecutableField)
            .panel
    }

    override fun resetEditorFrom(runConfiguration: MutationMateRunConfiguration) {
        gradleTaskField.text = runConfiguration.taskName
        runConfiguration.gradleExecutable.also { gradleExecutableField.text = it }
    }

    override fun applyEditorTo(runConfiguration: MutationMateRunConfiguration) {
        runConfiguration.taskName = gradleTaskField.text
        runConfiguration.gradleExecutable = gradleExecutableField.text
    }

    override fun createEditor(): JComponent {
        return myPanel
    }
}
