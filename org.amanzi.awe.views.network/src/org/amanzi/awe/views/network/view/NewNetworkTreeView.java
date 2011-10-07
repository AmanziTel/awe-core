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
package org.amanzi.awe.views.network.view;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.amanzi.awe.awe.views.view.provider.NetworkTreeLabelProvider;
import org.amanzi.awe.awe.views.view.provider.NewNetworkTreeContentProvider;
import org.amanzi.awe.catalog.neo.NeoCatalogPlugin;
import org.amanzi.awe.catalog.neo.upd_layers.events.ChangeSelectionEvent;
import org.amanzi.awe.views.network.NetworkTreePlugin;
import org.amanzi.awe.views.network.property.NetworkPropertySheetPage;
import org.amanzi.awe.views.network.proxy.NeoNode;
import org.amanzi.awe.views.network.proxy.Root;
import org.amanzi.neo.core.NeoCorePlugin;
import org.amanzi.neo.core.service.listener.NeoServiceProviderEventAdapter;
import org.amanzi.neo.services.INeoConstants;
import org.amanzi.neo.services.enums.GeoNeoRelationshipTypes;
import org.amanzi.neo.services.enums.NetworkRelationshipTypes;
import org.amanzi.neo.services.enums.NodeTypes;
import org.amanzi.neo.services.events.ShowPreparedViewEvent;
import org.amanzi.neo.services.events.UpdateDrillDownEvent;
import org.amanzi.neo.services.ui.NeoServiceProviderUi;
import org.amanzi.neo.services.ui.NeoServicesUiPlugin;
import org.amanzi.neo.services.ui.NeoUtils;
import org.apache.log4j.Logger;
import org.eclipse.core.runtime.Preferences.IPropertyChangeListener;
import org.eclipse.core.runtime.Preferences.PropertyChangeEvent;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.ITreeSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TextCellEditor;
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
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.views.properties.IPropertySheetPage;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.ReturnableEvaluator;
import org.neo4j.graphdb.StopEvaluator;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.TraversalPosition;
import org.neo4j.graphdb.Traverser;
import org.neo4j.graphdb.Traverser.Order;
import org.neo4j.neoclipse.Activator;

/**
 * This View contains a tree of objects found in the database. The tree is built based on the
 * existence of the NetworkRelationshipTypes.CHILD relation, and the set of Root nodes defined by
 * the Root.java class.
 * 
 * @author Lagutko_N
 * @since 1.0.0
 */

public class NewNetworkTreeView extends ViewPart {

    private static final Logger LOGGER = Logger.getLogger(NewNetworkTreeView.class);

    /*
     * ID of this View
     */
    public static final String NETWORK_TREE_VIEW_ID = "org.amanzi.awe.views.network.views.NewNetworkTreeView";

    public static final String TRANSMISSION_VIEW_ID = "org.amanzi.awe.views.neighbours.views.TransmissionView";
    public static final String NEIGHBOUR_VIEW_ID = "org.amanzi.awe.views.neighbours.views.NeighboursView";
    public static final String N2N_VIEW_ID = "org.amanzi.awe.views.neighbours.views.Node2NodeViews";
    public static final String DB_GRAPH_VIEW_ID = "org.neo4j.neoclipse.view.NeoGraphViewPart";

    /*
     * TreeViewer for database Nodes
     */
    protected TreeViewer viewer;

    /*
     * NeoService provider
     */
    private NeoServiceProviderUi neoServiceProvider;

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

    /**
     * The constructor.
     */
    public NewNetworkTreeView() {
    }

