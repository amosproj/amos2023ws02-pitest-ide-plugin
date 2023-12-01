// SPDX-FileCopyrightText: 2023 Lennart Heimbs
//
// SPDX-License-Identifier: MIT

package io.github.amosproj.pitmutationmate.override

import org.gradle.api.file.Directory

/**
 * Configuration values for the PIT plugin.
 */
class PITConfigurationValues {

    Integer threads = 4
    boolean verbose = true
    boolean includeLaunchClasspath = true
    boolean timestampedReports = true
    Set<String> targetClasses = ['test1', 'test2']
    Set<String> targetTests = ['test3', 'test4']
    Set<String> outputFormats = ['XML', 'HTML']
    Directory reportDir = new File('build/reports/pitest') as Directory     // groovylint-disable-line

}
