package org.amanzi.neo.geoptima.loader.ui.internal;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class GeoptimaLoaderUi implements BundleActivator {

	private static BundleContext context;

	static BundleContext getContext() {
		return context;
	}

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext bundleContext) throws Exception {
		GeoptimaLoaderUi.context = bundleContext;
	}

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext bundleContext) throws Exception {
		GeoptimaLoaderUi.context = null;
	}

}
