// SPDX-FileCopyrightText: 2023 2023
//
// SPDX-License-Identifier: MIT

package com.amos.pitmutationmate.pitmutationmate.visualization.treestructure

import com.intellij.ui.treeStructure.treetable.TreeTableModel
import javax.swing.JTree
import javax.swing.event.EventListenerList
import javax.swing.event.TreeModelEvent
import javax.swing.event.TreeModelListener
import javax.swing.tree.TreePath

class TreeTableModel(@JvmField var rootNode: DataNode) : TreeTableModel {
    protected var listenerList = EventListenerList()

    override fun getRoot(): Any {
        return rootNode
    }

    override fun getChild(parent: Any, index: Int): DataNode? {
        return (parent as DataNode).children?.get(index)
    }

    override fun getChildCount(parent: Any): Int {
        return (parent as DataNode).children?.size ?: 0
    }

    override fun isLeaf(node: Any): Boolean {
        return getChildCount(node) == 0
    }

    override fun valueForPathChanged(path: TreePath?, newValue: Any?) {
        TODO("Not yet implemented")
    }

    // This method should not be used.
    override fun getIndexOfChild(parent: Any?, child: Any?): Int {
        return 0
    }

    override fun addTreeModelListener(l: TreeModelListener) {
        listenerList.add(TreeModelListener::class.java, l)
    }

    override fun removeTreeModelListener(l: TreeModelListener) {
        listenerList.remove(TreeModelListener::class.java, l)
    }

    override fun getColumnCount(): Int {
        return columnNames.size
    }

    override fun setTree(tree: JTree?) {
        println("Not yet implemented")
    }

    override fun getColumnName(column: Int): String? {
        return columnNames[column]
    }

    override fun getColumnClass(column: Int): Class<*> {
        return columnTypes[column]
    }

    override fun getValueAt(node: Any?, column: Int): Any? {
        when (column) {
            0 -> return (node as DataNode?)?.name
            1 -> return (node as DataNode?)?.nbClasses
            2 -> return (node as DataNode?)?.lineCoverage
            3 -> return (node as DataNode?)?.mutationCoverage
            4 -> return (node as DataNode?)?.testStrength
            else -> {}
        }
        return null
    }

    override fun isCellEditable(node: Any?, column: Int): Boolean {
        return false
    }

    override fun setValueAt(aValue: Any?, node: Any?, column: Int) {}

    private fun fireTreeNode(
        changeType: Int,
        source: Any,
        path: Array<Any>,
        childIndices: IntArray,
        children: Array<Any>
    ) {
        val listeners = listenerList.listenerList
        val e = TreeModelEvent(source, path, childIndices, children)
        var i = listeners.size - 2
        while (i >= 0) {
            if (listeners[i] === TreeModelListener::class.java) {
                when (changeType) {
                    CHANGED -> (listeners[i + 1] as TreeModelListener).treeNodesChanged(e)
                    INSERTED -> (listeners[i + 1] as TreeModelListener).treeNodesInserted(e)
                    REMOVED -> (listeners[i + 1] as TreeModelListener).treeNodesRemoved(e)
                    STRUCTURE_CHANGED -> (listeners[i + 1] as TreeModelListener).treeStructureChanged(e)
                    else -> {}
                }
            }
            i -= 2
        }
    }

    protected fun fireTreeNodesChanged(source: Any, path: Array<Any>, childIndices: IntArray, children: Array<Any>) {
        fireTreeNode(CHANGED, source, path, childIndices, children)
    }

    protected fun fireTreeNodesInserted(source: Any, path: Array<Any>, childIndices: IntArray, children: Array<Any>) {
        fireTreeNode(INSERTED, source, path, childIndices, children)
    }

    protected fun fireTreeNodesRemoved(source: Any, path: Array<Any>, childIndices: IntArray, children: Array<Any>) {
        fireTreeNode(REMOVED, source, path, childIndices, children)
    }

    protected fun fireTreeStructureChanged(
        source: Any,
        path: Array<Any>,
        childIndices: IntArray,
        children: Array<Any>
    ) {
        fireTreeNode(STRUCTURE_CHANGED, source, path, childIndices, children)
    }

    companion object {
        protected var columnNames = arrayOf("Name", "Number of Classes", "Line Coverage", "Mutation Coverage", "Test Strength")
        protected var columnTypes = arrayOf(
            TreeTableModel::class.java,
            Integer::class.java,
            String::class.java,
            String::class.java,
            String::class.java
        )
        private const val CHANGED = 0
        private const val INSERTED = 1
        private const val REMOVED = 2
        private const val STRUCTURE_CHANGED = 3
    }
}
