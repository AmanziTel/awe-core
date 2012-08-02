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

import org.amanzi.neo.loader.ui.internal.Messages;
import org.amanzi.neo.loader.ui.page.widgets.impl.SelectDriveResourcesWidget;
import org.amanzi.neo.loader.ui.page.widgets.impl.SelectDriveResourcesWidget.ISelectDriveResourceListener;
import org.amanzi.neo.loader.ui.page.widgets.internal.AbstractPageWidget;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
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

    protected static final GridLayout FIXED_ONE_ROW_LAYOUT = new GridLayout(1, false);

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
    public DriveDataFileSelector(Composite parent, ISelectDriveResourceListener listener) {
        super(true, parent, listener, null);
    }

    @Override
    protected Composite createWidget(Composite parent, int style) {
        availableFilesList = createListComposite(parent, Messages.DriveDataFileSelector_DirectoryFilesLabel);

        selectedFilesList = createListComposite(parent, Messages.DriveDataFileSelector_ChoosenFilesLabel);

        return parent;
    }

    private List createListComposite(Composite parent, String labelText) {
        Composite panel = new Composite(parent, SWT.NONE);
        panel.setLayout(FIXED_ONE_ROW_LAYOUT);
        panel.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        Label listLabel = new Label(panel, SWT.NONE);
        listLabel.setText(labelText);
        listLabel.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));

        List list = new List(panel, SWT.BORDER | SWT.MULTI | SWT.V_SCROLL | SWT.H_SCROLL);
        GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true);
        gridData.minimumWidth = 150;
        list.setLayoutData(gridData);

        return list;
    }

    @Override
    protected int getStyle() {
        return SWT.FILL;
    }

}
