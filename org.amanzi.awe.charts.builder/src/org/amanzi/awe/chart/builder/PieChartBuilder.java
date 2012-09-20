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

import org.amanzi.awe.chart.builder.dataset.dto.impl.PieDatasetContainer;
import org.amanzi.awe.charts.model.IChartModel;
import org.amanzi.neo.models.exceptions.ModelException;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Vladislav_Kondratenko
 * @since 1.0.0
 */
public class PieChartBuilder extends AbstractChartBuilder {

    public PieChartBuilder(IChartModel model) {
        super(model);
    }

    @Override
    public JFreeChart createChart() throws ModelException {
        PieDatasetContainer dataset = new PieDatasetContainer(getModel());
        dataset.computeDatasets();
        return ChartFactory.createPieChart3D(getModel().getName(), dataset.getDataset(getModel().getMainRangeAxis()), true, true,
                true);
    }

}
