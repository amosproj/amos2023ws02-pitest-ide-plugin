// SPDX-FileCopyrightText: 2023 Lennart Heimbs
//
// SPDX-License-Identifier: MIT

package io.github.amosproj.pitmutationmate.override.communication

import io.github.amosproj.pitmutationmate.override.communicaiton.UdpMessagingService
import spock.lang.Specification

/**
 * UdpMessagingServiceSpec
 *
 * This class is responsible for testing the [UdpMessagingService].
 *
 * @see [UdpMessagingService]
 */
class UdpMessagingServiceSpec extends Specification {

    def "Test UDP client sends messages correctly"() {
        given:
        def port = findAvailablePort()
        def client = new UdpMessagingService(port: port)
        def mockServer = new MockUDPServer(port)
        mockServer.startServer()

        // test messages
        def testMessages = [
            "Test message",
            "Test message2",
            "",
            """
Lorem ipsum dolor sit amet, consectetur adipiscing elit. Fusce sed dolor ultrices,
suscipit libero non, cursus lectus. Phasellus quam lacus, porttitor in interdum ut,
suscipit non ligula. Donec ac augue in augue fringilla faucibus.
Duis eget erat ipsum. Vivamus imperdiet ante massa, fermentum fusce.
             """.trim()
                ]
        def expectedMessages = [
            "Test message",
            "Test message2",
            "",
            """
Lorem ipsum dolor sit amet, consectetur adipiscing elit. Fusce sed dolor ultrices,
suscipit libero non, cursus lectus. Phasellus quam lacus, porttitor in interdum ut,
suscipit non ligula. Donec ac augue in augue fringilla faucibus.
Duis eget erat ipsum. V""".trim()
        ]

        when:
        for (message in testMessages) {
            client.sendMessage(message)
        }

        then:
        Thread.sleep(100)
        mockServer.stopServer()
        mockServer.thread.join()
        for (message in expectedMessages) {
            assert mockServer.receivedMessages.contains(message)
        }
    }

    private int findAvailablePort() {
        def minPortNumber = 49152
        def maxPortNumber = 65535

        for (port in minPortNumber..maxPortNumber) {
            try {
                def socket = new DatagramSocket(port)
                socket.close()
                return port
            } catch (Exception ignored) {
                // Port is not available, continue searching
            }
        }

        throw new IllegalStateException("No available port found in the dynamic range")
    }

}
