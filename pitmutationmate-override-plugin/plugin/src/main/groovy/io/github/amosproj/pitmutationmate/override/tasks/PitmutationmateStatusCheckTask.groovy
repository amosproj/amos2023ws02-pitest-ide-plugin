// SPDX-FileCopyrightText: 2023-2024
//
// SPDX-License-Identifier: MIT

package io.github.amosproj.pitmutationmate.override.tasks

import groovy.json.JsonOutput
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

/**
 * Task to check the status of plugins needed for PITmutationmate intellij plugin.
 */
class PitmutationmateStatusCheckTask extends DefaultTask {

    public final static String TASK_NAME = "pitmutationmateStatusCheck"
    private final static String PITEST_PLUGIN_NAME = "info.solidsoft.pitest"
    private final static String ANDROID_PITEST_PLUGIN_NAME = "pl.droidsonroids.pitest"
    private final static String ANDROID_PLUGIN_NAME = "com.android.application"
    private boolean isPitestPluginApplied = false
    private boolean isAndroidPitestPluginApplied = false
    private boolean isAndroidPluginApplied = false
    private String[] androidBuildTypes = []

    /**
     * Check the status of the plugins.
     */
    private void checkPluginStatusInternal() {
        isPitestPluginApplied = project.plugins.hasPlugin(PITEST_PLUGIN_NAME)
        isAndroidPitestPluginApplied = project.plugins.hasPlugin(ANDROID_PITEST_PLUGIN_NAME)
        isAndroidPluginApplied = project.plugins.hasPlugin(ANDROID_PLUGIN_NAME)

        def projectIterator = project.subprojects.iterator()
        def subproject
        while (projectIterator.hasNext()) {
            subproject = projectIterator.next()
            if (subproject.plugins.hasPlugin(PITEST_PLUGIN_NAME)) {
                isPitestPluginApplied = true
            }
            if (subproject.plugins.hasPlugin(ANDROID_PITEST_PLUGIN_NAME)) {
                isAndroidPitestPluginApplied = true
            }
            if (subproject.plugins.hasPlugin(ANDROID_PLUGIN_NAME)) {
                isAndroidPluginApplied = true
            }
        }
    }

    /**
     * Check the build types of the android plugin.
     */
    private void checkAndroidBuildTypeInternal() {
        def extension = project.extensions.findByName("android")
        def projectIterator = project.subprojects.iterator()
        def subproject
        while (extension == null && projectIterator.hasNext()) {
            subproject = projectIterator.next()
            extension = subproject.extensions.findByName("android")
        }

        if (extension != null) {
            def buildTypes = extension.getProperty('buildTypes')
            if (buildTypes != null) {
                androidBuildTypes = buildTypes.collect { it.name }
            }
        }
    }

    /**
     * Print the status of the plugins.
     */
    private void printStatus() {
        String json = JsonOutput.toJson([
            pitestPluginApplied       : isPitestPluginApplied,
            androidPitestPluginApplied: isAndroidPitestPluginApplied,
            androidPluginApplied      : isAndroidPluginApplied,
            androidBuildTypes         : androidBuildTypes
        ])
        String prettyJson = JsonOutput.prettyPrint(json)
        println prettyJson
    }

    /**
     * Check the status of the plugins.
     */
    @TaskAction
    void checkStatus() {
        checkPluginStatusInternal()
        checkAndroidBuildTypeInternal()
        printStatus()
    }

}
