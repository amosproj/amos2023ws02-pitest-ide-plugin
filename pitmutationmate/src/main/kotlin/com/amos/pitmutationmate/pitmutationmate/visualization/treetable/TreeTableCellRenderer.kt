// SPDX-FileCopyrightText: 2023 2023
//
// SPDX-License-Identifier: MIT

package com.amos.pitmutationmate.pitmutationmate.visualization.treetable

import java.awt.Component
import java.awt.Graphics
import javax.swing.JTable
import javax.swing.JTree
import javax.swing.table.TableCellRenderer
import javax.swing.tree.TreeModel


class TreeTableCellRenderer(treeTable: TreeTable?, model: TreeModel?) : JTree(model),
    TableCellRenderer {
    /** Die letzte Zeile, die gerendert wurde.  */
    protected var visibleRow = 0
    private val treeTable: TreeTable?

    init {
        this.treeTable = treeTable

        // Setzen der Zeilenhoehe fuer die JTable
        // Muss explizit aufgerufen werden, weil treeTable noch
        // null ist, wenn super(model) setRowHeight aufruft!
        setRowHeight(getRowHeight())
    }

    /**
     * Tree und Table muessen die gleiche Hoehe haben.
     */
    override fun setRowHeight(rowHeight: Int) {
        if (rowHeight > 0) {
            super.setRowHeight(rowHeight)
            if (treeTable != null && treeTable.rowHeight !== rowHeight) {
                treeTable.setRowHeight(getRowHeight())
            }
        }
    }

    /**
     * Tree muss die gleiche Hoehe haben wie Table.
     */
    override fun setBounds(x: Int, y: Int, w: Int, h: Int) {
        treeTable?.let { super.setBounds(x, 0, w, it.height) }
    }

    /**
     * Sorgt fuer die Einrueckung der Ordner.
     */
    override fun paint(g: Graphics) {
        g.translate(0, -visibleRow * getRowHeight())
        super.paint(g)
    }

    /**
     * Liefert den Renderer mit der passenden Hintergrundfarbe zurueck.
     */
    override fun getTableCellRendererComponent(
        table: JTable,
        value: Any,
        isSelected: Boolean,
        hasFocus: Boolean,
        row: Int,
        column: Int
    ): Component {
        if (isSelected) setBackground(table.selectionBackground) else setBackground(table.getBackground())
        visibleRow = row
        return this
    }
}
