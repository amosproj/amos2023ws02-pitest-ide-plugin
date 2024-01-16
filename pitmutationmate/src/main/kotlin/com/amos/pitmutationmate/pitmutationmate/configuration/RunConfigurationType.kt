// SPDX-License-Identifier: MIT
// SPDX-FileCopyrightText: 2023 Lennart Heimbs <lennart@heimbs.me>

package com.amos.pitmutationmate.pitmutationmate.configuration

import com.amos.pitmutationmate.pitmutationmate.icons.Icons
import com.intellij.execution.configurations.ConfigurationTypeBase
import com.intellij.openapi.util.NotNullLazyValue

internal class RunConfigurationType : ConfigurationTypeBase(
    ID,
    "PITmutationmate",
    "PITmutationmate run configuration type",
    NotNullLazyValue.createValue { Icons.Logo16 }
) {
    init {
        addFactory(ConfigurationFactory(this))
    }

    companion object {
        const val ID = "MutationMateRunConfiguration"
    }
}
