package org.amanzi.neo.services.ui;

import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class NeoServicesUiPlugin extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "org.amanzi.neo.services.ui"; //$NON-NLS-1$

	// The shared instance
	private static NeoServicesUiPlugin plugin;
	private UiService service=null;
	private  Object mon=new Object();
	
	/**
	 * The constructor
	 */
	public NeoServicesUiPlugin() {
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}
	
	/**
	 * Gets the ui service.
	 *
	 * @return the ui service
	 */
	public UiService getUiService(){
	    if (service==null){
	        synchronized (mon) {
	            if (service==null){
	                service=new UiService();
	            }
            }
	    }
	    return service;
	}
	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static NeoServicesUiPlugin getDefault() {
		return plugin;
	}

}
