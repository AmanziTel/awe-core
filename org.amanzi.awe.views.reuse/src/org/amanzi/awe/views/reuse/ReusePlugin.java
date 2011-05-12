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
package org.amanzi.awe.views.reuse;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;

import org.amanzi.awe.views.reuse.mess_table.view.MessageAndEventTableView;
import org.amanzi.awe.views.reuse.views.FrequencyPlanAnalyser;
import org.amanzi.awe.views.reuse.views.ReuseAnalyserView;
import org.amanzi.neo.core.NeoCorePlugin;
import org.amanzi.neo.services.events.UpdateViewEvent;
import org.amanzi.neo.services.events.UpdateViewEventType;
import org.amanzi.neo.services.ui.IUpdateViewListener;
import org.amanzi.neo.services.ui.utils.ActionUtil;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class ReusePlugin extends AbstractUIPlugin implements IUpdateViewListener {
    private static final Collection<UpdateViewEventType> handedTypes;
    static {
        Collection<UpdateViewEventType> spr = new HashSet<UpdateViewEventType>();
        spr.add(UpdateViewEventType.GIS);
        spr.add(UpdateViewEventType.SELECT);
        spr.add(UpdateViewEventType.NEIGHBOUR);
        handedTypes = Collections.unmodifiableCollection(spr);
    }
    /** String VIEW_ID field */
    public static final String REUSE_VIEW_ID = "org.amanzi.awe.views.reuse.views.ReuseAnalyserView";
    public static final String FRQ_VIEW_ID = "org.amanzi.awe.views.reuse.views.FrequencyPlanAnalyser";
    public static final String MESS_TABLE_VIEW_ID = "org.amanzi.awe.views.reuse.views.MessageAndEventTableView";

    // The plug-in ID
    public static final String PLUGIN_ID = "org.amanzi.awe.views.reuse";

	// The shared instance
	private static ReusePlugin plugin;

	/**
	 * The constructor
	 */
	public ReusePlugin() {
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
        NeoCorePlugin.getDefault().getUpdateViewManager().removeListener(this);
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static ReusePlugin getDefault() {
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
     *updates ReuseAnalyserView
     */
    private void updateView() {
        ActionUtil.getInstance().runTask(new Runnable() {

            @Override
            public void run() {
                IViewPart reuseView = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().findView(REUSE_VIEW_ID);
                if (reuseView != null) {
                    ((ReuseAnalyserView)reuseView).updateGisNode();
                }
                reuseView = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().findView(FRQ_VIEW_ID);
                if (reuseView != null) {
                    ((FrequencyPlanAnalyser)reuseView).updateGisNode();
                }
                IViewPart tableView = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().findView(MESS_TABLE_VIEW_ID);
                if (tableView != null) {
                    ((MessageAndEventTableView )tableView).updateDatasetNodes();
                }
            }
        }, true);
    }

    @Override
    public void updateView(UpdateViewEvent event) {
        updateView();
    }

    @Override
    public Collection<UpdateViewEventType> getType() {
        return handedTypes;
    }

}
