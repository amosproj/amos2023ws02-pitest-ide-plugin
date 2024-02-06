// SPDX-License-Identifier: MIT
// SPDX-FileCopyrightText: 2023

package com.amos.pitmutationmate.pitmutationmate.reporting

import com.intellij.openapi.diagnostic.thisLogger
import org.w3c.dom.Document
import org.w3c.dom.Element
import java.io.File
import javax.xml.parsers.DocumentBuilderFactory

class XMLParser {
    fun loadResultsFromXmlReport(xmlMutationReportPath: String, xmlCoverageReportPath: String): ResultData {
        thisLogger().info("Loading XML reports from $xmlMutationReportPath and $xmlCoverageReportPath")
        val resultData = ResultData()
        try {
            val documentMutationReport = parseXmlDocument(File(xmlMutationReportPath))
            val documentCoverageReport = parseXmlDocument(File(xmlCoverageReportPath))

            extractMutationResults(documentMutationReport, resultData)
            extractCoverageReports(documentCoverageReport, resultData)
            extractCoverageReportPackage(documentCoverageReport, resultData)
            extractCoverageReportTotals(documentCoverageReport, resultData)
        } catch (e: Exception) {
            thisLogger().warn("Error while parsing XML reports: ${e.message}")
        }

        return resultData
    }

    private fun parseXmlDocument(xmlFile: File): Document {
        val factory = DocumentBuilderFactory.newInstance()
        val builder = factory.newDocumentBuilder()
        return builder.parse(xmlFile)
    }

    private fun extractMutationResults(document: Document, resultData: ResultData) {
        thisLogger().info("Extracting mutation results")
        val mutationsNodeList = document.getElementsByTagName("mutation")

        for (i in 0 until mutationsNodeList.length) {
            val element = mutationsNodeList.item(i) as? Element ?: continue

            val detected = getAttribute(element, "detected", false)
            val status = getAttribute(element, "status", "N/A")
            val numberOfTestsRun = getAttribute(element, "numberOfTestsRun", 0)
            val sourceFile = getTextContent(element, "sourceFile")
            val mutatedClass = getTextContent(element, "mutatedClass")
            val mutatedMethod = getTextContent(element, "mutatedMethod")
            val methodDescription = getTextContent(element, "methodDescription")
            val lineNumber = getTextContentAsInt(element, "lineNumber")
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
        }
    }

    private fun extractCoverageReports(document: Document, resultData: ResultData) {
        thisLogger().info("Extracting coverage reports")
        val mutationsNodeList = document.getElementsByTagName("testMetaData")

        for (i in 0 until mutationsNodeList.length) {
            val element = mutationsNodeList.item(i) as? Element ?: continue

            val fileName = getTextContent(element, "FileName")
            val packageName = getTextContent(element, "PackageName")
            val coverageReport = CoverageReport(
                fileName,
                packageName,
                getLineCoveragePercentage(element),
                getLineCoverageTextRatio(element),
                getMutationCoveragePercentage(element),
                getMutationCoverageTextRatio(element),
                getTestStrengthPercentage(element),
                getTestStrengthTextRatio(element),
                if (fileName == "N/A") 0 else 1
            )
            resultData.addCoverageReport(coverageReport)
        }
    }

    private fun extractCoverageReportPackage(document: Document, resultData: ResultData) {
        thisLogger().info("Extracting coverage report totals")
        val mutationsNodeList = document.getElementsByTagName("packageMetaData")

        for (i in 0 until mutationsNodeList.length) {
            val element = mutationsNodeList.item(i) as? Element ?: continue
            val packageName = getTextContent(element, "PackageName")
            val coverageReport = CoverageReport(
                "package",
                packageName,
                getLineCoveragePercentage(element),
                getLineCoverageTextRatio(element),
                getMutationCoveragePercentage(element),
                getMutationCoverageTextRatio(element),
                getTestStrengthPercentage(element),
                getTestStrengthTextRatio(element),
                getNumberOfClassFromPackage(element)
            )
            resultData.addPackageReport(coverageReport)
        }
    }

