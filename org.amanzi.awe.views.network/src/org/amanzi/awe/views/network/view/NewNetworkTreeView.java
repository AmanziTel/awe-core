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
import java.util.List;
import java.util.Set;

import org.amanzi.awe.awe.views.view.provider.NewNetworkTreeContentProvider;
import org.amanzi.awe.awe.views.view.provider.NewNetworkTreeLabelProvider;
import org.amanzi.awe.views.network.NetworkTreePlugin;
import org.amanzi.neo.core.NeoCorePlugin;
import org.amanzi.neo.services.INeoConstants;
import org.amanzi.neo.services.events.ShowPreparedViewEvent;
import org.amanzi.neo.services.exceptions.AWEException;
import org.amanzi.neo.services.model.IDataElement;
import org.amanzi.neo.services.model.INetworkModel;
import org.amanzi.neo.services.model.impl.DataElement;
import org.amanzi.neo.services.ui.NeoServiceProviderUi;
import org.amanzi.neo.services.ui.NeoUtils;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;

/**
 * This View contains a tree of objects found in the database. The tree is built based on the
 * existence of the NetworkRelationshipTypes.CHILD relation, and the set of Root nodes defined by
 * the Root.java class.
 * 
 * @author Lagutko_N
 * @since 1.0.0
 */

public class NewNetworkTreeView extends ViewPart {

	private static final String RENAME_MSG = "Enter new Name";
	
    /*
     * ID of this View
     */
    public static final String NETWORK_TREE_VIEW_ID = "org.amanzi.awe.views.network.views.NetworkTreeView";

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

    private Text tSearch;

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
    
        
        neoServiceProvider = NeoServiceProviderUi.getProvider();
        Transaction tx = neoServiceProvider.getService().beginTx();
        try {
            setProviders(neoServiceProvider);
            viewer.setInput(getSite());          
            hookContextMenu();
            getSite().setSelectionProvider(viewer); 
        } finally {
            tx.finish();
        }
        setLayout(parent);
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
    
    private void fillContextMenu(IMenuManager manager) {
        SelectAction select = new SelectAction((IStructuredSelection)viewer.getSelection());
        if (select.isEnabled()){
            manager.add(select); 
        }
        RenameAction reanmeAction = new RenameAction((IStructuredSelection)viewer.getSelection());
        manager.add(reanmeAction);

        DeleteAction deleteAction = new DeleteAction((IStructuredSelection)viewer.getSelection());
        manager.add(deleteAction);
//        createAdditionalAction(manager);
    }
    
    private class SelectAction extends Action {
        private boolean enabled;
        private final String text;
        private Set<Node> selectedNodes = new HashSet<Node>();

