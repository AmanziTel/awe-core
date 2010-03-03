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

package org.amanzi.printing;

import net.refractions.udig.printing.model.Box;
import net.refractions.udig.printing.model.ModelFactory;
import net.refractions.udig.printing.model.Page;
import net.refractions.udig.printing.model.impl.MapBoxPrinter;
import net.refractions.udig.printing.ui.internal.AbstractTemplate;
import net.refractions.udig.printing.ui.internal.template.AbstractPrinterPageTemplate;
import net.refractions.udig.project.internal.Map;

import org.amanzi.printing.JFreeChartBoxPrinter;
import org.amanzi.printing.TableBoxPrinter;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;

import com.lowagie.text.PageSize;
import com.lowagie.text.Rectangle;


/**
 * Template that contains a chart and a table
 * 
 * @author Pechko_E
 * @since 1.0.0
 */
public class ChartTemplate extends AbstractPrinterPageTemplate {

    private static final int MARGIN = 10;// from BasicTemplate
    private static final int SPACING = 10;// from BasicTemplate
    private int height;
    private int width;

    @Override
    public String getName() {
        return "Test template with chart";
    }

    @Override
    public void init(Page page, Map map) {
//        super.init(page,map);
        page.setPaperSize(new Dimension((int)getPaperSize().getWidth(),(int)getPaperSize().getHeight()));
        height = page.getSize().height;
        width = page.getSize().width;
        Dimension mapSize = addMap(map);
        Dimension chartSize = addChart(mapSize);
        addTable(mapSize, chartSize);
    }

    /**
     * Adds a map
     * 
     * @param map map to be added
     * @return map size
     */
    private Dimension addMap(Map map) {
        Box mapBox = ModelFactory.eINSTANCE.createBox();
        MapBoxPrinter mapBoxPrinter = new MapBoxPrinter();
        mapBox.setID("Standard Map Box"); //$NON-NLS-1$
        mapBox.setBoxPrinter(mapBoxPrinter);
        mapBoxPrinter.setMap(map);

        // calculate mapSize
        int bothMargins = (MARGIN * 2);
        int mapWidth = width - bothMargins - SPACING;
        int mapHeight = (height - bothMargins) / 2;
        Dimension mapSize = new Dimension(mapWidth, mapHeight);
        mapBox.setSize(mapSize);
        mapBox.setLocation(new Point(MARGIN, MARGIN));
        boxes.add(mapBox);
        return mapSize;
    }

    /**
     * Adds a table
     * 
     * @param mapSize TODO
     * @param chartSize TODO
     */
    private void addTable(Dimension mapSize, Dimension chartSize) {
        Box tableBox = ModelFactory.eINSTANCE.createBox();

        int bothMargins = (MARGIN * 2);
        int tableWidth = width - bothMargins;
        int currentHeight = mapSize.height + chartSize.height + 2 * SPACING + MARGIN;
        int tableHeight = height - currentHeight - MARGIN;
        Dimension tableSize = new Dimension(tableWidth, tableHeight);
        tableBox.setSize(tableSize);
        tableBox.setLocation(new Point(MARGIN, currentHeight + SPACING));

        TableBoxPrinter tableBoxPrinter = new TableBoxPrinter();
        tableBoxPrinter.setTable(TableBoxPrinter.getTestTable());
        tableBox.setBoxPrinter(tableBoxPrinter);

        boxes.add(tableBox);

    }

    /**
     * Adds a chart
     * 
     * @param mapSize TODO
     * @return chart size
     */
    private Dimension addChart(Dimension mapSize) {
        Box chartBox = ModelFactory.eINSTANCE.createBox();

        int bothMargins = (MARGIN * 2);
        int chartWidth = width - bothMargins;
        int chartHeight = (height - mapSize.height - bothMargins) / 2;
        Dimension chartSize = new Dimension(chartWidth, chartHeight);
        chartBox.setSize(chartSize);
        chartBox.setLocation(new Point(MARGIN, MARGIN + mapSize.height + SPACING));

        JFreeChartBoxPrinter chartBoxPrinter = new JFreeChartBoxPrinter();
        chartBoxPrinter.setChart(createChart());
        chartBox.setBoxPrinter(chartBoxPrinter);

        boxes.add(chartBox);
        return chartSize;
    }

    /**
     * Creates test chart
     * 
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

    @Override
    public String getAbbreviation() {
        return "Test";
    }
    protected Rectangle getPaperSize() {
        Rectangle a4 = PageSize.A4;
        return a4;
    }
}
