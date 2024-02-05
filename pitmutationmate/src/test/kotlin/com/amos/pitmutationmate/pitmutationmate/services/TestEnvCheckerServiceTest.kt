// SPDX-License-Identifier: MIT
// SPDX-FileCopyrightText: 2023

package com.amos.pitmutationmate.pitmutationmate.services

import com.intellij.testFramework.fixtures.LightJavaCodeInsightFixtureTestCase5
import com.intellij.testFramework.registerServiceInstance
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import org.mockito.Mockito
import org.mockito.Mockito.mock
import java.io.File

class TestEnvCheckerServiceTest : LightJavaCodeInsightFixtureTestCase5() {

    private lateinit var pluginCheckerMock: PluginCheckerService

    override fun getTestDataPath(): String {
        return "src/test/resources/test_MutationResultService/"
    }

    @BeforeEach
    fun setUp() {
        pluginCheckerMock = mock(PluginCheckerService::class.java)
        fixture.project.registerServiceInstance(PluginCheckerService::class.java, pluginCheckerMock)
    }

    @ParameterizedTest
    @ValueSource(
        strings = ["src/test/kotlin/TestEnvCheckerServiceTest.kt", "src/test/java/PluginCheckerServiceTest.java", "src/test/kotlin/PluginCheckerServiceTest.kt"]
    )
    fun `isTestFile returns true for files in test path`(testPath: String) {
        Mockito.`when`(pluginCheckerMock.getTestDirectories())
            .thenReturn(listOf(File("src/test/kotlin"), File("src/test/java")))

        val testEnvCheckerService = TestEnvCheckerService(fixture.project)
        val testFile = File(testPath)

        val result = testEnvCheckerService.isTestFile(testFile)

        assertTrue(result)
    }

    @ParameterizedTest
    @ValueSource(
        strings = ["src/test/kotlin/TestEnvCheckerServiceTest.kt", "src/test/java/PluginCheckerServiceTest.java", "src/test/kotlin/PluginCheckerServiceTest.kt"]
    )
    fun `isTestFile returns false for files not in test path`(testPath: String) {
        Mockito.`when`(pluginCheckerMock.getTestDirectories())
            .thenReturn(listOf(File("src/my_test/kotlin"), File("src/my_test/java")))

        val testEnvCheckerService = TestEnvCheckerService(fixture.project)
        val testFile = File(testPath)

        val result = testEnvCheckerService.isTestFile(testFile)

        assertFalse(result)
    }

    @Test
    fun `isPsiTestClass returns true for files in test path`() {
        Mockito.`when`(pluginCheckerMock.getTestDirectories()).thenReturn(listOf(File("/src/")))

        val cls = fixture.addClass("class TestClass{}")
        val testEnvCheckerService = TestEnvCheckerService(fixture.project)
        val result = testEnvCheckerService.isPsiTestClass(cls)
        assertTrue(result)
    }
}
