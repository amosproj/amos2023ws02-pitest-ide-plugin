// SPDX-License-Identifier: MIT
// SPDX-FileCopyrightText: 2023

package com.amos.pitmutationmate.pitmutationmate.reporting

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.io.File

class XMLParserTests {

    private fun getTestInputFilepath(filename: String): File {
        val path = "src/test/resources/$filename"

        return File(path)
    }

    @Test
    fun loadResultsFromXml_reportFlyerReport() {
        val file1 = getTestInputFilepath("test_report/mutations.xml")
        val file2 = getTestInputFilepath("test_report/coverageInformation.xml")
        val parser = XMLParser()
        val actualResultData = parser.loadResultsFromXmlReport(file1.absolutePath, file2.absolutePath)

        assertTrue(actualResultData.mutationResults.isNotEmpty())

        val firstMutationResult = actualResultData.mutationResults[0]
        assertTrue(firstMutationResult.detected)
        assertEquals("KILLED", firstMutationResult.status)
        assertEquals(1, firstMutationResult.numberOfTestsRun)
        assertEquals("Presenter.java", firstMutationResult.sourceFile)

        val seventhMutationResult = actualResultData.mutationResults[6]
        assertFalse(seventhMutationResult.detected)
        assertEquals("SURVIVED", seventhMutationResult.status)
        assertEquals("lambda\$calculateValue\$1", seventhMutationResult.mutatedMethod)
    }

    @Test
    fun loadResultFromXml_missingXmlNode() {
        val file1 = getTestInputFilepath("test_report/mutations_missingXmlNode.xml")
        val file2 = getTestInputFilepath("test_report/coverageInformation.xml")
        val parser = XMLParser()
        val actualResultData = parser.loadResultsFromXmlReport(file1.absolutePath, file2.absolutePath)

        assertTrue(actualResultData.mutationResults.isNotEmpty())
    }

    @Test
    fun loadResultFromXml_additionalXmlNode() {
        val file1 = getTestInputFilepath("test_report/mutations_additionalNodes.xml")
        val file2 = getTestInputFilepath("test_report/coverageInformation.xml")
        val parser = XMLParser()
        val actualResultData = parser.loadResultsFromXmlReport(file1.absolutePath, file2.absolutePath)

        assertTrue(actualResultData.mutationResults.isNotEmpty())
    }
}
