// SPDX-FileCopyrightText: 2023 2023
//
// SPDX-License-Identifier: MIT

package com.amos.pitmutationmate.pitmutationmate.visualization.treetable

import java.awt.GridLayout
import javax.swing.JPanel
import javax.swing.JScrollPane

// The implementation for the JTreeTable was taken from these two projects:
// https://github.com/javagl/JTreeTable
// https://www.hameister.org/JavaSwingTreeTable.html

// TODO This class needs the MutationTest Data-object to add/update/delete results from the TreeTable
class JTreeTable : JPanel() {
    init {
        layout = GridLayout(0, 1)
        val treeTableModel: AbstractTreeTableModel = DataModel(createDataStructure())
        val myTreeTable = TreeTable(treeTableModel)
        this.add(JScrollPane(myTreeTable))
        setSize(1000, 800)
    }

    companion object {
        private fun createDataStructure(): DataNode {
            val children1: MutableList<DataNode> = ArrayList()
            children1.add(DataNode("N12", "C12", "100% 10/10", "50", null))
            children1.add(DataNode("N13", "C13", "100% 10/10", "60", null))
            children1.add(DataNode("N14", "C14", "100% 10/10", "70", null))
            children1.add(DataNode("N15", "C15", "100% 10/10", "80", null))
            val children2: MutableList<DataNode> = ArrayList()
            children2.add(DataNode("N12", "C12", "100% 10/10", "10", null))
            children2.add(DataNode("N13", "C13", "100% 10/10", "20", children1))
            children2.add(DataNode("N14", "C14", "100% 10/10", "30", null))
            children2.add(DataNode("N15", "C15", "100% 10/10", "40", null))
            val rootNodes: MutableList<DataNode> = ArrayList()
            rootNodes.add(DataNode("N1", "C1", "100% 10/10", "10", children2))
            rootNodes.add(DataNode("N2", "C2", "100% 10/10", "10", children1))
            rootNodes.add(DataNode("N3", "C3", "100% 10/10", "10", children2))
            rootNodes.add(DataNode("N4", "C4", "100% 10/10", "10", children1))
            rootNodes.add(DataNode("N5", "C5", "100% 10/10", "10", children1))
            rootNodes.add(DataNode("N6", "C6", "100% 10/10", "10", children1))
            rootNodes.add(DataNode("N7", "C7", "100% 10/10", "10", children1))
            rootNodes.add(DataNode("N8", "C8", "100% 10/10", "10", children1))
            rootNodes.add(DataNode("N9", "C9", "100% 10/10", "10", children1))
            rootNodes.add(DataNode("N10", "C10", "100% 10/10", "10", children1))
            rootNodes.add(DataNode("N11", "C11", "100% 10/10", "10", children1))
            rootNodes.add(DataNode("N12", "C7", "100% 10/10", "10", children1))
            rootNodes.add(DataNode("N13", "C8", "100% 10/10", "10", children1))
            rootNodes.add(DataNode("N14", "C9", "100% 10/10", "10", children1))
            rootNodes.add(DataNode("N15", "C10", "100% 10/10", "10", children1))
            rootNodes.add(DataNode("N16", "100% 10/10", "83% 5/6", "83% 5/6", children1))
            return DataNode("All", "", "", "", rootNodes)
        }
    }
}
