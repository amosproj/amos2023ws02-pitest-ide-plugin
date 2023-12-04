// SPDX-License-Identifier: MIT
// SPDX-FileCopyrightText: 2023 Liam Fogarty <lfogarty9995@gmail.com>, Tim Herzig <tim.herzig@hotmail.com>

package com.amos.pitmutationmate.pitmutationmate.reporting

import HighlightGutterRenderer
import com.amos.pitmutationmate.pitmutationmate.reporting.XMLParser.ResultData
import com.intellij.openapi.editor.Editor
import java.nio.file.*

class XMLListener(private var dir: Path, private var editor: Editor) {
    private lateinit var result: ResultData

    fun listen() {
        val pwd: String? = editor.project?.basePath?.let { Paths.get(it).toAbsolutePath().toString() }
        this.dir = Paths.get(pwd.toString(), this.dir.toString())

        try {
            val fileSystem = FileSystems.getDefault()
            val watchService: WatchService = fileSystem.newWatchService()

            dir.register(
                watchService,
                StandardWatchEventKinds.ENTRY_CREATE
            )

            println("Watching " + dir.toString())

            while (true) {
                val key = watchService.take()

                for (event in key.pollEvents()) {
                    val kind = event.kind()

                    if (kind == StandardWatchEventKinds.ENTRY_CREATE) {
                        // This is temporary for demonstration purposes.
                        this.dir = Paths.get(pwd.toString(), "build","reports","pitest", "test", "mutations.xml")
                        loadResults()
                        displayResults()

                        break
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun loadResults() {
        val parser: XMLParser = XMLParser()
        result = parser.loadResultsFromXmlReport(this.dir.toString())
    }

    fun displayResults() {
        for (r in result.mutationResults) {
            val color = if (r.detected) "green" else "red"
            if (r.lineNumber > -1) {
                println(r.toString())
                HighlightGutterRenderer.GutterHighlighter.addBar(this.editor, color, r.lineNumber-1)
            }
        }
    }
}
