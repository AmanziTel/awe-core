package org.amanzi.awe.render.sites;

import java.net.URL;
import java.util.List;

import net.refractions.udig.catalog.CatalogPlugin;
import net.refractions.udig.catalog.ICatalog;
import net.refractions.udig.catalog.IService;

import org.eclipse.core.runtime.Plugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends Plugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "org.amanzi.awe.render.sites";

	// The shared instance
	private static Activator plugin;
	
	/**
	 * The constructor
	 */
	public Activator() {
	}

	/**
	 * We hard code the .csv and .geo_json resources off locahost:3005.
	 * TODO: Fix this so it does not need hard-coding. Need to support dragging
	 * a URL directly onto the catalog or map without getting the WFS activated. 
	 * @see org.eclipse.core.runtime.Plugins#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
        ICatalog catalog = CatalogPlugin.getDefault().getLocalCatalog();
        List<IService> services = CatalogPlugin.getDefault().getServiceFactory().createService(new URL("http://localhost:3005/sites/gis.geo_json"));
        catalog.add(services.get(1));
        services = CatalogPlugin.getDefault().getServiceFactory().createService(new URL("http://localhost:3005/sites.csv"));
        catalog.add(services.get(1));
	}

	/**
	 * Stop this plugin (free resources)
	 * @see org.eclipse.core.runtime.Plugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	/**
	 * @return the shared instance
	 */
	public static Activator getDefault() {
		return plugin;
	}

}
