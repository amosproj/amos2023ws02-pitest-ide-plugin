package com.amos.pitmutationmate.override.strategy

import org.gradle.api.Project

interface OverrideStrategy {
    void apply(Project project, String propertyName, String overrideValue)
}