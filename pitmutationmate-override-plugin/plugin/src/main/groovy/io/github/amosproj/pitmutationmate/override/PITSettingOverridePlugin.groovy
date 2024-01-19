// SPDX-FileCopyrightText: 2023 Lennart Heimbs
//
// SPDX-License-Identifier: MIT

package io.github.amosproj.pitmutationmate.override

import io.github.amosproj.pitmutationmate.override.communicaiton.MessagingService
import io.github.amosproj.pitmutationmate.override.communicaiton.UdpMessagingService
import io.github.amosproj.pitmutationmate.override.reader.OverrideReader
import io.github.amosproj.pitmutationmate.override.reader.SystemPropertyOverrideReader
import io.github.amosproj.pitmutationmate.override.strategy.DependencyInclusionStrategy
import io.github.amosproj.pitmutationmate.override.strategy.OverrideStrategy
import io.github.amosproj.pitmutationmate.override.strategy.PitestPropertyOverrideStrategy
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.logging.Logger
import org.gradle.api.logging.Logging

/**
 * Partner Plugin to the PITmutationmate Plugin to allow overriding of specific PITest configuration values.*/
class PITSettingOverridePlugin implements Plugin<Project> {

    @SuppressWarnings('FieldName')
    private final static Logger log = Logging.getLogger(PITSettingOverridePlugin)
    private final static String PORT_PROPERTY_NAME = 'port'

    void apply(Project project) {
        OverrideReader overrideReader = new SystemPropertyOverrideReader()
        Map<String, String> overrideProperties = overrideReader.parseProperties()
        log.debug("Found override properties: $overrideProperties")

        MessagingService messagingService
        if (overrideProperties.containsKey(PORT_PROPERTY_NAME)) {
            int port = overrideProperties.get(PORT_PROPERTY_NAME).toInteger()
            // remove the port property from the map so it is not overridden
            overrideProperties.remove(PORT_PROPERTY_NAME)
            log.info("Using UDP messaging service at port $port.")
            messagingService = new UdpMessagingService(port: port)
        } else {
            log.info('No port given for the message server. Not sending messages.')
        }

        project.gradle.projectsEvaluated {
            try {
                overrideProperties.each { propertyName, overrideValue ->
                    log.debug("Got property '$propertyName' with new value '$overrideValue'.")

                    OverrideStrategy strategy
                    if (propertyName == DependencyInclusionStrategy.OVERRIDE_ATTRIBUTE) {
                        strategy = new DependencyInclusionStrategy()
                    } else {
                        strategy = new PitestPropertyOverrideStrategy()
                    }
                    strategy.apply(project, propertyName, overrideValue)
                    if (messagingService != null) {
                        messagingService.sendMessage(
                            "Property '$propertyName' with '$overrideValue' successfully overwritten.")
                    }
                }
            } catch (Exception e) {
                log.error('Error overriding PITest settings.', e)
                if (messagingService != null) {
                    messagingService.sendMessage("Error while overriding PITest settings: ${e.message}")
                }
            }
        }
    }

}
