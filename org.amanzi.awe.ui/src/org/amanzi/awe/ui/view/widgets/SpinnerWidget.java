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

package org.amanzi.awe.ui.view.widgets;

import org.amanzi.awe.ui.view.widgets.SpinnerWidget.ISpinnerListener;
import org.amanzi.awe.ui.view.widgets.internal.AbstractAWEWidget;
import org.amanzi.awe.ui.view.widgets.internal.AbstractLabeledWidget;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Spinner;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Nikolay Lagutko (nikolay.lagutko@amanzitel.com)
 * @since 1.0.0
 */
public class SpinnerWidget extends AbstractLabeledWidget<Spinner, ISpinnerListener> {

    public interface ISpinnerListener extends AbstractAWEWidget.IAWEWidgetListener {

    }

    /**
     * @param parent
     * @param listener
     * @param label
     */
    protected SpinnerWidget(final Composite parent, final ISpinnerListener listener, final String label) {
        super(parent, listener, label);
    }

    @Override
    protected Spinner createControl(final Composite parent) {
        Spinner spinner = new Spinner(parent, SWT.BORDER);

        spinner.setDigits(0);
        spinner.setIncrement(1);
        spinner.setMinimum(0);
        spinner.setSelection(1);

        return spinner;
    }
}
