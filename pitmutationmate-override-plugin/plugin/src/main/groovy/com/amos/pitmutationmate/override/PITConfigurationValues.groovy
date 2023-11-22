package com.amos.pitmutationmate.override

import org.gradle.api.file.Directory

class PITConfigurationValues {
    boolean includeLaunchClasspath = true
    Directory reportDir = new File("build/reports/pitest") as Directory
    Set<String> targetClasses = [ "test", "test2" ]
    Set<String> targetTests = [ "test", "test2" ]
    boolean timestampedReports = true
    boolean verbose = true
    Set<String> outputFormats = [ "XML", "HTML" ]
    Integer threads = 4

    List<String> exampleList = [ "test", "test2" ]
}
