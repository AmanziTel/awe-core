package org.amanzi.neo.core;

import java.io.IOException;

import org.amanzi.neo.core.database.services.AweProjectService;
import org.amanzi.neo.core.database.services.UpdateDatabaseManager;
import org.amanzi.neo.core.preferences.NeoPreferencesInitializer;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.Status;
import org.osgi.framework.BundleContext;

/**
 * Plugin class for org.amanzi.neo.core
 * 
 * @author Lagutko_N
 * @since 1.1.0
 */

public class NeoCorePlugin extends Plugin {

	/*
	 * Plugin's ID
	 */

	private static final String ID = "org.amanzi.neo.core";

	/*
	 * Plugin variable
	 */

	static private NeoCorePlugin plugin;

	/*
	 * Initializer for AWE-specific Neo Preferences
	 */

	private NeoPreferencesInitializer initializer = new NeoPreferencesInitializer();

	private AweProjectService aweProjectService;
	private UpdateDatabaseManager updateBDManager;

	/**
	 * Constructor for SplashPlugin.
	 */
	public NeoCorePlugin() {
		super();
		plugin = this;
	}

	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
		updateBDManager = new UpdateDatabaseManager();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext
	 * )
	 */
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance.
	 */
	public static NeoCorePlugin getDefault() {
		return plugin;
	}

	/**
	 * Returns initializer of NeoPreferences
	 * 
	 * @return initializer of Neo Preferences
	 */

	public NeoPreferencesInitializer getInitializer() {
		return initializer;
	}

	/**
	 * 
	 * @return awe project service
	 */
	public AweProjectService getProjectService() {
		if (aweProjectService == null) {
			aweProjectService = new AweProjectService();
		}
		return aweProjectService;
	}

	/**
	 * 
	 * @return UpdateBDManager
	 */
	public UpdateDatabaseManager getUpdateDatabaseManager() {
		return updateBDManager;
	}

	/**
	 * Sets initializer of NeoPreferences
	 * 
	 * @param initializer
	 *            new initializer for NeoPreferences
	 */

	public void setInitializer(NeoPreferencesInitializer initializer) {
		this.initializer = initializer;
	}

	/**
	 * Print a message and information about exception to Log
	 * 
	 * @param message
	 *            message
	 * @param e
	 *            exception
	 */

	public static void error(String message, Throwable e) {
		getDefault().getLog().log(
				new Status(IStatus.ERROR, ID, 0,
						message == null ? "" : message, e)); //$NON-NLS-1$
	}
	
	public String getNeoPluginLocation() {
	    try {
	        return FileLocator.resolve(Platform.getBundle("org.neo4j").getEntry(".")).getFile();
	    }
	    catch (IOException e) {
	        error(null, e);
	        return null;
	    }
	}

}
