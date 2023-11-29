// SPDX-FileCopyrightText: 2023 Netflix, Inc.
//
// SPDX-License-Identifier: Apache-2.0

package io.github.amosproj.pitmutationmate.override.converter

import org.gradle.api.Project

interface TypeConverter {
    Object convert(String value, Class<?> clazz, Project project)
}
