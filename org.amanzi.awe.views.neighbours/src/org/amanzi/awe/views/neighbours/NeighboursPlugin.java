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
package org.amanzi.awe.views.neighbours;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;

import org.amanzi.awe.views.neighbours.views.NeighboursView;
import org.amanzi.awe.views.neighbours.views.TransmissionView;
import org.amanzi.neo.core.NeoCorePlugin;
import org.amanzi.neo.core.database.listener.IUpdateDatabaseListener;
import org.amanzi.neo.core.database.services.UpdateDatabaseEvent;
import org.amanzi.neo.core.database.services.UpdateDatabaseEventType;
import org.amanzi.neo.core.service.NeoServiceProvider;
import org.amanzi.neo.core.utils.ActionUtil;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class NeighboursPlugin extends AbstractUIPlugin implements IUpdateDatabaseListener {
    private static final Collection<UpdateDatabaseEventType> handedTypes;
    static {
        Collection<UpdateDatabaseEventType> spr = new HashSet<UpdateDatabaseEventType>();
        spr.add(UpdateDatabaseEventType.GIS);
        spr.add(UpdateDatabaseEventType.NEIGHBOUR);
        handedTypes = Collections.unmodifiableCollection(spr);
    }

    // The plug-in ID
    public static final String PLUGIN_ID = "org.amanzi.awe.views.neighbours";

	// The shared instance
    private static NeighboursPlugin plugin;

    /**
     * The constructor
     */
    public NeighboursPlugin() {
    }

	/*
     * (non-Javadoc)
     * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
     */
    public void start(BundleContext context) throws Exception {
        super.start(context);
        plugin = this;
        NeoCorePlugin.getDefault().getUpdateDatabaseManager().addListener(this);
    }

	/*
     * (non-Javadoc)
     * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
     */
    public void stop(BundleContext context) throws Exception {
        //Lagutko, 9.10.2009, use NeoServiceProvider instead NeoManager
        NeoServiceProvider.getProvider().rollback();
        plugin = null;
        super.stop(context);
        NeoCorePlugin.getDefault().getUpdateDatabaseManager().removeListener(this);
    }

	/**
     * Returns the shared instance
     * 
     * @return the shared instance
     */
    public static NeighboursPlugin getDefault() {
        return plugin;
    }

	/**
     * Returns an image descriptor for the image file at the given plug-in relative path
     * 
     * @param path the path
     * @return the image descriptor
     */
    public static ImageDescriptor getImageDescriptor(String path) {
        return imageDescriptorFromPlugin(PLUGIN_ID, path);
    }

    public void commit() {
        //Lagutko, 9.10.2009, use NeoServiceProvider instead NeoManager
        NeoServiceProvider.getProvider().commit();      
    }

    public void rollback() {
        //Lagutko, 9.10.2009, use NeoServiceProvider instead NeoManager
        NeoServiceProvider.getProvider().rollback();
    }

    /**
     *updates ReuseAnalyserView
     */
    private void updateView() {
        ActionUtil.getInstance().runTask(new Runnable() {

            @Override
            public void run() {
                IViewPart neighbourView = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().findView(
                        NeighboursView.ID);
                if (neighbourView != null) {
                    ((NeighboursView)neighbourView).updateView();
                }
            }
        }, true);
    }

    /**
     *updates ReuseAnalyserView
     */
    private void updateTransmissionView() {
        ActionUtil.getInstance().runTask(new Runnable() {

            @Override
            public void run() {
                IViewPart neighbourView = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().findView(
                        TransmissionView.ID);
                if (neighbourView != null) {
                    ((TransmissionView)neighbourView).updateView();
                }
            }
        }, true);
    }
    @Override
    public void databaseUpdated(UpdateDatabaseEvent event) {
        updateTransmissionView();
        updateView();

    }

    @Override
    public Collection<UpdateDatabaseEventType> getType() {
        return handedTypes;
    }
}
