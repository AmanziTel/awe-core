/**
 * 
 */
package org.amanzi.awe.ui.preference;

import org.amanzi.awe.ui.AweUiPlugin;
import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

/**
 * Network preference initializer
 * 
 * @author Bondoronok_P	 
 */
public class NetworkPreferenceInitializer extends AbstractPreferenceInitializer {
	
	private static final int DEF_BEAMWIDTH = 40;	

	/* (non-Javadoc)
	 * @see org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer#initializeDefaultPreferences()
	 */
	@Override
	public void initializeDefaultPreferences() {
		IPreferenceStore store = AweUiPlugin.getDefault().getPreferenceStore();
		store.setDefault(NetworkPreferences.BEAMWIDTH, DEF_BEAMWIDTH);
		store.setDefault(NetworkPreferences.SITE_SECTOR_NAME, Boolean.TRUE);		
	}

}
