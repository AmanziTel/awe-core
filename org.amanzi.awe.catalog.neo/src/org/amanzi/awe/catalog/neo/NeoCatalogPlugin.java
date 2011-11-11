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
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;

import net.refractions.udig.catalog.internal.ui.CatalogView;

import org.amanzi.awe.catalog.neo.upd_layers.LayerUpdateManager;
import org.amanzi.neo.services.events.UpdateViewEvent;
import org.amanzi.neo.services.events.UpdateViewEventType;
import org.amanzi.neo.services.ui.INeoServiceProviderListener;
import org.amanzi.neo.services.ui.IUpdateViewListener;
import org.amanzi.neo.services.ui.NeoServiceProviderUi;
import org.amanzi.neo.services.ui.utils.ActionUtil;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class NeoCatalogPlugin extends AbstractUIPlugin implements INeoServiceProviderListener,IUpdateViewListener {
    // The plug-in ID
    public static final String PLUGIN_ID = "org.amanzi.awe.mapgraphic.star";

    // The shared instance
    private static NeoCatalogPlugin plugin;
    
    private static final Collection<UpdateViewEventType> handedTypes;
    static {
        Collection<UpdateViewEventType> spr = new HashSet<UpdateViewEventType>();
        spr.add(UpdateViewEventType.GIS);
        handedTypes = Collections.unmodifiableCollection(spr);
    }

    private LayerUpdateManager layerManager;
    
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
        NeoServiceProviderUi.getProvider().addServiceProviderListener(this);
//        NeoCorePlugin.getDefault().getUpdateViewManager().addListener(this);
        layerManager = new LayerUpdateManager();
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
     */
    public void stop(BundleContext context) throws Exception {
        plugin = null;
        super.stop(context);
        NeoServiceProviderUi.getProvider().removeServiceProviderListener(this);
//        NeoCorePlugin.getDefault().getUpdateViewManager().removeListener(this);
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

    @Override
    public void updateView(UpdateViewEvent event) {
        updateCatalog();
    }

    @Override
    public Collection<UpdateViewEventType> getType() {
        return handedTypes;
    }
    
    /**
     * @return Returns the layerManager.
     */
    public LayerUpdateManager getLayerManager() {
        return layerManager;
    }

}
