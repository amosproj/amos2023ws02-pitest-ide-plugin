// SPDX-License-Identifier: MIT
// SPDX-FileCopyrightText: 2023

package com.amos.pitmutationmate.pitmutationmate

import org.w3c.dom.Element
import org.w3c.dom.Node
import java.io.File
import javax.xml.parsers.DocumentBuilderFactory

class XMLParser {
    fun loadResultsFromXmlReport(xmlReportPath: String?): ResultData {
        // Implement the logic to parse the XML report and load key results into a data structure
        val resultData = ResultData()
        val documentBuilderFactory = DocumentBuilderFactory.newInstance()
        val documentBuilder = documentBuilderFactory.newDocumentBuilder()
        val document = documentBuilder.parse(File(xmlReportPath))

        val mutationsNodeList = document.getElementsByTagName("mutation")

        for (i in 0 until mutationsNodeList.length) {
            val mutationNode = mutationsNodeList.item(i)

            if (mutationNode.nodeType == Node.ELEMENT_NODE) {
                val element = mutationNode as Element

                val detected = element.getAttribute("detected").toBoolean()
                val status = element.getAttribute("status")
                val numberOfTestsRun = element.getAttribute("numberOfTestsRun").toInt()
                val sourceFile = element.getElementsByTagName("sourceFile").item(0).textContent
                val mutatedClass = element.getElementsByTagName("mutatedClass").item(0).textContent
                val mutatedMethod = element.getElementsByTagName("mutatedMethod").item(0).textContent
                val methodDescription = element.getElementsByTagName("methodDescription").item(0).textContent
                val lineNumber = element.getElementsByTagName("lineNumber").item(0).textContent.toInt()
                val mutator = element.getElementsByTagName("mutator").item(0).textContent
                val indexesNodeList = element.getElementsByTagName("index")
                val indexes = (0 until indexesNodeList.length).map { indexesNodeList.item(it).textContent.toInt() }
                val blocksNodeList = element.getElementsByTagName("block")
                val blocks = (0 until blocksNodeList.length).map { blocksNodeList.item(it).textContent.toInt() }
                val killingTest = element.getElementsByTagName("killingTest").item(0).textContent
                val description = element.getElementsByTagName("description").item(0).textContent

                // Create a MutationResult object and add it to the data structure
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

        return resultData
    }

    data class ResultData(
        val mutationResults: MutableList<MutationResult> = mutableListOf()
    ) {
        fun addMutationResult(mutationResult: MutationResult) {
            mutationResults.add(mutationResult)
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
}
