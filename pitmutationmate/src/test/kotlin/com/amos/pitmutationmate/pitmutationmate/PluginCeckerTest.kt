// SPDX-License-Identifier: MIT
// SPDX-FileCopyrightText: 2023

package com.amos.pitmutationmate.pitmutationmate

import com.amos.pitmutationmate.pitmutationmate.plugincheck.PluginCheckerGroovy
import org.apache.commons.io.IOUtils
import org.codehaus.groovy.ast.builder.AstBuilder
import org.junit.jupiter.api.Test
import java.io.File
import java.io.FileInputStream

class PluginCeckerTest {

    private fun runPluginCheckerForTestFile(testFile: String): PluginCheckerGroovy {
        val builder = AstBuilder()
        val testFile = File("src/test/resources/test_build_scripts/$testFile")
        val nodes = builder.buildFromString(
            IOUtils.toString(FileInputStream(testFile),
                "UTF-8")
        )
        val pluginCheck = PluginCheckerGroovy()
        for(node in nodes) {
            node.visit(pluginCheck)
        }
        return pluginCheck
    }

    @Test
    fun checkTestBuildFile1_groovy() {
        val pluginCheck = runPluginCheckerForTestFile("testbuild1.gradle")
        assert(pluginCheck.pitestPluginAvailable)
        assert(pluginCheck.companionPluginAvailable)
    }

    @Test
    fun checkTestBuildFile2_groovy() {
        val pluginCheck = runPluginCheckerForTestFile("testbuild2.gradle")
        assert(!pluginCheck.pitestPluginAvailable)
        assert(pluginCheck.companionPluginAvailable)
    }

    @Test
    fun checkTestBuildFile3_groovy() {
        val pluginCheck = runPluginCheckerForTestFile("testbuild3.gradle")
        assert(pluginCheck.pitestPluginAvailable)
        assert(!pluginCheck.companionPluginAvailable)
    }

    @Test
    fun checkTestBuildFile4_groovy() {
        val pluginCheck = runPluginCheckerForTestFile("testbuild4.gradle")
        assert(!pluginCheck.pitestPluginAvailable)
        assert(!pluginCheck.companionPluginAvailable)
    }
}
