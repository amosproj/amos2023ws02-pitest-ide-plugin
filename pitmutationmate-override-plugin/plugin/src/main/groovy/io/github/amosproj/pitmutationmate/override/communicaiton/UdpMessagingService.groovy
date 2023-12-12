// SPDX-FileCopyrightText: 2023 Lennart Heimbs
//
// SPDX-License-Identifier: MIT

package io.github.amosproj.pitmutationmate.override.communicaiton

import org.gradle.api.logging.Logger
import org.gradle.api.logging.Logging

/**
 * Service to send status messages to the partner plugin.
 * This is a workaround for the lack of a proper API to communicate between plugins.
 *
 * This is a very simple implementation of a UDP client.
 * It is used to send messages to the partner plugin.
 */
class UdpMessagingService implements MessagingService {

    int port
    private InetAddress serverAddress = InetAddress.getByName("localhost")
    private final static Logger LOG = Logging.getLogger(UdpMessagingService)

    @Override
    void sendMessage(String message) {

        DatagramSocket socket
        try {
            socket = new DatagramSocket()
        } catch (SocketException | SecurityException e) {
            LOG.error("Error while creating UDP socket.", e)
            return
        }

        try {
            byte[] messageData = message.getBytes()
            byte[] sendData = new byte[messageData.length + 1]

            int messageLength = messageData.length
            if (messageLength > 255) {
                LOG.warn("Message length is greater than 255. Truncating message.")
                messageLength = 255
            }
            sendData[0] = (byte) messageLength

            // Copy the message bytes after the length byte
            System.arraycopy(messageData, 0, sendData, 1, messageLength)

            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, serverAddress, port)

            socket.send(sendPacket)
            LOG.info("Message sent: $message")
        } catch (Exception e) {
            LOG.error("Error while sending UDP message.", e)
        } finally {
            if (socket != null && !socket.isClosed()) {
                socket.close()
                LOG.debug("Closed UDP socket.")
            }
        }
    }

}
