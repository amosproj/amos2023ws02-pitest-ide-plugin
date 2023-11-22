package com.amos.pitmutationmate.override.converter

import org.apache.commons.beanutils.ConvertUtilsBean
import org.gradle.api.Project
import org.gradle.api.file.Directory

class NaiveTypeConverter implements TypeConverter {
    ConvertUtilsBean convertUtilsBean = new ConvertUtilsBean()

    @Override
    Object convert(String value, Class<?> clazz, Project project) {
        switch (clazz) {
            case Integer:
                return convertUtilsBean.convert(value, Integer)
            case Boolean:
                return convertUtilsBean.convert(value, Boolean)
            case String:
                return value
            case BigDecimal:
                return convertUtilsBean.convert(value, BigDecimal)
            case Set:
                if (value == '[]' || value == 'null') {
                    return [].toSet()
                }
                return trimArray(value, [].toSet())
                        .split(",")
                        .collect { it.trim() }.toSet()
            case List:
                if (value == '[]' || value == 'null') {
                    return []
                }
                return trimArray(value, [])
                        .split(",")
                        .collect { it.trim() }
            case Map:
                if (value == '[:]' || value == 'null') {
                    return [:]
                }
                return trimArray(value, [:])
                        .split(",")
                        .collect { it.trim() }
                        .collectEntries {
                            def split = it.split(":")
                            if (split.size() != 2) {
                                return null
                            }
                            else if (split[0].trim() == '' || split[1].trim() == '') {
                                return null
                            }
                            [(split[0]): split[1]]
                        }
            case Directory:
                return project.getLayout().getProjectDirectory().dir(value)
            default:
                return null
        }
    }

    static String trimArray(String value, defaultEmpty) {
        value = value.replace('[', '')
                .replace(']', '')
        if (value == 'null' || value == '' || value == '[]' || value == '[:]') {
            return defaultEmpty
        }
        return value
    }
}
