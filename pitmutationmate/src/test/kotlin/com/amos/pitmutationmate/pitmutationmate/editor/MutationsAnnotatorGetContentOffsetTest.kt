// SPDX-License-Identifier: MIT
// SPDX-FileCopyrightText: 2023 Lennart Heimbs

package com.amos.pitmutationmate.pitmutationmate.editor

import com.intellij.openapi.application.runReadAction
import com.intellij.openapi.editor.Document
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.testFramework.fixtures.LightJavaCodeInsightFixtureTestCase5
import org.jetbrains.kotlin.idea.core.util.toPsiFile
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource

class MutationsAnnotatorGetContentOffsetTest : LightJavaCodeInsightFixtureTestCase5() {

    @Override
    override fun getTestDataPath(): String {
        return "src/test/resources"
    }

    private lateinit var virtualFile: VirtualFile

    @ParameterizedTest(name = "Content offset for {0}, line {1}: Start = {2}, End = {3}")
    @CsvSource(
        "test_source_files/sample.kt,   3,  80, 105",
        "test_source_files/sample.kt,   6, 170, 182",
        "test_source_files/sample.kt,   7, 191, 226",
        "test_source_files/sample.kt,   8, 231, 232",
        "test_source_files/sample.java, 4, 107, 136",
        "test_source_files/sample.java, 5, 141, 173",
        "test_source_files/sample.java, 6, 182, 221",
        "test_source_files/sample.java, 7, 226, 227"
    )
    fun `test content offset is calculated correctly`(filePath: String, lineNumber: Int, expectedStart: Int, expectedEnd: Int) {
        virtualFile = fixture.copyFileToProject(filePath)
        val kotlinDocument = getDocumentFromPsiFile(virtualFile)
        val range = MutationsAnnotator.Util.getContentOffset(kotlinDocument, lineNumber)

        assertNotNull(range)
        assertEquals(expectedStart, range.startOffset)
        assertEquals(expectedEnd, range.endOffset)
    }

    private fun getDocumentFromPsiFile(file: VirtualFile): Document {
        return runReadAction {
            val psiFile = file.toPsiFile(fixture.project)
            assertNotNull(psiFile)
            val document = fixture.getDocument(psiFile!!)
            assertNotNull(document)
            document
        }
    }
}
