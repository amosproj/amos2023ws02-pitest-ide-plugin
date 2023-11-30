// SPDX-License-Identifier: MIT
// SPDX-FileCopyrightText: 2023 Lennart Heimbs <lennart@heimbs.me>

package com.amos.pitmutationmate.pitmutationmate.configuration

import com.intellij.execution.configurations.ConfigurationFactory
import com.intellij.execution.configurations.ConfigurationType
import com.intellij.execution.configurations.RunConfiguration
import com.intellij.openapi.components.BaseState
import com.intellij.openapi.project.Project

class MutationMateConfigurationFactory(type: ConfigurationType?) : ConfigurationFactory(type!!) {
    override fun getId(): String {
        return MutationMateRunConfigurationType.ID
    }

    override fun createTemplateConfiguration(project: Project): RunConfiguration {
        return MutationMateRunConfiguration(project, this, "PITmutationmate")
    }

    override fun getOptionsClass(): Class<out BaseState?> {
        return MutationMateRunConfigurationOptions::class.java
    }
}
