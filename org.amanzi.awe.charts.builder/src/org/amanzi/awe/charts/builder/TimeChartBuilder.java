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

import java.text.NumberFormat;

import org.amanzi.awe.charts.builder.dataset.dto.impl.TimeSeriesCollectionContainer;
import org.amanzi.awe.charts.builder.internal.Messages;
import org.amanzi.awe.charts.manger.ChartsManager;
import org.amanzi.awe.charts.model.IChartModel;
import org.amanzi.awe.charts.model.IRangeAxis;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.labels.StandardXYToolTipGenerator;
import org.jfree.chart.plot.DatasetRenderingOrder;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.StandardXYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.general.Dataset;
import org.jfree.data.xy.XYDataset;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Vladislav_Kondratenko
 * @since 1.0.0
 */
public class TimeChartBuilder
        extends
            AbstractMultiAxisChartBuilder<XYPlot, TimeSeriesCollectionContainer, XYLineAndShapeRenderer, StandardXYItemRenderer, DateAxis, NumberAxis> {

    private static final int MAXIMUM_FRACTION_DIGITS = 2;

    private static final StandardXYToolTipGenerator STANDARD_TOOL_TIP_GENERATOR = new StandardXYToolTipGenerator();

    /**
     * @param model
     */
    public TimeChartBuilder(IChartModel model) {
        super(model);
    }

    @Override
    protected void setSecondAxisForPlot(XYPlot plot, Dataset dataset, StandardXYItemRenderer subRenderer, NumberAxis secondAxis) {
        plot.setRangeAxis(1, secondAxis);
        plot.setDataset(1, (XYDataset)dataset);
        plot.mapDatasetToRangeAxis(1, 1);
        plot.setDatasetRenderingOrder(DatasetRenderingOrder.REVERSE);
        plot.setRenderer(1, subRenderer);

    }

    @Override
    protected NumberAxis configRangeAxis(IRangeAxis axis) {
        NumberAxis rangeAxis;
        if (axis.equals(getModel().getMainRangeAxis())) {
            rangeAxis = (NumberAxis)getPlot().getRangeAxis();
            rangeAxis.setNumberFormatOverride(NumberFormat.getInstance());
            rangeAxis.getNumberFormatOverride().setMaximumFractionDigits(MAXIMUM_FRACTION_DIGITS);
        } else {
            rangeAxis = new NumberAxis(getModel().getSecondRangeAxis().getName());
            rangeAxis.setNumberFormatOverride(NumberFormat.getInstance());
            rangeAxis.getNumberFormatOverride().setMaximumFractionDigits(MAXIMUM_FRACTION_DIGITS);
            rangeAxis.setAutoRangeIncludesZero(false);
        }
        return rangeAxis;
    }

    @Override
    protected DateAxis configDomainAxis(String domainAxisName) {
        DateAxis dateAxis = (DateAxis)getPlot().getDomainAxis();
        // TODO KV: move to date format manager;
        dateAxis.setDateFormatOverride(ChartsManager.getInstance().getDefaultDateFormat());
        dateAxis.setAutoRange(true);
        return dateAxis;
    }

    @Override
    protected TimeSeriesCollectionContainer createDataset(IChartModel model) {
        return new TimeSeriesCollectionContainer(model);
    }

    @Override
    protected XYPlot plotSetup(Dataset dataset, DateAxis domainAxis2, NumberAxis mainRangeAxis2,
            XYLineAndShapeRenderer mainRenderer2) {
        getPlot().setOrientation(getModel().getPlotOrientation());
        return getPlot();
    }

    @Override
    protected JFreeChart createDefaultChart() {
        JFreeChart chart = ChartFactory.createTimeSeriesChart(getModel().getName(), // title
                getModel().getDomainAxisName(), // x-axis label
                getModel().getMainRangeAxis().getName(), // y-axis label
                getDatasets().getDataset(getModel().getMainRangeAxis()), // data
                true, // create legend?
                true, // generate tooltips?
                false // generate URLs?
                );
        return chart;
    }

    @Override
    protected XYLineAndShapeRenderer configMainRenderer() {
        ((XYLineAndShapeRenderer)getPlot().getRenderer()).setBaseToolTipGenerator(STANDARD_TOOL_TIP_GENERATOR);
        return (XYLineAndShapeRenderer)getPlot().getRenderer();
    }

    @Override
    protected StandardXYItemRenderer configSubRenderer() {
        final StandardXYItemRenderer renderer = new StandardXYItemRenderer();
        renderer.setBaseToolTipGenerator(STANDARD_TOOL_TIP_GENERATOR);
        return renderer;
    }

    @Override
    protected JFreeChart finishUp(JFreeChart chart) {
        chart.addSubtitle(getSubTitle(Messages.clickItemToDrillDown));
        return chart;
    }
}
