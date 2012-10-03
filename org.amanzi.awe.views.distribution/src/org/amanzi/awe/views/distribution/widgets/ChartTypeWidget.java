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

import java.util.Collection;

import org.amanzi.awe.distribution.engine.manager.DistributionManager;
import org.amanzi.awe.distribution.model.type.IDistributionType.ChartType;
import org.amanzi.awe.ui.view.widgets.internal.AbstractComboWidget;
import org.amanzi.awe.views.distribution.widgets.ChartTypeWidget.IChartTypeListener;
import org.eclipse.swt.widgets.Composite;

/**
 * TODO Purpose of
 * <p>
 *
 * </p>
 * @author Nikolay Lagutko (nikolay.lagutko@amanzitel.com)
 * @since 1.0.0
 */
public class ChartTypeWidget extends AbstractComboWidget<ChartType, IChartTypeListener> {

    public interface IChartTypeListener extends AbstractComboWidget.IComboSelectionListener {

        public void onChartTypeSelected(ChartType chartType);

    }

    private DistributionManager distributionManager;

    /**
     * @param parent
     * @param listener
     * @param label
     * @param minimalLabelWidth
     */
    protected ChartTypeWidget(final Composite parent, final IChartTypeListener listener, final String label) {
        super(parent, listener, label);
    }

    @Override
    protected Collection<ChartType> getItems() {
        if (distributionManager != null) {
            return distributionManager.getPossibleDistributionTypes();
        }
        return null;
    }

    @Override
    protected String getItemName(final ChartType item) {
        return item.getTitle();
    }

    @Override
    protected void fireListener(final IChartTypeListener listener, final ChartType selectedItem) {
        listener.onChartTypeSelected(selectedItem);
    }

    public void setDistributionManager(final DistributionManager distributionManager) {
        this.distributionManager = distributionManager;
    }

}
