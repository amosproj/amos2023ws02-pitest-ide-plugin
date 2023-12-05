// SPDX-License-Identifier: MIT
// SPDX-FileCopyrightText: 2023 Liam Fogarty <lfogarty9995@gmail.com>, Tim Herzig <tim.herzig@hotmail.com>

package com.amos.pitmutationmate.pitmutationmate.reporting

import com.amos.pitmutationmate.pitmutationmate.reporting.XMLParser.ResultData
import com.intellij.openapi.editor.Editor
import HighlightGutterRenderer
import java.nio.file.*

class XMLListener(private var dir: Path, private var editor: Editor) {
    private lateinit var result: ResultData

    fun listen() {
        val pwd: String? = editor.project?.basePath?.let { Paths.get(it).toAbsolutePath().toString() }
        this.dir = Paths.get(pwd.toString(), this.dir.toString())
        // TODO: implement listening to changes in report directory
        loadResults()
        displayResults()
    }

    private fun loadResults() {
        val parser: XMLParser = XMLParser()
        result = parser.loadResultsFromXmlReport(this.dir.toString())
    }

    fun displayResults() {
        for (r in result.mutationResults) {
            val color = if (r.detected) "dark-green" else "dark-pink"
            if (r.lineNumber > -1) {
                println(r.toString())
                HighlightGutterRenderer.GutterHighlighter.addBar(this.editor, color, r.lineNumber - 1)
            }
        }
    }
}
