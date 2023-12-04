// SPDX-FileCopyrightText: 2023 Netflix, Inc.
//
// SPDX-License-Identifier: Apache-2.0

package io.github.amosproj.pitmutationmate.override.converter

import org.gradle.api.Project

/**
 * TypeConverter
 *
 * This interface is responsible for converting a string value to a given type.
 * It is used by the [PITSettingOverridePlugin] to convert the values of the
 * gradle-pitest-plugin extension to the correct type.
 *
 * @see [PITSettingOverridePlugin]
 */
interface TypeConverter {

    Object convert(String value, Class<?> clazz, Project project)

}
