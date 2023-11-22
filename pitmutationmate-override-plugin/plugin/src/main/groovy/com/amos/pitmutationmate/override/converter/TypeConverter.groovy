package com.amos.pitmutationmate.override.converter

import org.gradle.api.Project

interface TypeConverter {
    Object convert(String value, Class<?> clazz, Project project)
}