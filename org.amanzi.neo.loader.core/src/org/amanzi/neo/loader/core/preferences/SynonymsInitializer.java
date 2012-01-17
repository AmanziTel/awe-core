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

import java.util.Enumeration;
import java.util.ResourceBundle;

import org.amanzi.neo.loader.core.internal.NeoLoaderPlugin;
import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

/**
 * TODO Purpose of 
 * <p>
 *
 * </p>
 * @author lagutko_n
 * @since 1.0.0
 */
public class SynonymsInitializer extends AbstractPreferenceInitializer {
    
    public static final String SYNONYM_KEYS = "synonym_keys";
    
    public static final String SEPARATOR = ",";
    
    private static final ResourceBundle resourceBundle = ResourceBundle.getBundle(SynonymsInitializer.class.getName());

    @Override
    public void initializeDefaultPreferences() {
        StringBuilder keySynonyms = new StringBuilder();
        
        Enumeration<String> keyEnum = resourceBundle.getKeys();
        
        IPreferenceStore store = NeoLoaderPlugin.getDefault().getPreferenceStore();
        
        while (keyEnum.hasMoreElements()) {
            String key = keyEnum.nextElement();
            String synonyms = resourceBundle.getString(key);
            
            keySynonyms.append(key).append(SEPARATOR);
            
            store.setDefault(key, synonyms);
        }
        
        store.setDefault(SYNONYM_KEYS, keySynonyms.toString());
    }
    
    

}
