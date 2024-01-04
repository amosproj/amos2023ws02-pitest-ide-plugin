// SPDX-License-Identifier: MIT
// SPDX-FileCopyrightText: 2023 Lennart Heimbs

package com.amos.pitmutationmate.pitmutationmate.editor

import com.amos.pitmutationmate.pitmutationmate.reporting.XMLParser
import com.amos.pitmutationmate.pitmutationmate.services.MutationResultService
import com.amos.pitmutationmate.pitmutationmate.utils.PitestSeverity
import com.amos.pitmutationmate.pitmutationmate.visualization.HighlightGutterRenderer
import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.ExternalAnnotator
import com.intellij.openapi.components.service
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.editor.Document
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiDocumentManager
import com.intellij.psi.PsiFile

class MutationsAnnotator :
    ExternalAnnotator<List<XMLParser.MutationResult>, Map<Int, List<XMLParser.MutationResult>>>() {

    object Util {
        private fun isWhitespace(document: Document, offset: Int, endLineOffset: Int): Boolean {
            return offset < endLineOffset && Character.isWhitespace(document.charsSequence[offset])
        }

        fun getContentOffset(document: Document, lineNr: Int): TextRange {
            val lineStartOffset = document.getLineStartOffset(lineNr)
            val lineEndOffset = document.getLineEndOffset(lineNr)
            var contentStartOffset = lineStartOffset
            // Find the first non-whitespace character to exclude the leading indentation
            while (isWhitespace(document, contentStartOffset, lineEndOffset)) {
                contentStartOffset++
            }
            return TextRange(contentStartOffset, lineEndOffset)
        }

        fun getMessage(mutationResults: List<XMLParser.MutationResult>, isTooltip: Boolean): String {
            // TODO: add icon, link mutation result and improve formatting
            val separator = if (isTooltip) "<br/>" else ", "
            return mutationResults.mapIndexed { index, it -> "${index + 1}. ${it.description} → ${it.status}" }
                .joinToString(separator)
        }

        fun formatTooltipMessage(message: String): String {
            return """
                <html>
                     <body style="font-family: 'Arial', sans-serif; font-size: 12px;">
                        <h3>PiTest:</h3>
                        <p>$message</p>
                    </body>
                </html>
            """.trimIndent()
        }
    }

    companion object {
        private val log = Logger.getInstance(MutationsAnnotator::class.java)
    }

    override fun collectInformation(file: PsiFile): List<XMLParser.MutationResult> {
        log.debug("collectInformation")
        val resultGenerator = file.project.service<MutationResultService>()
        return resultGenerator.getMutationResult().mutationResults.filter { it.sourceFile == file.name }
    }

    override fun doAnnotate(
        annotationResult: List<XMLParser.MutationResult>
    ): Map<Int, List<XMLParser.MutationResult>> {
        log.debug("doAnnotate")

        // map fileResults by lineNumber into a map
        return annotationResult.groupBy { it.lineNumber }
    }

    override fun apply(
        file: PsiFile,
        annotationResult: Map<Int, List<XMLParser.MutationResult>>,
        holder: AnnotationHolder
    ) {
        log.debug("apply")
        val document: Document = PsiDocumentManager.getInstance(file.project).getDocument(file) ?: return

        for (r in annotationResult) {
            val lineRange = Util.getContentOffset(document, r.key - 1)
            val severity = PitestSeverity.fromMutationResults(r.value)
            holder.newAnnotation(
                severity.highlightSeverity(),
                Util.getMessage(r.value, false)
            ).range(lineRange)
                .tooltip(Util.formatTooltipMessage(Util.getMessage(r.value, true)))
                .highlightType(severity.highlightType())
                .gutterIconRenderer(HighlightGutterRenderer(severity.gutterIcon()))
                .create()
        }
    }
}
