package org.amanzi.awe.views.network.view;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.refractions.udig.catalog.IGeoResource;
import net.refractions.udig.project.ILayer;
import net.refractions.udig.project.IMap;
import net.refractions.udig.project.ui.ApplicationGIS;

import org.amanzi.awe.awe.views.view.provider.NetworkTreeContentProvider;
import org.amanzi.awe.awe.views.view.provider.NetworkTreeLabelProvider;
import org.amanzi.awe.catalog.neo.GeoNeo;
import org.amanzi.awe.views.network.NetworkTreePlugin;
import org.amanzi.awe.views.network.property.NetworkPropertySheetPage;
import org.amanzi.awe.views.network.proxy.NeoNode;
import org.amanzi.neo.core.enums.GisTypes;
import org.amanzi.neo.core.enums.NetworkElementTypes;
import org.amanzi.neo.core.service.NeoServiceProvider;
import org.amanzi.neo.core.service.listener.NeoServiceProviderEventAdapter;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.views.properties.IPropertySheetPage;
import org.neo4j.api.core.Transaction;
import org.neo4j.neoclipse.view.NeoGraphViewPart;

/**
 * NetworkTree View
 * 
 * @author Lagutko_N
 */

public class NetworkTreeView extends ViewPart {

    /*
     * ID of this View
     */
    public static final String NETWORK_TREE_VIEW_ID = "org.amanzi.awe.networktree.views.NetworkTreeView";

    /*
     * TreeViewer for database Nodes
     */
    private TreeViewer viewer;

    /*
     * NeoService provider
     */
    private NeoServiceProvider neoServiceProvider;

    /*
     * PropertySheetPage for Properties of Nodes
     */
    private IPropertySheetPage propertySheetPage;

    /*
     * Listener for Neo-Database Events
     */
    private NeoServiceEventListener neoEventListener;

    /**
     * The constructor.
     */
    public NetworkTreeView() {
    }

    /**
     * This is a callback that will allow us to create the viewer and initialize it.
     */
    public void createPartControl(Composite parent) {
        viewer = new TreeViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
        viewer.addSelectionChangedListener(new NetworkSelectionListener());
        viewer.addDoubleClickListener(new IDoubleClickListener() {
            @Override
            public void doubleClick(DoubleClickEvent event) {
                StructuredSelection sel = (StructuredSelection)event.getSelection();
                Object s = sel.getFirstElement();
                if ((s != null) && (s instanceof NeoNode)) {
                    NeoNode node = (NeoNode)s;
                    if (viewer != event.getViewer()) {
                        return;
                    }
                    showSelection(node);
                }
            }
        });

        neoServiceProvider = NeoServiceProvider.getProvider();
        neoEventListener = new NeoServiceEventListener();
        neoServiceProvider.addServiceProviderListener(neoEventListener);
        Transaction tx = neoServiceProvider.getService().beginTx();
        try {
            setProviders(neoServiceProvider);

            viewer.setInput(getSite());
            getSite().setSelectionProvider(viewer);

            hookContextMenu();
        } finally {
            tx.finish();
        }
    }

    /**
     * Set Label and Content providers for TreeView
     * 
     * @param neoServiceProvider
     */

    private void setProviders(NeoServiceProvider neoServiceProvider) {
        viewer.setContentProvider(new NetworkTreeContentProvider(neoServiceProvider));
        viewer.setLabelProvider(new NetworkTreeLabelProvider(viewer));
    }

    /**
     * Creates a popup menu
     */

    private void hookContextMenu() {
        MenuManager menuMgr = new MenuManager("#PopupMenu");
        menuMgr.setRemoveAllWhenShown(true);
        menuMgr.addMenuListener(new IMenuListener() {
            public void menuAboutToShow(IMenuManager manager) {
                NetworkTreeView.this.fillContextMenu(manager);
            }
        });
        Menu menu = menuMgr.createContextMenu(viewer.getControl());
        viewer.getControl().setMenu(menu);
        getSite().registerContextMenu(menuMgr, viewer);
    }

    /**
     * Creates items for popup menu
     * 
     * @param manager
     */

    private void fillContextMenu(IMenuManager manager) {
        manager.add(new Action("Properties") {
            public void run() {
                try {
                    PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView(IPageLayout.ID_PROP_SHEET);
                } catch (PartInitException e) {
                    NetworkTreePlugin.error(null, e);
                }
            }
        });
        manager.add(new Action("show in database graph") {
            public void run() {
                IStructuredSelection selection = (IStructuredSelection)viewer.getSelection();
                ITreeSelection selectionTree = (ITreeSelection)selection;
                showSelection((NeoNode)selection.getFirstElement());
            }

            @Override
            public boolean isEnabled() {
                return ((IStructuredSelection)viewer.getSelection()).size() == 1;
            }
        });
    }

    /**
     * @param firstElement
     */
    protected void showSelection(NeoNode firstElement) {
        try {
            IViewPart view = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView(
                    "org.neo4j.neoclipse.view.NeoGraphViewPart");
            NeoGraphViewPart viewGraph = (NeoGraphViewPart)view;
            viewGraph.showNode(firstElement.getNode());
            final StructuredSelection selection = new StructuredSelection(new Object[] {firstElement.getNode()});
            viewGraph.getViewer().setSelection(selection, true);
            PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView(NETWORK_TREE_VIEW_ID);
        } catch (Exception e) {
            throw (RuntimeException)new RuntimeException().initCause(e);
        }

    }

