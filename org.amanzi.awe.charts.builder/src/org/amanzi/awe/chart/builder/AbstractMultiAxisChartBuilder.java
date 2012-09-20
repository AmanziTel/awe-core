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

package org.amanzi.awe.chart.builder;

import org.amanzi.awe.chart.builder.dataset.dto.IChartDatasetContainer;
import org.amanzi.awe.charts.model.IChartModel;
import org.amanzi.awe.charts.model.IRangeAxis;
import org.amanzi.neo.models.exceptions.ModelException;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.Axis;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.renderer.AbstractRenderer;
import org.jfree.data.general.Dataset;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Vladislav_Kondratenko
 * @since 1.0.0
 */
public abstract class AbstractMultiAxisChartBuilder<P extends Plot, D extends IChartDatasetContainer, R extends AbstractRenderer, R2 extends AbstractRenderer, X extends Axis, Y extends Axis>
        extends
            AbstractChartBuilder {

    private JFreeChart chart;

    private D datasets;

    private X domainAxis;

    private Y mainRangeAxis;

    private R mainRenderer;

    private R2 subRenderer;

    private Y secondAxis;

    private P plot;

    protected AbstractMultiAxisChartBuilder(IChartModel model) {
        super(model);
    }

    @SuppressWarnings("unchecked")
    @Override
    public JFreeChart createChart() throws ModelException {
        datasets = createDataset(getModel());
        datasets.computeDatasets();
        this.chart = createDefaultChart();
        if (chart != null) {
            plot = (P)chart.getPlot();
        }
        domainAxis = configDomainAxis(getModel().getDomainAxisName());
        mainRangeAxis = configRangeAxis(getModel().getMainRangeAxis());
        mainRenderer = createMainRenderer();
        plot = plotSetup(datasets.getDataset(getModel().getMainRangeAxis()), domainAxis, mainRangeAxis, mainRenderer);
        if (datasets.isMultyAxis()) {
            subRenderer = createSubRenderer();
            secondAxis = configRangeAxis(getModel().getSecondRangeAxis());
            setSecondAxisForPlot(plot, datasets.getDataset(getModel().getSecondRangeAxis()), subRenderer, secondAxis);
        }

        chart = finishUp(chart);
        return chart;
    }

    protected abstract void setSecondAxisForPlot(P plot, Dataset dataset, R2 subRenderer, Y secondAxis);

    protected abstract Y configRangeAxis(IRangeAxis axis);

    protected abstract X configDomainAxis(String domainAxisName);

    protected abstract D createDataset(IChartModel model);

    protected abstract P plotSetup(Dataset dataset, X domainAxis2, Y mainRangeAxis2, R mainRenderer2);

    protected abstract JFreeChart createDefaultChart();

    protected abstract R createMainRenderer();

    protected abstract R2 createSubRenderer();

    protected D getDatasets() {
        return datasets;
    }

    /**
     * @return Returns the plot.
     */
    protected P getPlot() {
        return plot;
    }

    protected JFreeChart finishUp(JFreeChart chart) {
        return chart;

    }

    /**
     * @return Returns the chart.
     */
    protected JFreeChart getChart() {
        return chart;
    }
}