    /**
     * This is a callback that will allow us to create the viewer and initialize it.
     */
    @Override
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
                    showSelection(node, true);
                }
            }
        });

        neoServiceProvider = NeoServiceProviderUi.getProvider();
        neoEventListener = new NeoServiceEventListener();
        neoServiceProvider.addServiceProviderListener(neoEventListener);
        Transaction tx = neoServiceProvider.getService().beginTx();
        try {
            setProviders(neoServiceProvider);
            setEditable(false);
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
        Activator.getDefault().getPluginPreferences().addPropertyChangeListener(new PreferenceChangeHandler());
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
            selectNode(lastNode);
            // } catch (Exception e) {
            // e.printStackTrace();
            // throw (RuntimeException)new RuntimeException().initCause(e);
        } finally {
            tSearch.setEditable(true);
        }
    }

    /**
     * Select node
     * 
     * @param node - node to select
     */
    public void selectNode(Node node) {
        viewer.refresh();
        viewer.reveal(new NeoNode(node, 0));
        viewer.setSelection(new StructuredSelection(new Object[] {new NeoNode(node, 0)}));
    }

    /**
     * Create iterator of nodes with name, that contains necessary text
     * 
     * @param node - start node
     * @param text text to find
     * @return search iterator
     */
    private Iterator<Node> createSearchTraverser(Node node, final String text) {
        Traverser traverse = node.traverse(Order.DEPTH_FIRST, getSearchStopEvaluator(), new ReturnableEvaluator() {

            @Override
            public boolean isReturnableNode(TraversalPosition currentPos) {
                String type = NeoUtils.getNodeType(currentPos.currentNode(), "");
                return !(type.equals(NodeTypes.AWE_PROJECT.getId()) || type.equals(NodeTypes.GIS.getId()))
                        && NeoUtils.getFormatedNodeName(currentPos.currentNode(), "").toLowerCase().contains(text);
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
                String nodeType = NeoUtils.getNodeType(currentPos.currentNode(), "");
                boolean result = (nodeType.equals(NodeTypes.FILE.getId()) || nodeType.equals(NodeTypes.DATASET.getId()));
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

    protected void setProviders(NeoServiceProviderUi neoServiceProvider) {
        viewer.setContentProvider(new NewNetworkTreeContentProvider(neoServiceProvider));
        viewer.setLabelProvider(new NetworkTreeLabelProvider(viewer));
    }

    /**
     * sets edit mode for tree
     */
    protected void setEditable(boolean editable) {
        if (editable) {
            TextCellEditor textCellEditor = new TextCellEditor(viewer.getTree());
            viewer.setCellEditors(new CellEditor[] {textCellEditor, textCellEditor, textCellEditor, textCellEditor, textCellEditor});
            viewer.setColumnProperties(new String[] {"name"});
            ICellModifier modifier = new ICellModifier() {

                @Override
                public void modify(Object element, String property, Object value) {
                    String valueStr = value == null ? "" : value.toString().trim();
                    if (valueStr.isEmpty()) {
                        return;
                    }
                    NeoNode neoNode = element instanceof TreeItem ? (NeoNode)((TreeItem)element).getData() : (NeoNode)element;
                    neoNode.setName(valueStr);
                    LOGGER.debug(element);
                }

                @Override
                public Object getValue(Object element, String property) {
                    LOGGER.debug(element);
                    return ((NeoNode)element).getNode().getProperty(INeoConstants.PROPERTY_NAME_NAME);
                }

                @Override
                public boolean canModify(Object element, String property) {
                    LOGGER.debug(element);
                    return !(element instanceof Root) && ((NeoNode)element).getNode().hasProperty(INeoConstants.PROPERTY_NAME_NAME);
                }
            };
            viewer.setCellModifier(modifier);
        } else {
            viewer.setCellEditors(null);
            viewer.setCellModifier(null);
        }
    }

    /**
     * Creates a popup menu
     */

    private void hookContextMenu() {
        MenuManager menuMgr = new MenuManager("#PopupMenu");
        menuMgr.setRemoveAllWhenShown(true);
        menuMgr.addMenuListener(new IMenuListener() {
            public void menuAboutToShow(IMenuManager manager) {
                NewNetworkTreeView.this.fillContextMenu(manager);
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
        SelectAction select = new SelectAction((IStructuredSelection)viewer.getSelection());
        if (select.isEnabled()){
            manager.add(select); 
        }
        manager.add(new Action("Refresh") {
            @Override
            public void run() {
                viewer.refresh(((IStructuredSelection)viewer.getSelection()).getFirstElement());
            }
        });
        manager.add(new Action("Show in database graph") {
            @Override
            public void run() {
                IStructuredSelection selection = (IStructuredSelection)viewer.getSelection();
                showSelection((NeoNode)selection.getFirstElement(), false);
            }

            @Override
            public boolean isEnabled() {
                return ((IStructuredSelection)viewer.getSelection()).size() == 1;
            }
        });
    }

    protected void showCanNotOpenMessage() {
        MessageDialog.openInformation(viewer.getControl().getShell(), "No file found", "No file found for this data");
    }

    protected boolean userConfirmTooLarge(String filemane) {
        return MessageDialog.openConfirm(viewer.getControl().getShell(), "File is quite large", "The file " + filemane
                + " is quite large, do you want to open it anyway?");
    }

    /**
     * shows database graph with selected node
     * 
     * @param nodeToSelect - selected node
     */
    private void showSelection(NeoNode nodeToSelect, boolean isDrillDoun) {
        try {
            if (isDrillDoun) {
                NeoCorePlugin.getDefault().getUpdateViewManager().fireUpdateView(new UpdateDrillDownEvent(nodeToSelect.getNode(), NewNetworkTreeView.NETWORK_TREE_VIEW_ID));
            } else {
                NeoCorePlugin.getDefault().getUpdateViewManager().fireUpdateView(new ShowPreparedViewEvent(DB_GRAPH_VIEW_ID, nodeToSelect.getNode()));
            }
            showThisView();
        } catch (Exception e) {
            throw (RuntimeException)new RuntimeException().initCause(e);
        }

    }

    /**
     * show this view (brings it forward)
     */
    protected void showThisView() {
        try {
            PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView(NETWORK_TREE_VIEW_ID);
        } catch (Exception e) {
            throw (RuntimeException)new RuntimeException().initCause(e);
        }

    }

    @Override
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
    @Override
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
    @SuppressWarnings("rawtypes")
	@Override
    public Object getAdapter(final Class key) {
        if (key.equals(IPropertySheetPage.class)) {
            return getPropertySheetPage();
        } else {
            return super.getAdapter(key);
        }
    }

    /**
     * Listener handling selection events on the tree view. Selected items are added to a selection
     * collection provided to the map renderer so that highlighting can be draw on the map.
     * 
     * @author Cinkel_A
     * @since 1.0.0
     */
    private final class NetworkSelectionListener implements ISelectionChangedListener {

        @SuppressWarnings({ "rawtypes" })
        @Override
        public void selectionChanged(SelectionChangedEvent event) {
            StructuredSelection stSel = (StructuredSelection)event.getSelection();
            if (!stSel.isEmpty() && tSearch.getText().isEmpty()) {
                tSearch.setText(stSel.getFirstElement().toString());
            }
            ITreeSelection selected = (ITreeSelection)event.getSelection();
            Iterator iterator = selected.iterator();
            Set<Node> selection = new HashSet<Node>();
            while (iterator.hasNext()) {
                NeoNode selectedNode = (NeoNode)iterator.next();
                if (needAddNodeToSelect(selectedNode)) {
                    selection.addAll(selectedNode.getNodesForMap());
                }
            }
            NeoCatalogPlugin.getDefault().getLayerManager().sendUpdateMessage(new ChangeSelectionEvent(null, selection));
            NeoServicesUiPlugin.getDefault().getUpdateViewManager().fireUpdateView(new ShowPreparedViewEvent("", selection));
        }

        /**
         * Add selected node
         * 
         * @param selectedNode - selected node
         */
        private boolean needAddNodeToSelect(NeoNode selectedNode) {
            if (selectedNode == null) {
                return false;
            }
            String nodeType = selectedNode.getType();
            return NodeTypes.SITE.getId().equals(nodeType) || NodeTypes.SECTOR.getId().equals(nodeType) || NodeTypes.CITY.getId().equals(nodeType)
                    || NodeTypes.BSC.getId().equals(nodeType) || NodeTypes.DELTA_NETWORK.getId().equals(nodeType) || NodeTypes.DELTA_SITE.getId().equals(nodeType)
                    || NodeTypes.DELTA_SECTOR.getId().equals(nodeType) || NodeTypes.MISSING_SITES.getId().equals(nodeType)
                    || NodeTypes.MISSING_SECTORS.getId().equals(nodeType) || NodeTypes.MISSING_SITE.getId().equals(nodeType)
                    || NodeTypes.MISSING_SECTOR.getId().equals(nodeType) || NodeTypes.M.getId().equalsIgnoreCase(nodeType)
                    || NodeTypes.MP.getId().equalsIgnoreCase(nodeType)
                    || NodeTypes.FILE.getId().equalsIgnoreCase(nodeType) || NodeTypes.DATASET.getId().equalsIgnoreCase(nodeType)
                    || NodeTypes.S_CELL.getId().equals(nodeType) || NodeTypes.S_ROW.getId().equals(nodeType);
        }

    }

    /**
     * NeoProvider Event listener for this View
     * 
     * @author Lagutko_N
     * @since 1.0.0
     */

    private class NeoServiceEventListener extends NeoServiceProviderEventAdapter {

        private boolean neoStopped = false;

        public NeoServiceEventListener() {
            neoServiceProvider.addServiceProviderListener(this);
        }

        @Override
        public void onNeoStop(Object source) {
            neoStopped = true;
        }

        @Override
        public void onNeoStart(Object source) {
            neoStopped = false;
        }

        /**
         * If some data was committed to database than we must refresh content of TreeView
         */
        @Override
        public void onNeoCommit(Object source) {
            // TODO: Only modify part of tree specific to data modified
            if (!neoStopped) {
                if (!viewer.getControl().isDisposed()) {
                    viewer.getControl().getDisplay().syncExec(new Runnable() {
                        public void run() {
                            NewNetworkTreeView.this.viewer.refresh();
                        }
                    });
                }
            }
        }
    }

    /**
     * delete all incoming references
     * 
     * @param node
     */
    public static void deleteIncomingRelations(Node node) {
        Transaction transaction = NeoServiceProviderUi.getProvider().getService().beginTx();
        try {
            for (Relationship relation : node.getRelationships(Direction.INCOMING)) {
                relation.delete();
            }
            transaction.success();
        } finally {
            transaction.finish();
        }
    }
    private class SelectAction extends Action {
        private boolean enabled;
        private final String text;
        private Set<Node>selectedNodes=new HashSet<Node>();

        /**
         * Constructor
         * 
         * @param selection - selection
         */
        @SuppressWarnings("rawtypes")
		public SelectAction(IStructuredSelection selection) {
            Iterator it = selection.iterator();
            while (it.hasNext()) {
                NeoNode element = (NeoNode)it.next();
                if (element instanceof Root){
                    continue;
                }else{
                    selectedNodes.add(element.getNode());
                }
            }
            enabled = selectedNodes.size()>0;
            text=selectedNodes.size()>1?"Show properties":"Show/edit property";
        }

        @Override
        public boolean isEnabled() {
            return enabled;
        }

        @Override
        public String getText() {
            return text;
        }

        @Override
        public void run() {
            try {
                if (selectedNodes.size() > 1) {
                    PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView("org.amanzi.awe.views.reuse.views.MessageAndEventTableView");
                    NeoCorePlugin.getDefault().getUpdateViewManager().fireUpdateView(new ShowPreparedViewEvent("org.amanzi.awe.views.reuse.views.MessageAndEventTableView",selectedNodes));
                } else {
                    PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView(IPageLayout.ID_PROP_SHEET);
                }
            } catch (PartInitException e) {
                NetworkTreePlugin.error(null, e);
            }
        }     
    }

    /**
     * Class that responds to changes in preferences.
     */
    private class PreferenceChangeHandler implements IPropertyChangeListener {
        /**
         * Forward event, then refresh view.
         */
        public void propertyChange(final PropertyChangeEvent event) {
            // TODO use constant
            if ("nodePropertyNames".equals(event.getProperty())) {
                viewer.refresh();
            }
        }
    }
}
