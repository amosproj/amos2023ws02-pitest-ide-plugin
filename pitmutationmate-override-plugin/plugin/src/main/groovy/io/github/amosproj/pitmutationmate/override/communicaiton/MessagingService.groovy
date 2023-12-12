// SPDX-FileCopyrightText: 2023 Lennart Heimbs
//
// SPDX-License-Identifier: MIT

package io.github.amosproj.pitmutationmate.override.communicaiton

/**
 * Service to send status messages to the partner plugin.
 */
interface MessagingService {

    void sendMessage(String message)

}