        /**
         * Constructor
         * 
         * @param selection - selection
         */
        @SuppressWarnings("rawtypes")
		public SelectAction(IStructuredSelection selection) {
            Iterator it = selection.iterator();
            while (it.hasNext()) {
                IDataElement element = (IDataElement)it.next();
                if (element instanceof INetworkModel){
                    continue;
                }else{
                    selectedNodes.add(((DataElement)element).getNode());
                }
            }
            enabled = selectedNodes.size()>0;
            text = selectedNodes.size() > 1 ? 
            		"Show properties" : 
            			"Show/edit property";
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
    
    private class RenameAction extends Action {

        private boolean enabled;
        private final String text;
        private Node node = null;
        private DataElement dataElement;

        /**
         * Constructor
         * 
         * @param selection - selection
         */
        public RenameAction(IStructuredSelection selection) {
            text = "Rename";
            enabled = selection.size() == 1 && 
            		selection.getFirstElement() instanceof IDataElement
            		&& !(selection.getFirstElement() instanceof INetworkModel);
            if (enabled) {
                dataElement = (DataElement)selection.getFirstElement();
                node = dataElement.getNode();
                enabled = node.hasProperty(INeoConstants.PROPERTY_NAME_NAME);
            }
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
        	String value = 
        			getNewName(node.getProperty(INeoConstants.PROPERTY_NAME_NAME).toString());
        	INetworkModel networkModel = (INetworkModel)dataElement.get(INeoConstants.NETWORK_MODEL_NAME);
        	try {
                networkModel.renameElement(dataElement, value);
            } catch (AWEException e) {
                // TODO Handle AWEException
                throw (RuntimeException) new RuntimeException( ).initCause( e );
            }
            viewer.refresh();
        }
        
        /**
         * Opens a dialog asking the user for a new name.
         * 
         * @return The new name of the element.
         */
        private String getNewName(String oldName) {
            InputDialog dialog = new InputDialog(Display.getDefault().getActiveShell(), RENAME_MSG, "", oldName, null); //$NON-NLS-1$
            int result = dialog.open();
            if (result == Dialog.CANCEL)
                return oldName;
            return dialog.getValue();
        }
    }
    
    /**
     * Action to delete all selected nodes and their child nodes in the graph, but not nodes related
     * by other geographic relationships. The result is designed to remove sub-tree's from the tree
     * view, leaving remaining tree nodes in place.
     * 
     * @author Kasnitskij_V
     * @since 1.0.0
     */
    private class DeleteAction extends Action {
        private final List<IDataElement> nodesToDelete;
        private String text = null;
        private boolean interactive = false;

        private DeleteAction(List<IDataElement> nodesToDelete, String text) {
            this.nodesToDelete = nodesToDelete;
            this.text = text;
        }

        @SuppressWarnings("rawtypes")
		private DeleteAction(IStructuredSelection selection) {
            interactive = true;
            nodesToDelete = new ArrayList<IDataElement>();
            Iterator iterator = selection.iterator();
            HashSet<String> nodeTypes = new HashSet<String>();
            while (iterator.hasNext()) {
                Object element = iterator.next();
                if (element != null && 
                		element instanceof IDataElement && 
                		!(element instanceof INetworkModel)) {
                    nodesToDelete.add((IDataElement)element);
                    nodeTypes.add(NeoUtils.getNodeType(((DataElement)element).getNode()));
                }
            }
            String type = nodeTypes.size() == 1 ? nodeTypes.iterator().next() : "node";
            switch (nodesToDelete.size()) {
            case 0:
                text = "Select nodes to delete";
                break;
            case 1:
                text = "Delete " + type + " '" + nodesToDelete.get(0).toString() + "'";
                break;
            case 2:
            case 3:
            case 4:
                for (IDataElement dataElement : nodesToDelete) {
                    if (text == null) {
                        text = "Delete " + type + "s " + dataElement;
                    } else {
                        text += ", " + dataElement;
                    }
                }
                break;
            default:
                text = "Delete " + nodesToDelete.size() + " " + type + "s";
                break;
            }
            // TODO: Find a more general solution
            text = text.replaceAll("citys", "cities");
        }

        @Override
        public void run() {

            if (interactive) {
                MessageBox msg = new MessageBox(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), SWT.YES | SWT.NO);
                msg.setText("Delete node");
                msg.setMessage(getText() + "?\n\nAll contained data will also be deleted!");
                int result = msg.open();
                if (result != SWT.YES) {
                    return;
                }
            }
 
            // Kasnitskij_V:
            // It's need when user want to delete nodes using bad-way.
            // For example, if we have a structure city->site->sector with values
            // Dortmund->{AMZ000210, AMZ000234->{A0234, A0236, A0289} 
            // and user choose to delete nodes Dortmund, AMZ000234, A0236. 
            // We should delete in start A0236, then AMZ000234 and
            // all it remained nodes, and in the end - Dortmund and all it remained nodes
            int countOfNodesToDelete = nodesToDelete.size();
            IDataElement[] nodesToDeleteArray = new IDataElement[countOfNodesToDelete];
            nodesToDelete.toArray(nodesToDeleteArray);
            
            for (int i = countOfNodesToDelete - 1; i >= 0; i--) {
            	IDataElement dataElement = nodesToDeleteArray[i];
            	INetworkModel networkModel = (INetworkModel)dataElement.get(INeoConstants.NETWORK_MODEL_NAME);
            	try {
                    networkModel.deleteElement(dataElement);
                } catch (AWEException e) {
                    // TODO Handle AWEException
                    throw (RuntimeException) new RuntimeException( ).initCause( e );
                }
            }
            
            viewer.refresh();
        }

        @Override
        public String getText() {
            return text;
        }

        @Override
        public boolean isEnabled() {
            return nodesToDelete.size() > 0;
        }

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
        viewer.setLabelProvider(new NewNetworkTreeLabelProvider(viewer));
    }

    @Override
    public void dispose() {
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
     * Select node
     * 
     * @param node - node to select
     */
    public void selectNode(Node node) {
        viewer.refresh();
        viewer.reveal(new DataElement(node));
        viewer.setSelection(new StructuredSelection(new Object[] {new DataElement(node)}));
    }
}
