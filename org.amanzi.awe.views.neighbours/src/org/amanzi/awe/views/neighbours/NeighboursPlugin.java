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
import org.amanzi.neo.services.events.ShowPreparedViewEvent;
import org.amanzi.neo.services.events.UpdateDrillDownEvent;
import org.amanzi.neo.services.events.UpdateViewEvent;
import org.amanzi.neo.services.events.UpdateViewEventType;
import org.amanzi.neo.services.ui.IUpdateViewListener;
import org.amanzi.neo.services.ui.NeoServiceProviderUi;
import org.amanzi.neo.services.ui.utils.ActionUtil;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.neo4j.graphdb.Node;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class NeighboursPlugin extends AbstractUIPlugin implements IUpdateViewListener {
    private static final Collection<UpdateViewEventType> handedTypes;
    static {
        Collection<UpdateViewEventType> spr = new HashSet<UpdateViewEventType>();
        spr.add(UpdateViewEventType.GIS);
        spr.add(UpdateViewEventType.NEIGHBOUR);
        spr.add(UpdateViewEventType.DRILL_DOWN);
        spr.add(UpdateViewEventType.SHOW_PREPARED_VIEW);
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
        NeoCorePlugin.getDefault().getUpdateViewManager().addListener(this);
    }

	/*
     * (non-Javadoc)
     * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
     */
    public void stop(BundleContext context) throws Exception {
        //Lagutko, 9.10.2009, use NeoServiceProvider instead NeoManager
        NeoServiceProviderUi.getProvider().rollback();
        plugin = null;
        super.stop(context);
        NeoCorePlugin.getDefault().getUpdateViewManager().removeListener(this);
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
        NeoServiceProviderUi.getProvider().commit();      
    }

    public void rollback() {
        //Lagutko, 9.10.2009, use NeoServiceProvider instead NeoManager
        NeoServiceProviderUi.getProvider().rollback();
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
    public void updateView(UpdateViewEvent event) {
        switch (event.getType()) {
        case DRILL_DOWN:
            UpdateDrillDownEvent ddEvent = (UpdateDrillDownEvent)event;
            if (!ddEvent.getSource().equals(NeighboursView.ID)) {
                inputNodesToView(ddEvent.getNodes());
            }
            if (!ddEvent.getSource().equals(TransmissionView.ID)) {
                inputNodesToTransmissionView(ddEvent.getNodes());
            }
            break;
        case SHOW_PREPARED_VIEW:
            ShowPreparedViewEvent spvEvent = (ShowPreparedViewEvent)event;
            if (spvEvent.isViewNeedUpdate(NeighboursView.ID)) {
                inputNodesToViewAndShow(spvEvent.getNodes());
            }
            if (spvEvent.isViewNeedUpdate(TransmissionView.ID)) {
                inputNodesToTransmissionAndShow(spvEvent.getNodes());
            }
            break;
        default:
            updateTransmissionView();
            updateView();
        }
    }

    private void inputNodesToView(final Collection<Node> nodes) {
        ActionUtil.getInstance().runTask(new Runnable() {

            @Override
            public void run() {
                IViewPart neighbourView = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().findView(
                        NeighboursView.ID);
                if (neighbourView != null) {
                    ((NeighboursView)neighbourView).setInput(nodes);
                }
            }
        }, true);
    }
    
    private void inputNodesToTransmissionView(final Collection<Node> nodes) {
        ActionUtil.getInstance().runTask(new Runnable() {

            @Override
            public void run() {
                IViewPart neighbourView = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().findView(
                        TransmissionView.ID);
                if (neighbourView != null) {
                    ((TransmissionView)neighbourView).setInput(nodes);
                }
            }
        }, true);
    }
    
    private void inputNodesToViewAndShow(final Collection<Node> nodes) {
        ActionUtil.getInstance().runTask(new Runnable() {

            @Override
            public void run() {
                IViewPart neighbourView;
                try {
                    neighbourView = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
                                        .showView(NeighboursView.ID);
                } catch (PartInitException e) {
                    NeoCorePlugin.error(e.getLocalizedMessage(), e);
                    neighbourView = null;
                }
                if (neighbourView != null && nodes!=null) {
                    ((NeighboursView)neighbourView).setInput(nodes);
                }
            }
        }, true);
    }
    
    private void inputNodesToTransmissionAndShow(final Collection<Node> nodes) {
        ActionUtil.getInstance().runTask(new Runnable() {

            @Override
            public void run() {
                IViewPart neighbourView;
                try {
                    neighbourView = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
                                                .showView(TransmissionView.ID);
                } catch (PartInitException e) {
                    NeoCorePlugin.error(e.getLocalizedMessage(), e);
                    neighbourView = null;
                }
                if (neighbourView != null && nodes!=null) {
                    ((TransmissionView)neighbourView).setInput(nodes);
                }
            }
        }, true);
    }
    

    @Override
    public Collection<UpdateViewEventType> getType() {
        return handedTypes;
    }
}
