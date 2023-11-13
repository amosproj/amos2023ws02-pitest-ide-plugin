// SPDX-FileCopyrightText: 2023 2023
//
// SPDX-License-Identifier: MIT

package com.amos.pitmutationmate.pitmutationmate.visualization.treetable

import java.awt.Component
import java.awt.event.MouseEvent
import java.util.*
import javax.swing.AbstractCellEditor
import javax.swing.JTable
import javax.swing.JTree
import javax.swing.table.TableCellEditor


class TreeTableCellEditor(private val tree: JTree, private val table: JTable) : AbstractCellEditor(),
    TableCellEditor {
    override fun getTableCellEditorComponent(
        table: JTable,
        value: Any,
        isSelected: Boolean,
        r: Int,
        c: Int
    ): Component {
        return tree
    }

    override fun isCellEditable(e: EventObject): Boolean {
        if (e is MouseEvent) {
            val column1 = 0
            val doubleClick = 2
            val newME = MouseEvent(
                tree,
                e.id,
                e.getWhen(),
                e.modifiersEx,
                e.x - table.getCellRect(0, column1, true).x,
                e.y,
                doubleClick,
                e.isPopupTrigger, e.button
            )
            tree.dispatchEvent(newME)
        }
        return false
    }

    override fun getCellEditorValue(): Any? {
        return null
    }

}
