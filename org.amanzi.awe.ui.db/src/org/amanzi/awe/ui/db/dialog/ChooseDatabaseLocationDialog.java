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

import org.amanzi.awe.ui.db.DatabaseUiPluginMessages;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.preference.DirectoryFieldEditor;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;

/**
 * <p>
 * </p>
 * 
 * @author Vladislav_Kondratenko
 * @since 1.0.0
 */
public class ChooseDatabaseLocationDialog extends Dialog {

    private final DirectoryFieldEditor fileDialog;
    private final IPreferenceStore store;
    private static final int LAYOUT_SIZE = 600;

    private String databaseLocation;

    public ChooseDatabaseLocationDialog(Shell parent, final String defaultLocation) {
        super(parent);
        databaseLocation = defaultLocation;
        super.create();
        getShell().setText(DatabaseUiPluginMessages.warningDialogName);

        store = PlatformUI.getPreferenceStore();
        if (StringUtils.isEmpty(store.getString(DatabaseUiPluginMessages.preferencePageDatabaseLocationKey))) {
            store.putValue(DatabaseUiPluginMessages.preferencePageDatabaseLocationKey, databaseLocation);
        }
        getButton(OK).setEnabled(false);

        Composite warningComposite = new Composite((Composite)getDialogArea(), SWT.NONE);
        warningComposite.setLayout(new GridLayout(1, false));
        warningComposite.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, true));

        Label warning = new Label(warningComposite, SWT.NONE);
        warning.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, true));
        warning.setText(DatabaseUiPluginMessages.warningDialogMessage);

        final Composite selectionComposite = new Composite(warningComposite, SWT.NONE);
        selectionComposite.setLayout(new GridLayout(3, false));
        GridData data = new GridData(SWT.LEFT, SWT.CENTER, true, true);
        data.widthHint = LAYOUT_SIZE;
        selectionComposite.setLayoutData(data);

        fileDialog = new DirectoryFieldEditor(DatabaseUiPluginMessages.preferencePageDatabaseLocationKey,
                DatabaseUiPluginMessages.warningDialogChooseDatabaseLabel, selectionComposite);
        fileDialog.getTextControl(selectionComposite).setText(
                store.getString(DatabaseUiPluginMessages.preferencePageDatabaseLocationKey));
        fileDialog.getTextControl(selectionComposite).setSize(600, 50);
        fileDialog.getTextControl(selectionComposite).addModifyListener(new ModifyListener() {

            @Override
            public void modifyText(ModifyEvent e) {
                if (!((Text)e.getSource()).getText().equals(databaseLocation)) {
                    getButton(OK).setEnabled(true);
                }

            }
        });

        getShell().pack();
    }

    @Override
    protected void okPressed() {
        databaseLocation = fileDialog.getStringValue();
        store.putValue(DatabaseUiPluginMessages.preferencePageDatabaseLocationKey, databaseLocation);
        super.okPressed();
    }

    /**
     * @return Returns the databaseLocation.
     */
    public String getDatabaseLocation() {
        return databaseLocation;
    }

}
