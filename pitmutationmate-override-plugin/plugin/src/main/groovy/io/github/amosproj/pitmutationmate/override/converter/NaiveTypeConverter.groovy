// SPDX-FileCopyrightText: 2023 Lennart Heimbs
//
// SPDX-License-Identifier: MIT

package io.github.amosproj.pitmutationmate.override.converter


import io.github.amosproj.pitmutationmate.override.PITSettingOverridePlugin
import org.apache.commons.beanutils.ConvertUtilsBean
import org.gradle.api.Project
import org.gradle.api.file.Directory
import org.gradle.api.logging.Logger
import org.gradle.api.logging.Logging

class NaiveTypeConverter implements TypeConverter {
    @SuppressWarnings("FieldName")
    private final static Logger log = Logging.getLogger(PITSettingOverridePlugin)
    private final ConvertUtilsBean convertUtilsBean = new ConvertUtilsBean()

    @Override
    Object convert(String value, Class<?> clazz, Project project) {

        switch (clazz) {
            case Integer:
                log.trace("Converting Integer value '$value' to type '$clazz'.")
                return convertUtilsBean.convert(value, Integer)
            case Boolean:
                log.trace("Converting Boolean value '$value' to type '$clazz'.")
                return convertUtilsBean.convert(value, Boolean)
            case String:
                log.trace("Converting String value '$value' to type '$clazz'.")
                return value
            case BigDecimal:
                log.trace("Converting BigDecimal value '$value' to type '$clazz'.")
                return convertUtilsBean.convert(value, BigDecimal)
            case Set:
                log.trace("Converting Set value '$value' to type '$clazz'.")
                return trimArray(value).toSet()
            case List:
                log.trace("Converting List value '$value' to type '$clazz'.")
                return trimArray(value)
            case Map:
                log.trace("Converting Map value '$value' to type '$clazz'.")
                return trimArray(value)
                        .collectEntries {
                            def split = it.split(":", 2)
                            [(split[0]): split[1]]
                        }
            case Directory:
                if (value == 'null') {
                    log.trace("Directory value '$value' is empty. Returning null.")
                    return null
                }
                log.trace("Converting Directory value '$value' to type '$clazz'.")
                return project.getLayout().getProjectDirectory().dir(value)
            default:
                log.warn("Cannot convert value '$value' to type '$clazz'.")
        }
        return null
    }

    static List<String> trimArray(String value) {
        if (value == 'null' || value == '' || value == '[]' || value == '[:]') {
            log.trace("Value '$value' is empty.")
            return []
        }
        value = value.replace('[', '')
                .replace(']', '')
        return value.split(",").collect { it.trim() }
    }
}
