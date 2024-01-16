// SPDX-FileCopyrightText: 2023 2023
//
// SPDX-License-Identifier: MIT

package com.amos.pitmutationmate.pitmutationmate.visualization.treetable

import java.awt.Dimension
import java.util.*
import javax.swing.JTable

class TreeTable(treeTableModel: AbstractTreeTableModel?) : JTable() {
    private val tree: TreeTableCellRenderer

    init {
        // JTree erstellen.
        tree = TreeTableCellRenderer(this, treeTableModel)

        // Modell setzen.
        super.setModel(TreeTableModelAdapter(treeTableModel!!, tree))

        // Gleichzeitiges Selektieren fuer Tree und Table.
        val selectionModel = TreeTableSelectionModel()
        tree.selectionModel = selectionModel // For the tree
        setSelectionModel(selectionModel.getListSelectionModel()) // For the table

        // Renderer fuer den Tree.
        setDefaultRenderer(TreeTableModel::class.java, tree)
        // Editor fuer die TreeTable
        setDefaultEditor(TreeTableModel::class.java, TreeTableCellEditor(tree, this))

        // Kein Grid anzeigen.
        setShowGrid(false)

        // Keine Abstaende.
        intercellSpacing = Dimension(0, 0)
    }

    override fun editCellAt(row: Int, column: Int): Boolean {
        if (column > 0) {
            return false
        }
        return super.editCellAt(row, column)
    }

    override fun editCellAt(row: Int, column: Int, e: EventObject?): Boolean {
        if (column > 0) {
            return false
        }
        return super.editCellAt(row, column, e)
    }
}
