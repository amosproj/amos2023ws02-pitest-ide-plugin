// SPDX-License-Identifier: MIT
// SPDX-FileCopyrightText: 2023 Lennart Heimbs <lennart@heimbs.me>

package com.amos.pitmutationmate.pitmutationmate.configuration

import com.intellij.execution.configurations.RunConfigurationOptions
import com.intellij.ide.util.PropertiesComponent
import com.intellij.openapi.components.StoredProperty

class MutationMateRunConfigurationOptions : RunConfigurationOptions() {
    private var taskNameOption: StoredProperty<String?> = string("").provideDelegate(this, "gradle.task")
    var taskName: String?
        get() = taskNameOption.getValue(this)
        set(value) {
            taskNameOption.setValue(this, value)
        }

    private val gradleExecutableOption: StoredProperty<String?> = string("").provideDelegate(this, "gradle.executable")
    var gradleExecutable: String?
        get() = gradleExecutableOption.getValue(this)
        set(value) {
            gradleExecutableOption.setValue(this, value)
        }
}
