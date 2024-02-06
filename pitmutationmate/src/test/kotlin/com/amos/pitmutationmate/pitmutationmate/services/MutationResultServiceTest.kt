// SPDX-License-Identifier: MIT
// SPDX-FileCopyrightText: 2023

package com.amos.pitmutationmate.pitmutationmate.services

import com.amos.pitmutationmate.pitmutationmate.reporting.XMLParser
import com.intellij.testFramework.fixtures.LightJavaCodeInsightFixtureTestCase5
import com.intellij.testFramework.registerServiceInstance
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import java.nio.file.Path

class MutationResultServiceTest : LightJavaCodeInsightFixtureTestCase5() {

    private lateinit var pathGeneratorMock: ReportPathGeneratorService

    @BeforeEach
    fun setUp() {
        // Mock the ReportPathGeneratorService service
        pathGeneratorMock = mock(ReportPathGeneratorService::class.java)
        fixture.project.registerServiceInstance(ReportPathGeneratorService::class.java, pathGeneratorMock)
    }

    override fun getTestDataPath(): String {
        return "src/test/resources/test_MutationResultService"
    }

    @ParameterizedTest
    @ValueSource(strings = ["both_missing", "coverage_missing", "mutations_missing"])
    fun `getMutationResult returns null when any one report file is not present`(testFolder: String) {
        `when`(pathGeneratorMock.getReportMutationsFile()).thenReturn(Path.of("src/test/resources/test_MutationResultService/no_results/$testFolder/mutations.xml"))
        `when`(pathGeneratorMock.getReportCoverageFile()).thenReturn(Path.of("src/test/resources/test_MutationResultService/no_results/$testFolder/coverageInformation.xml"))

        val project = fixture.project
        val mutationResultService = MutationResultService(project)
        val mutationResult = mutationResultService.getMutationResult()
        Assertions.assertNull(mutationResult)
    }

    @ParameterizedTest
    @ValueSource(strings = ["malformed_results/both_malformed", "malformed_results/coverage_malformed", "malformed_results/mutations_malformed", "empty_results"])
    fun `getMutationResult returns empty results when any one report file is malformed or the report files have empty root nodes`(testFolder: String) {
        `when`(pathGeneratorMock.getReportMutationsFile()).thenReturn(Path.of("src/test/resources/test_MutationResultService/$testFolder/mutations.xml"))
        `when`(pathGeneratorMock.getReportCoverageFile()).thenReturn(Path.of("src/test/resources/test_MutationResultService/$testFolder/coverageInformation.xml"))

        val project = fixture.project
        val mutationResultService = MutationResultService(project)
        val mutationResult = mutationResultService.getMutationResult()
        Assertions.assertNotNull(mutationResult)
        Assertions.assertArrayEquals(emptyList<XMLParser.MutationResult>().toTypedArray(), mutationResult!!.mutationResults.toTypedArray())
        Assertions.assertArrayEquals(emptyList<XMLParser.CoverageReport>().toTypedArray(), mutationResult.coverageReports.toTypedArray())
        Assertions.assertNull(mutationResult.totalResult)
    }

