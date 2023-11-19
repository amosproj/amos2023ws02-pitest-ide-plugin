package com.amos.pitmutationmate.pitmutationmate.visualization

import com.intellij.openapi.editor.markup.RangeHighlighter

import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.markup.HighlighterLayer
import com.intellij.openapi.editor.markup.HighlighterTargetArea
import com.intellij.openapi.editor.markup.TextAttributes
import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.fileEditor.FileEditorManagerEvent
import com.intellij.openapi.fileEditor.FileEditorManagerListener
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Disposer
import com.intellij.openapi.vfs.VirtualFile
import java.awt.Color

class EditorLineHighlighter(private val project: Project) {
    private var highlighters: MutableMap<VirtualFile, MutableList<RangeHighlighter>> = mutableMapOf()

    init {
        // Register a listener to handle editor changes
        project.messageBus.connect().subscribe(FileEditorManagerListener.FILE_EDITOR_MANAGER, object : FileEditorManagerListener {
            override fun fileClosed(source: FileEditorManager, file: VirtualFile) {
                // Remove highlighters when a file is closed
//                removeHighlighters(file)
            }

            override fun fileOpened(source: FileEditorManager, file: VirtualFile) {
                // Add your logic when a file is opened, if needed
            }

            override fun selectionChanged(event: FileEditorManagerEvent) {
                // Add your logic when the selection changes, if needed
            }
        })
    }

    fun colorLine(editor: Editor, lineNumber: Int) {
        val virtualFile = FileDocumentManager.getInstance().getFile(editor.document)
        val markupModel = editor.markupModel
        val document = editor.document

        val lineStartOffset = document.getLineStartOffset(lineNumber)
        val lineEndOffset = document.getLineEndOffset(lineNumber)

        // Create TextAttributes with the desired properties (e.g., red foreground color)
        val textAttributes = TextAttributes()
        textAttributes.foregroundColor = Color.RED

        // Create a RangeHighlighter for the entire line
        val highlighter: RangeHighlighter = markupModel.addRangeHighlighter(
            lineStartOffset, lineEndOffset,
            HighlighterLayer.CARET_ROW, textAttributes,
            HighlighterTargetArea.LINES_IN_RANGE
        )

        // Store the highlighter instance in a map for later reference
        if (virtualFile != null) {
            highlighters.computeIfAbsent(virtualFile) { mutableListOf() }.add(highlighter)
        }
    }

    private fun removeHighlighters(file: VirtualFile) {
        highlighters[file]?.let {
//            it.forEach { Disposer.dispose(it) }
            println("Removing ${it.toString()}")
            it.clear()
        }
        highlighters.remove(file)
    }
}
