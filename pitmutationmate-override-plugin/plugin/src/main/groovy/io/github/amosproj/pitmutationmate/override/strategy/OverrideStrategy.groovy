// SPDX-FileCopyrightText: 2023 Netflix, Inc.
//
// SPDX-License-Identifier: Apache-2.0

package io.github.amosproj.pitmutationmate.override.strategy

import org.gradle.api.Project

/**
 * OverrideStrategy
 *
 * This interface is responsible for applying the override value to the gradle-pitest-plugin extension.
 * It is used by the [PITSettingOverridePlugin] to apply the values of the
 * gradle-pitest-plugin extension.
 *
 * @see [PITSettingOverridePlugin]
 */
interface OverrideStrategy {

    void apply(Project project, String propertyName, String overrideValue)

}
