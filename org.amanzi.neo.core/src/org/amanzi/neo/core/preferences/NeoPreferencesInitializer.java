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
package org.amanzi.neo.core.preferences;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import org.amanzi.neo.core.NeoCorePlugin;
import org.apache.log4j.Logger;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.IStartup;
import org.neo4j.neoclipse.Activator;
import org.neo4j.neoclipse.preference.NeoDecoratorPreferences;
import org.neo4j.neoclipse.preference.NeoPreferenceHelper;
import org.neo4j.neoclipse.preference.NeoPreferences;

/**
 * Initializes neoclipse preferences when the org.amanzi.neo.loader plugin is
 * started
 * 
 * @author Pechko_E
 * 
 */
public class NeoPreferencesInitializer extends AbstractPreferenceInitializer
		implements IStartup {
    private static final Logger LOGGER = Logger.getLogger(NeoPreferencesInitializer.class);

	@Override
	public void initializeDefaultPreferences() {
	    initializeDefaultPreferences("neo");
	}

	@SuppressWarnings("unchecked")
	private void initializeDefaultPreferences(String databaseName) {
		Activator neoclipsePlugin = Activator.getDefault();
		NeoPreferenceHelper neoPreferenceHelper = new NeoPreferenceHelper();
		IPreferenceStore pref = neoclipsePlugin.getPreferenceStore();
		pref.setDefault(NeoDecoratorPreferences.NODE_PROPERTY_NAMES, "name,value,time,code");
        pref.setDefault(NeoPreferences.MAXIMUM_NODES_RETURNED, 500);
        pref.setDefault(NeoDecoratorPreferences.NODE_ICON_PROPERTY_NAMES, "type");
        pref.setDefault(NeoPreferences.DATABASE_LOCATION, checkDirs(
                new String[] {System.getProperty("user.home"), ".amanzi", databaseName}).getPath());

        ArrayList<URL> iconDirs = new ArrayList<URL>(0);
        for (Object found : Collections.list(Platform.getBundle("org.amanzi.neo.loader").findEntries("/icons", "*", false))) {
            if (found instanceof URL) {
                URL dir = (URL)found;
                if (!dir.getPath().contains("svn") && (dir.getPath().endsWith("/") || dir.getPath().endsWith("\\"))) {
                    String[] comps = dir.getPath().split("/");
                    LOGGER.debug("Found directory: " + comps[2]);
                    try {
                        URL fileDir = FileLocator.toFileURL(dir);
                        iconDirs.add(fileDir);
                    } catch (IOException e) {
                        System.err.println("Failed to process directory '" + dir + "': " + e.getMessage());
                        NeoCorePlugin.error(null, e);
                    }
                }
            }
        }
        Collections.sort(iconDirs, new Comparator<URL>(){
            
            public int compare(URL a, URL b) {
                // TODO check maybe this will work: return a.toString().compareTo(b.toString());
                String ap[] = a.getPath().split("/");
                String bp[] = b.getPath().split("/");
                try {
                    int x = Integer.parseInt(ap[ap.length-1]);
                    int y = Integer.parseInt(bp[bp.length-1]);
                    return x-y;
                }catch(NumberFormatException e){
                    return ap[ap.length-1].compareTo(bp[bp.length-1]);
                }
            }
        });
        neoPreferenceHelper.setIconLocations(iconDirs);
        neoPreferenceHelper.setIconLocationLabel("Icon size (pixels):");
        neoPreferenceHelper.setIconLocationNote("The sizes are mapped to directories containing icons with filenames corresponding to the settings for node icon filename properties below");
        pref.setDefault(NeoDecoratorPreferences.NODE_ICON_LOCATION, iconDirs.get(0).getPath());
        neoclipsePlugin.setHelper(neoPreferenceHelper);
	}

	// todo checl if this method is actually needed
	private File checkDirs(String[] path) {
        File dir = new File(path[0]);
        for (int i = 1; i < path.length; i++) {
            dir = checkDirs(dir, path[i]);
        }
        return dir;
    }

    private File checkDirs(File root, String dirName) {
        File dir = new File(root, dirName);
        if (!dir.exists()) {
            dir.mkdirs();
        } else if (!dir.isDirectory()) {
            //TODO system.err shouldn't be used!
            System.err.println(dir.getPath() + " is not a directory");
        }
        return dir;
    }

    /**
     * Stratup with default runtime preferences
     */
    public void earlyStartup() {
		initializeDefaultPreferences();
	}

    /**
     * Startup with testing preferences (different database location)
     */
    public void startupTesting() {
        initializeDefaultPreferences("neo_test");
    }

}