    public void dispose() {
        if (propertySheetPage != null) {
            propertySheetPage.dispose();
        }

        if (neoEventListener != null) {
            neoServiceProvider.removeServiceProviderListener(neoEventListener);
        }

        super.dispose();
    }

    /**
     * Passing the focus request to the viewer's control.
     */
    public void setFocus() {
        viewer.getControl().setFocus();
    }

    /**
     * Returns (and creates is it need) property sheet page for this View
     * 
     * @return PropertySheetPage
     */

    private IPropertySheetPage getPropertySheetPage() {
        if (propertySheetPage == null) {
            propertySheetPage = new NetworkPropertySheetPage();
        }

        return propertySheetPage;
    }

    /**
     * This is how the framework determines which interfaces we implement.
     */
    @Override
    @SuppressWarnings("unchecked")
    public Object getAdapter(final Class key) {
        if (key.equals(IPropertySheetPage.class)) {
            return getPropertySheetPage();
        } else {
            return super.getAdapter(key);
        }
    }

    /**
     * <p>
     * Network selection Listener
     * </p>
     * 
     * @author Cinkel_A
     * @since 1.1.0
     */
    private final class NetworkSelectionListener implements ISelectionChangedListener {
        private List<ILayer> layers = new ArrayList<ILayer>();

        @Override
        public void selectionChanged(SelectionChangedEvent event) {
            ITreeSelection selected = (ITreeSelection)event.getSelection();
            Iterator iterator = selected.iterator();
            layers = findAllNetworkLayers();
            if (layers.isEmpty()) {
                return;
            };
            dropLayerSelection(layers);
            while (iterator.hasNext()) {
                NeoNode selectedNode = (NeoNode)iterator.next();
                addNodeToSelect(selectedNode);
            }
            for (ILayer singleLayer : layers) {
                singleLayer.refresh(null);
            }
        }

        /**
         * Drop selection in layers
         * 
         * @param list of layers
         */
        private void dropLayerSelection(List<ILayer> layers) {
            try {
                for (ILayer singleLayer : layers) {
                    GeoNeo resource = singleLayer.findGeoResource(GeoNeo.class).resolve(GeoNeo.class, null);
                    resource.setSelectedNodes(null);
                }
            } catch (IOException e) {
                // TODO Handle IOException
                throw (RuntimeException)new RuntimeException().initCause(e);
            }
        }

        /**
         * Add selected node
         * 
         * @param selectedNode - selected node
         */
        private void addNodeToSelect(NeoNode selectedNode) {
            try {
                if (selectedNode == null) {
                    return;
                }
                String nodeType = selectedNode.getType();
                if (!NetworkElementTypes.SITE.toString().equals(nodeType)
                        && (!NetworkElementTypes.SECTOR.toString().equals(nodeType))) {
                    return;
                }
                for (ILayer singleLayer : layers) {
                    GeoNeo resource = singleLayer.findGeoResource(GeoNeo.class).resolve(GeoNeo.class, null);
                    if (containsGisNode(resource, selectedNode)) {
                        resource.addNodeToSelect(selectedNode.getNode());
                    }
                }
            } catch (IOException e) {
                // TODO Handle IOException
                throw (RuntimeException)new RuntimeException().initCause(e);
            }
        }

        /**
         * checks if node is part of GIS tree
         * 
         * @param gisNode - gis node
         * @param selectedNode - selected node
         * @return now this method always return true, because expenses for check at present are not
         *         justified
         */
        private boolean containsGisNode(GeoNeo gisNode, NeoNode selectedNode) {
            return true;
        }

        /**
         * find all layers, that contains network gis node
         * 
         * @return
         */
        private List<ILayer> findAllNetworkLayers() {
            List<ILayer> result = new ArrayList<ILayer>();
            for (IMap activeMap : ApplicationGIS.getOpenMaps()) {
                for (ILayer layer : activeMap.getMapLayers()) {
                    IGeoResource resourse = layer.findGeoResource(GeoNeo.class);
                    if (resourse != null) {
                        try {
                            GeoNeo geo = resourse.resolve(GeoNeo.class, null);
                            if (geo.getGisType() == GisTypes.Network) {
                                result.add(layer);
                            }
                        } catch (IOException e) {
                            // TODO Handle IOException
                            throw (RuntimeException)new RuntimeException().initCause(e);
                        }
                    }
                }
            }
            return result;
        }
    }

    /**
     * NeoProvider Event listener for this View
     * 
     * @author Lagutko_N
     * @since 1.1.0
     */

    private class NeoServiceEventListener extends NeoServiceProviderEventAdapter {

        public void onNeoStop(Object source) {
            neoServiceProvider.shutdown();
        }

        public void onNeoCommit(Object source) {
            // if some data was commited to database than we must refresh content of TreeView
            viewer.getControl().getDisplay().syncExec(new Runnable() {
                public void run() {
                    NetworkTreeView.this.viewer.refresh();
                }
            });
        }
    }

}