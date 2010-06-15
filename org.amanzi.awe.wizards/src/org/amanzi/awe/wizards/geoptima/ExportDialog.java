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

package org.amanzi.awe.wizards.geoptima;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.StringTokenizer;

import org.amanzi.neo.core.enums.GeoNeoRelationshipTypes;
import org.amanzi.neo.core.enums.NodeTypes;
import org.amanzi.neo.core.service.NeoServiceProvider;
import org.amanzi.neo.core.utils.NeoTreeContentProvider;
import org.amanzi.neo.core.utils.NeoTreeElement;
import org.amanzi.neo.core.utils.NeoTreeLabelProvider;
import org.amanzi.neo.core.utils.NeoUtils;
import org.amanzi.neo.core.utils.PropertyHeader;
import org.amanzi.neo.loader.internal.NeoLoaderPlugin;
import org.amanzi.neo.preferences.DataLoadPreferences;
import org.eclipse.core.runtime.Preferences.IPropertyChangeListener;
import org.eclipse.core.runtime.Preferences.PropertyChangeEvent;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSource;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DragSourceListener;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.DropTargetAdapter;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.DropTargetListener;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.Transaction;

// TODO: Auto-generated Javadoc
/**
 * <p>
 * GeOptima export dialog
 * </p>
 * .
 * 
 * @author tsinkel_a
 * @since 1.0.0
 */
public class ExportDialog extends Dialog implements IPropertyChangeListener {

    /** The status. */
    private int status;

    /** The shell. */
    private Shell shell;

    /** The viewer. */
    private CheckboxTreeViewer viewer;

    /** The property. */
    private String property;

    /** The service. */
    private GraphDatabaseService service;

    private Display display;

    /**
     * Instantiates a new export dialog.
     * 
     * @param parent the parent
     */
    public ExportDialog(Shell parent) {
        super(parent, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL | SWT.CENTER);
    }

    /**
     * Property change.
     * 
     * @param event the event
     */
    @Override
    public void propertyChange(PropertyChangeEvent event) {
        if (property != getPreferenceStore().getString(DataLoadPreferences.SELECTED_DATA)) {
            formInput();
        }
    }

    /**
     * Form input.
     */
    private void formInput() {
        service = NeoServiceProvider.getProvider().getService();
        property = getPreferenceStore().getString(DataLoadPreferences.SELECTED_DATA);
        viewer.setInput(property);
        viewer.setAllChecked(false);
        validateExportButton();
    }

    /**
     * Validate export button.
     */
    private void validateExportButton() {
        // TODo implement
    }

    /**
     * Returs preference store.
     * 
     * @return IPreferenceStore
     */
    public IPreferenceStore getPreferenceStore() {
        return NeoLoaderPlugin.getDefault().getPreferenceStore();
    }

    /**
     * Open.
     * 
     * @return the int
     */
    public int open() {
        Shell parentShell = getParent();
        Shell shell = new Shell(parentShell, getStyle());
        shell.setText("Export");

        createContents(shell);
        shell.pack();

        // calculate location
        Point size = parentShell.getSize();
        int dlgWidth = shell.getSize().x;
        int dlgHeight = shell.getSize().y;
        shell.setLocation((size.x - dlgWidth) / 2, (size.y - dlgHeight) / 2);
        NeoLoaderPlugin.getDefault().getPluginPreferences().addPropertyChangeListener(this);
        beforeOpen();
        shell.open();
        // wait
        Display display = getParent().getDisplay();
        while (!shell.isDisposed()) {
            if (!display.readAndDispatch()) {
                display.sleep();
            }
        }
        NeoLoaderPlugin.getDefault().getPluginPreferences().removePropertyChangeListener(this);
        return status;
    }

    /**
     * Before open.
     */
    private void beforeOpen() {
        formInput();
    }

