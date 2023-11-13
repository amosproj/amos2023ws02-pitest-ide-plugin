// SPDX-FileCopyrightText: 2023 2023
//
// SPDX-License-Identifier: MIT

package com.amos.pitmutationmate.pitmutationmate.visualization.treetable

import javax.swing.tree.TreeModel


interface TreeTableModel : TreeModel {
    /**
     * Returns the number of available columns.
     * @return Number of Columns
     */
    val columnCount: Int

    /**
     * Returns the column name.
     * @param column Column number
     * @return Column name
     */
    fun getColumnName(column: Int): String?

    /**
     * Returns the type (class) of a column.
     * @param column Column number
     * @return Class
     */
    fun getColumnClass(column: Int): Class<*>?

    /**
     * Returns the value of a node in a column.
     * @param node Node
     * @param column Column number
     * @return Value of the node in the column
     */
    fun getValueAt(node: Any?, column: Int): Any?

    /**
     * Check if a cell of a node in one column is editable.
     * @param node Node
     * @param column Column number
     * @return true/false
     */
    fun isCellEditable(node: Any?, column: Int): Boolean

    /**
     * Sets a value for a node in one column.
     * @param aValue New value
     * @param node Node
     * @param column Column number
     */
    fun setValueAt(aValue: Any?, node: Any?, column: Int)
}
