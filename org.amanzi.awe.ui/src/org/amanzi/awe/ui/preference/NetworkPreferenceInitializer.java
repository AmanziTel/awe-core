/**
 * 
 */
package org.amanzi.awe.ui.preference;

import org.amanzi.awe.ui.AweUiPlugin;
import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

/**
 * @author pavleg
 *
 */
public class NetworkPreferenceInitializer extends AbstractPreferenceInitializer {

	/* (non-Javadoc)
	 * @see org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer#initializeDefaultPreferences()
	 */
	@Override
	public void initializeDefaultPreferences() {
		IPreferenceStore store = AweUiPlugin.getDefault().getPreferenceStore();
		store.setDefault(NetworkPreferences.BEAMWIDTH, 40);
		store.setDefault(NetworkPreferences.SITE_SECTOR_NAME, Boolean.FALSE);
	}

}
