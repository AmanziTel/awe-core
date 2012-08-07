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

package org.amanzi.awe.ui.db.dialog;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.preference.DirectoryFieldEditor;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Vladislav_Kondratenko
 * @since 1.0.0
 */
public class ChooseDatabaseLocationDialog extends Dialog {

    private DirectoryFieldEditor fileDialog;
    private String databaseLocation = System.getProperty("user.home") + "/.amanzi/neo";
    private IPreferenceStore store;
    private static final String DATABASE_LOCATION_PATH = "databaseLocation";

    private boolean isCanceled = false;

    public ChooseDatabaseLocationDialog(Shell parent) {
        super(parent);
        create();
        getShell().setText("Change database location");
        store = PlatformUI.getPreferenceStore();
        if (StringUtils.isEmpty(store.getString(DATABASE_LOCATION_PATH))) {
            store.putValue(DATABASE_LOCATION_PATH, databaseLocation);
        }
        Composite warningComposite = new Composite((Composite)getDialogArea(), SWT.NONE);
        warningComposite.setLayout(new GridLayout(1, false));
        warningComposite.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, true));
        Label warning = new Label(warningComposite, SWT.NONE);
        warning.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, true));
        warning.setText("Database path already used. Please select another database location");
        Composite selectionComposite = new Composite(warningComposite, SWT.NONE);
        selectionComposite.setLayout(new GridLayout(3, false));
        selectionComposite.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, true));
        fileDialog = new DirectoryFieldEditor(DATABASE_LOCATION_PATH, "Choose db location", selectionComposite);
        fileDialog.getTextControl(selectionComposite).setText(store.getString(DATABASE_LOCATION_PATH));
        getShell().pack();
    }

    @Override
    protected void okPressed() {
        databaseLocation = fileDialog.getStringValue();
        store.putValue(DATABASE_LOCATION_PATH, databaseLocation);
        super.okPressed();
    }

    @Override
    protected void cancelPressed() {
        isCanceled = true;
        super.cancelPressed();
    }

    /**
     * @return Returns the databaseLocation.
     */
    public String getDatabaseLocation() {
        return databaseLocation;
    }

    /**
     * @return Returns the isCancel.
     */
    public boolean isCanceled() {
        return isCanceled;
    }
}
