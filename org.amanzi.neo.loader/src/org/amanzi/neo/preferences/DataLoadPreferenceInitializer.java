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
        pref.setDefault(DataLoadPreferences.NETWORK_COMBINED_CALCULATION, true);
        pref.setDefault(DataLoadPreferences.ZOOM_TO_LAYER, true);
    }

}
