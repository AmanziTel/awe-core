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
import org.amanzi.awe.distribution.model.type.IDistributionType;
import org.amanzi.awe.ui.view.widgets.internal.AbstractComboWidget;
import org.amanzi.awe.views.distribution.widgets.DistributionTypeWidget.IDistributionTypeListener;
import org.eclipse.swt.widgets.Composite;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Nikolay Lagutko (nikolay.lagutko@amanzitel.com)
 * @since 1.0.0
 */
public class DistributionTypeWidget extends AbstractComboWidget<IDistributionType< ? >, IDistributionTypeListener> {

    public interface IDistributionTypeListener extends AbstractComboWidget.IComboSelectionListener {

        void onDistributionTypeSelected(IDistributionType< ? > distributionType);

    }

    private DistributionManager distributionManager;

    /**
     * @param parent
     * @param listener
     * @param label
     * @param minimalLabelWidth
     */
    public DistributionTypeWidget(final Composite parent, final IDistributionTypeListener listener, final String label,
            final int minimalLabelWidth) {
        super(parent, listener, label, minimalLabelWidth);
    }

    public void setDistributionManager(final DistributionManager manager) {
        this.distributionManager = manager;

        fillCombo();
    }

    @Override
    protected Collection<IDistributionType< ? >> getItems() {
        if (distributionManager != null) {
            return distributionManager.getAvailableDistirbutions();
        }
        return null;
    }

    @Override
    protected String getItemName(final IDistributionType< ? > item) {
        return item.getName();
    }

    @Override
    protected void fireListener(final IDistributionTypeListener listener, final IDistributionType< ? > selectedItem) {
        listener.onDistributionTypeSelected(selectedItem);
    }

    @Override
    protected int getDefaultSelectedItemIndex() {
        return -1;
    }

}
