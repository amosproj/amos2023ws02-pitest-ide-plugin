// SPDX-License-Identifier: MIT
// SPDX-FileCopyrightText: 2023 Lennart Heimbs

package com.amos.pitmutationmate.pitmutationmate.services

import com.intellij.openapi.components.Service
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.MessageType
import com.intellij.openapi.wm.ToolWindowManager
import org.jetbrains.kotlin.idea.gradleTooling.get
import java.net.DatagramPacket
import java.net.DatagramSocket

/**
 * A simple UDP server that listens on a given port and prints the received messages.
 */
@Service(Service.Level.PROJECT)
class UdpMessagingServer(private val project: Project) {

    private var _port: Int
    private val log: Logger = Logger.getInstance(UdpMessagingServer::class.java)
    private var isRunning = false
    private lateinit var socket: DatagramSocket
    private lateinit var receiveThread: Thread

    init {
        _port = findAvailablePort()
        log.debug("UdpMessagingServer: init at port: $_port")
    }

    val port: Int
        get() = _port

    fun startServer(overrideClassFQN: String? = null) {
        if (!isRunning) {
            socket = DatagramSocket(_port)
            isRunning = true
            receiveThread = Thread { receiveMessages(overrideClassFQN) }
            receiveThread.start()
        }
    }

    fun stopServer() {
        isRunning = false
        socket.close()
    }

    private fun receiveMessages(overrideClassFQN: String? = null) {
        while (isRunning) {
            val receiveData = ByteArray(256)
            val receivePacket = DatagramPacket(receiveData, receiveData.size)

            try {
                socket.receive(receivePacket)
            } catch (e: Exception) {
                // Socket was closed
                break
            }

            val data = receivePacket.data
            val messageLength = data[0].toInt() and 0xFF // Read the length byte
            val message = String(data, 1, messageLength) // Extract the message
            log.trace("Received message: $message")

            if (overrideClassFQN != null && message.contains(overrideClassFQN)) {
                // class was successfully overridden. Notify the user
                ToolWindowManager.getInstance(project).notifyByBalloon(
                    "Pitest Result",
                    MessageType.INFO,
                    "<p>Successfully applied pitest target class</p><p>$overrideClassFQN.</p>"
                )
            }
        }
    }

    private fun findAvailablePort(): Int {
        val minPortNumber = 49152
        val maxPortNumber = 65535

        for (port in minPortNumber..maxPortNumber) {
            try {
                val socket = DatagramSocket(port)
                socket.close()
                return port
            } catch (e: Exception) {
                // Port is not available, continue searching
            }
        }

        throw IllegalStateException("No available port found in the dynamic range")
    }
}
