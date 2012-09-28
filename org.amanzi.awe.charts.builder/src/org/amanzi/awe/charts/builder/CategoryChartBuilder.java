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

package org.amanzi.awe.charts.builder;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Paint;

import org.amanzi.awe.charts.builder.dataset.dto.impl.CategoryDatasetContainer;
import org.amanzi.awe.charts.builder.internal.Messages;
import org.amanzi.awe.charts.model.IChartModel;
import org.amanzi.awe.charts.model.IRangeAxis;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.AxisLocation;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.labels.StandardCategoryToolTipGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.general.Dataset;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Vladislav_Kondratenko
 * @since 1.0.0
 */
public class CategoryChartBuilder
        extends
            AbstractMultiAxisChartBuilder<CategoryPlot, CategoryDatasetContainer, BarRenderer, LineAndShapeRenderer, CategoryAxis, NumberAxis> {

    // private final static Color TRANSPERENT_COLOR = new Color(0, 0, 0, 0);

    private static final GradientPaint GRADIENT_PAINT = new GradientPaint(0.0f, 0.0f, Color.blue, 0.0f, 0.0f, new Color(0, 0, 64));

    private static final double ITEM_MARGIN = 0.2d;

    private static final double MAXIMUM_BAR_WIDTH = .04;

    private static final int MAXIMUM_CATEGORY_LABEL_WIDTH = 10;

    private static final int MAXIMUM_CATEGORY_LABEL_LINES = 2;

    private static final Paint BACKGROUND_COLOR = new Color(196, 196, 196);

    public CategoryChartBuilder(IChartModel model) {
        super(model);
    }

    @Override
    protected CategoryDatasetContainer createDataset(IChartModel model) {
        return new CategoryDatasetContainer(model);
    }

    @Override
    protected JFreeChart createDefaultChart() {
        return null;
    }

    @Override
    protected BarRenderer configMainRenderer() {
        BarRenderer renderer = new BarRenderer();
        renderer.setBaseToolTipGenerator(new StandardCategoryToolTipGenerator());
        renderer.setShadowVisible(true);
        renderer.setDrawBarOutline(false);
        renderer.setDrawBarOutline(true);
        renderer.setMaximumBarWidth(MAXIMUM_BAR_WIDTH);
        renderer.setSeriesPaint(0, GRADIENT_PAINT);
        renderer.setItemMargin(ITEM_MARGIN);
        return renderer;
    }

    @Override
    protected LineAndShapeRenderer configSubRenderer() {
        LineAndShapeRenderer renderer = new LineAndShapeRenderer();
        renderer.setBaseToolTipGenerator(new StandardCategoryToolTipGenerator());
        renderer.setItemMargin(ITEM_MARGIN);
        return renderer;
    }

    @Override
    protected NumberAxis configRangeAxis(IRangeAxis axis) {
        NumberAxis valueAxis = new NumberAxis(axis.getName());
        valueAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
        return valueAxis;
    }

    @Override
    protected CategoryAxis configDomainAxis(String domainAxisName) {
        CategoryAxis domainAxis = new CategoryAxis(domainAxisName);
        domainAxis.setMaximumCategoryLabelWidthRatio(MAXIMUM_CATEGORY_LABEL_WIDTH);
        domainAxis.setMaximumCategoryLabelLines(MAXIMUM_CATEGORY_LABEL_LINES);
        domainAxis.setLabel(domainAxisName);
        domainAxis.setTickLabelFont(getDefaulTickLabelFont());
        domainAxis.setLabelFont(getDefaultAxisFont());
        return domainAxis;
    }

    @Override
    protected CategoryPlot plotSetup(Dataset dataset, CategoryAxis domainAxis, NumberAxis mainRangeAxis, BarRenderer mainRenderer) {
        CategoryDataset catDataset = (CategoryDataset)dataset;
        CategoryPlot plot = new CategoryPlot(catDataset, domainAxis, mainRangeAxis, mainRenderer);
        plot.setOrientation(getModel().getPlotOrientation());
        plot.setBackgroundPaint(BACKGROUND_COLOR);
        plot.setDomainGridlinePaint(Color.white);
        plot.setRangeGridlinePaint(Color.white);
        return plot;
    }

    @Override
    protected void setSecondAxisForPlot(CategoryPlot plot, Dataset dataset, LineAndShapeRenderer subRenderer, NumberAxis secondAxis) {
        plot.setDataset(1, (CategoryDataset)dataset);
        plot.mapDatasetToRangeAxis(1, 1);
        plot.setRangeAxis(1, secondAxis);
        plot.setRangeAxisLocation(1, AxisLocation.TOP_OR_LEFT);
        plot.setRenderer(1, subRenderer);
    }

    @Override
    protected JFreeChart finishUp(JFreeChart chart) {
        chart = new JFreeChart(getModel().getName(), getPlot());
        chart.setBackgroundPaint(Color.WHITE);

        chart.addSubtitle(getSubTitle(Messages.clickItemToDrillDown));
        return chart;
    }
}
