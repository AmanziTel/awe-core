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

package org.amanzi.neo.loader.ui.page.widgets.impl;

import org.amanzi.neo.loader.ui.page.widgets.impl.CRSSelector.ICRSSelectorListener;
import org.amanzi.neo.loader.ui.page.widgets.internal.AbstractPageWidget;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Nikolay Lagutko (nikolay.lagutko@amanzitel.com)
 * @since 1.0.0
 */
// TODO: LN: 10.10.2012, refactor to use widgets from org.amanzi.awe.ui
public class CRSSelector extends AbstractPageWidget<Button, ICRSSelectorListener> implements SelectionListener {

    public interface ICRSSelectorListener extends AbstractPageWidget.IPageEventListener {
        void onCRSSelected(CoordinateReferenceSystem crs);
    }

    /**
     * @param isEnabled
     * @param parent
     * @param listener
     * @param projectModelProvider
     */
    protected CRSSelector(final Composite parent, final ICRSSelectorListener listener) {
        super(true, parent, listener, null);
    }

    @Override
    protected Button createWidget(final Composite parent, final int style) {
        final Button result = new Button(parent, style);

        // TODO: LN: 10.10.2012, make a factory for LayoutData
        result.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

        result.addSelectionListener(this);

        return result;
    }

    @Override
    protected int getStyle() {
        return SWT.FILL | SWT.PUSH;
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
