// SPDX-FileCopyrightText: 2023 Lennart Heimbs
//
// SPDX-License-Identifier: MIT

package io.github.amosproj.pitmutationmate.override.strategy

import io.github.amosproj.pitmutationmate.override.PITConfigurationValues
import io.github.amosproj.pitmutationmate.override.PITSettingOverridePlugin
import io.github.amosproj.pitmutationmate.override.converter.NaiveTypeConverter
import io.github.amosproj.pitmutationmate.override.converter.TypeConverter
import org.gradle.api.GradleException
import org.gradle.api.Project
import org.gradle.api.logging.Logger
import org.gradle.api.logging.Logging

class GradlePitestPluginOverrideStrategy implements OverrideStrategy {
    @SuppressWarnings("FieldName")
    private final static Logger log = Logging.getLogger(PITSettingOverridePlugin)
    TypeConverter typeConverter = new NaiveTypeConverter() as TypeConverter

    @Override
    void apply(Project project, String propertyName, String overrideValue) {
        log.debug("Overriding property '$propertyName' with '$overrideValue'.")

        def pitestExtension = project.extensions.findByName("pitest")
        if (pitestExtension == null) {
            throw new GradleException("PITest extension not found. Please apply the PITest plugin first.")
        }

        if (!pitestExtension.hasProperty(propertyName)) {
            throw new GradleException("Unknown property with name '$propertyName' for pitest extension.")
        }

        PITConfigurationValues overrideFields = new PITConfigurationValues()
        def overrideProperty = overrideFields.properties.find { it.key == propertyName }
        if (overrideProperty == null) {
            throw new GradleException("Cannot override property '$propertyName' for pitest extension: Unknown Property.")
        }

        Class clazz = overrideProperty.value.getClass()
        def newValue = typeConverter.convert(overrideValue, clazz, project)
        pitestExtension.setProperty(propertyName, newValue)

        log.debug("Property '$propertyName' successfully overwritten with '$newValue'.")
    }
}

