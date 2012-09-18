package org.amanzi.awe.charts.impl;

import org.amanzi.awe.charts.model.provider.IChartModelProvider;
import org.amanzi.awe.charts.model.provider.impl.ChartModelProvider;
import org.eclipse.core.runtime.Plugin;
import org.osgi.framework.BundleContext;

public class ChartModelPlugin extends Plugin {

    private static ChartModelPlugin instance;

    public ChartModelPlugin() {

    }

    @Override
    public void start(final BundleContext bundleContext) throws Exception {
        super.start(bundleContext);
        instance = this;
    }

    @Override
    public void stop(final BundleContext bundleContext) throws Exception {
        instance = null;
        super.stop(bundleContext);
    }

    public static ChartModelPlugin getDefault() {
        return instance;
    }

    public IChartModelProvider getChartModelProvider() {
        return ChartModelProvider.getInstance();
    }
}
