package com.amos.pitmutationmate.override

import nebula.test.functional.internal.toolingapi.ToolingExecutionResult
import spock.lang.Unroll

class ComplexTypePropertyIntegrationTest extends BaseOverridePluginIntegrationTest {

    @Unroll("'#commandLineParameter' should be converted to '#expectedValue'")
    def "Should allow to override List property"() {
        given:
        buildFile << """
                class MyExtension {
                    List exampleList
                }
                extensions.create('example', MyExtension)
                assert example.exampleList == null
                task checkOverridenProperties {
                    doLast {
                        assert example.exampleList == $expectedValue
                    }
                }
                """
        when:
        ToolingExecutionResult executionResult = runTasksSuccessfully(
                'checkOverridenProperties',
                "-Dpitmutationmate.override.example.exampleList=$commandLineParameter") as ToolingExecutionResult
        then:
        executionResult.standardOutput.contains(':checkOverridenProperties')
        where:
        commandLineParameter || expectedValue
        'foo,bar'            || "['foo', 'bar']"
        '[1,2]'              || "['1', '2']"
        'test'               || "['test']"
    }

    @Unroll("'#commandLineParameter' should be converted to '#expectedValue'")
    def "Should allow to override Set property"() {
        given:
        buildFile << """
                class MyExtension {
                    Set exampleList
                }
                extensions.create('example', MyExtension)
                assert example.exampleList == null
                task checkOverridenProperties {
                    doLast {
                        assert example.exampleList == $expectedValue
                    }
                }
                """
        when:
        ToolingExecutionResult executionResult = runTasksSuccessfully(
                'checkOverridenProperties',
                "-Dpitmutationmate.override.example.exampleList=$commandLineParameter") as ToolingExecutionResult
        then:
        executionResult.standardOutput.contains(':checkOverridenProperties')
        where:
        commandLineParameter || expectedValue
        'foo,bar'            || "['foo', 'bar'].toSet()"
        '[1,2]'              || "['1', '2'].toSet()"
        'test'               || "['test'].toSet()"
    }
}
