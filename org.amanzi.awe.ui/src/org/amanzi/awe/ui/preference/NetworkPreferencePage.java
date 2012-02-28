/**
 * 
 */
package org.amanzi.awe.ui.preference;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.IntegerFieldEditor;
import org.eclipse.swt.widgets.Composite;

/**
 * The page for Awe Network preferences
 * 
 * @author Bondoronok_P
 */
public class NetworkPreferencePage extends AbstractPreferencePage {

	private static final String DEFAULT_BEAMWIDTH = "Default beamwidth :";
	private static final String SITE_SECTOR_NAME = "Use the name of the sector for the name of the site";
	private static final int MIN_BEAMWIDTH = 10;
	private static final int MAX_BEAMWIDTH = 360;

	private IntegerFieldEditor integerFieldEditor;
	private BooleanFieldEditor booleanFieldEditor;

	/**
	 * The constructor
	 */
	public NetworkPreferencePage() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jface.preference.FieldEditorPreferencePage#createFieldEditors
	 * ()
	 */
	@Override
	protected void createFieldEditors() {
		Composite parent = getFieldEditorParent();
		integerFieldEditor = new IntegerFieldEditor(
				NetworkPreferences.BEAMWIDTH, DEFAULT_BEAMWIDTH, parent);
		integerFieldEditor.setValidRange(MIN_BEAMWIDTH, MAX_BEAMWIDTH);
		addField(integerFieldEditor);

		booleanFieldEditor = new BooleanFieldEditor(
				NetworkPreferences.SITE_SECTOR_NAME, SITE_SECTOR_NAME, parent);

		addField(booleanFieldEditor);
	}
}
