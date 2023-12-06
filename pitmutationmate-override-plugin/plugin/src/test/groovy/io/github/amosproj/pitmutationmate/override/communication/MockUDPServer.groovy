// SPDX-FileCopyrightText: 2023 Lennart Heimbs
//
// SPDX-License-Identifier: MIT

package io.github.amosproj.pitmutationmate.override.communication

/**
 * Mock UDP server to test the UDP messaging service.
 */
class MockUDPServer {

    private DatagramSocket socket
    Thread thread
    List<String> receivedMessages

    MockUDPServer(int port) {
        socket = new DatagramSocket(port)
        receivedMessages = []
    }

    void startServer() {
        thread = Thread.start {
            byte[] receiveData = new byte[1024]

            while (true) {
                try {
                    DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length)
                    socket.receive(receivePacket)
                    int messageLength = receiveData[0] & 0xFF
                    String message = new String(receivePacket.getData(), 1, messageLength)
                    receivedMessages.add(message)
                } catch (Exception e) {
                    // Ignore
                    break;
                }
            }
        }
    }

    void stopServer() {
        socket.close()
        thread.interrupt()
    }

}
