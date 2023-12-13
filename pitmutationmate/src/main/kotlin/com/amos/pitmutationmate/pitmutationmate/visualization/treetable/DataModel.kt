// SPDX-FileCopyrightText: 2023 2023
//
// SPDX-License-Identifier: MIT

package com.amos.pitmutationmate.pitmutationmate.visualization.treetable


class DataModel(rootNode: DataNode) : AbstractTreeTableModel(rootNode) {
    init {
        root = rootNode
    }

    override fun getChild(parent: Any, index: Int): DataNode? {
        return (parent as DataNode).children?.get(index) ?: null
    }

    override fun getChildCount(parent: Any): Int {
        return (parent as DataNode).children?.size ?: 0
    }

    override val columnCount: Int
        get() = columnNames.size

    override fun getColumnName(column: Int): String {
        return columnNames[column]
    }

    override fun getColumnClass(column: Int): Class<*> {
        return columnTypes[column]
    }

    override fun getValueAt(node: Any?, column: Int): Any? {
        when (column) {
            0 -> return (node as DataNode?)?.name
            1 -> return (node as DataNode?)?.lineCoverage
            2 -> return (node as DataNode?)?.mutationCoverage
            3 -> return (node as DataNode?)?.testStrength
            else -> {}
        }
        return null
    }

    override fun isCellEditable(node: Any?, column: Int): Boolean {
        return true // Important to activate TreeExpandListener
    }

    override fun setValueAt(aValue: Any?, node: Any?, column: Int) {}

    companion object {
        // Spalten Name.
        protected var columnNames = arrayOf("Class", "Line Coverage", "Mutation Coverage", "Test Strength")

        // Spalten Typen.
        protected var columnTypes = arrayOf(
            TreeTableModel::class.java,
            String::class.java,
            String::class.java,
            String::class.java
        )
    }
}
