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

package org.amanzi.awe.views.drive.preferencestore;

import org.amanzi.awe.views.drive.DriveTreePlugin;
import org.amanzi.awe.views.drive.provider.namesmanager.MeasurementNamesManager;
import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

/**
 * <p>
 * </p>
 * 
 * @author Vladislav_Kondratenko
 * @since 1.0.0
 */
public class PreferenceStoreInitialiser extends AbstractPreferenceInitializer {

    private static final IPreferenceStore PREFERENCE_STORE = DriveTreePlugin.getDefault().getPreferenceStore();

    @Override
    public void initializeDefaultPreferences() {
        PREFERENCE_STORE.setDefault(MeasurementNamesManager.MEASUREMENT_POINT_GENERAL_NAMES, "event,name,timestamp");
    }

}
