package org.amanzi.awe.correlation;

import org.amanzi.awe.correlation.provider.ICorrelationModelProvider;
import org.amanzi.neo.providers.internal.AbstractProviderPlugin;
import org.osgi.framework.BundleContext;

public class CorrelationPlugin extends AbstractProviderPlugin {

    private static final String PLUGIN_ID = "org.amanzi.awe.correlation";

    private static final String CORRELATION_MODEL_PROVIDER_ID = "org.amanzi.providers.correlationModelProvider";

    private static CorrelationPlugin instance;

    public static CorrelationPlugin getDefault() {
        return instance;
    }

    public CorrelationPlugin() {

    }

    @Override
    public String getPluginId() {
        return PLUGIN_ID;
    }

    public ICorrelationModelProvider getCorrelationModelProvider() {
        return getModelProvider(CORRELATION_MODEL_PROVIDER_ID);
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

}
