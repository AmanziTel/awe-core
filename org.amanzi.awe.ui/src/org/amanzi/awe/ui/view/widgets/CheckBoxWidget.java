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

import org.amanzi.awe.ui.view.widgets.CheckBoxWidget.ICheckBoxSelected;
import org.amanzi.awe.ui.view.widgets.internal.AbstractAWEWidget;
import org.amanzi.awe.ui.view.widgets.internal.AbstractLabeledWidget;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

/**
 * TODO Purpose of
 * <p>
 *
 * </p>
 * @author Nikolay Lagutko (nikolay.lagutko@amanzitel.com)
 * @since 1.0.0
 */
public class CheckBoxWidget extends AbstractLabeledWidget<Button, ICheckBoxSelected> implements SelectionListener {

    public interface ICheckBoxSelected extends AbstractAWEWidget.IAWEWidgetListener {

        public void onCheckBoxSelected(CheckBoxWidget source);

    }

    /**
     * @param parent
     * @param listener
     * @param label
     */
    protected CheckBoxWidget(final Composite parent, final ICheckBoxSelected listener, final String label) {
        super(parent, listener, label);
    }

    @Override
    protected Button createControl(final Composite parent) {
        Button checkBox = new Button(parent, SWT.CHECK);

        checkBox.addSelectionListener(this);

        return checkBox;
    }

    @Override
    public void widgetSelected(final SelectionEvent e) {
        for (ICheckBoxSelected listener : getListeners()) {
            listener.onCheckBoxSelected(this);
        }

    }

    @Override
    public void widgetDefaultSelected(final SelectionEvent e) {
        widgetSelected(e);
    }

    public boolean isChecked() {
        return getControl().getSelection();
    }

}
