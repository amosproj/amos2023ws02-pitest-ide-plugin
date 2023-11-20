// SPDX-License-Identifier: MIT
// SPDX-FileCopyrightText: 2023

package com.amos.pitmutationmate.pitmutationmate

import org.junit.Test
import  java.io.File
import org.junit.jupiter.api.Assertions.*

class XMLParserTests{
    fun getTestInputFilepath(filename: String): File {
        val classLoader = Thread.currentThread().contextClassLoader
        val resource = classLoader.getResource(filename)
        val file = File(resource.file)
        return file
    }

    @Test
    fun testloadResultsFromXmlReportFlyerReport(){
        val file = getTestInputFilepath("test_report/mutations.xml")
        val parser = XMLParser()
        val actualResultData = parser.loadResultsFromXmlReport(file.absolutePath)

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



}
