// SPDX-FileCopyrightText: 2023 Lennart Heimbs
//
// SPDX-License-Identifier: MIT

package io.github.amosproj.pitmutationmate.override.strategy

import io.github.amosproj.pitmutationmate.override.PITSettingOverridePlugin
import org.gradle.api.GradleException
import org.gradle.api.Project
import org.gradle.api.logging.Logger
import org.gradle.api.logging.Logging

/**
 * DependencyInclusionStrategy
 *
 * This class is responsible for adding a dependency to the pitest plugin extension.
 * It is used by the [PITSettingOverridePlugin].
 *
 * @see [ PITSettingOverridePlugin ]
 */
class DependencyInclusionStrategy implements OverrideStrategy {

    public static final String OVERRIDE_ATTRIBUTE = 'addCoverageListenerDependency'
    static final String PITEST_EXTENSION = 'pitest'
    static final String PITEST_PLUGIN = 'info.solidsoft.pitest'
    static final String PITEST_PLUGIN_ANDROID = 'pl.droidsonroids.pitest'

    @SuppressWarnings('FieldName')
    private final static Logger log = Logging.getLogger(PITSettingOverridePlugin)

    @Override
    void apply(Project proj, String propertyName, String overrideValue) {
        log.debug("Trying to add dependency '$overrideValue'.")

        boolean isApplied = false
        proj.allprojects {
            Project project = it
            def pitestPlugin = getPlugin(project, PITEST_PLUGIN)
            def androidPitestPlugin = getPlugin(project, PITEST_PLUGIN_ANDROID)

            if (pitestPlugin != null) {
                log.debug("Adding dependency for detected Pitest Plugin $PITEST_PLUGIN")
                project.dependencies.add(PITEST_EXTENSION, overrideValue)
                isApplied = true
            } else if (androidPitestPlugin != null) {
                log.debug("Adding dependency for detected Pitest Plugin $PITEST_PLUGIN_ANDROID")
                project.subprojects {
                    buildscript {
                        dependencies.add(PITEST_EXTENSION, overrideValue)
                    }
                }
                isApplied = true
            } else {
                log.info('No Pitest Plugin detected')
                return
            }
            log.info("Successfully added dependency '$overrideValue' to project ${project.name}.")
        }
        if (!isApplied) {
            throw new GradleException('PITest plugin not found. Please apply the PITest plugin first.')
        }
    }

    /**
     * Find a plugin in the project or its subprojects.
     * @param project : The project to search in.
     * @param pluginId : The id of the plugin to search for.
     * @return The plugin if found, null otherwise.
     */
    private static Object getPlugin(Project project, String pluginId) {
        def plugin = project.plugins.findPlugin(pluginId)
        def projectIterator = project.subprojects.iterator()
        def subproject
        while (plugin == null && projectIterator.hasNext()) {
            subproject = projectIterator.next()
            plugin = subproject.plugins.findPlugin(pluginId)
        }
        return plugin
    }

}
