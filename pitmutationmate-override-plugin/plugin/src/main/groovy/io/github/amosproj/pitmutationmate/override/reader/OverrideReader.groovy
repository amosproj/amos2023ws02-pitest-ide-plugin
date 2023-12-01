// SPDX-FileCopyrightText: 2023 Netflix, Inc.
//
// SPDX-License-Identifier: Apache-2.0

package io.github.amosproj.pitmutationmate.override.reader

/**
 * OverrideReader
 *
 * This interface is responsible for reading the override properties.
 * It is used by the [PITSettingOverridePlugin] to read the values of the
 * gradle-pitest-plugin extension.
 *
 * @see [PITSettingOverridePlugin]
 */
interface OverrideReader {

    Map<String, String> parseProperties()

}
