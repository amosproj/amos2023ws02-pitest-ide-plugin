// SPDX-FileCopyrightText: 2023 Netflix, Inc.
//
// SPDX-License-Identifier: Apache-2.0
//
// Modifications by: Lennart Heimbs
// - Adapt to pitest specific use case

package io.github.amosproj.pitmutationmate.override.strategy

import io.github.amosproj.pitmutationmate.override.PITConfigurationValues
import nebula.test.ProjectSpec
import org.gradle.api.GradleException

class GradlePitestPluginOverrideStrategyTest extends ProjectSpec {
    OverrideStrategy overrideStrategy = new GradlePitestPluginOverrideStrategy()
    def "Throws exception if pitest extension cannot be resolved"() {
        when:
        overrideStrategy.apply(project, 'doesntExist', 'test')

        then:
        Throwable t = thrown(GradleException)
        t.message == "PITest extension not found. Please apply the PITest plugin first."
    }

   def "Throws exception if extension does not have property"() {
       given:
       project.extensions.add('pitest', new Object())

       when:
       overrideStrategy.apply(project, 'doesntExist', 'test')

       then:
       Throwable t = thrown(GradleException)
       t.message == "Unknown property with name 'doesntExist' for pitest extension."
   }

   def "Throws exception if PITConfigurationValues does not have property"() {
       given:
       project.extensions.create('pitest', PitestSampleFailingExtension)

       when:
       overrideStrategy.apply(project, 'doesntExist', 'test')

       then:
       Throwable t = thrown(GradleException)
       t.message == "Cannot override property 'doesntExist' for pitest extension: Unknown Property."
   }

   def "Can override pitest property threads"() {
       given:
       project.extensions.create('pitest', PITConfigurationValues)
       assert project.pitest.threads == 4

       when:
       overrideStrategy.apply(project, 'threads', '8')

       then:
       project.pitest.threads == 8
   }

   def "Can override pitest property verbose"() {
       given:
       project.extensions.create('pitest', PITConfigurationValues)
       assert project.pitest.verbose == true

       when:
       overrideStrategy.apply(project, 'verbose', 'false')

       then:
       project.pitest.verbose == false
   }

   def "Can override pitest property includeLaunchClasspath"() {
       given:
       project.extensions.create('pitest', PITConfigurationValues)
       assert project.pitest.includeLaunchClasspath == true

       when:
       overrideStrategy.apply(project, 'includeLaunchClasspath', 'false')

       then:
       project.pitest.includeLaunchClasspath == false
   }

   def "Can override pitest property timestampedReports"() {
       given:
       project.extensions.create('pitest', PITConfigurationValues)
       assert project.pitest.timestampedReports == true

       when:
       overrideStrategy.apply(project, 'timestampedReports', 'false')

       then:
       project.pitest.timestampedReports == false
   }

   def "Can override pitest property targetClasses"() {
        given:
        OverrideStrategy overrideStrategy = new GradlePitestPluginOverrideStrategy()
        project.extensions.create('pitest', PITConfigurationValues)
        assert project.pitest.targetClasses == [ "test1", "test2" ].toSet()

       when:
       overrideStrategy.apply(project, 'targetClasses', 'good1,good2')

       then:
       project.pitest.targetClasses == ['good1', 'good2'].toSet()
   }

   def "Can override pitest property targetTests"() {
        given:
        OverrideStrategy overrideStrategy = new GradlePitestPluginOverrideStrategy()
        project.extensions.create('pitest', PITConfigurationValues)
        assert project.pitest.targetTests == [ "test3", "test4" ].toSet()

       when:
       overrideStrategy.apply(project, 'targetTests', 'good1,good2')

       then:
       project.pitest.targetTests == ['good1', 'good2'].toSet()
   }

   def "Can override pitest property outputFormats"() {
       given:
       project.extensions.create('pitest', PITConfigurationValues)
       assert project.pitest.outputFormats == [ "XML", "HTML" ].toSet()

       when:
       overrideStrategy.apply(project, 'outputFormats', 'good1,good2')

       then:
       project.pitest.outputFormats == ['good1', 'good2'].toSet()
   }

   def "Can override pitest property reportDir"() {
       given:
       project.extensions.create('pitest', PITConfigurationValues)
       def givenDir = new File("build/reports/pitest") as Directory
       def newDir = new File("/tmp/test") as Directory
       assert project.pitest.reportDir.absolutePath == givenDir.absolutePath

       when:
       overrideStrategy.apply(project, 'reportDir', '/tmp/test')

       then:
       project.pitest.reportDir.toString() == newDir.absolutePath
   }
}

class PitestSampleFailingExtension {
    String doesntExist
}