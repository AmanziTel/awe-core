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

package org.amanzi.awe.render.drive;

import org.amanzi.awe.console.AweConsolePlugin;
import org.osgi.framework.BundleContext;

/**
 * plugin initialization
 * 
 * @author Vladislav_Kondratenko
 */
public class DriveRendererPlugin extends AweConsolePlugin {

    // The plug-in ID
    public static final String PLUGIN_ID = "org.amanzi.awe.render.drive";

    // The shared instance
    private static DriveRendererPlugin plugin;

    // private Transaction currentTransaction;

    /**
     * The constructor
     */
    public DriveRendererPlugin() {
    }

    /**
     * We hard coded a test load of this for the purpose of getting screenshots done.
     * 
     * @see org.eclipse.core.runtime.Plugins#start(org.osgi.framework.BundleContext)
     */
    public void start(BundleContext context) throws Exception {
        super.start(context);
        plugin = this;
        // currentTransaction = NeoServiceProvider.getProvider().getService().beginTx();
    }

    /**
     * Stop this plugin (free resources)
     * 
     * @see org.eclipse.core.runtime.Plugin#stop(org.osgi.framework.BundleContext)
     */
    public void stop(BundleContext context) throws Exception {
        plugin = null;
        // currentTransaction.finish();
        super.stop(context);
    }

    /**
     * @return the shared instance
     */
    public static DriveRendererPlugin getDefault() {
        return plugin;
    }

}
