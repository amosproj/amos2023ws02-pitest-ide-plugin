// SPDX-License-Identifier: MIT
// SPDX-FileCopyrightText: 2023

package com.amos.pitmutationmate.pitmutationmate.reporting

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.io.File

class HTMLParserTests {
    private fun getTestInputFilepath(filename: String): File {
        val path = "src/test/resources/$filename"
        return File(path)
    }

    @Test
    fun testParsing() {
        val htmlParser = HTMLParser()
        val file = getTestInputFilepath("test_report/index.html")
        val resultData = htmlParser.loadResultsFromHtml(file.absolutePath)

        assertEquals("1", resultData.overallMutationResult?.numberOfClasses)
        assertEquals("100% 19/19", resultData.overallMutationResult?.lineCoverage)
        assertEquals("23% 3/13", resultData.overallMutationResult?.mutationCoverage)
        assertEquals("25% 3/12", resultData.overallMutationResult?.testStrength)

        val packageResult = resultData.packageResults[0]

        assertEquals("de.esolutions.pitest.showcase", packageResult.packageName)
        assertEquals("./de.esolutions.pitest.showcase/index.html", packageResult.packageHref)
        assertEquals("1", packageResult.mutationResult.numberOfClasses)
        assertEquals("100% 19/19", packageResult.mutationResult.lineCoverage)
        assertEquals("23% 3/13", packageResult.mutationResult.mutationCoverage)
        assertEquals("25% 3/12", packageResult.mutationResult.testStrength)
    }
}
