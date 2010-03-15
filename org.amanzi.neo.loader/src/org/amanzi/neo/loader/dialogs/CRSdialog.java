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

package org.amanzi.neo.loader.dialogs;

import net.refractions.udig.ui.CRSChooser;
import net.refractions.udig.ui.Controller;

import org.amanzi.neo.loader.internal.NeoLoaderPluginMessages;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 * TODO Purpose of 
 * <p>
 *
 * </p>
 * @author Cinkel_A
 * @since 1.0.0
 */
public class CRSdialog extends Dialog {
    protected int status = SWT.CANCEL;
    private CRSChooser crsch;
    protected CoordinateReferenceSystem selectedCRS;

    /**
     * @param parent
     */
    public CRSdialog(Shell parent, CoordinateReferenceSystem selectedCRS) {
        super(parent, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL | SWT.CENTER);
        this.selectedCRS = selectedCRS;
    }

    public int open() {
        Shell parentShell = getParent();
        Shell shell = new Shell(parentShell, getStyle());
        shell.setText(NeoLoaderPluginMessages.CRSdialog_TITLE);

        createContents(shell);
        shell.pack();

        // calculate location
        // Point size = parentShell.getSize();
        // int dlgWidth = shell.getSize().x;
        // int dlgHeight = shell.getSize().y;
        // shell.setLocation(100, 100);
        shell.open();

        // wait
        Display display = getParent().getDisplay();
        while (!shell.isDisposed()) {
            if (!display.readAndDispatch()) {
                display.sleep();
            }
        }
        return status;
    }

    private Control createContents(final Shell shell) {
        shell.setLayout(new GridLayout(2, true));

        Label lb = new Label(shell, SWT.NONE);
        lb.setText(NeoLoaderPluginMessages.CRSdialog_label_Select);
        GridData layoutData = new GridData();
        layoutData.horizontalSpan = 2;
        lb.setLayoutData(layoutData);
        crsch = new CRSChooser(new Controller() {

            @Override
            public void handleOk() {

            }

            @Override
            public void handleClose() {
            }
        });
        Control control = crsch.createControl(shell);
        GridData gridData = new GridData();
        gridData.horizontalSpan = 2;
        control.setLayoutData(gridData);
        Button btnSave = new Button(shell, SWT.PUSH);
        btnSave.setText(NeoLoaderPluginMessages.CRSdialog_button_SAVE);
        GridData gdBtnSave = new GridData();
        gdBtnSave.horizontalAlignment = GridData.CENTER;
        btnSave.setLayoutData(gdBtnSave);
        btnSave.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                selectedCRS = crsch.getCRS();
                status = SWT.OK;
                shell.close();
            }

        });
        Button btnCancel = new Button(shell, SWT.PUSH);
        btnCancel.setText(NeoLoaderPluginMessages.CRSdialog_button_CANSEL);
        GridData gdBtnCancel = new GridData();
        gdBtnCancel.horizontalAlignment = GridData.CENTER;
        btnCancel.setLayoutData(gdBtnCancel);
        btnCancel.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                status = SWT.CANCEL;
                selectedCRS = null;
                shell.close();
            }

        });

        return shell;
    }

    public CoordinateReferenceSystem getCRS() {
        return selectedCRS;
    }
}
