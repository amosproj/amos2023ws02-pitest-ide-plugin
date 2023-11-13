// SPDX-License-Identifier: MIT
// SPDX-FileCopyrightText: 2023 Lennart Heimbs <lennart@heimbs.me>

package com.amos.pitmutationmate.pitmutationmate.configuration

import com.intellij.execution.configurations.ConfigurationTypeBase
import com.intellij.icons.AllIcons
import com.intellij.openapi.util.NotNullLazyValue

internal class MutationMateRunConfigurationType : ConfigurationTypeBase(
    ID, "PITmutationmate", "PITmutationmate run configuration type",
    NotNullLazyValue.createValue { AllIcons.Nodes.Console }) {
    init {
        addFactory(MutationMateConfigurationFactory(this))
    }

    companion object {
        const val ID = "MutationMateRunConfiguration"
    }
}
