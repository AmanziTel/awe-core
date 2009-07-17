package org.amanzi.neo.loader.preferences;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import org.amanzi.neo.loader.internal.NeoLoaderPlugin;
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

	@SuppressWarnings("unchecked")
	@Override
	public void initializeDefaultPreferences() {
		Activator neoclipsePlugin = Activator.getDefault();
		NeoPreferenceHelper neoPreferenceHelper = new NeoPreferenceHelper();
		IPreferenceStore pref = neoclipsePlugin.getPreferenceStore();
		pref.setDefault(NeoDecoratorPreferences.NODE_PROPERTY_NAMES, "name,time,code");
        pref.setDefault(NeoDecoratorPreferences.NODE_ICON_PROPERTY_NAMES, "type");
        pref.setDefault(NeoPreferences.DATABASE_LOCATION, checkDirs(
                new String[] {System.getProperty("user.home"), ".amanzi", "neo"}).getPath());

        ArrayList<URL> iconDirs = new ArrayList<URL>();
        for (Object found : Collections.list(Platform.getBundle("org.amanzi.neo.loader").findEntries("/icons", "*", false))) {
            if (found instanceof URL) {
                URL dir = (URL)found;
                if (!dir.getPath().contains("svn") && (dir.getPath().endsWith("/") || dir.getPath().endsWith("\\"))) {
                    String[] comps = dir.getPath().split("/");
                    System.out.println("Found directory: " + comps[2]);
                    try {
                        URL fileDir = FileLocator.toFileURL(dir);
                        iconDirs.add(fileDir);
                    } catch (IOException e) {
                        System.err.println("Failed to process directory '" + dir + "': " + e.getMessage());
                        NeoLoaderPlugin.exception(e);
                    }
                }
            }
        }
        Collections.sort(iconDirs, new Comparator<URL>(){
            @Override
            public int compare(URL a, URL b) {
                try {
                    String ap[] = a.getPath().split("/");
                    int x = Integer.parseInt(ap[ap.length-1]);
                    String bp[] = b.getPath().split("/");
                    int y = Integer.parseInt(bp[bp.length-1]);
                    return x-y;
                }catch(NumberFormatException e){
                    return 0;
                }
            }
        });
        neoPreferenceHelper.setIconLocations(iconDirs);
        neoPreferenceHelper.setIconLocationLabel("Icon size (pixels):");
        neoPreferenceHelper.setIconLocationNote("The sizes are mapped to directories containing icons with filenames corresponding to the settings for node icon filename properties below");
        pref.setDefault(NeoDecoratorPreferences.NODE_ICON_LOCATION, iconDirs.get(0).getPath());
        neoclipsePlugin.setHelper(neoPreferenceHelper);
	}

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
            System.err.println(dir.getPath() + " is not a directory");
        }
        return dir;
    }

	public void earlyStartup() {
		initializeDefaultPreferences();
	}

}
