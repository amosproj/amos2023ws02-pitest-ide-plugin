package com.amos.pitmutationmate.pitmutationmate

import com.amos.pitmutationmate.pitmutationmate.reporting.HTMLParser
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

        assertEquals("100%", resultData.overallMutationResult?.lineCoverage)
        assertEquals("23%", resultData.overallMutationResult?.mutationCoverage)
        assertEquals("25%", resultData.overallMutationResult?.testStrength)

        assertEquals(1, resultData.packageResults.size)

        val packageResult = resultData.packageResults[0]
        assertEquals("de.esolutions.pitest.showcase", packageResult.packageName)
        assertEquals("./de.esolutions.pitest.showcase/index.html", packageResult.packageHref)
        assertEquals("100%", packageResult.mutationResult.lineCoverage)
        assertEquals("23%", packageResult.mutationResult.mutationCoverage)
        assertEquals("25%", packageResult.mutationResult.testStrength)
    }
}
