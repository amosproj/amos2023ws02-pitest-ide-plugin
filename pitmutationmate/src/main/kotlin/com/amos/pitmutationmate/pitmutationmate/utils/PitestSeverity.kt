// SPDX-License-Identifier: MIT
// SPDX-FileCopyrightText: 2023 Lennart Heimbs

package com.amos.pitmutationmate.pitmutationmate.utils

import com.amos.pitmutationmate.pitmutationmate.reporting.XMLParser
import com.intellij.codeInspection.ProblemHighlightType
import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.openapi.editor.colors.CodeInsightColors
import com.intellij.openapi.editor.colors.TextAttributesKey

enum class PitestSeverity(
    private val textAttributes: TextAttributesKey,
    private val highlightType: ProblemHighlightType,
    private val highlightSeverity: HighlightSeverity
) {
    HIGH(
        CodeInsightColors.INFORMATION_ATTRIBUTES,
        ProblemHighlightType.WARNING,
        HighlightSeverity.WARNING
    ),
    MEDIUM(CodeInsightColors.INFORMATION_ATTRIBUTES, ProblemHighlightType.WARNING, HighlightSeverity.WEAK_WARNING),
    LOW(CodeInsightColors.INFORMATION_ATTRIBUTES, ProblemHighlightType.INFORMATION, HighlightSeverity.INFORMATION);

    companion object {
        fun fromMutationResults(mutationResults: List<XMLParser.MutationResult>): PitestSeverity {
            val allSurvived = mutationResults.all { it.status == "SURVIVED" }
            val someSurvived = mutationResults.any { it.status == "SURVIVED" }

            return when {
                allSurvived -> HIGH
                someSurvived -> MEDIUM
                else -> LOW
            }
        }
    }

    fun defaultTextAttributes(): TextAttributesKey {
        return textAttributes
    }

    fun highlightType(): ProblemHighlightType {
        return highlightType
    }

    fun highlightSeverity(): HighlightSeverity {
        return highlightSeverity
    }

    fun gutterIcon(): String {
        return when (this) {
            HIGH -> "red"
            MEDIUM -> "yellow"
            LOW -> "green"
        }
    }
}
