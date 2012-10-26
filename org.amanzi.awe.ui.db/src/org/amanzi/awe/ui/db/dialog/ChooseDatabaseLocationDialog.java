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
import org.amanzi.neo.db.internal.DatabasePlugin;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.preference.DirectoryFieldEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/**
 * <p>
 * </p>
 * 
 * @author Vladislav_Kondratenko
 * @since 1.0.0
 */
public class ChooseDatabaseLocationDialog extends Dialog implements ModifyListener {

    private final DirectoryFieldEditor fileDialog;
    private static final int LAYOUT_SIZE = 600;
    private static final GridLayout ONE_ELEMENT_LAYOUT = new GridLayout(1, false);
    private static final GridLayout THREE_ELEMENT_LAYOUT = new GridLayout(3, false);
    private String databaseLocation;

    public ChooseDatabaseLocationDialog(final Shell parent, final String defaultLocation) {
        super(parent);
        databaseLocation = defaultLocation;
        super.create();
        getShell().setText(DatabaseUiPluginMessages.warningDialogName);

        getButton(OK).setEnabled(false);

        Composite warningComposite = createComposite((Composite)getDialogArea(), ONE_ELEMENT_LAYOUT);
        Label warning = new Label(warningComposite, SWT.NONE);
        warning.setLayoutData(createGridData());
        warning.setText(DatabaseUiPluginMessages.warningDialogMessage);

        final Composite selectionComposite = createComposite(warningComposite, THREE_ELEMENT_LAYOUT);
        fileDialog = new DirectoryFieldEditor(DatabasePlugin.PREFERENCE_KEY_DATABASE_LOCATION,
                DatabaseUiPluginMessages.warningDialogChooseDatabaseLabel, selectionComposite);

        Text textControl = fileDialog.getTextControl(selectionComposite);
        textControl.addModifyListener(this);
        textControl.setText(databaseLocation);
        getShell().pack();
    }

    /**
     * @param dialogArea
     * @param i
     * @return
     */
    private Composite createComposite(final Composite parentComposite, final GridLayout layot) {
        Composite composite = new Composite(parentComposite, SWT.NONE);
        composite.setLayout(layot);
        GridData data = createGridData();
        data.widthHint = LAYOUT_SIZE;
        composite.setLayoutData(data);
        return composite;
    }

    protected GridData createGridData() {
        return new GridData(SWT.LEFT, SWT.CENTER, true, true);
    }

    /**
     * @return Returns the databaseLocation.
     */
    public String getDatabaseLocation() {
        return databaseLocation;
    }

    @Override
    public void modifyText(final ModifyEvent e) {
        if (!((Text)e.getSource()).getText().equals(databaseLocation)) {
            databaseLocation = ((Text)e.getSource()).getText();
            getButton(OK).setEnabled(true);
        }

    }

    @Override
    protected void okPressed() {
        super.okPressed();
    }

}
