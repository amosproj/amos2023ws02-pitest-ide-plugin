// SPDX-FileCopyrightText: 2023 2023
//
// SPDX-License-Identifier: MIT

package com.amos.pitmutationmate.pitmutationmate.visualization.treetable

import javax.swing.JTree
import javax.swing.event.TreeExpansionEvent
import javax.swing.event.TreeExpansionListener
import javax.swing.table.AbstractTableModel


class TreeTableModelAdapter(var treeTableModel: AbstractTreeTableModel, var tree: JTree) : AbstractTableModel() {
    init {
        tree.addTreeExpansionListener(object : TreeExpansionListener {
            override fun treeExpanded(event: TreeExpansionEvent) {
                fireTableDataChanged()
            }

            override fun treeCollapsed(event: TreeExpansionEvent) {
                fireTableDataChanged()
            }
        })
    }

    override fun getColumnCount(): Int {
        return treeTableModel.columnCount
    }

    override fun getColumnName(column: Int): String {
        return treeTableModel.getColumnName(column)!!
    }

    override fun getColumnClass(column: Int): Class<*>? {
        return treeTableModel.getColumnClass(column)
    }

    override fun getRowCount(): Int {
        return tree.getRowCount()
    }

    protected fun nodeForRow(row: Int): Any {
        val treePath = tree.getPathForRow(row)
        return treePath.lastPathComponent
    }

    override fun getValueAt(row: Int, column: Int): Any {
        return treeTableModel.getValueAt(nodeForRow(row), column)!!
    }

    override fun isCellEditable(row: Int, column: Int): Boolean {
        return treeTableModel.isCellEditable(nodeForRow(row), column)
    }

    override fun setValueAt(value: Any, row: Int, column: Int) {
        treeTableModel.setValueAt(value, nodeForRow(row), column)
    }
}
