// SPDX-FileCopyrightText: 2023 2023
//
// SPDX-License-Identifier: MIT

package com.amos.pitmutationmate.pitmutationmate.visualization.treestructure

class DataNode(
    val name: String,
    val nbClasses: String,
    val lineCoverage: String,
    val mutationCoverage: String,
    val testStrength: String,
    var children: List<DataNode>?
) {

    init {
        if (this.children == null) {
            this.children = emptyList()
        }
    }

    /**
     * Node text from the JTree.
     */
    override fun toString(): String {
        return name
    }
}
