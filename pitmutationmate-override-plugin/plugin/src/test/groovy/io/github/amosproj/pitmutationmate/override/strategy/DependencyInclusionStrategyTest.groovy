// SPDX-FileCopyrightText: 2023 Lennart Heimbs
//
// SPDX-License-Identifier: MIT

package io.github.amosproj.pitmutationmate.override.strategy

import nebula.test.ProjectSpec
import org.gradle.api.GradleException

/**
 * Tests for [DependencyInclusionStrategy].
 * Cannot test more since adding plugins does not seem to work in tests.
 */
class DependencyInclusionStrategyTest extends ProjectSpec {

    def "Throws exception if pitest plugin cannot be resolved"() {
        given:
        OverrideStrategy overrideStrategy = new DependencyInclusionStrategy()

        when:
        overrideStrategy.apply(project, 'doesntExist', 'test')

        then:
        Throwable t = thrown(GradleException)
        t.message == "PITest plugin not found. Please apply the PITest plugin first."
    }

}
