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

package org.amanzi.neo.services.ui;

import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * start-up plugin
 * 
 * @author Vladislav_Kondratenko
 */
public class NeoServicesUiPlugin extends AbstractUIPlugin {
    // The plug-in ID
    public static final String PLUGIN_ID = "org.amanzi.neo.services.ui";

    // The shared instance
    private static NeoServicesUiPlugin plugin;

    // private Transaction currentTransaction;

    /**
     * The constructor
     */
    public NeoServicesUiPlugin() {
        super();
        plugin = this;
    }

    /**
     * We hard coded a test load of this for the purpose of getting screenshots done.
     * 
     * @see org.eclipse.core.runtime.Plugins#start(org.osgi.framework.BundleContext)
     */
    public void start(BundleContext context) throws Exception {
        super.start(context);
        plugin = this;
    }

    /**
     * Stop this plugin (free resources)
     * 
     * @see org.eclipse.core.runtime.Plugin#stop(org.osgi.framework.BundleContext)
     */
    public void stop(BundleContext context) throws Exception {
        plugin = null;
        super.stop(context);
    }

    /**
     * @return the shared instance
     */
    public static NeoServicesUiPlugin getDefault() {
        return plugin;
    }
}
