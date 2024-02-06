// SPDX-License-Identifier: MIT
// SPDX-FileCopyrightText: 2023

package io.github.amosproj.pitmutationmate.coverage

import org.apache.commons.text.StringEscapeUtils
import org.pitest.coverage.ReportCoverage
import org.pitest.mutationtest.ClassMutationResults
import org.pitest.mutationtest.MutationResultListener
import org.pitest.mutationtest.report.html.MutationHtmlReportListener
import org.pitest.mutationtest.report.html.MutationTestSummaryData
import org.pitest.mutationtest.report.html.MutationTotals
import org.pitest.mutationtest.report.html.PackageSummaryMap
import org.pitest.util.ResultOutputStrategy
import org.pitest.util.Unchecked
import java.io.IOException
import java.io.Writer

internal enum class Tag {
    FileName, PackageName, LineCoverage, LineCoveragePercentage, MutationCoverage,
    MutationCoveragePercentage, TestStrengthPercentage, TestStrength, NumberOfClasses
}

class CoverageResultListener(
    private var htmlListener: MutationResultListener,
    private var coverage: ReportCoverage?,
    outputStrategy: ResultOutputStrategy?
) : MutationResultListener {

    private var out: Writer? = null
    private var totals: MutationTotals = MutationTotals()
    private var packageSummaryData: PackageSummaryMap = PackageSummaryMap()
    private var fileSummaryData = HashMap<String, MutationTestSummaryData>()

    init {
        this.out = outputStrategy?.createWriterForFile("coverageInformation.xml")
    }

    private fun writeMetaDataNode(classTotals: MutationTotals) {
        write(makeNode(clean(classTotals.lineCoverage.toString()), Tag.LineCoveragePercentage))
        val lineCoverageTextRatio = classTotals.numberOfLinesCovered.toString() + "/" + classTotals.numberOfLines
        write(makeNode(clean(lineCoverageTextRatio), Tag.LineCoverage))

        write(makeNode(clean(classTotals.mutationCoverage.toString()), Tag.MutationCoveragePercentage))
        val mutationCoverageTextRatio = classTotals.numberOfMutationsDetected.toString() + "/" + classTotals.numberOfMutations
        write(makeNode(clean(mutationCoverageTextRatio), Tag.MutationCoverage))

        write(makeNode(clean(classTotals.testStrength.toString()), Tag.TestStrengthPercentage))
        val testStrengthTextRatio = classTotals.numberOfMutationsDetected.toString() + "/" + classTotals.numberOfMutationsWithCoverage
        write(makeNode(clean(testStrengthTextRatio), Tag.TestStrength))
    }

    private fun clean(value: String): String {
        return StringEscapeUtils.escapeXml11(value)
    }

    private fun makeNode(value: String?, tag: Tag): String {
        return if (value != null) {
            "<$tag>$value</$tag>"
        } else {
            "<$tag/>"
        }
    }
    private fun write(value: String) {
        try {
            out!!.write(value)
        } catch (e: IOException) {
            throw Unchecked.translateCheckedException(e)
        }
    }

    override fun runStart() {
        write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n")
        write("<coverageInformation>\n")
    }

    override fun handleMutationResult(results: ClassMutationResults?) {
        if (results == null) {
            return
        }
        if (htmlListener is MutationHtmlReportListener) {
            val testMetaData = (htmlListener as MutationHtmlReportListener).createSummaryData(coverage, results)
            val classTotals = testMetaData.totals
            totals.add(classTotals)
            packageSummaryData.update(results.packageName, testMetaData)
            fileSummaryData[results.fileName]?.add(testMetaData) ?: run {
                fileSummaryData[results.fileName] = testMetaData
            }
        }
    }

    override fun runEnd() {
        try {
            writeFileSummaries()
            writePackageSummaries()
            writeTotals()
            out!!.close()
        } catch (e: IOException) {
            throw Unchecked.translateCheckedException(e)
        }
    }

    private fun writeFileSummaries() {
        for(fileSummary in fileSummaryData.values) {
            write("<testMetaData>")
            write(makeNode(fileSummary.fileName, Tag.FileName))
            write(makeNode(fileSummary.packageName, Tag.PackageName))
            writeMetaDataNode(fileSummary.totals)
            write("</testMetaData>\n")
        }
    }

    private fun writeTotals() {
        write("<totalMetaData>")
        writeMetaDataNode(totals)
        write("</totalMetaData>\n")
        write("</coverageInformation>\n")
    }

    private fun writePackageSummaries() {
        for (packageData in packageSummaryData.values()) {
            write("<packageMetaData>")
            write(makeNode(packageData.packageName, Tag.PackageName))
            write(makeNode(packageData.summaryData.size.toString(), Tag.NumberOfClasses))
            writeMetaDataNode(packageData.totals)
            write("</packageMetaData>\n")
        }
    }
}
