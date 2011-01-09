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

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.StringTokenizer;

import org.amanzi.awe.wizards.WizardsPlugin;
import org.amanzi.awe.wizards.geoptima.export.NeoExportModelImpl;
import org.amanzi.awe.wizards.geoptima.export.NeoExportParameter;
import org.amanzi.neo.core.utils.export.IExportProvider;
import org.amanzi.neo.loader.core.preferences.DataLoadPreferences;
import org.amanzi.neo.loader.ui.NeoLoaderPlugin;
import org.amanzi.neo.services.enums.CorrelationRelationshipTypes;
import org.amanzi.neo.services.enums.GeoNeoRelationshipTypes;
import org.amanzi.neo.services.enums.NodeTypes;
import org.amanzi.neo.services.statistic.PropertyHeader;
import org.amanzi.neo.services.ui.NeoServiceProviderUi;
import org.amanzi.neo.services.ui.NeoUtils;
import org.amanzi.neo.services.ui.utils.NeoTreeContentProvider;
import org.amanzi.neo.services.ui.utils.NeoTreeElement;
import org.amanzi.neo.services.ui.utils.NeoTreeLabelProvider;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Preferences.IPropertyChangeListener;
import org.eclipse.core.runtime.Preferences.PropertyChangeEvent;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.wizard.WizardDialog;
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
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.ReturnableEvaluator;
import org.neo4j.graphdb.StopEvaluator;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.TraversalPosition;
import org.neo4j.graphdb.Traverser;
import org.neo4j.graphdb.Traverser.Order;

import au.com.bytecode.opencsv.CSVWriter;

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

    /** The display. */
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
        service = NeoServiceProviderUi.getProvider().getService();
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
     * Returns preference store.
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
        final Menu menu = new Menu(shell, SWT.POP_UP);
        MenuItem item = new MenuItem(menu, SWT.PUSH);
        item.setText("Export to CSV");
        item.addSelectionListener(new SelectionListener() {
            
            @Override
            public void widgetSelected(SelectionEvent e) {
                export();
            }
            
            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
                widgetSelected(e);
            }
        });
          MenuItem item2 = new MenuItem(menu, SWT.PUSH);
          item2.setText("Export to Spreadsheet");
          item2.addSelectionListener(new SelectionListener() {
            
            @Override
            public void widgetSelected(SelectionEvent e) {
                exportSpreadsheet();
            }
            
            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
                widgetSelected(e);
            }
        });
        final ToolBar bar=new ToolBar(shell, SWT.NONE);
        final ToolItem export=new ToolItem(bar, SWT.DROP_DOWN);
        export.setText("export to");
        export.addListener(SWT.Selection, new Listener() {
            public void handleEvent(Event event) {
              if (event.detail == SWT.ARROW) {
                Rectangle rect = export.getBounds();
                Point pt = new Point(rect.x, rect.y + rect.height);
                pt = bar.toDisplay(pt);
                menu.setLocation(pt.x, pt.y);
                menu.setVisible(true);
              }
            }

          });
