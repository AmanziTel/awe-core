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

import org.amanzi.awe.chart.builder.dataset.dto.impl.CategoryDatasetContainer;
import org.amanzi.awe.charts.model.IChartModel;
import org.amanzi.neo.models.exceptions.ModelException;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.renderer.category.StackedBarRenderer3D;
import org.jfree.data.category.CategoryDataset;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Vladislav_Kondratenko
 * @since 1.0.0
 */
public class StackedChartBuilder extends AbstractChartBuilder {

    public StackedChartBuilder(IChartModel model) {
        super(model);
    }

    @Override
    public JFreeChart createChart() throws ModelException {
        CategoryDatasetContainer dataset = new CategoryDatasetContainer(getModel());
        JFreeChart chart = ChartFactory.createStackedBarChart(
                getModel().getName(), // chart
                // title
                getModel().getDomainAxisName(), getModel().getMainRangeAxis().getName(),
                (CategoryDataset)dataset.getDataset(getModel().getMainRangeAxis()), // data
                getModel().getPlotOrientation(), true, true, // tooltips
                false // urls
                );
        ((CategoryPlot)chart.getPlot()).setRenderer(new StackedBarRenderer3D());
        return null;
    }

}
