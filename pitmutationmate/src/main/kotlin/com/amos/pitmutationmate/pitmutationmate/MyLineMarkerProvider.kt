// SPDX-FileCopyrightText: 2023 2023
//
// SPDX-License-Identifier: MIT

package com.amos.pitmutationmate.pitmutationmate

import com.intellij.codeInsight.daemon.LineMarkerInfo
import com.intellij.codeInsight.daemon.LineMarkerProvider
import com.intellij.openapi.editor.impl.DocumentMarkupModel
import com.intellij.openapi.editor.markup.GutterIconRenderer
import com.intellij.openapi.editor.markup.HighlighterLayer
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.psi.PsiDocumentManager
import com.intellij.psi.PsiElement
import com.intellij.psi.util.elementType
import java.awt.Color
import java.awt.Component
import java.awt.Graphics
import java.util.function.Supplier
import javax.swing.Icon


class MyLineMarkerProvider : LineMarkerProvider {

    override fun getLineMarkerInfo(element: PsiElement): LineMarkerInfo<PsiElement>? {
        // TODO: We need to get the Information from the report which lines we should highlight and with which color.
        // We need to think about how to store it.
        // Performance issue if we check for every element if it should be highlighted? HashMap?
        //TODO Test how highlighting looks when other gutters are active (git, coverage, run button,...)

        if(!element.elementType.toString().contains("EXPRESSION_STATEMENT")) {
            return null
        }
        if(getLineNumber(element) > 0) { // check if linenumber should be highlighted with information from report
            println(element.elementType)
            return LineMarkerInfo(element,
                element.textRange,
                ColoredBarIcon(Color.GREEN, 10, 21),
                null,
                null,
                GutterIconRenderer.Alignment.LEFT,
                Supplier { "TEST" }
            )
         }

        return null
    }

    private fun getLineNumber(element: PsiElement): Int {
        var doc = PsiDocumentManager.getInstance(element.project).getDocument(element.containingFile) ?: return -1
        try {

            var ed = FileEditorManager.getInstance(element.project).allEditors
            println(ed.size)
            var mm = DocumentMarkupModel.forDocument(doc, element.project, true)
            mm.addLineHighlighter(1, HighlighterLayer.FIRST, null)
        } catch (e: Exception) {
            println("EXcpetion" + element.containingFile.virtualFile)
        }

        //(doc).addLineHighlighter(null, 0, 0)
        return doc.getLineNumber(element.textOffset) + 1
    }

    private class ColoredBarIcon(color: Color, width: Int, height: Int) : Icon {
        private val color: Color
        private val widthI : Int
        private val heightI: Int

        init {
            this.color = color
            this.widthI = width
            this.heightI = height
        }

        override fun paintIcon(c: Component?, g: Graphics, x: Int, y: Int) {
            val g2d = g
            g2d.color = color
            g2d.fillRect(x, y, widthI, heightI)
        }

        override fun getIconWidth(): Int {
            return widthI
        }

        override fun getIconHeight(): Int {
            return heightI
        }
    }


}
