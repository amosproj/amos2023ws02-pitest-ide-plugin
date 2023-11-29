// SPDX-FileCopyrightText: 2023 Netflix, Inc.
//
// SPDX-License-Identifier: Apache-2.0

package io.github.amosproj.pitmutationmate.override.strategy

import org.gradle.api.Project

interface OverrideStrategy {
    void apply(Project project, String propertyName, String overrideValue)
}
