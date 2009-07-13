package org.amanzi.neo.loader.preferences;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.net.URL;

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

	@Override
	public void initializeDefaultPreferences() {
		Activator neoclipsePlugin = Activator.getDefault();
		NeoPreferenceHelper neoPreferenceHelper = new NeoPreferenceHelper();
		IPreferenceStore pref = neoclipsePlugin.getPreferenceStore();
		pref.setDefault(NeoDecoratorPreferences.NODE_PROPERTY_NAMES, "name");

		URL url = Platform.getBundle("org.amanzi.neo.loader").getEntry("");
		try {
			// Resolve the URL
			URL resolvedURL = FileLocator.resolve(url);
			pref.setDefault(NeoDecoratorPreferences.NODE_ICON_PROPERTY_NAMES,
					"type,time,code");
			pref.setDefault(NeoPreferences.DATABASE_LOCATION, System
					.getProperty("user.home")
					+ "/.amanzi/neo");

			File dir = new File(resolvedURL.getPath() + "icons");
			// This filter only returns directories
			FileFilter dirFilter = new FileFilter() {
				public boolean accept(File dir) {
					return dir.isDirectory() && !dir.getName().endsWith("svn");
				}
			};
			StringBuffer directoriesList = new StringBuffer();
			File[] files = dir.listFiles(dirFilter);
			/*
			 * for (File file : files) { directoriesList.append(file.getPath() +
			 * File.pathSeparator + "\n\r"); }
			 */
			neoPreferenceHelper.setIconDirectories(files);
			pref.setDefault(NeoDecoratorPreferences.NODE_ICON_LOCATION,
					files[0].getPath());
		} catch (IOException e) {
			NeoLoaderPlugin.exception(e);
		}
		neoclipsePlugin.setHelper(neoPreferenceHelper);
	}

	@Override
	public void earlyStartup() {
		initializeDefaultPreferences();
	}

}
