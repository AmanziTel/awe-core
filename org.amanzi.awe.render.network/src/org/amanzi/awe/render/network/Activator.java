package org.amanzi.awe.render.network;

import org.amanzi.neo.core.service.NeoServiceProvider;
import org.eclipse.core.runtime.Plugin;
import org.neo4j.api.core.Transaction;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends Plugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "org.amanzi.awe.render.network";

	// The shared instance
	private static Activator plugin;

    private Transaction currentTransaction;
	
	/**
	 * The constructor
	 */
	public Activator() {
	}

	/**
	 * We hard coded a test load of this for the purpose of getting screenshots done.
	 * @see org.eclipse.core.runtime.Plugins#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
        currentTransaction = NeoServiceProvider.getProvider().getService().beginTx();
	}

	/**
	 * Stop this plugin (free resources)
	 * @see org.eclipse.core.runtime.Plugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		plugin = null;
        currentTransaction.finish();
		super.stop(context);
	}

	/**
	 * @return the shared instance
	 */
	public static Activator getDefault() {
		return plugin;
	}

}
