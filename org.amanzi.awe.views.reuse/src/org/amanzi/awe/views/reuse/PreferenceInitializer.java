package org.amanzi.awe.views.reuse;

import org.eclipse.core.runtime.Preferences;
import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;

public class PreferenceInitializer extends AbstractPreferenceInitializer {
    public static String RV_MODELS = "RV_MODELS";
    public PreferenceInitializer() {
    }

    @Override
    public void initializeDefaultPreferences() {
        Preferences store = ReusePlugin.getDefault().getPluginPreferences();
        store.setDefault(RV_MODELS, "");
    }

}
