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
package org.amanzi.awe.views.tree.drive;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import org.amanzi.awe.views.network.view.NetworkTreeView;
import org.amanzi.awe.views.tree.drive.views.DriveTreeView;
import org.amanzi.neo.core.NeoCorePlugin;
import org.amanzi.neo.core.database.listener.IUpdateViewListener;
import org.amanzi.neo.core.database.nodes.DistributionSelectionNode;
import org.amanzi.neo.core.database.nodes.StatisticSelectionNode;
import org.amanzi.neo.core.database.services.events.ShowPreparedViewEvent;
import org.amanzi.neo.core.database.services.events.UpdateDrillDownEvent;
import org.amanzi.neo.core.database.services.events.UpdateViewEvent;
import org.amanzi.neo.core.database.services.events.UpdateViewEventType;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.StructuredSelection;
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
public class DriveViewPlugin extends AbstractUIPlugin implements IUpdateViewListener {
    
    private static final Collection<UpdateViewEventType> handedTypes;
    static {
        Collection<UpdateViewEventType> spr = new HashSet<UpdateViewEventType>();
        spr.add(UpdateViewEventType.DRILL_DOWN);
        spr.add(UpdateViewEventType.SHOW_PREPARED_VIEW);
        spr.add(UpdateViewEventType.STATISTICS);
        handedTypes = Collections.unmodifiableCollection(spr);
    }

	// The plug-in ID
    public static final String PLUGIN_ID = "org.amanzi.awe.views.tree.drive";

	// The shared instance
	private static DriveViewPlugin plugin;
	
	/**
	 * The constructor
	 */
	public DriveViewPlugin() {
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
	public static DriveViewPlugin getDefault() {
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

    @Override
    public void updateView(UpdateViewEvent event) {
        switch (event.getType()) {
        case DRILL_DOWN:
            updateView((UpdateDrillDownEvent)event);
            break;
        case SHOW_PREPARED_VIEW:
            showPreparedView((ShowPreparedViewEvent)event);
            break;
        case STATISTICS:
            ((Viewer)getTreeView().getSite().getSelectionProvider()).refresh();
            break;
        default:
        }
    }
    
    private void updateView(UpdateDrillDownEvent event){
        String source = event.getSource();
        if(!source.equals(DriveTreeView.ID)&& !source.contentEquals(NetworkTreeView.NETWORK_TREE_VIEW_ID)){
            List<Node> nodes = event.getNodes();
            StructuredSelection selection;
            if (nodes.size()>1) {
                Node node = nodes.get(0);
                Node periodNode = nodes.get(1);
                selection = new StructuredSelection(new Object[] {new StatisticSelectionNode(node, periodNode)});
            }
            else{
                selection = new StructuredSelection(new Object[] {nodes.get(0)});
            }
            IViewPart viewNetwork = getTreeView();
            if (viewNetwork != null) {
                Viewer networkView = (Viewer)viewNetwork.getSite().getSelectionProvider();
                networkView.setSelection(selection, true);
            }
        }
    }

    private IViewPart showTreeView() {
        IViewPart viewNetwork;
        try {
            viewNetwork = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
                            .showView(DriveTreeView.ID);
        } catch (PartInitException e) {
            NeoCorePlugin.error(e.getLocalizedMessage(), e);
            viewNetwork = null;
        }
        return viewNetwork;
    }
    
    private IViewPart getTreeView() {
        return PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
        .findView(DriveTreeView.ID);
    }
    
    private void showPreparedView(ShowPreparedViewEvent event){
        if (event.isViewNeedUpdate(DriveTreeView.ID)) {
            List<Node> nodes = event.getNodes();
            Node node = nodes.get(0);
            StructuredSelection selection;
            if (nodes.size()>1) {
                Node periodNode = nodes.get(1);
                selection = new StructuredSelection(new Object[] {new StatisticSelectionNode(node, periodNode)});
            }else{
                selection = new StructuredSelection(new Object[] {new DistributionSelectionNode(node)});
            }
            IViewPart viewNetwork = showTreeView();
            if (viewNetwork != null) {
                Viewer networkView = (Viewer)viewNetwork.getSite().getSelectionProvider();
                networkView.setSelection(selection, true);
            }
        }
    }

    @Override
    public Collection<UpdateViewEventType> getType() {
        return handedTypes;
    }
}
