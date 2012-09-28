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

package org.amanzi.awe.views.distribution.widgets;

import java.awt.Color;

import org.amanzi.awe.distribution.model.IDistributionModel;
import org.amanzi.awe.ui.view.widgets.internal.AbstractAWEWidget;
import org.amanzi.awe.views.distribution.charts.DistributionBarRenderer;
import org.amanzi.awe.views.distribution.charts.DistributionChartDataset;
import org.amanzi.awe.views.distribution.widgets.DistributionChartWidget.IDistributionChartListener;
import org.amanzi.neo.models.exceptions.ModelException;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.labels.StandardCategoryToolTipGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.CategoryItemRenderer;
import org.jfree.experimental.chart.swt.ChartComposite;

/**
 * TODO Purpose of
 * <p>
 *
 * </p>
 * @author Nikolay Lagutko (nikolay.lagutko@amanzitel.com)
 * @since 1.0.0
 */
public class DistributionChartWidget extends AbstractAWEWidget<ChartComposite, IDistributionChartListener> {

    private static final Color PLOT_BACKGROUND = new Color(230, 230, 230);

    private static final Color CHART_BACKGROUND = Color.WHITE;

    public interface IDistributionChartListener extends AbstractAWEWidget.IAWEWidgetListener {

    }

    private JFreeChart distributionChart;

    private DistributionChartDataset dataset;

    /**
     * @param parent
     * @param style
     * @param listener
     */
    public DistributionChartWidget(final Composite parent, final IDistributionChartListener listener) {
        super(parent, SWT.NONE, listener);
    }

    @Override
    protected ChartComposite createWidget(final Composite parent, final int style) {
        dataset = getDataset();
        CategoryItemRenderer renderer = getRenderer(dataset);

        distributionChart = createChart(dataset, renderer);

        ChartComposite frame = new ChartComposite(parent, style, distributionChart, true);
        frame.pack();

        frame.setEnabled(false);

        return frame;
    }

    private CategoryItemRenderer getRenderer(final DistributionChartDataset dataset) {
        CategoryItemRenderer renderer = new DistributionBarRenderer(dataset);

        renderer.setBaseToolTipGenerator(new StandardCategoryToolTipGenerator());

        return renderer;
    }

    private DistributionChartDataset getDataset() {
        return new DistributionChartDataset();
    }

    private JFreeChart createChart(final DistributionChartDataset distributionDataset, final CategoryItemRenderer renderer) {
        JFreeChart chart = ChartFactory.createBarChart("Distribution Chart", "Values", "Numbers", distributionDataset, PlotOrientation.VERTICAL, false, false, false);

        CategoryPlot plot = (CategoryPlot)chart.getPlot();
        NumberAxis rangeAxis = (NumberAxis)plot.getRangeAxis();
        rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
        rangeAxis.setAutoRange(true);

        plot.setRenderer(renderer);
        plot.setBackgroundPaint(PLOT_BACKGROUND);

        chart.setBackgroundPaint(CHART_BACKGROUND);

        return chart;
    }

    public void updateDistribution(final IDistributionModel distributionModel) {
        try {
            distributionChart.setTitle(distributionModel.getName());
            dataset.setDistributionBars(distributionModel.getDistributionBars());

            distributionChart.fireChartChanged();
        } catch (ModelException e) {
            //TODO: handle error
        } finally {
            setVisible(true);
            setEnabled(true);
        }
    }

}
