// SPDX-FileCopyrightText: 2023 Netflix, Inc.
//
// SPDX-License-Identifier: Apache-2.0
//
// Modified by Lennart Heimbs, 2023

package io.github.amosproj.pitmutationmate.override.reader

/**
 * Parses override properties from system properties prefixed with "pitmutationmate.override.".
 *
 * Example:
 *
 * <pre>
 *     -Dpitmutationmate.override.verbose=true
 * </pre>
 */
class SystemPropertyOverrideReader implements OverrideReader {

    static final String OVERRIDE_PROPERTY_PREFIX = 'pitmutationmate.override.'

    /**
     * Parsed properties with the expected prefix.
     *
     * @return Override properties without prefix as key/value pairs
     */
    @Override
    Map<String, String> parseProperties() {
        def overrideProperties = System.properties.findAll { property ->
            property.key.startsWith(OVERRIDE_PROPERTY_PREFIX) }
        // Remove property prefix
        return overrideProperties.collectEntries { key, value -> [key - OVERRIDE_PROPERTY_PREFIX, value] }
    }

}
