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
import net.refractions.udig.catalog.internal.ui.CatalogView;

import org.amanzi.neo.core.service.NeoServiceProvider;
import org.amanzi.neo.core.service.listener.INeoServiceProviderListener;
import org.amanzi.neo.core.utils.ActionUtil;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class NeoCatalogPlugin extends AbstractUIPlugin implements INeoServiceProviderListener {
    // The plug-in ID
    public static final String PLUGIN_ID = "org.amanzi.awe.mapgraphic.star";

    // The shared instance
    private static NeoCatalogPlugin plugin;

    /**
     * The constructor
     */
    public NeoCatalogPlugin() {
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
     */
    public void start(BundleContext context) throws Exception {
        super.start(context);
        plugin = this;
        NeoServiceProvider.getProvider().addServiceProviderListener(this);

    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
     */
    public void stop(BundleContext context) throws Exception {
        plugin = null;
        super.stop(context);
        NeoServiceProvider.getProvider().removeServiceProviderListener(this);
    }

    /**
     * Returns the shared instance
     * 
     * @return the shared instance
     */
    public static NeoCatalogPlugin getDefault() {
        return plugin;
    }

    @Override
    public void onNeoCommit(Object source) {
        updateCatalog();
    }

    /**
     *
     */
    public void updateCatalog() {
        try {
            ActionUtil.getInstance().runTask(new Runnable() {
                @Override
                public void run() {
                    if (!getWorkbench().isClosing()) {
                        CatalogView view = (CatalogView)getWorkbench().getActiveWorkbenchWindow().getActivePage().findView(
                                CatalogView.VIEW_ID);
                        if (view != null) {
                            view.getTreeviewer().refresh();
                        }
                    }
                }
            }, true);
        } catch (Exception e) {
            // e.printStackTrace();
        }

    }

    @Override
    public void onNeoRollback(Object source) {
    }

    @Override
    public void onNeoStart(Object source) {
    }

    @Override
    public void onNeoStop(Object source) {
    }

}
