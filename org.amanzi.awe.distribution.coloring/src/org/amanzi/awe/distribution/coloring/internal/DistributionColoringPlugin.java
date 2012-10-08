package org.amanzi.awe.distribution.coloring.internal;

import org.amanzi.awe.distribution.provider.IDistributionModelProvider;
import org.amanzi.neo.providers.internal.AbstractProviderPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class DistributionColoringPlugin extends AbstractProviderPlugin {

    // The plug-in ID
    private static final String PLUGIN_ID = "org.amanzi.awe.distribution.coloring"; //$NON-NLS-1$

    private static final String DISTRIBUTION_MODEL_PROVIDER = "org.amanzi.providers.distributionModelProvider";

    // The shared instance
    private static DistributionColoringPlugin plugin;

    /**
     * The constructor
     */
    public DistributionColoringPlugin() {
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
     */
    @Override
    public void start(final BundleContext context) throws Exception {
        super.start(context);
        plugin = this;
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
     */
    @Override
    public void stop(final BundleContext context) throws Exception {
        plugin = null;
        super.stop(context);
    }

    /**
     * Returns the shared instance
     * 
     * @return the shared instance
     */
    public static DistributionColoringPlugin getDefault() {
        return plugin;
    }

    @Override
    public String getPluginId() {
        return PLUGIN_ID;
    }

    public IDistributionModelProvider getDistributionModelProvider() {
        return getModelProvider(DISTRIBUTION_MODEL_PROVIDER);
    }

}
