// SPDX-FileCopyrightText: 2023 2023
//
// SPDX-License-Identifier: MIT

package com.amos.pitmutationmate.pitmutationmate.visualization

import org.jfree.chart.ChartFactory
import org.jfree.chart.ChartPanel
import org.jfree.data.category.CategoryDataset
import org.jfree.chart.plot.PlotOrientation
import org.jfree.data.category.DefaultCategoryDataset

import javax.swing.JPanel

class BarGraph : JPanel() {

    init {
        val dataset = createDataset()
        val chart = ChartFactory.createBarChart(
            "Bar Chart Example",  //Chart Title
            "Year",  // Category axis
            "Population in Million",  // Value axis
            dataset,
            PlotOrientation.VERTICAL,
            true, true, false
        )
        this.add(ChartPanel(chart))
    }

    private fun createDataset(): CategoryDataset {
        val dataset = DefaultCategoryDataset()

        // Population in 2005
        dataset.addValue(10, "USA", "2005")
        dataset.addValue(15, "India", "2005")
        dataset.addValue(20, "China", "2005")

        // Population in 2010
        dataset.addValue(15, "USA", "2010")
        dataset.addValue(20, "India", "2010")
        dataset.addValue(25, "China", "2010")

        // Population in 2015
        dataset.addValue(20, "USA", "2015")
        dataset.addValue(25, "India", "2015")
        dataset.addValue(30, "China", "2015")
        return dataset
    }
}
