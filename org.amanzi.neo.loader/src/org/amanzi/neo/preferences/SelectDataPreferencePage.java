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

package org.amanzi.neo.preferences;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

import org.amanzi.neo.core.enums.GeoNeoRelationshipTypes;
import org.amanzi.neo.core.enums.NodeTypes;
import org.amanzi.neo.core.enums.SplashRelationshipTypes;
import org.amanzi.neo.core.service.NeoServiceProvider;
import org.amanzi.neo.core.utils.NeoTreeContentProvider;
import org.amanzi.neo.core.utils.NeoTreeElement;
import org.amanzi.neo.core.utils.NeoTreeLabelProvider;
import org.amanzi.neo.core.utils.NeoUtils;
import org.amanzi.neo.loader.internal.NeoLoaderPlugin;
import org.apache.log4j.Logger;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.hsqldb.lib.StringUtil;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.ReturnableEvaluator;
import org.neo4j.graphdb.StopEvaluator;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.Traverser;
import org.neo4j.graphdb.Traverser.Order;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author TsAr
 * @since 1.0.0
 */
public class SelectDataPreferencePage extends PreferencePage implements IWorkbenchPreferencePage {
    private static final Logger LOGGER = Logger.getLogger(SelectDataPreferencePage.class);
    private Composite mainFrame;
    private CheckboxTreeViewer viewer;
    private TreeContentProvider provider;
    private NeoTreeLabelProvider labelProvider;
    private GraphDatabaseService service;
    private final Set<Node> selectedNode = new LinkedHashSet<Node>();
    @Override
    protected Control createContents(Composite parent) {
        mainFrame = new Composite(parent, SWT.FILL);
        GridLayout mainLayout = new GridLayout(1, false);
        viewer = new CheckboxTreeViewer(mainFrame);
        viewer.setContentProvider(provider);
        viewer.setLabelProvider(labelProvider);
        GridData layoutData = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
        viewer.getControl().setLayoutData(layoutData);
        viewer.setAutoExpandLevel(2);
        viewer.addCheckStateListener(new ICheckStateListener() {
            public void checkStateChanged(CheckStateChangedEvent event) {
                if (event.getElement() instanceof TreeElement) {
                    TreeElement item = (TreeElement)event.getElement();
                    if (!item.isRoot) {
                        Node node = item.getNode();
                        if (event.getChecked()) {
                            selectedNode.add(node);
                        } else {
                            selectedNode.remove(node);
                        }
                    }
                }
                if (event.getChecked()) {
                    viewer.setSubtreeChecked(event.getElement(), true);
                    if (event.getElement() instanceof TreeElement) {
                        TreeElement item = (TreeElement)event.getElement();
                        NeoTreeElement[] child = item.getChildren();
                        if (child != null) {
                            for (NeoTreeElement it : child) {
                                Node node = it.getNode();
                                if (event.getChecked()) {
                                    selectedNode.add(node);
                                } else {
                                    selectedNode.remove(node);
                                }
                            }
                        }
                    }
                }
            }
        });
        mainFrame.setLayout(mainLayout);
        formInput();
        return mainFrame;
    }

    @Override
    protected void performDefaults() {
        getPreferenceStore().setToDefault(DataLoadPreferences.SELECTED_DATA);
        loadSelectedData();
        formInput();
        super.performDefaults();
    }
    @Override
    public IPreferenceStore getPreferenceStore() {
        return NeoLoaderPlugin.getDefault().getPreferenceStore();
    }

    @Override
    public boolean performOk() {
        saveSelected();

        return super.performOk();
    }

    /**
 *
 */
    private void saveSelected() {
        StringBuilder st = new StringBuilder();
        for (Node selNode : selectedNode) {
            st.append(DataLoadPreferences.CRS_DELIMETERS).append(selNode.getId());
        }
        String value = st.length() < 1 ? "" : st.substring(DataLoadPreferences.CRS_DELIMETERS.length());
        getPreferenceStore().setValue(DataLoadPreferences.SELECTED_DATA, value);
    }

