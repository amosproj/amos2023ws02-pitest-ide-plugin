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
import org.gradle.internal.component.model.ConfigurationNotFoundException

/**
 * GradlePitestPluginOverrideStrategy
 *
 * This class is responsible for overriding the properties of the gradle-pitest-plugin.
 * It is used by the [PITSettingOverridePlugin] to override the values of the
 * gradle-pitest-plugin extension.
 *
 * @see [PITSettingOverridePlugin]
 */
class GradlePitestPluginOverrideStrategy implements OverrideStrategy {

    static final String OVERRIDE_SECTION = 'pitest'

    @SuppressWarnings('FieldName')
    private final static Logger log = Logging.getLogger(PITSettingOverridePlugin)
    TypeConverter typeConverter = new NaiveTypeConverter() as TypeConverter

    @Override
    void apply(Project project, String propertyName, String overrideValue) {
        log.debug("Overriding property '$propertyName' with '$overrideValue'.")
        def pitestExtension = project.extensions.findByName(OVERRIDE_SECTION)
        def projIter = project.subprojects.iterator()
        def subproject = project
        while (pitestExtension == null && projIter.hasNext()) {
            subproject = projIter.next()
            pitestExtension = subproject.extensions.findByName(OVERRIDE_SECTION)
        }

        if (pitestExtension == null) {
            throw new GradleException("PITest extension not found. Please apply the PITest plugin first.")
        }
        if (propertyName == "addCoverageListenerDependency") {
            project.gradle.allprojects {
                try {
                    it.dependencies.add('pitest', overrideValue)
                } catch (ConfigurationNotFoundException e) {
                    addPitestDependency(it, overrideValue)
                    log.debug('Tried to add the dependency directly to project.dependencies ' + e)
                }
            }
            return
        }

        if (!pitestExtension.hasProperty(propertyName)) {
            throw new GradleException("Unknown property with name '$propertyName' for pitest extension.")
        }

        PITConfigurationValues overrideFields = new PITConfigurationValues()
        def overrideProperty = overrideFields.properties.find { it.key == propertyName }
        if (overrideProperty == null) {
            throw new GradleException(
                "Cannot override property '$propertyName' for pitest extension: Unknown Property.")
        }

        Class clazz = overrideProperty.value.getClass()
        def newValue = typeConverter.convert(overrideValue, clazz, project)
        pitestExtension.setProperty(propertyName, newValue)

        log.debug("Property '$propertyName' successfully overwritten with '$newValue'.")
    }

    void addPitestDependency(Project project, String overrideValue) {
        try {
            project.subprojects {
                buildscript {
                    dependencies.add('pitest', overrideValue)
                }
            }
        } catch (ConfigurationNotFoundException e) {
            log.debug('Adding the dependency to all subprojects in buildscript.dependencies failed. ' +
                    'So the ConfigurationName "pitest" was not found.' +
                    'Most probably this means that the pitest-plugin is not included.' + e)
        }
    }
}
