// SPDX-License-Identifier: MIT
// SPDX-FileCopyrightText: 2023

package com.amos.pitmutationmate.pitmutationmate.plugincheck

import org.apache.commons.io.IOUtils
import org.codehaus.groovy.ast.builder.AstBuilder
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.io.File
import java.io.FileInputStream

class PluginCheckerGroovyTest {

    private fun runPluginCheckerForTestFile(testFile: String): PluginCheckerGroovy {
        val builder = AstBuilder()
        val testBuildFile = File("src/test/resources/test_build_scripts/$testFile")
        val nodes = builder.buildFromString(
            IOUtils.toString(
                FileInputStream(testBuildFile),
                "UTF-8"
            )
        )
        val pluginCheck = PluginCheckerGroovy()
        for (node in nodes) {
            node.visit(pluginCheck)
        }
        return pluginCheck
    }

    @Test
    fun checkTestBuildFile1_groovy() {
        val pluginCheck = runPluginCheckerForTestFile("testbuild1.gradle")
        assertTrue(pluginCheck.pitestPluginAvailable)
        assertTrue(pluginCheck.companionPluginAvailable)
    }

    @Test
    fun checkTestBuildFile2_groovy() {
        val pluginCheck = runPluginCheckerForTestFile("testbuild2.gradle")
        assertFalse(pluginCheck.pitestPluginAvailable)
        assertTrue(pluginCheck.companionPluginAvailable)
    }

    @Test
    fun checkTestBuildFile3_groovy() {
        val pluginCheck = runPluginCheckerForTestFile("testbuild3.gradle")
        assertTrue(pluginCheck.pitestPluginAvailable)
        assertFalse(pluginCheck.companionPluginAvailable)
    }

    @Test
    fun checkTestBuildFile4_groovy() {
        val pluginCheck = runPluginCheckerForTestFile("testbuild4.gradle")
        assertFalse(pluginCheck.pitestPluginAvailable)
        assertFalse(pluginCheck.companionPluginAvailable)
    }
}
