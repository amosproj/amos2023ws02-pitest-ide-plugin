// SPDX-License-Identifier: MIT
// SPDX-FileCopyrightText: 2023

package com.amos.pitmutationmate.pitmutationmate.reporting
import org.w3c.dom.Document
import org.w3c.dom.Element
import org.w3c.dom.Node
import java.io.File
import javax.xml.parsers.DocumentBuilderFactory

class XMLParser {
    fun loadResultsFromXmlReport(xmlMutationReportPath: String, xmlCoverageReportPath: String): ResultData {
        val resultData = ResultData()
        try {
            val documentBuilderFactory = DocumentBuilderFactory.newInstance()
            val documentBuilder = documentBuilderFactory.newDocumentBuilder()
            val documentMutationReport = documentBuilder.parse(File(xmlMutationReportPath))
            val documentCoverageReport = documentBuilder.parse(File(xmlCoverageReportPath))

            extractMutationResults(documentMutationReport, resultData)
            extractCoverageReports(documentCoverageReport, resultData, false)
            extractCoverageReports(documentCoverageReport, resultData, true)
        } catch (e: Exception) {
            // TODO: Handle Parser exceptions
            e.printStackTrace()
        }

        return resultData
    }

    private fun extractMutationResults(document: Document, resultData: ResultData) {
        val mutationsNodeList = document.getElementsByTagName("mutation")

        for (i in 0 until mutationsNodeList.length) {
            val mutationNode = mutationsNodeList.item(i)

            if (mutationNode.nodeType == Node.ELEMENT_NODE) {
                val element = mutationNode as Element

                val detected = getAttribute(element, "detected", false)
                val status = getAttribute(element, "status", "N/A")
                val numberOfTestsRun = getAttribute(element, "numberOfTestsRun", -1)
                val sourceFile = getTextContent(element, "sourceFile")
                val mutatedClass = getTextContent(element, "mutatedClass")
                val mutatedMethod = getTextContent(element, "mutatedMethod")
                val methodDescription = getTextContent(element, "methodDescription")
                val lineNumber = getTextContent(element, "lineNumber").toInt()
                val mutator = getTextContent(element, "mutator")
                val indexes = getListContent(element, "index")
                val blocks = getListContent(element, "block")
                val killingTest = getTextContent(element, "killingTest")
                val description = getTextContent(element, "description")

                val mutationResult = MutationResult(
                    detected,
                    status,
                    numberOfTestsRun,
                    sourceFile,
                    mutatedClass,
                    mutatedMethod,
                    methodDescription,
                    lineNumber,
                    mutator,
                    indexes,
                    blocks,
                    killingTest,
                    description
                )
                resultData.addMutationResult(mutationResult)
                // display color bars for mutation result
                resultData.displayResult(mutationResult)
            }
        }
    }

    private fun extractCoverageReports(document: Document, resultData: ResultData, totals: Boolean) {
        val mutationsNodeList = document.getElementsByTagName(if (totals) "totalMetaData" else "coverageInformation")

        for (i in 0 until mutationsNodeList.length) {
            val mutationNode = mutationsNodeList.item(i)

            if (mutationNode.nodeType == Node.ELEMENT_NODE) {
                val element = mutationNode as Element

                val fileName = if (totals) "totals" else getTextContent(element, "FileName")
                val packageName = if (totals) "totals" else getTextContent(element, "PackageName")
                val mutatedClass = if (totals) "totals" else getTextContent(element, "MutatedClass")
                val lineCoveragePercentage = getTextContent(element, "LineCoveragePercentage").toInt()
                val lineCoverageTextRatio = getTextContent(element, "LineCoverage")
                val mutationCoveragePercentage = getTextContent(element, "MutationCoveragePercentage").toInt()
                val mutationCoverageTextRatio = getTextContent(element, "MutationCoverage")
                val testStrengthPercentage = getTextContent(element, "TestStrengthPercentage").toInt()
                val testStrengthTextRatio = getTextContent(element, "TestStrength")

                val coverageReport = CoverageReport(
                    fileName,
                    packageName,
                    mutatedClass,
                    lineCoveragePercentage,
                    lineCoverageTextRatio,
                    mutationCoveragePercentage,
                    mutationCoverageTextRatio,
                    testStrengthPercentage,
                    testStrengthTextRatio,
                    0
                )
                if (totals) resultData.totalResult = coverageReport else resultData.addCoverageReport(coverageReport)
            }
        }
    }

    private fun getTextContent(element: Element, tagName: String): String {
        val nodeList = element.getElementsByTagName(tagName)
        return if (nodeList.length > 0) {
            nodeList.item(0).textContent
        } else {
            "N/A"
        }
    }

    private fun getListContent(element: Element, tagName: String): List<Int> {
        val nodeList = element.getElementsByTagName(tagName)
        return if (nodeList.length > 0) {
            (0 until nodeList.length).map { nodeList.item(it).textContent.toInt() }
        } else {
            emptyList()
        }
    }

    private fun <T> getAttribute(element: Element, attributeName: String, defaultValue: T): T {
        return try {
            val attributeValue = element.getAttribute(attributeName)
            if (attributeValue.isNotEmpty()) {
                when (defaultValue) {
                    is Boolean -> attributeValue.toBoolean() as T
                    is Int -> attributeValue.toInt() as T
                    is String -> attributeValue as T
                    else -> defaultValue
                }
            } else {
                defaultValue
            }
        } catch (e: Exception) {
            defaultValue
        }
    }

    data class ResultData(
        // placeholder field for coverage report results to be displayed in visualisation
        val coverageReports: MutableList<CoverageReport> = mutableListOf(),
        val mutationResults: MutableList<MutationResult> = mutableListOf(),
        var totalResult: CoverageReport? = null
    ) {
        fun addMutationResult(mutationResult: MutationResult) {
            mutationResults.add(mutationResult)
        }

        fun addCoverageReport(coverageReport: CoverageReport) {
            coverageReports.add(coverageReport)
        }

        fun displayResult(mutationResult: MutationResult) {
            // removed in favor of MutationsAnnotator
        }
    }

    data class MutationResult(
        val detected: Boolean,
        val status: String,
        val numberOfTestsRun: Int,
        val sourceFile: String,
        val mutatedClass: String,
        val mutatedMethod: String,
        val methodDescription: String,
        val lineNumber: Int,
        val mutator: String,
        val indexes: List<Int>,
        val blocks: List<Int>,
        val killingTest: String,
        val description: String
    )

    data class CoverageReport(
        val fileName: String,
        val packageName: String,
        val mutatedClass: String,
        val lineCoveragePercentage: Int,
        val lineCoverageTextRatio: String,
        val mutationCoveragePercentage: Int,
        val mutationCoverageTextRatio: String,
        val testStrengthPercentage: Int,
        val testStrengthTextRatio: String,
        val numberOfClasses: Int
    )
}
