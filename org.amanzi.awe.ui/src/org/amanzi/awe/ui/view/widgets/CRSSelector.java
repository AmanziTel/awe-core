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

import org.amanzi.awe.ui.view.widgets.internal.AbstractAWEWidget;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.amanzi.awe.ui.view.widgets.CRSSelector.ICRSSelectorListener;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Nikolay Lagutko (nikolay.lagutko@amanzitel.com)
 * @since 1.0.0
 */
public class CRSSelector extends AbstractAWEWidget<Button, ICRSSelectorListener> implements SelectionListener {

    public interface ICRSSelectorListener extends AbstractAWEWidget.IAWEWidgetListener {
        void onCRSSelected(CoordinateReferenceSystem crs);
    }

    /**
     * @param parent
     * @param style
     * @param listener
     */
    protected CRSSelector(Composite parent, ICRSSelectorListener listener) {
        super(parent, SWT.FILL | SWT.PUSH, listener);
    }

    @Override
    protected Button createWidget(final Composite parent, final int style) {
        Button result = new Button(parent, style);

        result.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

        result.addSelectionListener(this);

        return result;
    }

    @Override
    public void widgetSelected(final SelectionEvent e) {
        // TODO Auto-generated method stub

    }

    @Override
    public void widgetDefaultSelected(final SelectionEvent e) {
        widgetSelected(e);

    }
}