    @Test
    fun `getMutationResult returns results when the report files have valid data`() {
        `when`(pathGeneratorMock.getReportMutationsFile()).thenReturn(Path.of("src/test/resources/test_MutationResultService/valid_results/mutations.xml"))
        `when`(pathGeneratorMock.getReportCoverageFile()).thenReturn(Path.of("src/test/resources/test_MutationResultService/valid_results/coverageInformation.xml"))

        val project = fixture.project
        val mutationResultService = MutationResultService(project)
        val mutationResult = mutationResultService.getMutationResult()
        Assertions.assertNotNull(mutationResult)
        Assertions.assertArrayEquals(
            listOf(
                XMLParser.MutationResult(
                    detected = true,
                    status = "KILLED",
                    numberOfTestsRun = 1,
                    sourceFile = "Presenter.java",
                    mutatedClass = "de.esolutions.pitest.showcase.Presenter",
                    mutatedMethod = "<init>",
                    methodDescription = "(Lde/esolutions/pitest/showcase/View;Lde/esolutions/pitest/showcase/Model;)V",
                    lineNumber = 10,
                    mutator = "org.pitest.mutationtest.engine.gregor.mutators.VoidMethodCallMutator",
                    indexes = listOf(15),
                    blocks = listOf(1),
                    killingTest = "de.esolutions.pitest.showcase.PresenterTest.[engine:junit-jupiter]/[class:de.esolutions.pitest.showcase.PresenterTest]/[method:shouldSetDataCorrectlyWhenFilterIsSet()]",
                    description = "removed call to de/esolutions/pitest/showcase/View::onFilterSelected"
                ),
                XMLParser.MutationResult(
                    detected = true,
                    status = "KILLED",
                    numberOfTestsRun = 2,
                    sourceFile = "Presenter.java",
                    mutatedClass = "de.esolutions.pitest.showcase.Presenter",
                    mutatedMethod = "<init>",
                    methodDescription = "(Lde/esolutions/pitest/showcase/View;Lde/esolutions/pitest/showcase/Model;)V",
                    lineNumber = 12,
                    mutator = "org.pitest.mutationtest.engine.gregor.mutators.VoidMethodCallMutator",
                    indexes = listOf(22),
                    blocks = listOf(2),
                    killingTest = "de.esolutions.pitest.showcase.PresenterTest.[engine:junit-jupiter]/[class:de.esolutions.pitest.showcase.PresenterTest]/[method:shouldSetDataCorrectlyWhenNoFilterIsSet()]",
                    description = "removed call to de/esolutions/pitest/showcase/Model::onDataUpdated"
                ),
                XMLParser.MutationResult(
                    detected = false,
                    status = "SURVIVED",
                    numberOfTestsRun = 1,
                    sourceFile = "Presenter.java",
                    mutatedClass = "de.esolutions.pitest.showcase.Presenter",
                    mutatedMethod = "calculateValue",
                    methodDescription = "(Ljava/util/List;)I",
                    lineNumber = 24,
                    mutator = "org.pitest.mutationtest.engine.gregor.mutators.returns.PrimitiveReturnsMutator",
                    indexes = listOf(23),
                    blocks = listOf(6),
                    killingTest = "",
                    description = "replaced int return with 0 for de/esolutions/pitest/showcase/Presenter::calculateValue"
                )
            ).toTypedArray(),
            mutationResult!!.mutationResults.toTypedArray()
        )
        Assertions.assertArrayEquals(
            listOf(
                XMLParser.CoverageReport(
                    fileName = "Presenter.java",
                    packageName = "de.esolutions.pitest.showcase",
                    lineCoveragePercentage = 100,
                    lineCoverageTextRatio = "19/19",
                    mutationCoveragePercentage = 23,
                    mutationCoverageTextRatio = "3/13",
                    testStrengthPercentage = 25,
                    testStrengthTextRatio = "3/12",
                    numberOfClasses = 1
                )
            ).toTypedArray(),
            mutationResult.coverageReports.toTypedArray()
        )
        Assertions.assertNotNull(mutationResult.totalResult)
        Assertions.assertEquals(
            XMLParser.CoverageReport(
                fileName = "totals",
                packageName = "totals",
                lineCoveragePercentage = 100,
                lineCoverageTextRatio = "19/19",
                mutationCoveragePercentage = 23,
                mutationCoverageTextRatio = "3/13",
                testStrengthPercentage = 25,
                testStrengthTextRatio = "3/12",
                numberOfClasses = 1
            ),
            mutationResult.totalResult
        )
    }

    @Test
    fun `getMutationResult returns results when the report files have valid data for multiple classes`() {
        `when`(pathGeneratorMock.getReportMutationsFile()).thenReturn(Path.of("src/test/resources/test_MutationResultService/valid_mulitclass_results/mutations.xml"))
        `when`(pathGeneratorMock.getReportCoverageFile()).thenReturn(Path.of("src/test/resources/test_MutationResultService/valid_mulitclass_results/coverageInformation.xml"))

        val project = fixture.project
        val mutationResultService = MutationResultService(project)
        val mutationResult = mutationResultService.getMutationResult()

        Assertions.assertNotNull(mutationResult)
        Assertions.assertEquals(23, mutationResult!!.mutationResults.size)
        val expected = listOf(
            "isPalindrome:9:KILLED",
            "isPalindrome:10:KILLED",
            "isPalindrome:5:KILLED",
            "isPalindrome:11:KILLED",
            "isPalindrome:11:KILLED",
            "isPalindrome:5:SURVIVED",
            "isPalindrome:11:SURVIVED",
            "isPalindrome:11:SURVIVED",
            "isPalindrome:6:KILLED",
            "isPalindrome:11:SURVIVED",
            "getRecipe:16:SURVIVED",
            "getRecipe:16:KILLED",
            "getRecipe:17:KILLED",
            "getRecipe:20:KILLED",
            "lambda\$getRecipe\$0:22:KILLED",
            "lambda\$getRecipe\$0:22:KILLED",
            "getRecipe:16:SURVIVED",
            "getRecipe:16:KILLED",
            "getRecipe:17:NO_COVERAGE",
            "getRecipe:20:KILLED",
            "lambda\$getRecipe\$0:22:KILLED",
            "lambda\$getRecipe\$0:22:KILLED",
            "doNothing:19:NO_COVERAGE"
        )
        val actual = mutationResult.mutationResults.map { "${it.mutatedMethod}:${it.lineNumber}:${it.status}" }
        Assertions.assertEquals(expected, actual)
        Assertions.assertEquals(4, mutationResult.coverageReports.size)
        Assertions.assertNotNull(mutationResult.totalResult)
        Assertions.assertEquals(4, mutationResult.totalResult!!.numberOfClasses)
    }
}
