package org.amanzi.neo.preferences;

import org.amanzi.neo.loader.internal.NeoLoaderPlugin;
import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

/**
 * <p>
 * Preference initializer
 * </p>
 * 
 * @author Cinkel_A
 * @since 1.1.0
 */
public class DataLoadPreferenceInitializer extends AbstractPreferenceInitializer {

    /**
     * constructor
     */
    public DataLoadPreferenceInitializer() {
        super();
    }

    @Override
    public void initializeDefaultPreferences() {
        IPreferenceStore pref = NeoLoaderPlugin.getDefault().getPreferenceStore();
        pref.setDefault(DataLoadPreferences.REMOVE_SITE_NAME, true);
    }

}
