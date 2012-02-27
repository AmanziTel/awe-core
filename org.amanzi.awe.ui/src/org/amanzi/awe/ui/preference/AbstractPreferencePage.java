/**
 * 
 */
package org.amanzi.awe.ui.preference;

import org.amanzi.awe.ui.AweUiPlugin;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

/**
 * This is the common superclass for all awe preference pages.
 * 
 * @author Bondoronok_P
 */
public abstract class AbstractPreferencePage extends FieldEditorPreferencePage
		implements IWorkbenchPreferencePage {

	/**
	 * The constructor
	 */
	public AbstractPreferencePage() {
		super();
		setPreferenceStore(AweUiPlugin.getDefault().getPreferenceStore());
	}

	/**
	 * Initializes the page.
	 */
	@Override
	public void init(final IWorkbench workbench) {
	}
}
