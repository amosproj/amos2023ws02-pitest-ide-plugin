package com.amos.pitmutationmate.override

import nebula.test.IntegrationSpec

abstract class BaseOverridePluginIntegrationTest extends IntegrationSpec {

    def setup() {
        buildFile << """
        apply plugin: 'com.amos.pitmutationmate.override'
        """
    }
}
