package com.amos.pitmutationmate.override.reader

/**
 * Parses override properties from system properties prefixed with "override.".
 *
 * Example:
 *
 * <pre>
 *     -Dpitmutationmate.override.example.test=replace
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
        def overrideProperties = System.properties.findAll { it.key.startsWith(OVERRIDE_PROPERTY_PREFIX) }
        // Remove property prefix
        overrideProperties.collectEntries { key, value -> [key - OVERRIDE_PROPERTY_PREFIX, value] }
    }
}