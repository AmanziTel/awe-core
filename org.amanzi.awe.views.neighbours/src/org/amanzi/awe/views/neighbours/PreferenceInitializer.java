package org.amanzi.awe.views.neighbours;

import org.eclipse.core.runtime.Preferences;
import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;

public class PreferenceInitializer extends AbstractPreferenceInitializer {
    public static final String N2N_MAX_SORTED_ROW = "N2N_MAX_SORTED_ROW";
    public static final String N2N_FORMATTED_MASK = "N2N_FORMATTED_MASK";

    public PreferenceInitializer() {
    }

    @Override
    public void initializeDefaultPreferences() {
        Preferences store = NeighboursPlugin.getDefault().getPluginPreferences();
        store.setDefault(N2N_MAX_SORTED_ROW, 1000);
        store.setDefault(N2N_FORMATTED_MASK, "#.####");
    }
}
