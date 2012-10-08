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

import org.amanzi.awe.charts.builder.dataset.dto.IChartDatasetContainer;
import org.amanzi.awe.charts.model.IChartModel;
import org.amanzi.awe.charts.model.IRangeAxis;
import org.amanzi.neo.models.exceptions.ModelException;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.Axis;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.renderer.AbstractRenderer;
import org.jfree.data.general.Dataset;

/**
 * <p>
 * contains common finctional for charts which can contains more than one axis
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
        mainRenderer = configMainRenderer();
        plot = plotSetup(datasets.getDataset(getModel().getMainRangeAxis()), domainAxis, mainRangeAxis, mainRenderer);
        if (datasets.isMultyAxis()) {
            subRenderer = configSubRenderer();
            secondAxis = configRangeAxis(getModel().getSecondRangeAxis());
            setSecondAxisForPlot(plot, datasets.getDataset(getModel().getSecondRangeAxis()), subRenderer, secondAxis);
        }
        domainAxis.setTickLabelFont(getDefaulTickLabelFont());
        domainAxis.setLabelFont(getDefaultAxisFont());
        chart = finishUp(chart);
        return chart;
    }

    /**
     * set second axis for prepared plot
     * 
     * @param plot
     * @param dataset
     * @param subRenderer
     * @param secondAxis
     */
    protected abstract void setSecondAxisForPlot(P plot, Dataset dataset, R2 subRenderer, Y secondAxis);

    /**
     * config or create new range axis
     * 
     * @param axis
     * @return
     */
    protected abstract Y configRangeAxis(IRangeAxis axis);

    /**
     * config or create new domain axis
     * 
     * @param domainAxisName
     * @return
     */
    protected abstract X configDomainAxis(String domainAxisName);

    /**
     * create dataset containr
     * 
     * @param model
     * @return
     */
    protected abstract D createDataset(IChartModel model);

    /**
     * setup plot or create new one
     * 
     * @param dataset
     * @param domainAxis2
     * @param mainRangeAxis2
     * @param mainRenderer2
     * @return
     */
    protected abstract P plotSetup(Dataset dataset, X domainAxis2, Y mainRangeAxis2, R mainRenderer2);

    /**
     * if it possible to create default chart -than create it; else this method should be empty
     * 
     * @return
     */
    protected abstract JFreeChart createDefaultChart();

    /**
     * create or config main renderer
     * 
     * @return
     */
    protected abstract R configMainRenderer();

    /**
     * config sub renderer
     * 
     * @return
     */
    protected abstract R2 configSubRenderer();

    /**
     * get datasets container
     * 
     * @return
     */
    protected D getDatasets() {
        return datasets;
    }

    /**
     * @return Returns the plot.
     */
    protected P getPlot() {
        return plot;
    }

    /**
     * final actions for create chart this method also can me invoked for creation chart with early
     * setup components
     * 
     * @param chart
     * @return
     */
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
