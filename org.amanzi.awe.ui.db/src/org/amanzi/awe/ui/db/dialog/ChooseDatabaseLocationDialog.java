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
	private static final int THREE_COLUMN_SIZE = 3;

	private String databaseLocation;

	public ChooseDatabaseLocationDialog(final Shell parent, final String defaultLocation) {
		super(parent);
		databaseLocation = defaultLocation;
		super.create();
		getShell().setText(DatabaseUiPluginMessages.warningDialogName);

		store = PlatformUI.getPreferenceStore();
		if (StringUtils.isEmpty(store.getString(DatabaseUiPluginMessages.preferencePageDatabaseLocationKey))) {
			store.putValue(DatabaseUiPluginMessages.preferencePageDatabaseLocationKey, databaseLocation);
		}
		getButton(OK).setEnabled(false);

		Composite warningComposite = createComposite((Composite)getDialogArea(), 1);
		Label warning = new Label(warningComposite, SWT.NONE);
		warning.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, true));
		warning.setText(DatabaseUiPluginMessages.warningDialogMessage);

		final Composite selectionComposite = createComposite(warningComposite, THREE_COLUMN_SIZE);
		fileDialog = new DirectoryFieldEditor(DatabaseUiPluginMessages.preferencePageDatabaseLocationKey,
				DatabaseUiPluginMessages.warningDialogChooseDatabaseLabel, selectionComposite);

		Text textControl = fileDialog.getTextControl(selectionComposite);
		textControl.setText(store.getString(DatabaseUiPluginMessages.preferencePageDatabaseLocationKey));

		//TODO: LN: 07.08.2012, make this class as a listener
		textControl.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(final ModifyEvent e) {
				if (!((Text)e.getSource()).getText().equals(databaseLocation)) {
					getButton(OK).setEnabled(true);
				}

			}
		});

		getShell().pack();
	}

	/**
	 * @param dialogArea
	 * @param i
	 * @return
	 */
	private Composite createComposite(final Composite parentComposite, final int columnCount) {
		Composite composite = new Composite(parentComposite, SWT.NONE);
		//TODO: LN: 07.08.2012, GridLayout can be moved to constants
		composite.setLayout(new GridLayout(columnCount, false));
		//TODO: LN: 07.08.2012, GridData duplicated, NOTE: do not make a constant - just make a method 'createSomeGridData' since SWT changes this object on rendering
		GridData data = new GridData(SWT.LEFT, SWT.CENTER, true, true);
		data.widthHint = LAYOUT_SIZE;
		composite.setLayoutData(data);
		return composite;
	}

	@Override
	protected void okPressed() {
		databaseLocation = fileDialog.getStringValue();
		//TODO: LN: 07.08.2012, key of PreferenceStore value can not be internationalized
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
