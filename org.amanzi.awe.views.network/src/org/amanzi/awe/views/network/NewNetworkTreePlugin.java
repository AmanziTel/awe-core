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
package org.amanzi.awe.views.network;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;

import org.amanzi.awe.views.network.view.NewNetworkTreeView;
import org.amanzi.neo.core.NeoCorePlugin;
import org.amanzi.neo.services.events.NewShowPreparedViewEvent;
import org.amanzi.neo.services.events.NewUpdateDrillDownEvent;
import org.amanzi.neo.services.events.UpdateViewEvent;
import org.amanzi.neo.services.events.UpdateViewEventType;
import org.amanzi.neo.services.model.IDataElement;
import org.amanzi.neo.services.ui.IUpdateViewListener;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class NewNetworkTreePlugin extends AbstractUIPlugin implements IUpdateViewListener{
    
    public static final String DRIVE_TREE_VIEW_ID = "org.amanzi.awe.views.tree.drive.views.DriveTreeView";
    
    private static final Collection<UpdateViewEventType> handedTypes;
    static {
        Collection<UpdateViewEventType> spr = new HashSet<UpdateViewEventType>();
        spr.add(UpdateViewEventType.DRILL_DOWN);
        spr.add(UpdateViewEventType.GIS);
        spr.add(UpdateViewEventType.SHOW_PREPARED_VIEW);
        handedTypes = Collections.unmodifiableCollection(spr);
    }

	// The plug-in ID
	public static final String PLUGIN_ID = "org.amanzi.awe.networktree";

	// The shared instance
	private static NewNetworkTreePlugin plugin;
	
	/**
	 * The constructor
	 */
	public NewNetworkTreePlugin() {
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
	    NeoCorePlugin.getDefault().getUpdateViewManager().removeListener(this);
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static NewNetworkTreePlugin getDefault() {
		return plugin;
	}

	/**
	 * Returns an image descriptor for the image file at the given
	 * plug-in relative path
	 *
	 * @param path the path
	 * @return the image descriptor
	 */
	public static ImageDescriptor getImageDescriptor(String path) {
		return imageDescriptorFromPlugin(PLUGIN_ID, path);
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
        switch (event.getType()) {
        case DRILL_DOWN:
            updateView((NewUpdateDrillDownEvent)event);
            break;
        case SHOW_PREPARED_VIEW:
            showPreparedView((NewShowPreparedViewEvent)event);
            break;
        default:
            IViewPart viewNetwork = findTreeView();
            if (viewNetwork != null) {
                NewNetworkTreeView networkView = (NewNetworkTreeView)viewNetwork;
                ((Viewer)networkView.getSite().getSelectionProvider()).refresh();
            }
        }
    }
    
    private void updateView(NewUpdateDrillDownEvent event){
        String source = event.getSource();
        if(!source.equals(NewNetworkTreeView.NETWORK_TREE_VIEW_ID)&& !source.equals(DRIVE_TREE_VIEW_ID)){
            IDataElement node = event.getDataElements().get(0);
            IViewPart viewNetwork = findTreeView();
            if (viewNetwork != null) {
                NewNetworkTreeView networkView = (NewNetworkTreeView)viewNetwork;
                networkView.selectDataElement(node);
            }
        }
    }

    private IViewPart showTreeView() {
        IViewPart viewNetwork;
        try {
            viewNetwork = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
                            .showView(NewNetworkTreeView.NETWORK_TREE_VIEW_ID);
        } catch (PartInitException e) {
            NeoCorePlugin.error(e.getLocalizedMessage(), e);
            viewNetwork = null;
        }
        return viewNetwork;
    }
    
    private IViewPart findTreeView() {
        return PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
        .findView(NewNetworkTreeView.NETWORK_TREE_VIEW_ID);
    }
    
    private void showPreparedView(NewShowPreparedViewEvent event){
        if (event.isViewNeedUpdate(NewNetworkTreeView.NETWORK_TREE_VIEW_ID)) {
            IDataElement dataElement = event.getDataElements().get(0);
            IViewPart viewNetwork = showTreeView();
            if (viewNetwork != null) {
                NewNetworkTreeView networkView = (NewNetworkTreeView)viewNetwork;
                networkView.selectDataElement(dataElement);
            }
        }
    }

    @Override
    public Collection<UpdateViewEventType> getType() {        
        return handedTypes;
    }
}
