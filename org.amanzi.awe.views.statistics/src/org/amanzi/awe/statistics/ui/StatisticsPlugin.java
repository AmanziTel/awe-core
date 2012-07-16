package org.amanzi.awe.statistics.ui;

import org.amanzi.neo.model.distribution.impl.DistributionManager;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

public class StatisticsPlugin extends AbstractUIPlugin {

    public static final String PLUGIN_ID = "org.amanzi.awe.views.statistics";
    // The shared instance
    private static StatisticsPlugin plugin;

    /**
     * The constructor
     */
    public StatisticsPlugin() {
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

        DistributionManager.getManager().registerReusePlugin(this);
    }

    /**
     * Returns the shared instance
     * 
     * @return the shared instance
     */
    public static StatisticsPlugin getDefault() {
        return plugin;
    }

    /**
     * get Image descriptor for this plugin
     * 
     * @param imageFilePath
     * @return
     */
    public static ImageDescriptor getImageDescriptor(String imageFilePath) {
        return imageDescriptorFromPlugin(PLUGIN_ID, imageFilePath);
    }

}
