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

package org.amanzi.neo.loader.ui.page.widgets.impl.internal;

import org.amanzi.neo.loader.ui.page.widgets.impl.SelectDriveResourcesWidget;
import org.amanzi.neo.loader.ui.page.widgets.impl.SelectDriveResourcesWidget.ISelectDriveResourceListener;
import org.amanzi.neo.loader.ui.page.widgets.internal.AbstractPageWidget;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.List;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Nikolay Lagutko (nikolay.lagutko@amanzitel.com)
 * @since 1.0.0
 */
public class DriveDataFileSelector extends AbstractPageWidget<Composite, SelectDriveResourcesWidget.ISelectDriveResourceListener> {

    private List availableFilesList;

    private List selectedFilesList;

    private Button addAllButton;

    private Button addSelectedButton;

    private Button removeAllButton;

    private Button removeSelectedButton;

    /**
     * @param isEnabled
     * @param parent
     * @param listener
     * @param projectModelProvider
     */
    protected DriveDataFileSelector(Composite parent, ISelectDriveResourceListener listener) {
        super(true, parent, listener, null);
    }

    @Override
    protected Composite createWidget(Composite parent, int style) {

        return parent;
    }

    @Override
    protected int getStyle() {
        return SWT.FILL;
    }

}
