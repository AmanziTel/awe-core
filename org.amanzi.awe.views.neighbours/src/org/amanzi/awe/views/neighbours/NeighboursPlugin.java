package org.amanzi.awe.views.neighbours;

import org.amanzi.neo.core.service.NeoServiceProvider;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class NeighboursPlugin extends AbstractUIPlugin {

    // The plug-in ID
    public static final String PLUGIN_ID = "org.amanzi.awe.views.neighbours";

	// The shared instance
    private static NeighboursPlugin plugin;

    /**
     * The constructor
     */
    public NeighboursPlugin() {
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
        //Lagutko, 9.10.2009, use NeoServiceProvider instead NeoManager
        NeoServiceProvider.getProvider().commit();
        plugin = null;
        super.stop(context);
    }

	/**
     * Returns the shared instance
     * 
     * @return the shared instance
     */
    public static NeighboursPlugin getDefault() {
        return plugin;
    }

	/**
     * Returns an image descriptor for the image file at the given plug-in relative path
     * 
     * @param path the path
     * @return the image descriptor
     */
    public static ImageDescriptor getImageDescriptor(String path) {
        return imageDescriptorFromPlugin(PLUGIN_ID, path);
    }

    public void commit() {
        //Lagutko, 9.10.2009, use NeoServiceProvider instead NeoManager
        NeoServiceProvider.getProvider().commit();      
    }

    public void rollback() {
        //Lagutko, 9.10.2009, use NeoServiceProvider instead NeoManager
        NeoServiceProvider.getProvider().rollback();
    }
}
