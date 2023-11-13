// SPDX-FileCopyrightText: 2023 2023
//
// SPDX-License-Identifier: MIT

package com.amos.pitmutationmate.pitmutationmate.visualization

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.data.category.DefaultCategoryDataset
import javax.swing.JPanel

class LineGraph : JPanel() {

    init {
        var dataset = createDataset();
        var chart = ChartFactory.createLineChart(
            "Line Chart Example", // Chart title
            "Date", // X-Axis Label
            "Number of Visitor", // Y-Axis Label
            dataset
        )
        this.add(ChartPanel(chart))
    }

    private fun createDataset(): DefaultCategoryDataset {
        val series1 = "Visitor"
        val series2 = "Unique Visitor"
        val dataset = DefaultCategoryDataset()
        dataset.addValue(200, series1, "2016-12-19")
        dataset.addValue(150, series1, "2016-12-20")
        dataset.addValue(100, series1, "2016-12-21")
        dataset.addValue(210, series1, "2016-12-22")
        dataset.addValue(240, series1, "2016-12-23")
        dataset.addValue(195, series1, "2016-12-24")
        dataset.addValue(245, series1, "2016-12-25")
        dataset.addValue(150, series2, "2016-12-19")
        dataset.addValue(130, series2, "2016-12-20")
        dataset.addValue(95, series2, "2016-12-21")
        dataset.addValue(195, series2, "2016-12-22")
        dataset.addValue(200, series2, "2016-12-23")
        dataset.addValue(180, series2, "2016-12-24")
        dataset.addValue(230, series2, "2016-12-25")
        return dataset
    }
}
