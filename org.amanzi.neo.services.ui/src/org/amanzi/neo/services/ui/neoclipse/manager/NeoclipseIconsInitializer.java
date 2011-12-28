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

package org.amanzi.neo.services.ui.neoclipse.manager;

import java.io.IOException;
import java.net.URL;

import org.amanzi.neo.services.AbstractService;
import org.amanzi.neo.services.NetworkService;
import org.amanzi.neo.services.ui.NeoServicesUiPlugin;
import org.apache.log4j.Logger;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.preference.IPreferenceStore;
import org.neo4j.neoclipse.Activator;
import org.neo4j.neoclipse.preference.DecoratorPreferences;

/**
 * Initialize property for neoclipse node view.
 * 
 * @author Vladislav_Kondratneko
 */
public class NeoclipseIconsInitializer {
    private static final Logger LOGGER = Logger.getLogger(NeoclipseIconsInitializer.class);
    /**
     * path to icons directory
     */
    private final String ICONS_PATH = "images/icons";
    /**
     * path to necessary directory which situated in icons folder
     */
    private final String NECESSARY_DIRECTORY_PATH = "16";

    /**
     * initialize preferences;
     */
    private void initializeDefaultPreferences() {
        Activator neoclipseActivator = org.neo4j.neoclipse.Activator.getDefault();
        if (neoclipseActivator == null) {
            LOGGER.error("cann't find activator org.neo4j.neoclipse.Activator");
            return;
        }
        IPreferenceStore pref = neoclipseActivator.getPreferenceStore();
        pref.setDefault(DecoratorPreferences.NODE_PROPERTY_NAMES, NetworkService.NAME + ",value,time,code,call_type,"
                + NetworkService.SOURCE_NAME);
        pref.setDefault(DecoratorPreferences.NODE_ICON_PROPERTY_NAMES, AbstractService.TYPE);
        URL iconsUrl = Platform.getBundle(NeoServicesUiPlugin.PLUGIN_ID).findEntries(ICONS_PATH, NECESSARY_DIRECTORY_PATH, false)
                .nextElement();
        URL fileDir;
        try {
            fileDir = FileLocator.toFileURL(iconsUrl);
        } catch (IOException e) {
            LOGGER.error("<initializeDefaultPreferences()> Error while try to get icons location", e);
            return;
        }
        pref.setDefault(DecoratorPreferences.NODE_ICON_LOCATION, fileDir.getPath());
    }

    /**
     * create instance of neoclipse icons initializer and initialize neoclipse with default values;
     */
    public NeoclipseIconsInitializer() {
        super();
        initializeDefaultPreferences();
    }

}
