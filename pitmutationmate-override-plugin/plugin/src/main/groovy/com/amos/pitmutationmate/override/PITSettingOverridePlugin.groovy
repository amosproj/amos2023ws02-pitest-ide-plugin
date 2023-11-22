/*
 * This Groovy source file was generated by the Gradle 'init' task.
 */
package com.amos.pitmutationmate.override


import com.amos.pitmutationmate.override.reader.OverrideReader
import com.amos.pitmutationmate.override.reader.SystemPropertyOverrideReader
import com.amos.pitmutationmate.override.strategy.GradlePitestPluginOverrideStrategy
import com.amos.pitmutationmate.override.strategy.OverrideStrategy
import org.gradle.api.Project
import org.gradle.api.Plugin

/**
 * Partner Plugin to the PITmutationmate Plugin to allow overriding of specific PITest configuration values.
 */
class PITSettingOverridePlugin implements Plugin<Project> {
    void apply(Project project) {
        OverrideReader overrideReader = new SystemPropertyOverrideReader()
        def overrideProperties = overrideReader.parseProperties()

        project.afterEvaluate {
            List<UnknownPropertyException> upes = new ArrayList<UnknownPropertyException>();
            overrideProperties.each { propertyName, overrideValue ->
                project.logger.info "Overriding property '$propertyName' with '$overrideValue'."
                OverrideStrategy gradlePitestPluginOverrideStrategy = new GradlePitestPluginOverrideStrategy()
                try {
                    gradlePitestPluginOverrideStrategy.apply(project, propertyName, overrideValue)
                } catch(UnknownPropertyException upe) {
                    upes.add(upe)
                }
            }
            upes.each { upe ->
                project.logger.warn(upe.getMessage());
            }
        }
    }
}
