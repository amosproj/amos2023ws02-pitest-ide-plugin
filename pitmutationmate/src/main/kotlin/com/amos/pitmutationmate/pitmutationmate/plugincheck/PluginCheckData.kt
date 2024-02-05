// SPDX-License-Identifier: MIT
// SPDX-FileCopyrightText: 2024

package com.amos.pitmutationmate.pitmutationmate.plugincheck

import kotlinx.serialization.Serializable

@Serializable
data class PluginCheckData(
    val pitestPluginApplied: Boolean,
    val androidPitestPluginApplied: Boolean,
    val androidPluginApplied: Boolean,
    val androidBuildTypes: List<String>
)
