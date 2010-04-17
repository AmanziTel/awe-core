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

package org.amanzi.awe.l3messages;

import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.Status;
import org.osgi.framework.BundleContext;

/**
 * Level 3 Messages Plugin
 * 
 * @author Lagutko_N
 * @since 1.0.0
 */
public class L3MessagesPlugin extends Plugin {
    
    // The shared instance
    private static L3MessagesPlugin plugin;
    
    // The plug-in ID
    public static final String PLUGIN_ID = "org.amanzi.awe.l3messages";
    
    /*
     * (non-Javadoc)
     * @see org.eclipse.ui.plugin.Plugin#start(org.osgi.framework.BundleContext)
     */
    @Override
    public void start(BundleContext context) throws Exception {
        super.start(context);
        plugin = this;
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.ui.plugin.Plugin#stop(org.osgi.framework.BundleContext)
     */
    @Override
    public void stop(BundleContext context) throws Exception {
        plugin = null;
        super.stop(context);
    }

    /**
     * Returns the shared instance
     *
     * @return the shared instance
     */
    public static L3MessagesPlugin getDefault() {
        return plugin;
    }
    
    public void logError(Exception e) {
        getLog().log(new Status(Status.ERROR, PLUGIN_ID, "Error on parsing Lever 3 Message", e));
    }
}
