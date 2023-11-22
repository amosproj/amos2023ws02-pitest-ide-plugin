package com.amos.pitmutationmate.override.strategy

import com.amos.pitmutationmate.override.PITConfigurationValues
import com.amos.pitmutationmate.override.UnknownPropertyException
import com.amos.pitmutationmate.override.converter.NaiveTypeConverter
import com.amos.pitmutationmate.override.converter.TypeConverter
import org.gradle.api.Project

class GradlePitestPluginOverrideStrategy implements OverrideStrategy {
    TypeConverter typeConverter = new NaiveTypeConverter() as TypeConverter

    @Override
    void apply(Project project, String propertyName, String overrideValue) {
        String[] extractedOptions = propertyName.split('\\.')

        def node = project
        def parent = null
        def fieldName = null

        extractedOptions.eachWithIndex { String entry, int index ->
            if (index == extractedOptions.size() - 1) {
                parent = node
                fieldName = entry
            }
            node = determineProperty(node, entry, propertyName)
        }

        PITConfigurationValues overrideFields = new PITConfigurationValues()
        def overrideProperty = overrideFields.properties.find { it.key == fieldName }

        if (overrideProperty == null) {
            throw new UnknownPropertyException("Unknown property with name '$propertyName' on instance of type ${node.getClass()}")
        }
        Class clazz = overrideProperty.value.getClass()

        def newValue = typeConverter.convert(overrideValue, clazz, project)
        parent.setProperty(fieldName, newValue)
    }

    /**
     * Checks if node has a property with the provided name.
     *
     * @param node Gradle domain object
     * @param propertyName Property name
     * @return Property instance
     * @throws UnknownPropertyException
     */
    static def determineProperty(Object node, String propertyName, String fullOverridePath) {
        if (node.hasProperty(propertyName)) {
            return node.getProperty(propertyName)
        }
        throw new UnknownPropertyException("Unknown property with name '$propertyName' on instance of type ${node.getClass()} (Full property path: '$fullOverridePath')")
    }
}
