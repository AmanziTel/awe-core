package org.amanzi.awe.properties.ui;

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

import org.amanzi.awe.properties.ui.views.AWEPropertySheet;
import org.amanzi.awe.ui.views.IAWEView;
import org.amanzi.neo.providers.internal.AbstractProviderPlugin;
import org.eclipse.ui.views.properties.IPropertySheetPage;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class AWEPropertiesPlugin extends AbstractProviderPlugin {

    // The plug-in ID
    public static final String PLUGIN_ID = "org.amanzi.awe.views.property";

    // The shared instance
    private static AWEPropertiesPlugin plugin;

    private AWEPropertySheet propertySheet;

    /**
     * The constructor
     */
    public AWEPropertiesPlugin() {
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
    public static AWEPropertiesPlugin getDefault() {
        return plugin;
    }

    @Override
    public String getPluginId() {
        return PLUGIN_ID;
    }

    public IPropertySheetPage registerView(final IAWEView view) {
        if (propertySheet == null) {
            propertySheet = new AWEPropertySheet();
        }

        propertySheet.registerView(view);

        return propertySheet;
    }

    public void unregisterView(final IAWEView view) {
        if (propertySheet != null) {
            propertySheet.unregisterView(view);
        }
    }
}
