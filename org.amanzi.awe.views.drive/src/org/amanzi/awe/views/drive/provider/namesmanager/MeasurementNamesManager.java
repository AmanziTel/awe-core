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

package org.amanzi.awe.views.drive.provider.namesmanager;

import org.amanzi.awe.views.drive.DriveTreePlugin;
import org.eclipse.jface.preference.IPreferenceStore;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Vladislav_Kondratenko
 * @since 1.0.0
 */
public class MeasurementNamesManager {

    private static final IPreferenceStore PREFERENCE_STORE = DriveTreePlugin.getDefault().getPreferenceStore();

    public static final String MEASUREMENT_POINT_GENERAL_NAMES = "measuremrnt_point_general_names";

    private static final String COMMA_SEPARATOR = ",";

    private static class InstanceHolder {
        private static final MeasurementNamesManager INSTANCE = new MeasurementNamesManager();
    }

    public static MeasurementNamesManager getInstance() {
        return InstanceHolder.INSTANCE;
    }

    private String[] generalNames;

    /**
     * 
     */
    private MeasurementNamesManager() {
        super();
        // TODO Auto-generated constructor stub
    }

    public String[] getGeneralNames() {
        if (generalNames == null) {
            generalNames = PREFERENCE_STORE.getString(MEASUREMENT_POINT_GENERAL_NAMES).split(COMMA_SEPARATOR);
        }
        return generalNames;
    }
}
