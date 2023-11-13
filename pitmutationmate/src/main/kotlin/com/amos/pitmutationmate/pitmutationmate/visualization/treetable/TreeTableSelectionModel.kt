// SPDX-FileCopyrightText: 2023 2023
//
// SPDX-License-Identifier: MIT

package com.amos.pitmutationmate.pitmutationmate.visualization.treetable

import javax.swing.ListSelectionModel
import javax.swing.tree.DefaultTreeSelectionModel


class TreeTableSelectionModel : DefaultTreeSelectionModel() {
    init {
        this.listSelectionModel.addListSelectionListener { }
    }

    fun getListSelectionModel() : ListSelectionModel {
        return this.listSelectionModel
    }
}
