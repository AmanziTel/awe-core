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

package org.amanzi.awe.nem.ui.widgets;

import org.amanzi.awe.nem.ui.widgets.CRSSelectionWidget.ICRSSelectedListener;
import org.amanzi.awe.ui.view.widgets.internal.AbstractAWEWidget;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Vladislav_Kondratenko
 * @since 1.0.0
 */
// TODO: LN: 16.10.2012, move this dialog to org.amanzi.awe.ui
public class CRSSelectionWidget extends AbstractAWEWidget<Button, ICRSSelectedListener> implements SelectionListener {

    public interface ICRSSelectedListener extends AbstractAWEWidget.IAWEWidgetListener {
        void onCRSSelecte();
    }

    private Button bCRS;

    public CRSSelectionWidget(final Composite parent, final int style, final ICRSSelectedListener listener) {
        super(parent, style, listener);
    }

    @Override
    protected Button createWidget(final Composite parent, final int style) {
        bCRS = new Button(parent, SWT.PUSH);
        bCRS.addSelectionListener(this);
        bCRS.setText("crs selection");
        bCRS.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        return bCRS;
    }

    @Override
    public void widgetSelected(final SelectionEvent e) {

    }

    @Override
    public void widgetDefaultSelected(final SelectionEvent e) {
        // TODO Auto-generated method stub

    }
}
