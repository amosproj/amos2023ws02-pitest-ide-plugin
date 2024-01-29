// SPDX-License-Identifier: MIT
// SPDX-FileCopyrightText: 2023-2024

package com.amos.pitmutationmate.pitmutationmate.execution

import com.amos.pitmutationmate.pitmutationmate.editor.PluginState
import com.amos.pitmutationmate.pitmutationmate.services.MutationResultService
import com.amos.pitmutationmate.pitmutationmate.services.RunArchiveService
import com.amos.pitmutationmate.pitmutationmate.services.UdpMessagingServer
import com.intellij.codeInsight.daemon.DaemonCodeAnalyzer
import com.intellij.execution.process.ProcessEvent
import com.intellij.testFramework.fixtures.LightJavaCodeInsightFixtureTestCase5
import com.intellij.testFramework.registerServiceInstance
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.NullSource
import org.junit.jupiter.params.provider.ValueSource
import org.mockito.Mockito

class ExecutionDoneProcessListenerTest : LightJavaCodeInsightFixtureTestCase5() {

    private lateinit var messagingServer: UdpMessagingServer
    private lateinit var resultService: MutationResultService
    private lateinit var runArchiveService: RunArchiveService
    private lateinit var daemonCodeAnalyzer: DaemonCodeAnalyzer

    private val testClass1 =
        """
        package com.amos.pitmutationmate;
        public class MutationResultService {
        }
        """.trimIndent()
    private val testClass2 =
        """
        package com.amos.pitmutationmate;
        public class RunArchiveService {
        }
        """.trimIndent()

    override fun getTestDataPath(): String {
        return "src/test/resources/test_MutationResultService"
    }

    @BeforeEach
    fun setUp() {
        daemonCodeAnalyzer = Mockito.mock(DaemonCodeAnalyzer::class.java)
        Mockito.mockStatic(DaemonCodeAnalyzer::class.java)
            .`when`<Any> { DaemonCodeAnalyzer.getInstance(fixture.project) }
            .thenReturn(daemonCodeAnalyzer)
        messagingServer = Mockito.mock(UdpMessagingServer::class.java)
        fixture.project.registerServiceInstance(UdpMessagingServer::class.java, messagingServer)
        resultService = Mockito.mock(MutationResultService::class.java)
        fixture.project.registerServiceInstance(MutationResultService::class.java, resultService)
        runArchiveService = Mockito.mock(RunArchiveService::class.java)
        fixture.project.registerServiceInstance(RunArchiveService::class.java, runArchiveService)
    }

    @AfterEach
    fun tearDown() {
        Mockito.clearAllCaches()
    }

    // jscpd:ignore-start
    @ParameterizedTest
    @NullSource
    @ValueSource(strings = [""])
    fun `test processTerminated with null or empty FQDN does not run anything`(classFqdns: String?) {
        runTestClassWithMockedEvent(classFqdns)

        Assertions.assertFalse(PluginState.isAnnotatorEnabled)
        Mockito.verify(messagingServer, Mockito.times(1)).stopServer()
        Mockito.verifyNoInteractions(runArchiveService)
        Mockito.verifyNoInteractions(daemonCodeAnalyzer)
    }

    @ParameterizedTest
    @ValueSource(strings = ["com.amos.pitmutationmate.MutationResultService", "com.amos.pitmutationmate.RunArchiveService,com.amos.pitmutationmate.UdpMessagingServer", "com.amos.pitmutationmate.MutationResultService,com.amos.pitmutationmate.RunArchiveService,com.amos.pitmutationmate.UdpMessagingServer"])
    fun `test processTerminated with classes in FQDNs but no PSI files`(classFqdns: String) {
        runTestClassWithMockedEvent(classFqdns)

        Assertions.assertTrue(PluginState.isAnnotatorEnabled)
        Mockito.verify(messagingServer, Mockito.times(1)).stopServer()
        Mockito.verify(runArchiveService, Mockito.times(1)).archiveRun()
        Mockito.verifyNoInteractions(daemonCodeAnalyzer)
    }

    @Test
    fun `test processTerminated with one class in FQDNs and PSI file present`() {
        fixture.addClass(testClass1)
        val classFqdns = "com.amos.pitmutationmate.MutationResultService"

        runTestClassWithMockedEvent(classFqdns)

        Assertions.assertTrue(PluginState.isAnnotatorEnabled)
        Mockito.verify(messagingServer, Mockito.times(1)).stopServer()
        Mockito.verify(runArchiveService, Mockito.times(1)).archiveRun()
        Mockito.verify(daemonCodeAnalyzer, Mockito.times(1)).restart(Mockito.any())
    }

    @Test
    fun `test processTerminated with 3 classes in FQDNs and two PSI file present`() {
        fixture.addClass(testClass1)
        fixture.addClass(testClass2)
        val classFqdns =
            "com.amos.pitmutationmate.MutationResultService,com.amos.pitmutationmate.RunArchiveService,com.amos.pitmutationmate.UdpMessagingServer"

        runTestClassWithMockedEvent(classFqdns)

        Assertions.assertTrue(PluginState.isAnnotatorEnabled)
        Mockito.verify(messagingServer, Mockito.times(1)).stopServer()
        Mockito.verify(runArchiveService, Mockito.times(1)).archiveRun()
        Mockito.verify(daemonCodeAnalyzer, Mockito.times(2)).restart(Mockito.any())
    }

    private fun runTestClassWithMockedEvent(classFqdns: String?) {
        val adapter = ExecutionDoneProcessListener(fixture.project, classFqdns)
        val processEvent: ProcessEvent = Mockito.mock(ProcessEvent::class.java)

        adapter.processTerminated(processEvent)
    }
    // jscpd:ignore-end
}
