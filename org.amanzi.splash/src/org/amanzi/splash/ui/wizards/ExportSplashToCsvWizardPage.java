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

package org.amanzi.splash.ui.wizards;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.amanzi.neo.core.enums.NodeTypes;
import org.amanzi.neo.core.enums.SplashRelationshipTypes;
import org.amanzi.neo.core.icons.IconManager;
import org.amanzi.neo.core.service.NeoServiceProvider;
import org.amanzi.neo.core.utils.NeoUtils;
import org.apache.commons.lang.StringUtils;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.preference.FileFieldEditor;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.neo4j.api.core.Direction;
import org.neo4j.api.core.NeoService;
import org.neo4j.api.core.Node;
import org.neo4j.api.core.ReturnableEvaluator;
import org.neo4j.api.core.StopEvaluator;
import org.neo4j.api.core.Transaction;
import org.neo4j.api.core.TraversalPosition;
import org.neo4j.api.core.Traverser;
import org.neo4j.api.core.Traverser.Order;

/**
 * Wizard page for exporting CSV
 * @author NiCK
 * @since 1.0.0
 */
public class ExportSplashToCsvWizardPage extends WizardPage {

    private FileFieldEditor editor;
    private Group main;
//    private Combo network;
    private String fileName;
    private HashMap<String, Node> members;
    private TreeViewer viewer;
    private Node selectedNode;
    private final NeoService service = NeoServiceProvider.getProvider().getService();
    /**
     * @param pageName
     */
    protected ExportSplashToCsvWizardPage(String pageName) {
        // super(pageName);
        super(pageName, Messages.ExportSplashToCsvWizardPage_0, null);
        setDescription(Messages.ExportSplashToCsvWizardPage_1);
//        setMessage("Bugoga message!");
        setPageComplete(false);
    }

