// SPDX-FileCopyrightText: 2023 Lennart Heimbs
//
// SPDX-License-Identifier: MIT

package io.github.amosproj.pitmutationmate.override

import io.github.amosproj.pitmutationmate.override.reader.OverrideReader
import io.github.amosproj.pitmutationmate.override.reader.SystemPropertyOverrideReader
import io.github.amosproj.pitmutationmate.override.strategy.GradlePitestPluginOverrideStrategy
import io.github.amosproj.pitmutationmate.override.strategy.OverrideStrategy
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.logging.Logger
import org.gradle.api.logging.Logging

/**
 * Partner Plugin to the PITmutationmate Plugin to allow overriding of specific PITest configuration values.
 */
class PITSettingOverridePlugin implements Plugin<Project> {

    @SuppressWarnings("FieldName")
    private final static Logger log = Logging.getLogger(PITSettingOverridePlugin)

    void apply(Project project) {
        OverrideReader overrideReader = new SystemPropertyOverrideReader()
        def overrideProperties = overrideReader.parseProperties()
        log.debug("Found override properties: $overrideProperties")

        project.afterEvaluate {
            overrideProperties.each { propertyName, overrideValue ->
                project.logger.info "Overriding property '$propertyName' with '$overrideValue'."
                OverrideStrategy gradlePitestPluginOverrideStrategy = new GradlePitestPluginOverrideStrategy()
                gradlePitestPluginOverrideStrategy.apply(project, propertyName, overrideValue)
            }
        }
    }

}