    /**
     * Creates the contents.
     * 
     * @param shell the shell
     */
    private void createContents(final Shell shell) {
        this.shell = shell;
        display = shell.getDisplay();
        shell.setLayout(new GridLayout(2, true));
        Label label = new Label(shell, SWT.NONE);
        label.setText("Select necessary data and properties for export:");
        GridData layoutData = new GridData();
        layoutData.horizontalSpan = 2;
        label.setLayoutData(layoutData);
        viewer = new CheckboxTreeViewer(shell);
        viewer.setLabelProvider(new NeoTreeLabelProvider());
        viewer.setContentProvider(new TreeContentProvider());
        layoutData = new GridData(SWT.FILL, SWT.FILL, false, true, 2, 1);
        layoutData.widthHint = 300;
        layoutData.heightHint = 400;
        viewer.getControl().setLayoutData(layoutData);
        Button bCorrelate = new Button(shell, SWT.PUSH);
        bCorrelate.setText("Export");
        bCorrelate.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                export();
            }

        });

        Button btnOk = new Button(shell, SWT.PUSH);
        btnOk.setText("OK");
        GridData gdBtnOk = new GridData();
        gdBtnOk.horizontalAlignment = GridData.END;
        gdBtnOk.widthHint = 70;
        btnOk.setLayoutData(gdBtnOk);
        btnOk.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                status = SWT.OK;
                shell.close();
            }

        });
        viewer.addCheckStateListener(new ICheckStateListener() {

            @Override
            public void checkStateChanged(CheckStateChangedEvent event) {
                // TODO implement
                validateExportButton();
            }
        });
        int ops = DND.DROP_MOVE;
        final TreeItem[] dragSourceItem = new TreeItem[1];
        Transfer[] transfers = new Transfer[] {TreeViewerTransfer.getInstance()};
        DragSourceListener dragListener = new DragSourceListener() {

            public void dragStart(DragSourceEvent event) {
                TreeViewerTransfer.getInstance().setViewer(viewer);
                TreeItem[] selection = viewer.getTree().getSelection();
                if (selection.length > 0 && selection[0].getItemCount() == 0) {
                    event.doit = true;
                    dragSourceItem[0] = selection[0];
                } else {
                    event.doit = false;
                }
            };

            public void dragSetData(DragSourceEvent event) {
                event.data = dragSourceItem[0].getData();
            }

            public void dragFinished(DragSourceEvent event) {
                dragSourceItem[0] = null;
            }
        };
        final Tree tree = viewer.getTree();
        viewer.addDragSupport(ops, transfers, dragListener);
        DropTargetListener dropListener = new DropTargetListener() {

            @Override
            public void dropAccept(DropTargetEvent event) {
                if (event.item == null) {
                    event.detail = DND.DROP_NONE;
                    return;
                }

            }

            @Override
            public void drop(DropTargetEvent event) {
                if (event.data == null) {
                    event.detail = DND.DROP_NONE;
                    return;
                }
                TreeElem elem = (TreeElem)event.data;
                TreeItem item = (TreeItem)event.item;
                TreeElem itemData = (TreeElem)item.getData();
                Point pt = display.map(null, tree, event.x, event.y);
                Rectangle bounds = item.getBounds();
                TreeItem parent = item.getParentItem();
                if (parent == null) {
                    event.detail = DND.DROP_NONE;
                    return;
                }
                if (!parent.getData().equals(elem.getParent())) {
                    event.detail = DND.DROP_NONE;
                    return;
                }

                TreeItem[] items = parent.getItems();
                int index = 0;
                for (int i = 0; i < items.length; i++) {
                    if (items[i] == item) {
                        index = i;
                        break;
                    }
                }
                boolean before = pt.y < bounds.y + bounds.height / 2;

                ((TreeElem)parent.getData()).moveElem(elem, itemData, before);
                viewer.refresh();

            }

            @Override
            public void dragOver(DropTargetEvent event) {
                event.feedback = DND.FEEDBACK_EXPAND | DND.FEEDBACK_SCROLL;
                if (event.item != null) {
                    TreeItem item = (TreeItem)event.item;
                    Point pt = display.map(null, viewer.getTree(), event.x, event.y);
                    Rectangle bounds = item.getBounds();
                    if (pt.y < bounds.y + bounds.height / 2) {
                        event.feedback |= DND.FEEDBACK_INSERT_BEFORE;
                    } else {
                        event.feedback |= DND.FEEDBACK_INSERT_AFTER;
                    }
                }
            }

            @Override
            public void dragOperationChanged(DropTargetEvent event) {
                System.out.println(event);
            }

            @Override
            public void dragLeave(DropTargetEvent event) {
                System.out.println(event);
            }

            @Override
            public void dragEnter(DropTargetEvent event) {
                System.out.println(event);
            }
        };
        viewer.addDropSupport(ops, transfers, dropListener);

    }

    /**
     * Export.
     */
    protected void export() {
        // TODO implement
    }

    /**
     * The Class TreeContentProvider.
     */
    private class TreeContentProvider extends NeoTreeContentProvider {

        /** The elements. */
        LinkedHashSet<TreeElem> elements = new LinkedHashSet<TreeElem>();

        /**
         * Gets the elements.
         * 
         * @param inputElement the input element
         * @return the elements
         */
        @Override
        public Object[] getElements(Object inputElement) {
            return elements.toArray(new TreeElem[0]);
        }

        /**
         * Dispose.
         */
        @Override
        public void dispose() {
        }

        /**
         * Input changed.
         * 
         * @param viewer the viewer
         * @param oldInput the old input
         * @param newInput the new input
         */
        @Override
        public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
            if (newInput == null) {
                elements.clear();
            } else {
                Transaction tx = service.beginTx();
                try {
                    String nodeSet = (String)newInput;
                    StringTokenizer st = new StringTokenizer(nodeSet, DataLoadPreferences.CRS_DELIMETERS);
                    while (st.hasMoreTokens()) {
                        String nodeId = st.nextToken();
                        Node node = service.getNodeById(Long.parseLong(nodeId));
                        elements.add(new TreeElem(ElemType.ROOT, null, node, null, service));
                    }
                } finally {
                    tx.finish();
                }
            }
        }

    }

    /**
     * <p>
     * Wrapper of element data
     * </p>
     * 
     * @author TsAr
     * @since 1.0.0
     */
    private static class TreeElem extends NeoTreeElement {

        /** The parent. */
        private final TreeElem parent;

        /** The elem type. */
        private final ElemType elemType;

        /** The childs. */
        NeoTreeElement[] childs = null;

        /**
         * Instantiates a new tree elem.
         * 
         * @param elemType the elem type
         * @param name the name
         * @param node the node
         * @param parent the parent
         * @param service the service
         */
        public TreeElem(ElemType elemType, String name, Node node, TreeElem parent, GraphDatabaseService service) {
            super(node, service);
            this.elemType = elemType;
            this.parent = parent;
            switch (elemType) {
            case PROPERTY:
                setText(name);
                break;
            case CORRELATE:
                setText("Correlated sets");
                break;
            case SITE:
                setText("Sites properties");
                break;
            case SECTOR:
                setText("Sector properties");
                break;
            default:
                break;
            }
        }

        /**
         * @param elem
         * @param itemData
         * @param before
         */
        public void moveElem(TreeElem elem, TreeElem itemData, boolean before) {
            if (childs == null || elem.equals(itemData)) {
                return;
            }
            NeoTreeElement[] childsNew = new NeoTreeElement[childs.length];
            int j = 0;
            for (int i = 0; i < childsNew.length; i++) {

                if (childs[i].equals(elem)) {
                    continue;
                }
                if (childs[i].equals(itemData)) {
                    if (before){
                        childsNew[j++] = elem;
                        childsNew[j++] = itemData;
                    } else {
                        childsNew[j++] = itemData;
                        childsNew[j++] = elem;
                    }
                }else{
                    childsNew[j++] = childs[i];
                }
            }
            childs=childsNew;
        }

        /**
         * Gets the children.
         * 
         * @return the children
         */
        @Override
        public NeoTreeElement[] getChildren() {
            if (childs == null) {
                childs = getNeoChildren();
            }
            return childs;
        }

        public NeoTreeElement[] getNeoChildren() {
            // TODO handle correlate
            Transaction tx;
            switch (elemType) {
            case ROOT:
                tx = service.beginTx();
                try {
                    ArrayList<TreeElem> networkElements = new ArrayList<TreeElem>();
                    if (NodeTypes.NETWORK.checkNode(node)) {
                        Relationship sitePropertyRel = node.getSingleRelationship(GeoNeoRelationshipTypes.VIRTUAL_DATASET, Direction.OUTGOING);
                        TreeElem siteNode = sitePropertyRel == null ? null : new TreeElem(ElemType.SITE, "site", sitePropertyRel.getOtherNode(node),
                                this, service);
                        if (siteNode != null) {
                            networkElements.add(siteNode);
                        }
                        networkElements.add(new TreeElem(ElemType.SECTOR, null, node, this, service));
                        networkElements.add(new TreeElem(ElemType.CORRELATE, null, node, this, service));
                        return networkElements.toArray(new TreeElem[0]);
                    }
                } finally {
                    tx.finish();
                }
                // handle in next case - not use break!
            case SITE:
            case SECTOR:
            case SET:
                tx = service.beginTx();
                try {
                    ArrayList<TreeElem> networkElements = new ArrayList<TreeElem>();
                    String[] allProperties = new PropertyHeader(node).getAllFields();
                    if (allProperties != null) {
                        for (String string : allProperties) {
                            networkElements.add(new TreeElem(ElemType.PROPERTY, string, node, this, service));
                        }
                    }
                    return networkElements.toArray(new TreeElem[0]);
                } finally {
                    tx.finish();
                }
            case CORRELATE:
                ArrayList<TreeElem> networkElements = new ArrayList<TreeElem>();
                tx = service.beginTx();
                try {
                    for (Node data : NeoUtils.getAllCorrelatedDatasets(node, service)) {
                        networkElements.add(new TreeElem(ElemType.SET, null, data, this, service));
                    }
                    return networkElements.toArray(new TreeElem[0]);
                } finally {
                    tx.finish();
                }
            default:
                return new TreeElem[0];
            }

        }

        /**
         * Gets the parent.
         * 
         * @return the parent
         */
        @Override
        public NeoTreeElement getParent() {
            return parent;
        }

        /**
         * Checks for children.
         * 
         * @return true, if successful
         */
        @Override
        public boolean hasChildren() {
            Transaction tx;
            switch (elemType) {
            case PROPERTY:
                return false;
            case ROOT:
                tx = service.beginTx();
                try {
                    return NodeTypes.NETWORK.checkNode(node) || new PropertyHeader(node).isHavePropertyNode();
                } finally {
                    tx.finish();
                }
            case SITE:
            case SECTOR:
            case SET:
                tx = service.beginTx();
                try {
                    return new PropertyHeader(node).isHavePropertyNode();
                } finally {
                    tx.finish();
                }
            case CORRELATE:
                tx = service.beginTx();
                try {
                    return NeoUtils.getAllCorrelatedDatasets(node, service).iterator().hasNext();
                } finally {
                    tx.finish();
                }
            default:
                return false;
            }

        }

        /**
         * Hash code.
         * 
         * @return the int
         */
        @Override
        public int hashCode() {
            final int prime = 31;
            int result = super.hashCode();
            result = prime * result + ((elemType == null) ? 0 : elemType.hashCode());
            result = prime * result + ((parent == null) ? 0 : parent.hashCode());
            result = prime * result + ((node == null) ? 0 : node.hashCode());
            return result;
        }

        /**
         * Equals.
         * 
         * @param obj the obj
         * @return true, if successful
         */
        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (!super.equals(obj))
                return false;
            if (getClass() != obj.getClass())
                return false;
            TreeElem other = (TreeElem)obj;
            if (elemType == null) {
                if (other.elemType != null)
                    return false;
            } else if (!elemType.equals(other.elemType))
                return false;
            if (parent == null) {
                if (other.parent != null)
                    return false;
            } else if (!parent.equals(other.parent))
                return false;
            if (node == null) {
                if (other.node != null)
                    return false;
            } else if (!node.equals(other.node))
                return false;
            if (elemType == ElemType.PROPERTY && !getText().equals(other.getText())) {
                return false;
            }
            return true;
        }

    }

    /**
     * The Enum ElemType.
     */
    private static enum ElemType {

        /** The SITE. */
        SITE,
        /** The SECTOR. */
        SECTOR,
        /** The CORRELATE. */
        CORRELATE,
        /** The PROPERTY. */
        PROPERTY,
        /** The ROOT. */
        ROOT,
        /** The SET. */
        SET;
    }

    /**
     * The main method.
     * 
     * @param args the arguments
     */
    public static void main(String[] args) {

        final Display display = new Display();
        final Shell shell = new Shell(display);
        shell.setLayout(new FillLayout());
        final Tree tree = new Tree(shell, SWT.BORDER);
        for (int i = 0; i < 3; i++) {
            TreeItem item = new TreeItem(tree, SWT.NONE);
            item.setText("item " + i);
            for (int j = 0; j < 3; j++) {
                TreeItem subItem = new TreeItem(item, SWT.NONE);
                subItem.setText("item " + i + " " + j);
                for (int k = 0; k < 3; k++) {
                    TreeItem subsubItem = new TreeItem(subItem, SWT.NONE);
                    subsubItem.setText("item " + i + " " + j + " " + k);
                }
            }
        }

        Transfer[] types = new Transfer[] {TextTransfer.getInstance()};
        int operations = DND.DROP_MOVE | DND.DROP_COPY | DND.DROP_LINK;

        final DragSource source = new DragSource(tree, operations);
        source.setTransfer(types);
        final TreeItem[] dragSourceItem = new TreeItem[1];
        source.addDragListener(new DragSourceListener() {
            public void dragStart(DragSourceEvent event) {
                TreeItem[] selection = tree.getSelection();
                if (selection.length > 0 && selection[0].getItemCount() == 0) {
                    event.doit = true;
                    dragSourceItem[0] = selection[0];
                } else {
                    event.doit = false;
                }
            };

            public void dragSetData(DragSourceEvent event) {
                event.data = dragSourceItem[0].getText();
            }

            public void dragFinished(DragSourceEvent event) {
                if (event.detail == DND.DROP_MOVE)
                    dragSourceItem[0].dispose();
                dragSourceItem[0] = null;
            }
        });

        DropTarget target = new DropTarget(tree, operations);
        target.setTransfer(types);
        target.addDropListener(new DropTargetAdapter() {
            public void dragOver(DropTargetEvent event) {
                event.feedback = DND.FEEDBACK_EXPAND | DND.FEEDBACK_SCROLL;
                if (event.item != null) {
                    TreeItem item = (TreeItem)event.item;
                    Point pt = display.map(null, tree, event.x, event.y);
                    Rectangle bounds = item.getBounds();
                    if (pt.y < bounds.y + bounds.height / 3) {
                        event.feedback |= DND.FEEDBACK_INSERT_BEFORE;
                    } else if (pt.y > bounds.y + 2 * bounds.height / 3) {
                        event.feedback |= DND.FEEDBACK_INSERT_AFTER;
                    } else {
                        event.feedback |= DND.FEEDBACK_SELECT;
                    }
                }
            }

            public void drop(DropTargetEvent event) {
                if (event.data == null) {
                    event.detail = DND.DROP_NONE;
                    return;
                }
                String text = (String)event.data;
                if (event.item == null) {
                    TreeItem item = new TreeItem(tree, SWT.NONE);
                    item.setText(text);
                } else {
                    TreeItem item = (TreeItem)event.item;
                    Point pt = display.map(null, tree, event.x, event.y);
                    Rectangle bounds = item.getBounds();
                    TreeItem parent = item.getParentItem();
                    if (parent != null) {
                        TreeItem[] items = parent.getItems();
                        int index = 0;
                        for (int i = 0; i < items.length; i++) {
                            if (items[i] == item) {
                                index = i;
                                break;
                            }
                        }
                        if (pt.y < bounds.y + bounds.height / 3) {
                            TreeItem newItem = new TreeItem(parent, SWT.NONE, index);
                            newItem.setText(text);
                        } else if (pt.y > bounds.y + 2 * bounds.height / 3) {
                            TreeItem newItem = new TreeItem(parent, SWT.NONE, index + 1);
                            newItem.setText(text);
                        } else {
                            TreeItem newItem = new TreeItem(item, SWT.NONE);
                            newItem.setText(text);
                        }

                    } else {
                        TreeItem[] items = tree.getItems();
                        int index = 0;
                        for (int i = 0; i < items.length; i++) {
                            if (items[i] == item) {
                                index = i;
                                break;
                            }
                        }
                        if (pt.y < bounds.y + bounds.height / 3) {
                            TreeItem newItem = new TreeItem(tree, SWT.NONE, index);
                            newItem.setText(text);
                        } else if (pt.y > bounds.y + 2 * bounds.height / 3) {
                            TreeItem newItem = new TreeItem(tree, SWT.NONE, index + 1);
                            newItem.setText(text);
                        } else {
                            TreeItem newItem = new TreeItem(item, SWT.NONE);
                            newItem.setText(text);
                        }
                    }

                }
            }
        });

        shell.setSize(400, 400);
        shell.open();
        while (!shell.isDisposed()) {
            if (!display.readAndDispatch())
                display.sleep();
        }
        display.dispose();
    }

}
