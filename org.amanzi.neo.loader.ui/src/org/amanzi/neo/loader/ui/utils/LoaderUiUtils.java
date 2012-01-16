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
package org.amanzi.neo.loader.ui.utils;

import java.util.ArrayList;
import java.util.List;

import org.amanzi.neo.loader.core.LoaderUtils;
import org.amanzi.neo.loader.core.preferences.DataLoadPreferences;
import org.amanzi.neo.loader.core.preferences.PreferenceStore;

/**
 * TODO Purpose of
 * <p>
 * common actions in loaders ui
 * </p>
 * 
 * @author Vladislav_Kondratenko
 * @since 1.0.0
 */
public class LoaderUiUtils extends LoaderUtils {    
    /*
     * constants
     */
    public static final String FILE_PREFIX = "file://";
    public static final String COMMA_SEPARATOR = ",";

    /**
     * @param key -key of value from preference store
     * @return array of possible headers
     */
    public static String[] getPossibleHeaders(String key) {

        String text = PreferenceStore.getPreferenceStore().getValue(key);
        if (text == null) {
            return new String[0];
        }
        String[] array = text.split(COMMA_SEPARATOR);
        List<String> result = new ArrayList<String>();
        for (String string : array) {
            String value = string.trim();
            if (!value.isEmpty()) {
                result.add(value);
            }
        }
        return result.toArray(new String[0]);
    }
   
    /**
     * Returns Default Directory path for file dialogs in DriveLoad and NetworkLoad
     * 
     * @return default directory
     */

    public static String getDefaultDirectory() {
        String result = PreferenceStore.getPreferenceStore().getValue(DataLoadPreferences.DEFAULT_DIRRECTORY_LOADER);
        if (result == null) {
            result = System.getProperty("user.home");
        }

        return result;
    }

    /**
     * Sets Default Directory path for file dialogs in DriveLoad and NetworkLoad
     * 
     * @param newDirectory new default directory
     */

    public static void setDefaultDirectory(String newDirectory) {
        PreferenceStore.getPreferenceStore().setDefault(DataLoadPreferences.DEFAULT_DIRRECTORY_LOADER, newDirectory);
    }

}
