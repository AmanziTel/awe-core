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

package org.amanzi.neo.loader.core.preferences;

import java.util.HashMap;

// TODO: Auto-generated Javadoc
/**
 * <p>
 * Preference store
 * </p>.
 *
 * @author tsinkel_a
 * @since 1.0.0
 */
public class PreferenceStore {
    
    /** The Constant instance. */
    private static final PreferenceStore instance = new PreferenceStore();

    /**
     * Gets the preference store.
     *
     * @return the preference store
     */
    public static PreferenceStore getPreferenceStore() {
        return instance;
    }

    /** The property map. */
    private final HashMap<String, Object> propertyMap;

    /**
     * Instantiates a new preference store.
     */
    private PreferenceStore() {
        propertyMap=new HashMap<String, Object>();
    }

    /**
     * Change property.
     *
     * @param property the property
     * @param newValue the new value
     * @param oldValue the old value
     */
    public void changeProperty(String property, Object newValue, Object oldValue) {
        if (newValue!=null){
            propertyMap.put(property, newValue);
        }else{
            propertyMap.remove(property);
        }
    }


    /**
     * Sets the property.
     *
     * @param name the name
     * @param value the value
     */
    public void setProperty(String name, Object value) {
            propertyMap.put(name, value);
    }
}
