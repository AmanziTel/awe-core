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

import org.amanzi.awe.views.network.view.NetworkTreeView;
import org.amanzi.neo.services.events.ShowPreparedViewEvent;
import org.amanzi.neo.services.events.UpdateDrillDownEvent;
import org.amanzi.neo.services.events.UpdateViewEvent;
import org.amanzi.neo.services.events.UpdateViewEventType;
import org.amanzi.neo.services.ui.IUpdateViewListener;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.neo4j.graphdb.Node;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class NetworkTreePlugin extends AbstractUIPlugin implements IUpdateViewListener{
    
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
	private static NetworkTreePlugin plugin;
	
	/**
	 * The constructor
	 */
	public NetworkTreePlugin() {
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
//		NeoCorePlugin.getDefault().getUpdateViewManager().addListener(this);
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
//	    NeoCorePlugin.getDefault().getUpdateViewManager().removeListener(this);
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static NetworkTreePlugin getDefault() {
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
            updateView((UpdateDrillDownEvent)event);
            break;
        case SHOW_PREPARED_VIEW:
            showPreparedView((ShowPreparedViewEvent)event);
            break;
        default:
            IViewPart viewNetwork = findTreeView();
            if (viewNetwork != null) {
                NetworkTreeView networkView = (NetworkTreeView)viewNetwork;
                ((Viewer)networkView.getSite().getSelectionProvider()).refresh();
            }
        }
    }
    
    private void updateView(UpdateDrillDownEvent event){
        String source = event.getSource();
        if(!source.equals(NetworkTreeView.NETWORK_TREE_VIEW_ID)&& !source.equals(DRIVE_TREE_VIEW_ID)){
            Node node = event.getNodes().get(0);
            IViewPart viewNetwork = findTreeView();
            if (viewNetwork != null) {
                NetworkTreeView networkView = (NetworkTreeView)viewNetwork;
                networkView.selectNode(node);
            }
        }
    }

    private IViewPart showTreeView() {
        IViewPart viewNetwork;
        try {
            viewNetwork = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
                            .showView(NetworkTreeView.NETWORK_TREE_VIEW_ID);
        } catch (PartInitException e) {
//            NeoCorePlugin.error(e.getLocalizedMessage(), e);
            viewNetwork = null;
        }
        return viewNetwork;
    }
    
    private IViewPart findTreeView() {
        return PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
        .findView(NetworkTreeView.NETWORK_TREE_VIEW_ID);
    }
    
    private void showPreparedView(ShowPreparedViewEvent event){
        if (event.isViewNeedUpdate(NetworkTreeView.NETWORK_TREE_VIEW_ID)) {
            Node node = event.getNodes().get(0);
            IViewPart viewNetwork = showTreeView();
            if (viewNetwork != null) {
                NetworkTreeView networkView = (NetworkTreeView)viewNetwork;
                networkView.selectNode(node);
            }
        }
    }

    @Override
    public Collection<UpdateViewEventType> getType() {        
        return handedTypes;
    }
}
