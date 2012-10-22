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

package org.amanzi.awe.nem.managers.properties;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.amanzi.awe.nem.internal.NemPlugin;
import org.eclipse.jface.preference.IPreferenceStore;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Vladislav_Kondratenko
 * @since 1.0.0
 */
public class NetworkPropertiesManager {

    private static final IPreferenceStore PREFERENCE_STORE = NemPlugin.getDefault().getPreferenceStore();

    private static final String COMMA_SEPARATOR = ",";

    private static class ManagerInstanceHolder {
        private static final NetworkPropertiesManager MANAGER = new NetworkPropertiesManager();
    }

    public static NetworkPropertiesManager getInstance() {
        return ManagerInstanceHolder.MANAGER;
    }

    private NetworkPropertiesManager() {
        initTypesMap();
    }

    /**
     *
     */
    private void initTypesMap() {
        for (KnownTypes type : KnownTypes.values()) {
            String knownProperties = PREFERENCE_STORE.getString(type.getId());
            if (knownProperties == null) {
                continue;
            }
            List<String> newProperties = new ArrayList<String>();
            String[] properties = knownProperties.split(COMMA_SEPARATOR);
            for (String singleProperty : properties) {
                newProperties.add(singleProperty);
            }
            propertiesTypes.put(type, newProperties);
        }
    }

    private Map<KnownTypes, List<String>> propertiesTypes = new HashMap<KnownTypes, List<String>>();

    public List<PropertyContainer> getProperties(String type) {
        List<PropertyContainer> properties;
        properties = new ArrayList<PropertyContainer>();
        String[] exitedProperties = getPropertiesForType(type);
        for (String singleProperty : exitedProperties) {
            KnownTypes propertyType = getPropertyType(singleProperty);
            properties.add(new PropertyContainer(singleProperty, propertyType));
        }
        return properties;
    }

    /**
     * @param singleProperty
     * @return
     */
    private KnownTypes getPropertyType(String singleProperty) {
        for (Entry<KnownTypes, List<String>> typesAssociation : propertiesTypes.entrySet()) {
            if (typesAssociation.getValue().contains(singleProperty)) {
                return typesAssociation.getKey();
            }
        }
        return null;
    }

    /**
     * @param type
     * @return
     */
    private String[] getPropertiesForType(String type) {
        String value = PREFERENCE_STORE.getString(type);
        String[] properties;
        if (value.length() > 0) {
            properties = value.split(COMMA_SEPARATOR);
        } else {
            properties = new String[0];
        }
        return properties;
    }
}
