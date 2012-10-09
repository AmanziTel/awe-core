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

import org.amanzi.awe.ui.view.widgets.internal.AbstractAWEWidget;
import org.amanzi.awe.ui.view.widgets.internal.AbstractAWEWidget.IAWEWidgetListener;
import org.amanzi.awe.nem.ui.widgets.CRSSelectionWidget.ICRSSelectedListener;
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
public class CRSSelectionWidget extends AbstractAWEWidget<Button, ICRSSelectedListener> implements SelectionListener {

    public CRSSelectionWidget(Composite parent, int style, ICRSSelectedListener listener) {
        super(parent, style, listener);
    }

    public interface ICRSSelectedListener extends IAWEWidgetListener {
        void onCRSSelecte();
    }

    private Button bCRS;

    @Override
    protected Button createWidget(Composite parent, int style) {
        bCRS = new Button(parent, SWT.PUSH);
        bCRS.addSelectionListener(this);
        bCRS.setText("crs selection");
        bCRS.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        return bCRS;
    }

    @Override
    public void widgetSelected(SelectionEvent e) {

    }

    @Override
    public void widgetDefaultSelected(SelectionEvent e) {
        // TODO Auto-generated method stub

    }
}