//        Button bCorrelate = new Button(shell, SWT.PUSH);
//        bCorrelate.setText("Export");
//        bCorrelate.addSelectionListener(new SelectionAdapter() {
//
//            @Override
//            public void widgetSelected(SelectionEvent e) {
//                export();
//            }
//
//        });

        Button btnOk = new Button(shell, SWT.PUSH);
        btnOk.setText("Exit");
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
                    dragSourceItem[0] = selection[0];
                    event.doit = ((TreeElem)dragSourceItem[0].getData()).elemType == ElemType.PROPERTY;
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
        viewer.addCheckStateListener(new ICheckStateListener() {

            @Override
            public void checkStateChanged(CheckStateChangedEvent event) {
                if (event.getChecked()) {
                    NeoTreeElement parent = ((TreeElem)event.getElement()).getParent();
                    if (parent != null) {
                        viewer.setChecked(parent, true);
                    }
                }
            }
        });
    }

    /**
     *
     */
    protected void exportSpreadsheet() {
        final List<IExportProvider> exports = new ArrayList<IExportProvider>();
        Object[] elements = viewer.getCheckedElements();
        if (elements.length == 0) {
            return;
        }
        Transaction tx = service.beginTx();
        try {
            for (TreeItem rootItem : viewer.getTree().getItems()) {
                if (rootItem.getChecked()) {
                    if (NodeTypes.NETWORK.checkNode(((TreeElem)rootItem.getData()).getNode())) {
                        NetworkExport network = new NetworkExport(rootItem);
                        if (network.isValid()) {
                            exports.add(network);
                        }
                    } else {
                        DatasetExport dataset = new DatasetExport(rootItem);
                        if (dataset.isValid()) {
                            exports.add(dataset);
                        }
                    }
                }
            }
            if (exports.isEmpty()) {
                return;
            }
            IWorkbench wb = PlatformUI.getWorkbench();
            final IWorkbenchWindow window = wb.getActiveWorkbenchWindow();
                ExportToSpreadsheetWizard wizard = new ExportToSpreadsheetWizard(exports);
                wizard.init(window.getWorkbench(), null);
                Shell parent = window.getShell();
                WizardDialog dialog = new WizardDialog(parent, wizard);
                dialog.create();
                dialog.open();
        } finally {
            tx.finish();
        }
    }

    /**
     * Export.
     */
    protected void export() {
        final List<IExportProvider> exports = new ArrayList<IExportProvider>();
        Object[] elements = viewer.getCheckedElements();
        if (elements.length == 0) {
            return;
        }
        Transaction tx = service.beginTx();
        try {
            for (TreeItem rootItem : viewer.getTree().getItems()) {
                if (rootItem.getChecked()) {
                    if (NodeTypes.NETWORK.checkNode(((TreeElem)rootItem.getData()).getNode())) {
                        NetworkExport network = new NetworkExport(rootItem);
                        if (network.isValid()) {
                            exports.add(network);
                        }
                    } else {
                        DatasetExport dataset = new DatasetExport(rootItem);
                        if (dataset.isValid()) {
                            exports.add(dataset);
                        }
                    }
                }
            }
            if (exports.isEmpty()) {
                return;
            }

            FileDialog outFile = new FileDialog(shell);
            final String file = outFile.open();
            if (file != null) {
                Job job = new Job("export to CSV") {

                    @Override
                    protected IStatus run(IProgressMonitor monitor) {
                        Transaction tx = service.beginTx();
                        try {
                            CSVWriter writer = new CSVWriter(new FileWriter(file));
                            monitor.beginTask("export to CSV", exports.size());
                            for (IExportProvider exporter : exports) {
                                if (monitor.isCanceled()) {
                                    break;
                                }
                                String dataName = exporter.getDataName();
                                String[] line = new String[] {dataName};
                                writer.writeNext(line);
                                line = formatList(exporter.getHeaders());
                                writer.writeNext(line);
                                while (exporter.hasNextLine() && !monitor.isCanceled()) {
                                    if (exporter instanceof NetworkExport){
                                        if (!dataName.equals(exporter.getDataName())){
                                            dataName = exporter.getDataName();
                                            line = new String[] {dataName};
                                            writer.writeNext(line);
                                            line = formatList(exporter.getHeaders());
                                            writer.writeNext(line);                                           
                                        }
                                    }
                                    line = formatList(exporter.getNextLine());
                                    writer.writeNext(line);
                                }
                                monitor.done();
                            }
                            writer.close();
                            return Status.OK_STATUS;
                        } catch (IOException e) {
                            e.printStackTrace();
                            return new Status(Status.ERROR, WizardsPlugin.PLUGIN_ID, e.getLocalizedMessage(), e);
                        } finally {
                            tx.finish();
                        }
                    }

                };
                job.schedule();
            }
        } finally {
            tx.finish();
        }

    }

    /**
     * Format string array depend on list.
     * 
     * @param list the list
     * @return the string[]
     */
    private String[] formatList(List list) {
        String[] result = new String[list.size()];
        for (int i = 0; i < result.length; i++) {
            Object obj = list.get(i);
            result[i] = null == obj ? "" : String.valueOf(obj);
        }
        return result;
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
                        if (!NodeTypes.NETWORK.checkNode(node) && node.hasRelationship(GeoNeoRelationshipTypes.VIRTUAL_DATASET, Direction.OUTGOING)) {
                            for (Relationship rel : node.getRelationships(GeoNeoRelationshipTypes.VIRTUAL_DATASET, Direction.OUTGOING)) {
                                elements.add(new TreeElem(ElemType.ROOT, null, rel.getOtherNode(node), null, service));
                            }
                        }
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
     * </p>.
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
         * Move elem.
         *
         * @param elem the elem
         * @param itemData the item data
         * @param before the before
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
                    if (before) {
                        childsNew[j++] = elem;
                        childsNew[j++] = itemData;
                    } else {
                        childsNew[j++] = itemData;
                        childsNew[j++] = elem;
                    }
                } else {
                    childsNew[j++] = childs[i];
                }
            }
            childs = childsNew;
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

        /**
         * Gets the neo children.
         *
         * @return the neo children
         */
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
                    String[] allProperties = PropertyHeader.getPropertyStatistic(node).getAllFields("-main-type-");
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
                    return NodeTypes.NETWORK.checkNode(node) || PropertyHeader.getPropertyStatistic(node).isHavePropertyNode();
                } finally {
                    tx.finish();
                }
            case SITE:
            case SECTOR:
            case SET:
                tx = service.beginTx();
                try {
                    return PropertyHeader.getPropertyStatistic(node).isHavePropertyNode();
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
            @Override
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

            @Override
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

    /**
     * <p>
     * Dataset importer class
     * </p>.
     *
     * @author tsinkel_a
     * @since 1.0.0
     */
    public class DatasetExport implements IExportProvider {
        
        /** The valid. */
        private boolean valid;
        
        /** The model. */
        private  NeoExportModelImpl model;
        
        /** The main node iterator. */
        private  Iterator<Node> mainNodeIterator;
        
        /** The dataname. */
        private  String dataname;

        /**
         * Instantiates a new dataset export.
         *
         * @param root the root
         */
        public DatasetExport(TreeItem root) {

            valid = false;
            if (!root.getChecked()) {
                return;
            }

            TreeItem[] items = root.getItems();
            List<String> results = new LinkedList<String>();

            if (items == null) {
                return;
            }
            for (TreeItem treeItem : items) {
                if (treeItem.getChecked()) {
                    results.add(((TreeElem)treeItem.getData()).getText());
                }
            }
            if (results.isEmpty()) {
                return;
            }
            model = new NeoExportModelImpl(service, 1);
            model.addPropertyList(0, results);
            final Node node = ((TreeElem)root.getData()).getNode();
            dataname = NeoUtils.getNodeName(node);
            mainNodeIterator = NeoUtils.getPrimaryElemTraverser(node, service).iterator();
            valid = true;
        }

        /**
         * Checks for next line.
         *
         * @return true, if successful
         */
        @Override
        public boolean hasNextLine() {
            return valid && mainNodeIterator.hasNext();
        }

        /**
         * Gets the headers.
         *
         * @return the headers
         */
        @Override
        public List<String> getHeaders() {

            return valid ? model.getHeaders() : null;
        }

        /**
         * Gets the next line.
         *
         * @return the next line
         */
        @Override
        public List<Object> getNextLine() {
            if (!isValid() || !hasNextLine()) {
                return null;
            }
            NeoExportParameter parameter = new NeoExportParameter();
            parameter.addToList(mainNodeIterator.next());
            return model.getResults(parameter);
        }

        /**
         * Checks if is valid.
         *
         * @return true, if is valid
         */
        @Override
        public boolean isValid() {
            return valid;
        }

        /**
         * Gets the data name.
         *
         * @return the data name
         */
        @Override
        public String getDataName() {
            return dataname;
        }
    }

    /**
     * <p>
     * NetworkExport export networks
     * </p>.
     *
     * @author TsAr
     * @since 1.0.0
     */
    public class NetworkExport implements IExportProvider {

        /** The valid. */
        private boolean valid;
        
        /** The have sector. */
        private boolean haveSector;
        
        /** The have site. */
        private boolean haveSite;
        
        /** The have correlate. */
        private boolean haveCorrelate;
        
        /** The model. */
        private  NeoExportModelImpl model;
        
        /** The site prop. */
        private  LinkedList<String> siteProp;
        
        /** The sector. */
        private  LinkedList<String> sector;
        
        /** The dataset prop. */
        private  LinkedHashMap<Node, List<String>> datasetProp;
        
        /** The iter cor. */
        private Iterator<Entry<Node, List<String>>> iterCor;
        
        /** The cor entry. */
        private Entry<Node, List<String>> corEntry=null;
        
        /** The traverser. */
        private Traverser traverser;
        
        /** The main node. */
        private  Node mainNode;
        
        /** The name. */
        private String name;

        /**
         * Instantiates a new network export.
         *
         * @param root the root
         */
        public NetworkExport(TreeItem root) {

            valid = false;
            if (!root.getChecked()) {
                return;
            }

            TreeItem[] items = root.getItems();

            if (items == null) {
                return;
            }
            siteProp = new LinkedList<String>();
            sector = new LinkedList<String>();
            datasetProp = new LinkedHashMap<Node, List<String>>();
            for (TreeItem treeItem : items) {
                if (treeItem.getChecked()) {
                    TreeElem data = (TreeElem)treeItem.getData();
                    if (data.elemType == ElemType.SITE) {
                        TreeItem[] childs = treeItem.getItems();
                        for (TreeItem elem : childs) {
                            if (elem.getChecked()) {
                                siteProp.add(((TreeElem)elem.getData()).getText());
                            }
                        }
                        haveSite = !siteProp.isEmpty();
                    } else if (data.elemType == ElemType.SECTOR) {
                        TreeItem[] childs = treeItem.getItems();
                        for (TreeItem elem : childs) {
                            if (elem.getChecked()) {
                                sector.add(((TreeElem)elem.getData()).getText());
                            }
                        }
                        haveSector = !sector.isEmpty();
                    } else if (data.elemType == ElemType.CORRELATE) {
                        haveCorrelate = true;
                        boolean empty = true;
                        TreeItem[] dataset = treeItem.getItems();
                        for (TreeItem set : dataset) {
                            if (set.getChecked()) {
                                Node node = ((TreeElem)set.getData()).getNode();
                                TreeItem[] childs = set.getItems();
                                LinkedList<String> list = new LinkedList<String>();
                                for (TreeItem setProp : childs) {
                                    if (setProp.getChecked()) {
                                        list.add(((TreeElem)setProp.getData()).getText());
                                    }
                                }
                                if (!list.isEmpty()) {
                                    datasetProp.put(node, list);
                                }
                            }
                        }
                        if (datasetProp.isEmpty()) {
                            valid = false;
                            return;
                        }
                    }
                }
            }
            int gr = 0;
            if (haveSite) {
                gr++;
            }
            if (haveSector) {
                gr++;
            }
            if (haveCorrelate) {
                gr++;
            }
            if (gr == 0) {
                return;
            }
            model = new NeoExportModelImpl(service, gr);
            int ind = 0;
            if (haveSite) {
                model.addPropertyList(ind++, siteProp);
            }
            if (haveSector) {
                model.addPropertyList(ind++, sector);
            }
            mainNode = ((TreeElem)root.getData()).getNode();
            if (haveCorrelate) {
                iterCor = datasetProp.entrySet().iterator();
                corEntry = iterCor.next();
                model.setPropertyList(model.getGroupCount() - 1, corEntry.getValue());
                setCorrelatetraverser();
                name=formCorName();
            } else {
                name=NeoUtils.getNodeName(mainNode);
                if (haveSector){
                    traverser = mainNode.traverse(Order.BREADTH_FIRST, StopEvaluator.END_OF_GRAPH,new ReturnableEvaluator() {
                        
                        @Override
                        public boolean isReturnableNode(TraversalPosition currentPos) {
                            return NodeTypes.SECTOR.checkNode(currentPos.currentNode());
                        }
                    }, GeoNeoRelationshipTypes.CHILD,Direction.OUTGOING,GeoNeoRelationshipTypes.NEXT,Direction.OUTGOING);
                }else if (haveSite){
                    traverser = mainNode.traverse(Order.BREADTH_FIRST, StopEvaluator.END_OF_GRAPH,new ReturnableEvaluator() {
                        
                        @Override
                        public boolean isReturnableNode(TraversalPosition currentPos) {
                            return NodeTypes.SITE.checkNode(currentPos.currentNode());
                        }
                    }, GeoNeoRelationshipTypes.CHILD,Direction.OUTGOING,GeoNeoRelationshipTypes.NEXT,Direction.OUTGOING);
                   
                }else{
                    return;
                }
               
            }
            valid = true;
        }


        /**
         * Sets the correlate traverser.
         */
        private void setCorrelatetraverser() {
            traverser = mainNode.traverse(Order.BREADTH_FIRST, new StopEvaluator() {

                @Override
                public boolean isStopNode(TraversalPosition currentPos) {
                    Relationship rel = currentPos.lastRelationshipTraversed();
                    return rel != null && rel.isType(CorrelationRelationshipTypes.CORRELATED);
                }
            }, new ReturnableEvaluator() {

                @Override
                public boolean isReturnableNode(TraversalPosition currentPos) {
                    Relationship rel = currentPos.lastRelationshipTraversed();
                    return rel != null && rel.isType(CorrelationRelationshipTypes.CORRELATED);
                }
            }, GeoNeoRelationshipTypes.CHILD, Direction.OUTGOING, GeoNeoRelationshipTypes.NEXT, Direction.OUTGOING,
                    CorrelationRelationshipTypes.CORRELATED, Direction.OUTGOING, CorrelationRelationshipTypes.CORRELATION, Direction.INCOMING);
        }

        /**
         * Gets the next line.
         *
         * @return the next line
         */
        @Override
        public List<Object> getNextLine() {
            if (!isValid()){
                return null;
            }
            Node node = traverser.iterator().next();
            NeoExportParameter parameter=new NeoExportParameter();
            if (haveCorrelate){
                Node dataNode = node;
                Node sector=traverser.currentPosition().lastRelationshipTraversed().getOtherNode(node).getSingleRelationship(CorrelationRelationshipTypes.CORRELATION, Direction.OUTGOING).getEndNode();
                Node site=sector.getSingleRelationship(GeoNeoRelationshipTypes.CHILD, Direction.INCOMING).getOtherNode(sector);
               if (haveSite){
                   parameter.addToList(site);
               }
               if (haveSector){
                   parameter.addToList(sector);
               }
               parameter.addToList(dataNode);
            }else if (haveSector){
                Node sector=node;
                Node site=sector.getSingleRelationship(GeoNeoRelationshipTypes.CHILD, Direction.INCOMING).getOtherNode(sector);
                if (haveSite){
                    parameter.addToList(site);
                }
                if (haveSector){
                    parameter.addToList(sector);
                }
            }else{
                parameter.addToList(node); 
            }
            return model.getResults(parameter);
        }

        /**
         * Checks for next line.
         *
         * @return true, if successful
         */
        @Override
        public boolean hasNextLine() {
            if (!isValid()){
                return false;
            }
            if (!haveCorrelate){
                return traverser.iterator().hasNext();
            }else{
                if (traverser.iterator().hasNext()) {
                    return true;
                }
                while (iterCor.hasNext()) {
                    corEntry = iterCor.next();
                    name=formCorName();
                    model.setPropertyList(model.getGroupCount() - 1, corEntry.getValue());
                    setCorrelatetraverser();
                    if (traverser.iterator().hasNext()) {
                        return true;
                    }
                }
                return false;
            }
        }

        /**
         * Form cor name.
         *
         * @return the string
         */
        private String formCorName() {
            return String.format("Network: %s, dataset %s", NeoUtils.getNodeName(mainNode), NeoUtils.getNodeName(corEntry.getKey()));
        }

        /**
         * Checks if is valid.
         *
         * @return true, if is valid
         */
        @Override
        public boolean isValid() {
            return valid;
        }

        /**
         * Gets the data name.
         *
         * @return the data name
         */
        @Override
        public String getDataName() {
            return name;
        }

        /**
         * Gets the headers.
         *
         * @return the headers
         */
        @Override
        public List<String> getHeaders() {
            if (!isValid()){
                return null;
            }
            return model.getHeaders();
        }

    }
}
