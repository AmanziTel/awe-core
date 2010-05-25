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
package org.amanzi.awe.views.drive;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;

import org.amanzi.awe.views.drive.views.CorrelationList;
import org.amanzi.awe.views.drive.views.CorrelationManager;
import org.amanzi.awe.views.drive.views.DriveInquirerView;
import org.amanzi.neo.core.NeoCorePlugin;
import org.amanzi.neo.core.database.listener.IUpdateViewListener;
import org.amanzi.neo.core.database.services.events.NewCorrelationEvent;
import org.amanzi.neo.core.database.services.events.UpdateViewEvent;
import org.amanzi.neo.core.database.services.events.UpdateViewEventType;
import org.amanzi.neo.core.utils.ActionUtil;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class DriveInquirerPlugin extends AbstractUIPlugin implements IUpdateViewListener {
    private static final Collection<UpdateViewEventType> handedTypes;
    static {
        Collection<UpdateViewEventType> spr = new HashSet<UpdateViewEventType>();
        spr.add(UpdateViewEventType.GIS);
        spr.add(UpdateViewEventType.NEW_CORRELATION);
        handedTypes = Collections.unmodifiableCollection(spr);
    }
    // The plug-in ID
    public static final String PLUGIN_ID = "org.amanzi.awe.views.drive";

    // The shared instance
    private static DriveInquirerPlugin plugin;

    /**
     * The constructor
     */
    public DriveInquirerPlugin() {
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
     */
    public void start(BundleContext context) throws Exception {
        super.start(context);
        plugin = this;
        NeoCorePlugin.getDefault().getUpdateViewManager().addListener(this);
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
     */
    public void stop(BundleContext context) throws Exception {
        plugin = null;
        super.stop(context);
    }

    /**
     * Returns the shared instance
     * 
     * @return the shared instance
     */
    public static DriveInquirerPlugin getDefault() {
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

    /**
     *updates ReuseAnalyserView
     */
    private void updateView() {
        ActionUtil.getInstance().runTask(new Runnable() {

            @Override
            public void run() {
                IViewPart reuseView = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().findView(DriveInquirerView.ID);
                if (reuseView != null) {
                    ((DriveInquirerView)reuseView).updateGisNode();
                }
                reuseView = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().findView(CorrelationManager.ID);
                if (reuseView != null) {
                    ((CorrelationManager)reuseView).updateGisNode();
                }
                reuseView = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().findView(CorrelationList.ID);
                if (reuseView != null) {
                    ((CorrelationList)reuseView).updateGisNode();
                }
            }
        }, true);
    }

    /**
     * Print a message and information about exception to Log
     * 
     * @param message message
     * @param e exception
     */

    public static void error(String message, Throwable e) {
        getDefault().getLog().log(new Status(IStatus.ERROR, PLUGIN_ID, 0, message == null ? "" : message, e)); //$NON-NLS-1$
    }

    @Override
    public void updateView(UpdateViewEvent event) {
        if (event.getType() == UpdateViewEventType.NEW_CORRELATION) {
            final NewCorrelationEvent newCorrelaionEvent = (NewCorrelationEvent)event;
            ActionUtil.getInstance().runTask(new Runnable() {

                @Override
                public void run() {
                    IViewPart reuseView;
                    try {
                        reuseView = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView(CorrelationList.ID);
                    } catch (PartInitException e) {
                        // TODO Handle PartInitException
                        throw (RuntimeException)new RuntimeException().initCause(e);
                    }
                    if (reuseView != null) {
                        ((CorrelationList)reuseView).showCurrentCorrelation(newCorrelaionEvent.getNetworkNode(), newCorrelaionEvent.getDriveNode());
                    }
                }
            }, true);

        } else {
            updateView();
        }
    }

    @Override
    public Collection<UpdateViewEventType> getType() {
        return handedTypes;
    }
}
