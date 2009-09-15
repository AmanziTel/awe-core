package org.amanzi.awe.views.network.view;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.refractions.udig.catalog.IGeoResource;
import net.refractions.udig.project.ILayer;
import net.refractions.udig.project.IMap;
import net.refractions.udig.project.internal.command.navigation.SetViewportCenterCommand;
import net.refractions.udig.project.ui.ApplicationGIS;

import org.amanzi.awe.awe.views.view.provider.NetworkTreeContentProvider;
import org.amanzi.awe.awe.views.view.provider.NetworkTreeLabelProvider;
import org.amanzi.awe.catalog.neo.GeoNeo;
import org.amanzi.awe.views.network.NetworkTreePlugin;
import org.amanzi.awe.views.network.property.NetworkPropertySheetPage;
import org.amanzi.awe.views.network.proxy.NeoNode;
import org.amanzi.awe.views.network.proxy.Root;
import org.amanzi.neo.core.INeoConstants;
import org.amanzi.neo.core.NeoCorePlugin;
import org.amanzi.neo.core.enums.GeoNeoRelationshipTypes;
import org.amanzi.neo.core.enums.GisTypes;
import org.amanzi.neo.core.enums.NetworkElementTypes;
import org.amanzi.neo.core.enums.NetworkRelationshipTypes;
import org.amanzi.neo.core.service.NeoServiceProvider;
import org.amanzi.neo.core.service.listener.NeoServiceProviderEventAdapter;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.ITreeSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.views.properties.IPropertySheetPage;
import org.geotools.referencing.CRS;
import org.neo4j.api.core.Direction;
import org.neo4j.api.core.Node;
import org.neo4j.api.core.Relationship;
import org.neo4j.api.core.ReturnableEvaluator;
import org.neo4j.api.core.StopEvaluator;
import org.neo4j.api.core.Transaction;
import org.neo4j.api.core.TraversalPosition;
import org.neo4j.api.core.Traverser;
import org.neo4j.api.core.Traverser.Order;
import org.neo4j.neoclipse.view.NeoGraphViewPart;
import org.opengis.referencing.NoSuchAuthorityCodeException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import com.vividsolutions.jts.geom.Coordinate;

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
    protected TreeViewer viewer;

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

    private Text tSearch;
    private Iterator<Node> searchIterator = new ArrayList<Node>().iterator();
    private boolean autoZoom = true;

    /**
     * The constructor.
     */
    public NetworkTreeView() {
    }

    /**
     * This is a callback that will allow us to create the viewer and initialize it.
     */
    public void createPartControl(Composite parent) {

        tSearch = new Text(parent, SWT.BORDER);
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
                    showSelectionOnMap(node);
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
        tSearch.addModifyListener(new ModifyListener() {

            @Override
            public void modifyText(ModifyEvent e) {
                searchIterator = new ArrayList<Node>().iterator();
                // findAndSelectNode();
            }
        });
        tSearch.addKeyListener(new KeyListener() {

            @Override
            public void keyPressed(KeyEvent e) {
            }

            @Override
            public void keyReleased(KeyEvent e) {
                if (e.keyCode == '\r' || e.keyCode == SWT.KEYPAD_CR) {
                    findAndSelectNode();
                }
            }
        });
        setLayout(parent);
    }

    /**
     * Searches for a node based on matching text to the name field. The resulting node is show in
     * the tree. Multiple searches on the same text will step through multiple results, and loop
     * back to the first.
     */
    protected void findAndSelectNode() {
        tSearch.setEditable(false);
        try {
            String text = tSearch.getText();
            text = text == null ? "" : text.toLowerCase().trim();
            if (text.isEmpty()) {
                return;
            }
            Root rootTree = (Root)((ITreeContentProvider)viewer.getContentProvider()).getElements(0)[0];
            if (searchIterator == null || !searchIterator.hasNext()) {
                // Do completely new search on changed text
                searchIterator = createSearchTraverser(rootTree.getNode(), text);
                viewer.collapseAll();
            }
            if (!searchIterator.hasNext()) {
                return;
            }
            Node lastNode = searchIterator.next();
            viewer.reveal(new NeoNode(lastNode));
            viewer.setSelection(new StructuredSelection(new Object[] {new NeoNode(lastNode)}));
        } catch (Exception e) {
            e.printStackTrace();
            // TODO Handle Exception
            throw (RuntimeException)new RuntimeException().initCause(e);
        } finally {
            tSearch.setEditable(true);
        }
    }

    /**
     * @param node
     * @param text
     * @return
     */
    private Iterator<Node> createSearchTraverser(Node node, final String text) {
        Traverser traverse = node.traverse(Order.DEPTH_FIRST, getSearchStopEvaluator(), new ReturnableEvaluator() {

            @Override
            public boolean isReturnableNode(TraversalPosition currentPos) {
                String type = getNodeType(currentPos.currentNode(), "");
                return !(type.equals(INeoConstants.AWE_PROJECT_NODE_TYPE) || type.equals(INeoConstants.GIS_TYPE_NAME))
                        && getNodeName(currentPos.currentNode()).toLowerCase().contains(text);
            }
        }, NetworkRelationshipTypes.CHILD, Direction.OUTGOING, GeoNeoRelationshipTypes.NEXT, Direction.OUTGOING);
        // TODO needs sort by name
        return traverse.iterator();
    }

    /**
     * gets search stop Evaluator
     * 
     * @return search stop Evaluator
     */
    protected StopEvaluator getSearchStopEvaluator() {
        return new StopEvaluator() {

            @Override
            public boolean isStopNode(TraversalPosition currentPos) {
                String nodeType = getNodeType(currentPos.currentNode(), "");
                boolean result = (nodeType.equals(INeoConstants.FILE_TYPE_NAME) || nodeType.equals(INeoConstants.DATASET_TYPE_NAME));
                return result;
            }
        };
    }

    /**
     * @param parent
     */
    private void setLayout(Composite parent) {
        FormLayout layout = new FormLayout();
        layout.marginHeight = 0;
        layout.marginWidth = 0;
        layout.marginWidth = 0;
        layout.spacing = 0;
        parent.setLayout(layout);
        FormData formData = new FormData();
        formData.top = new FormAttachment(0, 5);
        formData.left = new FormAttachment(0, 5);
        formData.right = new FormAttachment(100, -5);
        tSearch.setLayoutData(formData);

        formData = new FormData();
        formData.top = new FormAttachment(tSearch, 5);
        formData.left = new FormAttachment(0, 5);
        formData.right = new FormAttachment(100, -5);
        formData.bottom = new FormAttachment(100, -5);
        viewer.getTree().setLayoutData(formData);

    }

    /**
     * Set Label and Content providers for TreeView
     * 
     * @param neoServiceProvider
     */

    protected void setProviders(NeoServiceProvider neoServiceProvider) {
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
        manager.add(new Action("Show in database graph") {
            public void run() {
                IStructuredSelection selection = (IStructuredSelection)viewer.getSelection();
                showSelection((NeoNode)selection.getFirstElement());
            }

            @Override
            public boolean isEnabled() {
                return ((IStructuredSelection)viewer.getSelection()).size() == 1;
            }
        });
        manager.add(new Action("Show in active map") {
            public void run() {
                IStructuredSelection selection = (IStructuredSelection)viewer.getSelection();
                showSelectionOnMap((NeoNode)selection.getFirstElement());
            }

            @Override
            public boolean isEnabled() {
                return ((IStructuredSelection)viewer.getSelection()).size() == 1
                        && ApplicationGIS.getActiveMap() != ApplicationGIS.NO_MAP;
            }
        });
        manager.add(new DeleteAction());
    }

    /**
     * Shows selected node on map
     * 
     * @param node selected node
     */
    protected void showSelectionOnMap(NeoNode node) {
        try {
            IMap map = ApplicationGIS.getActiveMap();
            if (map == ApplicationGIS.NO_MAP || node == null) {
                return;
            }
            Node gis = getGisNode(node.getNode());
            if (gis == null) {
                return;
            }
            boolean presentFlag = false;
            for (ILayer layer : map.getMapLayers()) {
                IGeoResource resource = layer.findGeoResource(Node.class);
                if (resource != null && gis.equals(resource.resolve(Node.class, null))) {
                    presentFlag = true;
                    break;
                }
            }
            if (!presentFlag) {
                return;
            }
            CoordinateReferenceSystem crs = null;
            if (!gis.getProperty(INeoConstants.PROPERTY_CRS_TYPE_NAME, "").toString().equalsIgnoreCase(("projected"))) {
                autoZoom = false;
            }
            if (gis.hasProperty(INeoConstants.PROPERTY_CRS_NAME)) {
                crs = CRS.decode(gis.getProperty(INeoConstants.PROPERTY_CRS_NAME).toString());
            } else if (gis.hasProperty(INeoConstants.PROPERTY_CRS_HREF_NAME)) {
                URL crsURL = new URL(gis.getProperty(INeoConstants.PROPERTY_CRS_HREF_NAME).toString());
                crs = CRS.decode(crsURL.getContent().toString());
            }
            double[] c = getCoords(node.getNode());
            if (c == null) {
                return;
            }
            if (autoZoom) {
                // TODO: Check that this works with all CRS
                map.sendCommandASync(new net.refractions.udig.project.internal.command.navigation.SetViewportWidth(30000));
                autoZoom = false; // only zoom first time, then rely on user to control zoom level
            }
            map.sendCommandASync(new SetViewportCenterCommand(new Coordinate(c[0], c[1]), crs));
        } catch (NoSuchAuthorityCodeException e) {
            throw (RuntimeException)new RuntimeException().initCause(e);
        } catch (MalformedURLException e) {
            throw (RuntimeException)new RuntimeException().initCause(e);
        } catch (IOException e) {
            throw (RuntimeException)new RuntimeException().initCause(e);
        }

    }

    /**
     * Gets GIS node of necessary subnode
     * 
     * @param node subnode
     * @return gis node or null
     */
    private Node getGisNode(Node node) {
        Traverser traverse = node.traverse(Order.DEPTH_FIRST, StopEvaluator.END_OF_GRAPH, new ReturnableEvaluator() {

            @Override
            public boolean isReturnableNode(TraversalPosition currentPos) {
                return currentPos.currentNode().getProperty(INeoConstants.PROPERTY_TYPE_NAME, "").equals(
                        INeoConstants.GIS_TYPE_NAME);
            }
        }, NetworkRelationshipTypes.CHILD, Direction.INCOMING, GeoNeoRelationshipTypes.NEXT, Direction.INCOMING);
        return traverse.iterator().hasNext() ? traverse.iterator().next() : null;
    }

    /**
     * shows database graph with selected node
     * 
     * @param nodeToSelect - selected node
     */
    protected void showSelection(NeoNode nodeToSelect) {
        try {
            IViewPart view = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView(NeoGraphViewPart.ID);
            NeoGraphViewPart viewGraph = (NeoGraphViewPart)view;
            viewGraph.showNode(nodeToSelect.getNode());
            final StructuredSelection selection = new StructuredSelection(new Object[] {nodeToSelect.getNode()});
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

        @SuppressWarnings("unchecked")
        @Override
        public void selectionChanged(SelectionChangedEvent event) {
            StructuredSelection stSel = (StructuredSelection)event.getSelection();
            if (!stSel.isEmpty() && tSearch.getText().isEmpty()) {
                tSearch.setText(stSel.getFirstElement().toString());
            }
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
                if (NetworkElementTypes.SITE.toString().equals(nodeType) ||
                        NetworkElementTypes.SECTOR.toString().equals(nodeType) ||
                        NetworkElementTypes.CITY.toString().equals(nodeType) ||
                        NetworkElementTypes.BSC.toString().equals(nodeType) ||
                        INeoConstants.HEADER_MS.toString().equalsIgnoreCase(nodeType) ||
                        INeoConstants.MP_TYPE_NAME.toString().equalsIgnoreCase(nodeType) ||
                        INeoConstants.FILE_TYPE_NAME.toString().equalsIgnoreCase(nodeType) ||
                        INeoConstants.DATASET_TYPE_NAME.toString().equalsIgnoreCase(nodeType)) {
                    for (ILayer singleLayer : layers) {
                        GeoNeo resource = singleLayer.findGeoResource(GeoNeo.class).resolve(GeoNeo.class, null);
                        if (containsGisNode(resource, selectedNode)) {
                            resource.addNodeToSelect(selectedNode.getNode());
                            if (nodeType.equals("city")) {
                                System.out.println("Adding city node " + selectedNode.toString());
                                for (Node node : resource.getSelectedNodes()) {
                                    System.out.println("Have selected nodes: " + node);
                                }
                            }
                        }
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

    /**
     *Returns layer, that contains necessary gis node
     * 
     * @param map map
     * @param gisNode gis node
     * @return layer or null
     */
    public static ILayer findLayerByNode(IMap map, Node gisNode) {
        try {
            for (ILayer layer : map.getMapLayers()) {
                IGeoResource resource = layer.findGeoResource(GeoNeo.class);
                if (resource != null && resource.resolve(GeoNeo.class, null).getMainGisNode().equals(gisNode)) {
                    return layer;
                }
            }
            return null;
        } catch (IOException e) {
            // TODO Handle IOException
            e.printStackTrace();
            return null;
        }
    }

    /**
     * get coordinates of selected node if node do not contains coordinates, try to find it in
     * parent node
     * 
     * @param node node
     * @return
     */
    private static double[] getCoords(Node node) {
        for (int i = 0; i <= 1; i++) {
            if (node.hasProperty(INeoConstants.PROPERTY_COORDS_NAME)) {
                return (double[])node.getProperty(INeoConstants.PROPERTY_COORDS_NAME);
            }
            if (node.hasProperty(INeoConstants.PROPERTY_X_NAME) && node.hasProperty(INeoConstants.PROPERTY_Y_NAME)) {
                return new double[] {(Float)node.getProperty(INeoConstants.PROPERTY_X_NAME),
                        (Float)node.getProperty(INeoConstants.PROPERTY_Y_NAME)};
            }
            if (node.hasProperty(INeoConstants.PROPERTY_LAT_NAME)) {
                if (node.hasProperty(INeoConstants.PROPERTY_LON_NAME)) {
                    try {
                        return new double[] {(Float)node.getProperty(INeoConstants.PROPERTY_LON_NAME),
                                (Float)node.getProperty(INeoConstants.PROPERTY_LAT_NAME)};
                    } catch (ClassCastException e) {
                        return new double[] {(Double)node.getProperty(INeoConstants.PROPERTY_LON_NAME),
                                (Double)node.getProperty(INeoConstants.PROPERTY_LAT_NAME)};
                    }
                }
            }
            // try to up by 1 lvl
            Traverser traverse = node.traverse(Traverser.Order.BREADTH_FIRST, StopEvaluator.DEPTH_ONE,
                    ReturnableEvaluator.ALL_BUT_START_NODE, NetworkRelationshipTypes.CHILD, Direction.BOTH);
            if (traverse.iterator().hasNext()) {
                node = traverse.iterator().next();
            } else {
                return null;
            }
        }
        return null;

    }

    // TODO move to utility class
    /**
     * Gets node type
     * 
     * @param node node
     * @return node type or empty string
     */
    public static String getNodeName(Node node) {
        String type = node.getProperty(INeoConstants.PROPERTY_TYPE_NAME, "").toString();
        if (type.equals(INeoConstants.MP_TYPE_NAME)) {
            return node.getProperty(INeoConstants.PROPERTY_TIME_NAME, "").toString();

        } else if (type.equals(INeoConstants.HEADER_MS)) {
            return node.getProperty(INeoConstants.PROPERTY_CODE_NAME, "").toString();

        } else {
            return node.getProperty(INeoConstants.PROPERTY_NAME_NAME, "").toString();
        }
    }

    /**
     * gets node name
     * 
     * @param node node
     * @param defValue default value
     * @return node name or defValue
     */
    public static String getNodeType(Node node, String... defValue) {
        String def = defValue == null || defValue.length < 1 ? null : defValue[0];
        return (String)node.getProperty(INeoConstants.PROPERTY_TYPE_NAME, def);
    }

    /**
     * delete all incoming reference
     * 
     * @param node
     */
    public static void deleteIncomingRelations(Node node) {
        for (Relationship relation : node.getRelationships(Direction.INCOMING)) {
            relation.delete();
        }
    }

    /**
     * <p>
     * Delete node action
     * </p>
     * 
     * @author Cinkel_A
     * @since 1.1.0
     */
    private class DeleteAction extends Action {

        @Override
        public void run() {
            MessageBox msg = new MessageBox(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), SWT.YES | SWT.NO);
            msg.setText("Delete node");
            final String deleteText = getText();
            msg.setMessage(deleteText + "?");
            int result = msg.open();
            if (result != SWT.YES) {
                return;
            }
            final IStructuredSelection selection = (IStructuredSelection)viewer.getSelection();

            final Iterator iterator = selection.iterator();
            while (iterator.hasNext()) {
                Object element = iterator.next();
                if (!(element instanceof NeoNode)) {
                    continue;
                }
                NeoNode neoNode = (NeoNode)element;
                Node node = neoNode.getNode();
                String type = getNodeType(node, "");

                if (INeoConstants.MP_TYPE_NAME.equals(type) || (NetworkElementTypes.SITE.toString().equals(type))
                        || (INeoConstants.HEADER_MS.equals(type))) {
                    // relink node
                    Relationship relation = node.getSingleRelationship(GeoNeoRelationshipTypes.NEXT, Direction.OUTGOING);
                    if (relation != null) {
                        Node nodeNext = relation.getEndNode();
                        Relationship singleRelationship = node.getSingleRelationship(GeoNeoRelationshipTypes.NEXT,
                                Direction.INCOMING);
                        if (singleRelationship != null) {
                            singleRelationship.getStartNode().createRelationshipTo(nodeNext, GeoNeoRelationshipTypes.NEXT);
                        }
                        relation.delete();
                    }
                }
                // fast delete reference
                deleteIncomingRelations(node);
            }
            viewer.refresh();

            NeoServiceProvider.getProvider().commit();
            Job job = new Job(deleteText) {

                @Override
                protected IStatus run(IProgressMonitor monitor) {
                    final Iterator iterator = selection.iterator();
                    Transaction tx = NeoServiceProvider.getProvider().getService().beginTx();
                    try {
                        while (iterator.hasNext()) {
                            Object element = iterator.next();
                            if (!(element instanceof NeoNode)) {
                                continue;
                            }
                            NeoNode neoNode = (NeoNode)element;
                            Node node = neoNode.getNode();
                            NeoCorePlugin.getDefault().getProjectService().deleteNode(node);
                        }
                        tx.success();
                        NeoServiceProvider.getProvider().commit();
                        return Status.OK_STATUS;
                    } finally {
                        tx.finish();
                    }
                }

            };
            job.schedule();

        }

        @Override
        public String getText() {
            IStructuredSelection selection = (IStructuredSelection)viewer.getSelection();
            if (selection.size() != 1) {
                // type may be different
                return "Delete " + selection.size() + " nodes";
            }
            Object element = selection.getFirstElement();
            if (element == null || !(element instanceof NeoNode) || (element instanceof Root)) {
                return "Delete node";
            }
            NeoNode neoNode = (NeoNode)element;
            return "Delete " + getNodeType(neoNode.getNode(), "") + " '" + neoNode.toString() + "'";
        }

        @Override
        public boolean isEnabled() {
            IStructuredSelection selection = (IStructuredSelection)viewer.getSelection();
            final Iterator iterator = selection.iterator();
            while (iterator.hasNext()) {
                if (iterator.next() instanceof Root) {
                    return false;
                }
            }
            return true;
        }

    }

}