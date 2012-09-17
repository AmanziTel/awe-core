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

package org.amanzi.awe.views.statistics.widget;

import java.util.Collection;

import org.amanzi.awe.ui.view.widget.internal.AbstractComboWidget;
import org.amanzi.awe.views.statistics.widget.PeriodComboWidget.IPeriodSelectionListener;
import org.amanzi.neo.core.period.Period;
import org.amanzi.neo.models.measurement.IMeasurementModel;
import org.eclipse.swt.widgets.Composite;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Nikolay Lagutko (nikolay.lagutko@amanzitel.com)
 * @since 1.0.0
 */
public class PeriodComboWidget extends AbstractComboWidget<Period, IPeriodSelectionListener> {

    public interface IPeriodSelectionListener extends AbstractComboWidget.IComboSelectionListener {

        void onPeriodSelected(Period period);

    }

    private IMeasurementModel model;

    /**
     * @param parent
     * @param label
     */
    public PeriodComboWidget(final Composite parent, final IPeriodSelectionListener listener, final String label,
            final int minimalLabelWidth) {
        super(parent, listener, label, minimalLabelWidth);
    }

    @Override
    protected Collection<Period> getItems() {
        if (model != null) {
            return Period.getAvailablePeriods(model.getMinTimestamp(), model.getMaxTimestamp());
        }
        return null;
    }

    public void setModel(final IMeasurementModel model) {
        if (model != null) {
            this.model = model;

            fillCombo();
            setEnabled(true);
        } else {
            setEnabled(false);
        }
    }

    @Override
    protected String getItemName(final Period item) {
        return item.getId();
    }

    @Override
    protected void fireListener(final IPeriodSelectionListener listener, final Period selectedItem) {
        listener.onPeriodSelected(selectedItem);
    }
}