    @Override
    public void createControl(Composite parent) {
        main = new Group(parent, SWT.NULL);
        main.setLayout(new GridLayout(3, false));
        // viewer = new TreeViewer(main);
        // GridData layoutData = new GridData(GridData.FILL_HORIZONTAL, GridData.FILL_VERTICAL,
        // false, false, 3, 10);
        // layoutData.minimumHeight = 200;
        // viewer.getControl().setLayoutData(layoutData);
        // ITreeContentProvider provider;
        // viewer.setContentProvider(new );
        // viewer.setLabelProvider(labelProvider);
        Label label = new Label(main, SWT.LEFT);
        label.setText(Messages.ExportSplashToCsvWizardPage_2);
        label.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 3, 1));

        viewer = new TreeViewer(main, SWT.SINGLE | SWT.H_SCROLL | SWT.V_SCROLL);
        GridData layoutData = new GridData(SWT.FILL, SWT.FILL, false, false, 3, 1);
        viewer.getControl().setLayoutData(layoutData);
        layoutData.grabExcessVerticalSpace=true;
        viewer.setContentProvider(new ViewContentProvider());
        viewer.setLabelProvider(new ViewLabelProvider());
        viewer.setInput(new Object[0]);
        viewer.addSelectionChangedListener(new ISelectionChangedListener() {
            
            @Override
            public void selectionChanged(SelectionChangedEvent event) {
                IStructuredSelection selection = (IStructuredSelection)event.getSelection();
                if (selection.size() != 1) {
                    return;
                }
                TreeNeoNode element = (TreeNeoNode)selection.getFirstElement();
                if (element.getType() == NodeTypes.SPREADSHEET){
                    selectedNode=element.getNode();
                }else{
                    selectedNode=null; 
                }
                setPageComplete(isValidPage());
            }
        });
        editor = new FileFieldEditor("fileSelectNeighb", Messages.ExportSplashToCsvWizardPage_4, main); // NON-NLS-1 //$NON-NLS-1$

        editor.getTextControl(main).addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent e) {
                setFileName(editor.getStringValue());
            }
        });
        setControl(main);
    }

    private String[] getSplashItems() {
        Transaction tx = NeoUtils.beginTransaction();
        try {
            Node refNode = service.getReferenceNode();
            Traverser traverse = refNode.traverse(Order.DEPTH_FIRST, StopEvaluator.END_OF_GRAPH, new ReturnableEvaluator() {

                @Override
                public boolean isReturnableNode(TraversalPosition currentPos) {
                    return NeoUtils.getNodeType(currentPos.currentNode(), "").equals(NodeTypes.SPREADSHEET.getId()); //$NON-NLS-1$
                }
            },
            SplashRelationshipTypes.AWE_PROJECT, Direction.OUTGOING,
            SplashRelationshipTypes.RUBY_PROJECT, Direction.OUTGOING,
            SplashRelationshipTypes.SPREADSHEET, Direction.OUTGOING);

            members = new HashMap<String, Node>();
            for (Node node : traverse) {
                members.put(NeoUtils.getSimpleNodeName(node, "", null), node); //$NON-NLS-1$
            }
            return members.keySet().toArray(new String[] {});
        } finally {
            tx.finish();
        }
    }

    /**
     * @param fileName
     */
    protected void setFileName(String fileName) {
        this.fileName = fileName;
        setPageComplete(isValidPage());
    }
    /**
     * @return fileName
     */
    public String getFileName() {
        return this.fileName;
    }
    /**
     * @return selectedNode
     */
    public Node getSelectedNode() {
        return this.selectedNode;
    }

    /**
     * @return
     */
    protected boolean isValidPage() {
        return selectedNode != null && StringUtils.isNotEmpty(fileName);
    }
    
    static class TreeNeoNode implements IAdaptable {
        private  String name;
        private final Node node;
        private  NodeTypes type;

        public TreeNeoNode(Node node) {

            this.node = node;
            this.name = NeoUtils.getNodeName(node);
            this.type = NodeTypes.getNodeType(node, null);
        }

        /**
         * @return node name
         */
        public String getName() {
            return name;
        }

        public TreeNeoNode getParent(NeoService service) {
            if (isRootNode()){
                return null;
            }
            Transaction tx = NeoUtils.beginTx(service);
            try {
                Iterator<Node> iter = node.traverse(Order.DEPTH_FIRST, StopEvaluator.DEPTH_ONE, ReturnableEvaluator.ALL_BUT_START_NODE, SplashRelationshipTypes.AWE_PROJECT,Direction.INCOMING,SplashRelationshipTypes.RUBY_PROJECT,Direction.INCOMING, SplashRelationshipTypes.SPREADSHEET,Direction.INCOMING).iterator();
                return iter.hasNext()?new TreeNeoNode(iter.next()):null;
            } finally {
                NeoUtils.finishTx(tx);
            }
        }

        /**
         *checks node - is root node
         * @return
         */
        private boolean isRootNode() {
            return type==NodeTypes.AWE_PROJECT;
        }

        public TreeNeoNode[] getChildren(NeoService service) {
            Transaction tx = NeoUtils.beginTx(service);
            try {
                List<TreeNeoNode>result=new LinkedList<TreeNeoNode>();
                Traverser travers = node.traverse(Order.DEPTH_FIRST, StopEvaluator.DEPTH_ONE, ReturnableEvaluator.ALL_BUT_START_NODE, SplashRelationshipTypes.AWE_PROJECT,Direction.OUTGOING,SplashRelationshipTypes.RUBY_PROJECT,Direction.OUTGOING, SplashRelationshipTypes.SPREADSHEET,Direction.OUTGOING);
                for (Node node : travers) {
                    result.add(new TreeNeoNode(node));
                }
                return result.toArray(new TreeNeoNode[0]);
            } finally {
                NeoUtils.finishTx(tx);
            }
        }

        @Override
        public String toString() {
            return getName();
        }

        @Override
        public Object getAdapter(Class adapter) {
            if (adapter == TreeNeoNode.class) {
                return this;
            }
            if (adapter == Node.class) {
                return node;
            }
            return null;
        }

        /**
         * @return Returns the node.
         */
        public Node getNode() {
            return node;
        }

        /**
         * @return Returns the type.
         */
        public NodeTypes getType() {
            return type;
        }

        /**
         * @param service
         * @return
         */
        public boolean hasChildren(NeoService service) {
            Transaction tx = NeoUtils.beginTx(service);
            try {
                return node.traverse(Order.DEPTH_FIRST, StopEvaluator.DEPTH_ONE, ReturnableEvaluator.ALL_BUT_START_NODE, SplashRelationshipTypes.AWE_PROJECT,Direction.OUTGOING,SplashRelationshipTypes.RUBY_PROJECT,Direction.OUTGOING, SplashRelationshipTypes.SPREADSHEET,Direction.OUTGOING).iterator().hasNext();
            } finally {
                NeoUtils.finishTx(tx);
            }
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((node == null) ? 0 : node.hashCode());
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (!(obj instanceof IAdaptable)){
                return false;
            }
            Node othernode=(Node)((IAdaptable)obj).getAdapter(Node.class);
            if (node == null) {
                if (othernode != null)
                    return false;
            } else if (!node.equals(othernode))
                return false;
            return true;
        }

        /**
         *
         */
        public void refresh() {
            name = NeoUtils.getNodeName(node);
           type = NodeTypes.getNodeType(node, null);
        }

    }

    /**
     * <p>
     * filter tree label provider
     * </p>
     * 
     * @author Cinkel_A
     * @since 1.0.0
     */
    class ViewLabelProvider extends LabelProvider {

        @Override
        public String getText(Object obj) {
            return obj.toString();
        }

        @Override
        public Image getImage(Object obj) {
            IconManager iconManager = IconManager.getIconManager();
            if (obj instanceof TreeNeoNode) {
                return ((TreeNeoNode)obj).getType().getImage();
            }
            return iconManager.getImage(IconManager.NEO_ROOT);

            // String imageKey = ISharedImages.IMG_OBJ_ELEMENT;
            // if (obj instanceof TreeNeoNode && ((TreeNeoNode)obj).getType() != NodeTypes.FILTER) {
            // imageKey = ISharedImages.IMG_OBJ_FOLDER;
            // }
            // return PlatformUI.getWorkbench().getSharedImages().getImage(imageKey);
        }
    }

    class ViewContentProvider implements IStructuredContentProvider, ITreeContentProvider {
        private List<TreeNeoNode> elements=new LinkedList<TreeNeoNode>();

        public void inputChanged(Viewer v, Object oldInput, Object newInput) {
            if (newInput==null){
                elements.clear();
            }else{
                Transaction tx = NeoUtils.beginTx(service);
                try{
              Traverser tr = service.getReferenceNode().traverse(Order.DEPTH_FIRST, StopEvaluator.DEPTH_ONE, ReturnableEvaluator.ALL_BUT_START_NODE,SplashRelationshipTypes.AWE_PROJECT,Direction.OUTGOING);
              for (Node node : tr) {
                elements.add(new TreeNeoNode(node));
              }
                }finally{
                    NeoUtils.finishTx(tx);
                }
            }

        }

        public void dispose() {
        }

        public Object[] getElements(Object parent) {
            return elements.toArray(new TreeNeoNode[0]);
        }

        public Object getParent(Object child) {
            if (child instanceof TreeNeoNode) {
                return ((TreeNeoNode)child).getParent(service);
            }
            return null;
        }

        public Object[] getChildren(Object parent) {
            if (parent instanceof TreeNeoNode) {
                return ((TreeNeoNode)parent).getChildren(service);
            }
            return new Object[0];
        }

        public boolean hasChildren(Object parent) {
            if (parent instanceof TreeNeoNode)
                return ((TreeNeoNode)parent).hasChildren(service);
            return false;
        }

    }
}
