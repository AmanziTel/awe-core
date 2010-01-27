/* AWE - Amanzi Wireless Explorer
 * http://awe.amanzi.org
 * (C) 2008-2009, AmanziTel AB
 *
 * This library is provided under the terms of the Eclipse Public License
 * as described at http://www.eclipse.org/legal/epl-v10.html. Any use,
 * reproduction or distribution of the library constitutes recipient's
 * acceptance of this agreement.
 *
 * This library is distributed WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 */

package org.amanzi.splash.printing;

import net.refractions.udig.printing.model.Box;
import net.refractions.udig.printing.model.ModelFactory;
import net.refractions.udig.printing.model.Page;
import net.refractions.udig.printing.ui.internal.AbstractTemplate;
import net.refractions.udig.project.internal.Map;

import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;

/**
 * Template that contains a chart and a table
 * 
 * @author Pechko_E
 * @since 1.0.0
 */
public class ChartTemplate extends AbstractTemplate {

    private static final int MARGIN = 10;// from BasicTemplate
    private static final int SPACING = 10;// from BasicTemplate
    private int height;

    @Override
    public String getName() {
        return "Test template with chart";
    }

    @Override
    public void init(Page page, Map map) {
        int height = page.getSize().height;
        int width = page.getSize().width;
        final int chartWidth = width;
//        final int chartHeight;
        addChart();
        addTable();
    }

    /**
     * Adds a table
     */
    private void addTable() {
        Box tableBox = ModelFactory.eINSTANCE.createBox();

        tableBox.setSize(new Dimension(200, 300));
        tableBox.setLocation(new Point(MARGIN, MARGIN + height + 10));

        TableBoxPrinter tableBoxPrinter = new TableBoxPrinter();
        tableBoxPrinter.setTable(TableBoxPrinter.getTestTable());
        tableBox.setBoxPrinter(tableBoxPrinter);

        boxes.add(tableBox);

    }

    /**
     * Adds a chart
     */
    private void addChart() {
        Box chartBox = ModelFactory.eINSTANCE.createBox();

        chartBox.setSize(new Dimension(300, 300));
        chartBox.setLocation(new Point(MARGIN, MARGIN));

        height = chartBox.getSize().height;

        JFreeChartBoxPrinter chartBoxPrinter = new JFreeChartBoxPrinter();
        chartBoxPrinter.setChart(createChart());
        chartBox.setBoxPrinter(chartBoxPrinter);

        boxes.add(chartBox);

    }

    /**
     * Creates test chart
     * @return a chart
     */
    private JFreeChart createChart() {
        DefaultCategoryDataset ds = new DefaultCategoryDataset();
        ds.addValue(10, "series1", "jan");
        ds.addValue(20, "series1", "feb");
        ds.addValue(15, "series1", "mar");
        // create the chart...
        return ChartFactory.createBarChart("Test chart", // chart title
                "values", // domain axis label
                "range", // range axis label
                ds, // data
                PlotOrientation.HORIZONTAL, // orientation
                true, // include legend
                true, // tooltips?
                false // URLs?
                );
    }

}
