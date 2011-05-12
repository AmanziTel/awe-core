package org.amanzi.awe.afp;

import org.eclipse.core.runtime.Preferences;
import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;

public class PreferenceInitializer extends AbstractPreferenceInitializer {
    public static final String AFP_MIN_CO = "AFP_MIN_CO";
    public static final String AFP_MIN_PROP_VALUE = "AFP_MIN_PROP_VALUE";
    public PreferenceInitializer() {
    }

    @Override
    public void initializeDefaultPreferences() {
        Preferences store = Activator.getDefault().getPluginPreferences();
        store.setDefault(AFP_MIN_CO, 0.0001);
        store.setDefault(AFP_MIN_PROP_VALUE, 0.0001);
    }

}