    @Override
    public void init(IWorkbench workbench) {
        service = NeoServiceProvider.getProvider().getService();
        loadSelectedData();
        provider = new TreeContentProvider();
        labelProvider = new NeoTreeLabelProvider();
    }


    private void loadSelectedData() {
        selectedNode.clear();
        String storedId = getPreferenceStore().getString(DataLoadPreferences.SELECTED_DATA);
        if (!StringUtil.isEmpty(storedId)) {
            Transaction tx = service.beginTx();
            try {
                StringTokenizer st = new StringTokenizer(storedId, DataLoadPreferences.CRS_DELIMETERS);
                while (st.hasMoreTokens()) {
                    String nodeId = st.nextToken();
                    try {
                        Node node = service.getNodeById(Long.parseLong(nodeId));
                        if (NeoUtils.isRoootNode(node)) {
                            selectedNode.add(node);
                        }
                    } catch (Exception e) {
                        LOGGER.error("not loaded id " + nodeId, e);
                    }

                }
            } finally {
                tx.finish();
            }
        }
    }

    /**
     *
     */
    private void formInput() {

        Transaction tx = NeoUtils.beginTx(service);
        Node[] projects;
        try {
            Traverser tr = service.getReferenceNode().traverse(Order.DEPTH_FIRST, StopEvaluator.DEPTH_ONE, ReturnableEvaluator.ALL_BUT_START_NODE,
                    SplashRelationshipTypes.AWE_PROJECT, Direction.OUTGOING);
            projects = tr.getAllNodes().toArray(new Node[0]);
        } finally {
            NeoUtils.finishTx(tx);
        }

        viewer.setInput(projects);
        setChecked();
    }

    /**
     *
     */
    private void setChecked() {
        List<TreeElement> selectedItems = new ArrayList<TreeElement>();
        for (Node node : selectedNode) {
            selectedItems.add(new TreeElement(node, service));
        }
        viewer.setCheckedElements(selectedItems.toArray(new TreeElement[0]));
    }

    private static class TreeElement extends NeoTreeElement {

        private final boolean isRoot;
        private final boolean haveChild;

        /**
         * @param node
         * @param service
         */
        public TreeElement(Node node, GraphDatabaseService service) {
            super(node, service);
            isRoot = NodeTypes.AWE_PROJECT == getType();
            haveChild = !NeoUtils.isRoootNode(node);

        }

        @Override
        public NeoTreeElement[] getChildren() {
            if (!haveChild) {
                return null;
            }
            Transaction tx = NeoUtils.beginTx(service);
            try {
                Set<TreeElement> result = new LinkedHashSet<TreeElement>();
                Traverser travers = node.traverse(Order.DEPTH_FIRST, StopEvaluator.DEPTH_ONE, ReturnableEvaluator.ALL_BUT_START_NODE,
                        GeoNeoRelationshipTypes.CHILD, Direction.OUTGOING);
                for (Node node : travers) {
                    result.add(new TreeElement(node, service));
                }
                return result.toArray(new TreeElement[0]);
            } finally {
                NeoUtils.finishTx(tx);
            }
        }

        @Override
        public NeoTreeElement getParent() {
            if (isRoot) {
                return null;
            }
            Transaction tx = service.beginTx();
            try {
                Relationship rel = node.getSingleRelationship(GeoNeoRelationshipTypes.CHILD, Direction.INCOMING);
                if (rel != null) {
                    return new TreeElement(rel.getOtherNode(node), service);
                }
                return null;
            } finally {
                tx.finish();
            }
        }

        @Override
        public boolean hasChildren() {
            return haveChild;
        }

    }

    private static class TreeContentProvider extends NeoTreeContentProvider {

        private TreeElement[] elements;

        @Override
        public Object[] getElements(Object inputElement) {
            return elements;
        }

        @Override
        public void dispose() {
        }

        @Override
        public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
            if (newInput == null) {
                elements = null;
            } else {
                Node[] nodes = (Node[])newInput;
                elements = new TreeElement[nodes.length];
                for (int i = 0; i < nodes.length; i++) {
                    elements[i] = new TreeElement(nodes[i], NeoServiceProvider.getProvider().getService());
                }
            }
        }

    }
}
