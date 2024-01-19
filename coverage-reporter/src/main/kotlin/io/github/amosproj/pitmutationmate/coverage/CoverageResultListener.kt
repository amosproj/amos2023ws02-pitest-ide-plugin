// SPDX-License-Identifier: MIT
// SPDX-FileCopyrightText: 2023

package io.github.amosproj.pitmutationmate.coverage

import org.apache.commons.text.StringEscapeUtils
import org.pitest.coverage.ReportCoverage
import org.pitest.mutationtest.ClassMutationResults
import org.pitest.mutationtest.MutationResultListener
import org.pitest.mutationtest.report.html.MutationHtmlReportListener
import org.pitest.mutationtest.report.html.MutationTotals
import org.pitest.util.ResultOutputStrategy
import org.pitest.util.Unchecked
import java.io.IOException
import java.io.Writer

internal enum class Tag {
    FileName, PackageName, MutatedClass, LineCoverage, LineCoveragePercentage, MutationCoverage,
    MutationCoveragePercentage, TestStrengthPercentage, TestStrength
}

class CoverageResultListener(
    private var htmlListener: MutationResultListener,
    private var coverage: ReportCoverage?,
    outputStrategy: ResultOutputStrategy?
) : MutationResultListener {

    private var out: Writer? = null
    private var totals: MutationTotals? = null

    init {
        this.totals = MutationTotals()
        out = outputStrategy?.createWriterForFile("coverageInformation.xml")
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
        if (htmlListener is MutationHtmlReportListener) {
            val testMetaData = (htmlListener as MutationHtmlReportListener).createSummaryData(coverage, results)
            val classTotals = testMetaData.totals
            totals?.add(classTotals)
            write("<testMetaData>")
            if (results != null) {
                write(makeNode(results.fileName, Tag.FileName))
                write(makeNode(results.packageName, Tag.PackageName))
                write(makeNode(results.mutatedClass.toString(), Tag.MutatedClass))
            }
            writeMetaDataNode(classTotals)
            write("</testMetaData>\n")
        }
    }

    override fun runEnd() {
        try {
            write("<totalMetaData>")
            totals?.let { writeMetaDataNode(it) }
            write("</totalMetaData>\n")
            write("</coverageInformation>\n")
            out!!.close()
        } catch (e: IOException) {
            throw Unchecked.translateCheckedException(e)
        }
    }
}