    private fun extractCoverageReportTotals(document: Document, resultData: ResultData) {
        thisLogger().info("Extracting coverage report totals")
        val mutationsNodeList = document.getElementsByTagName("totalMetaData")

        for (i in 0 until mutationsNodeList.length) {
            val element = mutationsNodeList.item(i) as? Element ?: continue

            val coverageReport = CoverageReport(
                "totals",
                "totals",
                getLineCoveragePercentage(element),
                getLineCoverageTextRatio(element),
                getMutationCoveragePercentage(element),
                getMutationCoverageTextRatio(element),
                getTestStrengthPercentage(element),
                getTestStrengthTextRatio(element),
                0
            )
            resultData.addCoverageReportTotals(coverageReport)
        }
    }

    private fun getLineCoveragePercentage(element: Element): Int {
        return getTextContentAsInt(element, "LineCoveragePercentage")
    }

    private fun getLineCoverageTextRatio(element: Element): String {
        return getTextContent(element, "LineCoverage")
    }

    private fun getMutationCoveragePercentage(element: Element): Int {
        return getTextContentAsInt(element, "MutationCoveragePercentage")
    }

    private fun getMutationCoverageTextRatio(element: Element): String {
        return getTextContent(element, "MutationCoverage")
    }

    private fun getTestStrengthPercentage(element: Element): Int {
        return getTextContentAsInt(element, "TestStrengthPercentage")
    }

    private fun getTestStrengthTextRatio(element: Element): String {
        return getTextContent(element, "TestStrength")
    }

    private fun getNumberOfClassFromPackage(element: Element): Int {
        return getTextContentAsInt(element, "NumberOfClasses")
    }
    private fun getTextContent(element: Element, tagName: String): String {
        val nodeList = element.getElementsByTagName(tagName)
        return if (nodeList.length > 0) {
            nodeList.item(0).textContent.trim()
        } else {
            "N/A"
        }
    }

    private fun getTextContentAsInt(element: Element, tagName: String): Int {
        val nodeList = element.getElementsByTagName(tagName)
        return if (nodeList.length > 0) {
            val content = nodeList.item(0).textContent.trim()
            return if (content.toIntOrNull() != null) {
                content.toInt()
            } else {
                0
            }
        } else {
            0
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

    private inline fun <reified T> getAttribute(element: Element, attributeName: String, defaultValue: T): T {
        return try {
            val attributeValue = element.getAttribute(attributeName)
            if (attributeValue.isNotEmpty()) {
                when (T::class) {
                    Boolean::class -> attributeValue.toBoolean() as T
                    Int::class -> attributeValue.toIntOrNull() as? T ?: defaultValue
                    String::class -> attributeValue as T
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
        val coverageReports: MutableList<CoverageReport> = mutableListOf(),
        val packageReports: MutableList<CoverageReport> = mutableListOf(),
        val mutationResults: MutableList<MutationResult> = mutableListOf(),
        var totalResult: CoverageReport? = null
    ) {
        fun addMutationResult(mutationResult: MutationResult) {
            mutationResults.add(mutationResult)
        }

        fun addCoverageReport(coverageReport: CoverageReport) {
            coverageReports.add(coverageReport)
        }

        fun addPackageReport(packageReport: CoverageReport) {
            packageReports.add(packageReport)
        }
        fun addCoverageReportTotals(coverageReport: CoverageReport) {
            if (packageReports.isNotEmpty()) {
                for (packageReport in packageReports) {
                    coverageReport.numberOfClasses += packageReport.numberOfClasses
                }
            }
            totalResult = coverageReport
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
        var lineCoveragePercentage: Int,
        val lineCoverageTextRatio: String,
        var mutationCoveragePercentage: Int,
        val mutationCoverageTextRatio: String,
        var testStrengthPercentage: Int,
        val testStrengthTextRatio: String,
        var numberOfClasses: Int
    )
}
