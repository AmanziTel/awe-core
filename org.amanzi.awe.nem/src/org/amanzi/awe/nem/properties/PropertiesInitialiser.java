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

package org.amanzi.awe.nem.properties;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map.Entry;
import java.util.Properties;

import org.amanzi.awe.nem.internal.NemPlugin;
import org.apache.log4j.Logger;
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
public class PropertiesInitialiser extends AbstractPreferenceInitializer {
    private static final Logger LOGGER = Logger.getLogger(PropertiesInitialiser.class);

    private final static IPreferenceStore PREFERENCE_STORE = NemPlugin.getDefault().getPreferenceStore();

    private static final String DEFAUL_NETWORK_PROPERTIES_FILE = "default_network.properties";

    @Override
    public void initializeDefaultPreferences() {
        Properties properties = new Properties();
        try {
            properties.load(new FileInputStream(DEFAUL_NETWORK_PROPERTIES_FILE));
            for (Entry<Object, Object> singleProperty : properties.entrySet()) {
                PREFERENCE_STORE.setDefault(singleProperty.getValue().toString(), singleProperty.getKey().toString());
            }
        } catch (FileNotFoundException e) {
            LOGGER.error("file not found", e);
        } catch (IOException e) {
            LOGGER.error(e);
        }
    }
}
