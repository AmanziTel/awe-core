package org.amanzi.awe.statistics.impl.internal;

import org.amanzi.awe.statistics.provider.IStatisticsModelProvider;
import org.amanzi.neo.providers.internal.AbstractProviderPlugin;
import org.osgi.framework.BundleContext;

public class StatisticsModelPlugin extends AbstractProviderPlugin {

    private static final String PLUGIN_ID = "org.amanzi.awe.statistics.impl";

    private static final String STATISTICS_MODEL_PROVIDER_ID = "org.amanzi.awe.statistics.StatisticsModelProvider";

    private static StatisticsModelPlugin instance;

    public StatisticsModelPlugin() {

    }

    /*
     * (non-Javadoc)
     * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
     */
    @Override
    public void start(final BundleContext bundleContext) throws Exception {
        super.start(bundleContext);
        instance = this;
    }

    /*
     * (non-Javadoc)
     * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
     */
    @Override
    public void stop(final BundleContext bundleContext) throws Exception {
        instance = null;
        super.stop(bundleContext);
    }

    public static StatisticsModelPlugin getDefault() {
        return instance;
    }

    @Override
    public String getPluginId() {
        return PLUGIN_ID;
    }

    public IStatisticsModelProvider getStatisticsModelProvider() {
        return getModelProvider(STATISTICS_MODEL_PROVIDER_ID);
    }
}
