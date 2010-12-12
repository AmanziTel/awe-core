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

import java.util.LinkedList;
import java.util.List;

import org.amanzi.awe.views.network.proxy.NeoNode;
import org.amanzi.neo.services.DatasetService;
import org.amanzi.neo.services.NeoServiceFactory;
import org.amanzi.neo.services.enums.NetworkRelationshipTypes;
import org.amanzi.neo.services.enums.NodeTypes;
import org.amanzi.neo.services.enums.SplashRelationshipTypes;
import org.amanzi.neo.services.ui.IconManager;
import org.amanzi.neo.services.ui.NeoServiceProviderUi;
import org.amanzi.neo.services.ui.NeoServicesUiPlugin;
import org.amanzi.neo.services.ui.NeoUtils;
import org.apache.commons.lang.StringUtils;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.preference.FileFieldEditor;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
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
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Path;
import org.neo4j.graphdb.ReturnableEvaluator;
import org.neo4j.graphdb.StopEvaluator;
import org.neo4j.graphdb.Traverser;
import org.neo4j.graphdb.Traverser.Order;
import org.neo4j.helpers.Predicate;

/**
 * <p>
 * </p>
 * 
 * @author NiCK
 * @since 1.0.0
 */
public class ExportNetworkWizardSelectionPage extends WizardPage {

    private FileFieldEditor editor;
    private Group main;

    private String fileName;
    private TreeViewer viewer;
    private Node selectedNode;

    private final List<TreeNeoNode> elements = new LinkedList<TreeNeoNode>();
    // private final GraphDatabaseService service = NeoServiceProviderUi.getProvider().getService();
    private final DatasetService service = NeoServiceFactory.getInstance().getDatasetService();
    private final IStructuredSelection selection;

    /**
     * @param pageName
     * @param selection
     */
    protected ExportNetworkWizardSelectionPage(String pageName, IStructuredSelection selection) {
        // super(pageName);
        super(pageName, "Export network", null);
        this.selection = selection;
        setDescription("Choose network that should be exported");
        setPageComplete(false);
    }

    @Override
    public void createControl(Composite parent) {
        main = new Group(parent, SWT.NULL);
        main.setLayout(new GridLayout(3, false));

        Label label = new Label(main, SWT.LEFT);
        label.setText("Network");
        label.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 3, 1));

        viewer = new TreeViewer(main, SWT.SINGLE | SWT.H_SCROLL | SWT.V_SCROLL);
        GridData layoutData = new GridData(SWT.FILL, SWT.FILL, false, false, 3, 1);
        viewer.getControl().setLayoutData(layoutData);
        layoutData.grabExcessVerticalSpace = true;
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
                if (element.getType() == NodeTypes.NETWORK) {
                    selectedNode = element.getNode();
                } else {
                    selectedNode = null;
                }
                setPageComplete(isValidPage());
            }
        });

        if (selection != null && !selection.isEmpty()) {
            if (selection.size() == 1) {
                Object elem = selection.getFirstElement();
                if (elem instanceof NeoNode) {
                    Node node = ((NeoNode)elem).getNode();
                    if (NodeTypes.NETWORK.checkNode(node)) {
                        StructuredSelection sel = new StructuredSelection(new TreeNeoNode(node));
                        viewer.setSelection(sel, true);
                    }
                }
            }
        }

        editor = new FileFieldEditor("fileSelectNeighb", "File", main); // NON-NLS-1 //$NON-NLS-1$

        editor.getTextControl(main).addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent e) {
                setFileName(editor.getStringValue());
            }
        });
        setControl(main);
        // viewer.setSelection(new StructuredSelection(new
        // TreeNeoNode(spreadsheet.getUnderlyingNode())), true);
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
        private String name;
        private final Node node;
        private NodeTypes type;

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

        public TreeNeoNode getParent(final DatasetService service) {
            if (isRootNode())
                return null;
            else
                return new TreeNeoNode(node.getSingleRelationship(NetworkRelationshipTypes.CHILD, Direction.INCOMING).getStartNode());
        }

        /**
         *checks node - is root node
         * 
         * @return
         */
        private boolean isRootNode() {
            return type == NodeTypes.AWE_PROJECT;
        }

        public TreeNeoNode[] getChildren(final DatasetService service) {

            Iterable<Node> networks = service.getRoots(node, new Predicate<Path>() {

                @Override
                public boolean accept(Path item) {
                    return NodeTypes.NETWORK.equals(service.getNodeType(item.endNode()));
                }
            }).nodes();
            List<TreeNeoNode> result = new LinkedList<TreeNeoNode>();
            for (Node network : networks) {
                result.add(new TreeNeoNode(network));
            }
            return result.toArray(new TreeNeoNode[0]);
        }

        @Override
        public String toString() {
            return getName();
        }

        @SuppressWarnings("unchecked")
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
        public boolean hasChildren(final DatasetService service) {
            Iterable<Node> networks = service.getRoots(node, new Predicate<Path>() {

                @Override
                public boolean accept(Path item) {
                    return NodeTypes.NETWORK.equals(service.getNodeType(item.endNode()));
                }
            }).nodes();
            List<TreeNeoNode> result = new LinkedList<TreeNeoNode>();
            for (Node network : networks) {
                result.add(new TreeNeoNode(network));
            }
            return !result.isEmpty();

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
            if (!(obj instanceof IAdaptable)) {
                return false;
            }
            Node othernode = (Node)((IAdaptable)obj).getAdapter(Node.class);
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
                return NeoServicesUiPlugin.getDefault().getImageForType(((TreeNeoNode)obj).getType());
            }
            return iconManager.getImage(IconManager.NEO_ROOT);
        }
    }

    class ViewContentProvider implements IStructuredContentProvider, ITreeContentProvider {

        public void inputChanged(Viewer v, Object oldInput, Object newInput) {
            if (newInput == null) {
                elements.clear();
            } else {
                GraphDatabaseService gdService = NeoServiceProviderUi.getProvider().getService();
                Traverser tr = gdService.getReferenceNode().traverse(Order.DEPTH_FIRST, StopEvaluator.DEPTH_ONE, ReturnableEvaluator.ALL_BUT_START_NODE,
                        SplashRelationshipTypes.AWE_PROJECT, Direction.OUTGOING);

                for (Node node : tr) {
                    elements.add(new TreeNeoNode(node));
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
