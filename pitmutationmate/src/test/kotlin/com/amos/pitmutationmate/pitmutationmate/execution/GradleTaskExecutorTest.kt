// SPDX-License-Identifier: MIT
// SPDX-FileCopyrightText: 2023 Lennart Heimbs

package com.amos.pitmutationmate.pitmutationmate.execution

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import java.nio.file.Path

/**
 * Tests for GradleTaskExecutor
 *
 * @see GradleTaskExecutor
 */
class GradleTaskExecutorTest {
    @Mock
    lateinit var systemInfo: SystemInfoProvider // Assuming SystemInfo is a dependency

    @InjectMocks
    lateinit var gradleTaskExecutor: GradleTaskExecutor

    @BeforeEach
    fun setup() {
        MockitoAnnotations.openMocks(this)
    }

    @Test
    fun `test buildCommandLine for Windows`() {
        `when`(systemInfo.isWindows()).thenReturn(true)

        val commandLine = gradleTaskExecutor.buildCommandLine(
            null,
            "clean",
            "/path/to/project",
            "com.example.Class",
            Path.of("/path/to/report"),
            8080
        )

        assertEquals(GradleTaskExecutor.WINDOWS_SHELL_EXECUTABLE, commandLine.exePath)
        assertEquals(GradleTaskExecutor.WINDOWS_FIRST_PARAMETER, commandLine.parametersList.parameters[0])
        assertEquals(GradleTaskExecutor.WINDOWS_GRADLE_EXECUTABLE, commandLine.parametersList.parameters[1])
    }

    @Test
    fun `test buildCommandLine for Unix`() {
        `when`(systemInfo.isWindows()).thenReturn(false)

        val commandLine = gradleTaskExecutor.buildCommandLine(
            null,
            "clean",
            "/path/to/project",
            "com.example.Class",
            Path.of("/path/to/report"),
            8080
        )

        assertEquals(GradleTaskExecutor.UNIX_SHELL_EXECUTABLE, commandLine.exePath)
        assertEquals(GradleTaskExecutor.UNIX_FIRST_PARAMETER, commandLine.parametersList.parameters[0])
        assertEquals(GradleTaskExecutor.UNIX_GRADLE_EXECUTABLE, commandLine.parametersList.parameters[1])
    }

    @Test
    fun `test buildCommandLine without taskName uses default taskName`() {
        `when`(systemInfo.isWindows()).thenReturn(true)

        val commandLine = gradleTaskExecutor.buildCommandLine(
            null,
            null,
            "/path/to/project",
            "com.example.Class",
            Path.of("/path/to/report"),
            8080
        )

        assertEquals(GradleTaskExecutor.PITEST_TASK_NAME, commandLine.parametersList.parameters[2])
    }

    @Test
    fun `test buildCommandLine with taskName uses given taskName`() {
        `when`(systemInfo.isWindows()).thenReturn(true)

        val taskName = "test123"
        val commandLine = gradleTaskExecutor.buildCommandLine(
            null,
            taskName,
            "/path/to/project",
            "com.example.Class",
            Path.of("/path/to/report"),
            8080
        )

        assertEquals(taskName, commandLine.parametersList.parameters[2])
    }

    @Test
    fun `test buildCommandLine without classFQDN does not add targetClass override`() {
        `when`(systemInfo.isWindows()).thenReturn(true)

        val commandLine = gradleTaskExecutor.buildCommandLine(
            null,
            "clean",
            "/path/to/project",
            null,
            Path.of("/path/to/report"),
            8080
        )

        for (parameter in commandLine.parametersList.parameters) {
            assertFalse(parameter.contains("-Dpitmutationmate.override.targetClasses"))
        }
    }

    @Test
    fun `test buildCommandLine with classFQDN adds targetClass override`() {
        `when`(systemInfo.isWindows()).thenReturn(true)

        val classFQN = "com.example.Class"
        val commandLine = gradleTaskExecutor.buildCommandLine(
            null,
            "clean",
            "/path/to/project",
            classFQN,
            Path.of("/path/to/report"),
            8080
        )

        assertTrue(
            commandLine.parametersList.parameters.contains("-Dpitmutationmate.override.targetClasses=$classFQN")
        )
    }
}
