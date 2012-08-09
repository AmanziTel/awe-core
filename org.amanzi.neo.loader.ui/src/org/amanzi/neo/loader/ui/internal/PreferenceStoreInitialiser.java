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

package org.amanzi.neo.loader.ui.internal;

import java.util.Arrays;
import java.util.List;

import org.amanzi.neo.loader.ui.preference.dateformat.manager.DateFormatManager;
import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Vladislav_Kondratenko
 * @since 1.0.0
 */
public class PreferenceStoreInitialiser extends AbstractPreferenceInitializer {
    private static final IPreferenceStore PREFERENCE_STORE = LoaderUIPlugin.getDefault().getPreferenceStore();
    private List<String> formatList = Arrays.asList("yyyy/MM/dd//hh", "yyyy/dd/MM/HH:mm:ss", "dd:mm:hh");

    @Override
    public void initializeDefaultPreferences() {
        PREFERENCE_STORE.setDefault(DateFormatManager.FORMATS_SIZE_KEY, formatList.size());
        for (int i = 0; i < formatList.size(); i++) {
            PREFERENCE_STORE.setDefault(DateFormatManager.DATE_KEY_PREFIX + i, formatList.get(i));
        }
    }

}
