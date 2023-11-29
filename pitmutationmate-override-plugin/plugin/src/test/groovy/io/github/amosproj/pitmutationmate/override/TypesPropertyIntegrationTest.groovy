// SPDX-FileCopyrightText: 2023 Netflix, Inc.
//
// SPDX-License-Identifier: Apache-2.0
//
// Modifications by: Lennart Heimbs
// - Adapt to pitest specific use case

package io.github.amosproj.pitmutationmate.override

import nebula.test.functional.internal.toolingapi.ToolingExecutionResult
import spock.lang.Unroll

class TypesPropertyIntegrationTest extends BaseOverridePluginIntegrationTest {

    @Unroll("'#commandLineParameter' should be converted to '#expectedValue'")
    def "Should allow to override Set property"() {
        given:
        buildFile << """
                class MyExtension {
                    Set targetClasses
                }
                extensions.create('pitest', MyExtension)
                assert pitest.targetClasses == null
                task checkOverriddenProperties {
                    doLast {
                        assert pitest.targetClasses == $expectedValue
                    }
                }
                """
        when:
        ToolingExecutionResult executionResult = runTasksSuccessfully(
                'checkOverriddenProperties',
                "-Dpitmutationmate.override.targetClasses=$commandLineParameter") as ToolingExecutionResult
        then:
        executionResult.standardOutput.contains(':checkOverriddenProperties')
        where:
        commandLineParameter || expectedValue
        'foo,bar'            || "['foo', 'bar'].toSet()"
        '[1,2]'              || "['1', '2'].toSet()"
        'test'               || "['test'].toSet()"
    }

    @Unroll("'#commandLineParameter' should be converted to '#expectedValue'")
    def "Should allow to override boolean pitest properties"() {
        given:
        buildFile << """
                class MyExtension {
                    boolean includeLaunchClasspath
                }
                extensions.create('pitest', MyExtension)
                assert pitest.includeLaunchClasspath == false
                task checkOverriddenProperties {
                    doLast {
                        assert pitest.includeLaunchClasspath == $expectedValue
                    }
                }
                """
        when:
        ToolingExecutionResult executionResult = runTasksSuccessfully(
                'checkOverriddenProperties',
                "-Dpitmutationmate.override.includeLaunchClasspath=$commandLineParameter") as ToolingExecutionResult
        then:
        executionResult.standardOutput.contains(':checkOverriddenProperties')
        where:
        commandLineParameter || expectedValue
        'true'               || true
        'false'              || false
        'null'               || false           // should stay false
    }

    @Unroll("'#commandLineParameter' should be converted to '#expectedValue'")
    def "Should allow to override Integer pitest properties"() {
        given:
        buildFile << """
                class MyExtension {
                    Integer threads
                }
                extensions.create('pitest', MyExtension)
                assert pitest.threads == null
                task checkOverriddenProperties {
                    doLast {
                        assert pitest.threads == $expectedValue
                    }
                }
                """
        when:
        ToolingExecutionResult executionResult = runTasksSuccessfully(
                'checkOverriddenProperties',
                "-Dpitmutationmate.override.threads=$commandLineParameter") as ToolingExecutionResult
        then:
        executionResult.standardOutput.contains(':checkOverriddenProperties')
        where:
        commandLineParameter || expectedValue
        '0'                  || 0
        '99999'              || 99999
        'null'               || 0           // should become 0
    }

    @Unroll("'#commandLineParameter' should be converted to '#expectedValue'")
    def "Should allow to override Directory pitest properties"() {
        given:
        buildFile << """
                class MyExtension {
                    Directory reportDir
                }
                extensions.create('pitest', MyExtension)
                assert pitest.reportDir == null
                task checkOverriddenProperties {
                    doLast {
                        assert pitest.reportDir.toString() == $expectedValue
                    }
                }
                """
        when:
        ToolingExecutionResult executionResult = runTasksSuccessfully(
                'checkOverriddenProperties',
                "-Dpitmutationmate.override.reportDir=$commandLineParameter") as ToolingExecutionResult
        then:
        executionResult.standardOutput.contains(':checkOverriddenProperties')
        where:
        commandLineParameter || expectedValue
        '/tmp/test'          || 'new File("/tmp/test").toString()'
        'null'               || '"null"'
    }
}
