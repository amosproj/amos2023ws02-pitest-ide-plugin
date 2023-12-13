// SPDX-License-Identifier: MIT
// SPDX-FileCopyrightText: 2023

package com.amos.pitmutationmate.pitmutationmate.reporting

import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import java.io.File

class HTMLParser {
    fun loadResultsFromHtml(htmlReportPath: String): OverallResultData {
        val overallResultData = OverallResultData()
        try {
            val document = Jsoup.parse(File(htmlReportPath), "UTF-8")

            val projectSummaryTable = document.selectFirst("h3:contains(Project Summary) + table")
            if (projectSummaryTable != null) {
                val rows = projectSummaryTable.select("tbody tr")
                if (rows.isNotEmpty()) {
                    val overallRow = rows.first()
                    overallResultData.overallMutationResult = extractOverallMutationResultFromRow(overallRow)
                }
            }

            val breakdownByPackageTable = document.selectFirst("h3:contains(Breakdown by Package) + table")
            if (breakdownByPackageTable != null) {
                val rows = breakdownByPackageTable.select("tbody tr")
                if (rows.isNotEmpty()) {
                    for (row in rows) {
                        val packageResult = PackageResult(
                            extractPackageNameFromRow(row),
                            extractPackageHrefFromRow(row),
                            extractMutationResultFromRow(row)
                        )
                        overallResultData.addPackageResult(packageResult)
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return overallResultData
    }

    private fun extractOverallMutationResultFromRow(row: Element?): MutationResult {
        val cells = row?.select("td") ?: emptyList()

        val numberOfClasses = cells.getOrNull(0)?.text() ?: "N/A"
        val lineCoverage = cells.getOrNull(1)?.text() ?: "N/A"
        val mutationCoverage = cells.getOrNull(2)?.text() ?: "N/A"
        val testStrength = cells.getOrNull(3)?.text() ?: "N/A"

        return MutationResult(numberOfClasses, lineCoverage, mutationCoverage, testStrength)
    }

    private fun extractMutationResultFromRow(row: Element?): MutationResult {
        val cells = row?.select("td") ?: emptyList()

        val numberOfClasses = cells.getOrNull(1)?.text() ?: "N/A"
        val lineCoverage = cells.getOrNull(2)?.text() ?: "N/A"
        val mutationCoverage = cells.getOrNull(3)?.text() ?: "N/A"
        val testStrength = cells.getOrNull(4)?.text() ?: "N/A"

        return MutationResult(numberOfClasses, lineCoverage, mutationCoverage, testStrength)
    }

    private fun extractPackageNameFromRow(row: Element?): String {
        val packageElement = row?.selectFirst("td a")
        return packageElement?.text() ?: "N/A"
    }

    private fun extractPackageHrefFromRow(row: Element?): String {
        val packageElement = row?.selectFirst("td a")
        return packageElement?.attr("href") ?: "N/A"
    }

    data class OverallResultData(
        var overallMutationResult: MutationResult? = null,
        val packageResults: MutableList<PackageResult> = mutableListOf()
    ) {
        fun addPackageResult(packageResult: PackageResult) {
            packageResults.add(packageResult)
        }
    }

    data class MutationResult(
        val numberOfClasses: String,
        val lineCoverage: String,
        val mutationCoverage: String,
        val testStrength: String
    )

    data class PackageResult(
        val packageName: String,
        val packageHref: String,
        val mutationResult: MutationResult
    )
}
