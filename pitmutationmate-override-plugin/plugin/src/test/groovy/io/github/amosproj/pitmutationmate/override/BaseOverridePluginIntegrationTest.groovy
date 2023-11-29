// SPDX-FileCopyrightText: 2023 Netflix, Inc.
//
// SPDX-License-Identifier: Apache-2.0

package io.github.amosproj.pitmutationmate.override

import nebula.test.IntegrationSpec

abstract class BaseOverridePluginIntegrationTest extends IntegrationSpec {

    def setup() {
        buildFile << """
        apply plugin: 'io.github.amosproj.pitmutationmate.override'
        """
    }
}
