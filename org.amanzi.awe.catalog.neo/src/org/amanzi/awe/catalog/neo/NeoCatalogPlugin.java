/* AWE - Amanzi Wireless Explorer
 * http://awe.amanzi.org
 * (C) 2008-2009, AmanziTel AB
 *
 * This library is provided under the terms of the Eclipse Public License
 * as described at http://www.eclipse.org/legal/epl-v10.html. Any use,
 * reproduction or distribution of the library constitutes recipient's
 * acceptance of this agreement.
 *
 * This library is distributed WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 */
package org.amanzi.awe.catalog.neo;

import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * <p>
 * Neo Catalog Plugin
 * </p>
 * 
 * @author Bondoronok_p
 */
public class NeoCatalogPlugin extends AbstractUIPlugin {

	/**
	 * Plugin variable
	 */
	static private NeoCatalogPlugin plugin;
	private IPropertyChangeListener propertyListener;

	/**
	 * Constructor for SplashPlugin.
	 */
	public NeoCatalogPlugin() {
		super();
		plugin = this;
	}

	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
//		propertyListener = new IPropertyChangeListener() {
//
//			@Override
//			public void propertyChange(PropertyChangeEvent event) {
//				getPreferenceStore().firePropertyChangeEvent(
//						event.getProperty(), event.getNewValue(),
//						event.getOldValue());
//			}
//		};
//		getPreferenceStore().addPropertyChangeListener(propertyListener);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext
	 * )
	 */
	@Override
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		getPreferenceStore().removePropertyChangeListener(propertyListener);
		super.stop(context);
	}

	/**
	 * Returns the shared instance.
	 */
	public static NeoCatalogPlugin getDefault() {
		return plugin;
	}
}
