package org.amanzi.rdt.internal.launching;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.ui.IStartup;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class AweLaunchingPlugin extends Plugin implements IStartup {

	// The plug-in ID
	public static final String PLUGIN_ID = "org.amanzi.rdt.launching";

	// The shared instance
	private static AweLaunchingPlugin plugin;
	
	/**
	 * The constructor
	 */
	public AweLaunchingPlugin() {
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.core.runtime.Plugins#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		//ResourcesPlugin.getWorkspace().getRoot().getpr
		
		super.start(context);
		plugin = this;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.core.runtime.Plugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static AweLaunchingPlugin getDefault() {
		return plugin;
	}
	
	/**
	 * Actions on startup
	 * 
	 */

	public void earlyStartup() {
		
	}

}
