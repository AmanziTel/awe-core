package org.amanzi.awe.views.neighbours;

import org.amanzi.neo.core.utils.NeoUtils;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.neo4j.api.core.Transaction;
import org.neo4j.neoclipse.Activator;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class NeighboursPlugin extends AbstractUIPlugin {

    // The plug-in ID
    public static final String PLUGIN_ID = "org.amanzi.awe.views.neighbours";

	// The shared instance
    private static NeighboursPlugin plugin;

    private Transaction tx;

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
        tx = Activator.getDefault().getNeoServiceManager().getNeoService().beginTx();
        super.start(context);
        plugin = this;
    }

	/*
     * (non-Javadoc)
     * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
     */
    public void stop(BundleContext context) throws Exception {
        tx.failure();
        tx.finish();
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
        tx.success();
        tx.finish();
        Activator.getDefault().getNeoServiceManager().commit();
        tx = NeoUtils.beginTransaction();
    }

    public void rollback() {
        tx.failure();
        tx.finish();
        Activator.getDefault().getNeoServiceManager().rollback();
        tx = NeoUtils.beginTransaction();
    }
}
