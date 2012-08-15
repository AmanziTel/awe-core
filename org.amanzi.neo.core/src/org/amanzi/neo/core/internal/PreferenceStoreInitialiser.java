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

package org.amanzi.neo.core.internal;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.amanzi.neo.dateformat.DateFormatManager;
import org.apache.log4j.Logger;
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
    private static final Logger LOGGER = Logger.getLogger(PreferenceStoreInitialiser.class);

    private static final IPreferenceStore PREFERENCE_STORE = NeoCorePlugin.getDefault().getPreferenceStore();
    private static final String PROPERTIES_FILE_NAME = "dates_format.txt";

    @Override
    public void initializeDefaultPreferences() {
        List<String> formatList = processFormatListFromProperties();
        PREFERENCE_STORE.setDefault(DateFormatManager.FORMATS_SIZE_KEY, formatList.size());
        for (int i = 0; i < formatList.size(); i++) {
            PREFERENCE_STORE.setDefault(DateFormatManager.DATE_KEY_PREFIX + i, formatList.get(i));
        }
    }

    /**
     *
     */
    private List<String> processFormatListFromProperties() {
        LOGGER.info("start processFormatListFromProperties");
        InputStream inputStream = null;
        try {
            inputStream = this.getClass().getResourceAsStream(PROPERTIES_FILE_NAME);
            Scanner scanner = new Scanner(inputStream);
            List<String> formatList = new ArrayList<String>();
            while (scanner.hasNext()) {
                formatList.add(scanner.nextLine());
            }
            inputStream.close();
            return formatList;
        } catch (IOException e) {
            LOGGER.error("Can't execute processFormatListFromProperties", e);
        }
        return null;
    }
}
