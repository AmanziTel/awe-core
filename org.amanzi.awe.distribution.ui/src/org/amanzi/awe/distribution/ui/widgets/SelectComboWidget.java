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

import java.util.Collection;

import org.amanzi.awe.distribution.engine.manager.DistributionManager;
import org.amanzi.awe.distribution.model.type.IDistributionType.Select;
import org.amanzi.awe.distribution.ui.widgets.SelectComboWidget.ISelectChanged;
import org.amanzi.awe.ui.view.widgets.internal.AbstractComboWidget;
import org.eclipse.swt.widgets.Composite;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Nikolay Lagutko (nikolay.lagutko@amanzitel.com)
 * @since 1.0.0
 */
public class SelectComboWidget extends AbstractComboWidget<Select, ISelectChanged> {

    public interface ISelectChanged extends AbstractComboWidget.IComboSelectionListener {

        void onSelectChanged(Select select);

    }

    private DistributionManager distributionManager;

    /**
     * @param parent
     * @param listener
     * @param label
     * @param minimalLabelWidth
     */
    public SelectComboWidget(final Composite parent, final ISelectChanged listener, final String label, final int minimalLabelWidth) {
        super(parent, listener, label, minimalLabelWidth);
    }

    public void setDistributionManager(final DistributionManager distributionManager) {
        this.distributionManager = distributionManager;

        fillCombo();
    }

    @Override
    protected Collection<Select> getItems() {
        if (distributionManager != null) {
            return distributionManager.getPossibleSelects();
        }
        return null;
    }

    @Override
    public void fillCombo() {
        super.fillCombo();

        if (getItems() != null && getItems().size() == 1) {
            setEnabled(false);
        }
    }

    @Override
    protected String getItemName(final Select item) {
        return item.name();
    }

    @Override
    protected void fireListener(final ISelectChanged listener, final Select selectedItem) {
        listener.onSelectChanged(selectedItem);

    }

}
