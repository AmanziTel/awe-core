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

package org.amanzi.awe.distribution.ui.widgets;

import java.awt.Color;

import org.amanzi.awe.distribution.model.IDistributionModel;
import org.amanzi.awe.distribution.model.bar.IDistributionBar;
import org.amanzi.awe.distribution.model.type.IDistributionType.ChartType;
import org.amanzi.awe.distribution.ui.charts.DistributionBarRenderer;
import org.amanzi.awe.distribution.ui.charts.DistributionChartDataset;
import org.amanzi.awe.distribution.ui.widgets.DistributionChartWidget.IDistributionChartListener;
import org.amanzi.awe.ui.view.widgets.internal.AbstractAWEWidget;
import org.amanzi.neo.models.exceptions.ModelException;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartMouseEvent;
import org.jfree.chart.ChartMouseListener;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.LogarithmicAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.entity.CategoryItemEntity;
import org.jfree.chart.entity.ChartEntity;
import org.jfree.chart.labels.StandardCategoryToolTipGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.CategoryItemRenderer;
import org.jfree.experimental.chart.swt.ChartComposite;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Nikolay Lagutko (nikolay.lagutko@amanzitel.com)
 * @since 1.0.0
 */
public class DistributionChartWidget extends AbstractAWEWidget<ChartComposite, IDistributionChartListener>
        implements
            ChartMouseListener {

    public interface IDistributionChartListener extends AbstractAWEWidget.IAWEWidgetListener {

        void onBarSelected(IDistributionBar bar, int index);

    }

    private static final Color PLOT_BACKGROUND = new Color(230, 230, 230);

    private static final Color CHART_BACKGROUND = Color.WHITE;

    private JFreeChart distributionChart;

    private DistributionChartDataset dataset;

    private IDistributionBar selectedBar;

    /**
     * @param parent
     * @param style
     * @param listener
     */
    public DistributionChartWidget(final Composite parent, final IDistributionChartListener listener) {
        super(parent, SWT.NONE, listener);
    }

    private JFreeChart createChart(final DistributionChartDataset distributionDataset, final CategoryItemRenderer renderer) {
        final JFreeChart chart = ChartFactory.createBarChart("Distribution Chart", "Values", "Numbers", distributionDataset,
                PlotOrientation.VERTICAL, false, false, false);

        final CategoryPlot plot = (CategoryPlot)chart.getPlot();

        plot.setRenderer(renderer);
        plot.setBackgroundPaint(PLOT_BACKGROUND);

        chart.setBackgroundPaint(CHART_BACKGROUND);

        return chart;
    }

    @Override
    protected ChartComposite createWidget(final Composite parent, final int style) {
        dataset = getDataset();
        final CategoryItemRenderer renderer = getRenderer(dataset);

        distributionChart = createChart(dataset, renderer);
        updateChartType(ChartType.COUNTS);

        final ChartComposite frame = new ChartComposite(parent, style, distributionChart, true);
        frame.pack();

        frame.setEnabled(false);

        frame.addChartMouseListener(this);

        return frame;
    }

    private DistributionChartDataset getDataset() {
        return new DistributionChartDataset();
    }

    private CategoryItemRenderer getRenderer(final DistributionChartDataset dataset) {
        final CategoryItemRenderer renderer = new DistributionBarRenderer(dataset);

        renderer.setBaseToolTipGenerator(new StandardCategoryToolTipGenerator());

        return renderer;
    }

    public void updateChartType(final ChartType chartType) {
        final CategoryPlot plot = (CategoryPlot)distributionChart.getPlot();

        switch (chartType) {
        case LOGARITHMIC:
            final LogarithmicAxis logAxis = new LogarithmicAxis("Logarithmic");
            logAxis.setAllowNegativesFlag(true);
            plot.setRangeAxis(logAxis);

            logAxis.setAutoRange(true);
            break;
        case COUNTS:
            final NumberAxis countAxis = new NumberAxis("Counts");
            countAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());

            plot.setRangeAxis(countAxis);
            countAxis.setAutoRange(true);
            break;
        case PERCENTS:
            final NumberAxis percentageAxis = new NumberAxis("Percentage");
            percentageAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
            percentageAxis.setRange(0, 100);

            plot.setRangeAxis(percentageAxis);
            break;
        case CDF:
            break;
        }

        dataset.updateDelegate(chartType);

        update();
    }

    public void update() {
        distributionChart.fireChartChanged();
    }

    public void updateDistribution(final IDistributionModel distributionModel) {
        try {
            distributionChart.setTitle(distributionModel.getName());
            dataset.setDistributionBars(distributionModel.getDistributionBars());

            update();
        } catch (final ModelException e) {
            // TODO: handle error
        } finally {
            setVisible(true);
            setEnabled(true);
        }
    }

    @Override
    public void chartMouseClicked(final ChartMouseEvent arg0) {
        final ChartEntity entity = arg0.getEntity();
        if (entity instanceof CategoryItemEntity) {
            final IDistributionBar bar = (IDistributionBar)((CategoryItemEntity)entity).getColumnKey();
            final int index = dataset.getColumnIndex(bar);

            if (selectedBar == null || !selectedBar.equals(bar)) {
                for (final IDistributionChartListener listener : getListeners()) {
                    listener.onBarSelected(bar, index);
                }

                selectedBar = bar;
            }
        }
    }

    @Override
    public void chartMouseMoved(final ChartMouseEvent arg0) {
        // do nothing
    }

}
