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

package org.amanzi.awe.views.drive.views;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

import org.amanzi.awe.views.drive.DriveInquirerPlugin;
import org.amanzi.neo.core.INeoConstants;
import org.amanzi.neo.core.enums.GeoNeoRelationshipTypes;
import org.amanzi.neo.core.enums.GisTypes;
import org.amanzi.neo.core.enums.NetworkRelationshipTypes;
import org.amanzi.neo.core.service.NeoServiceProvider;
import org.amanzi.neo.core.utils.ActionUtil;
import org.amanzi.neo.core.utils.NeoUtils;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.part.ViewPart;
import org.neo4j.api.core.Direction;
import org.neo4j.api.core.NeoService;
import org.neo4j.api.core.Node;
import org.neo4j.api.core.Relationship;
import org.neo4j.api.core.ReturnableEvaluator;
import org.neo4j.api.core.StopEvaluator;
import org.neo4j.api.core.Transaction;
import org.neo4j.api.core.TraversalPosition;
import org.neo4j.api.core.Traverser;
import org.neo4j.api.core.Traverser.Order;

/**
 * <p>
 * Correlation Manager between Drive and Network
 * </p>
 * 
 * @author Cinkel_A
 * @since 1.0.0
 */
public class CorrelationManager extends ViewPart {
    /**
     * The ID of the view as specified by the extension.
     */
    public static final String ID = "org.amanzi.awe.views.drive.views.CorrelationManager";
    private static final String REMOVE_CORRELATION = "Remove correlation between '%s' and '%s'.";
    private Combo cDrive;
    private Combo cNetwork;
    private Button bCorrelate;
    private TableViewer table;
    private NeoService service = NeoServiceProvider.getProvider().getService();
    private LinkedHashMap<String, Node> gisDriveNodes = new LinkedHashMap<String, Node>();
    private LinkedHashMap<String, Node> gisNetworkNodes = new LinkedHashMap<String, Node>();
    private TableLabelProvider labelProvider;
    private TableContentProvider provider;
    protected Point point;

    @Override
    public void createPartControl(Composite parent) {
        Composite frame = new Composite(parent, SWT.FILL);
        FormLayout formLayout = new FormLayout();
        formLayout.marginHeight = 0;
        formLayout.marginWidth = 0;
        formLayout.spacing = 0;
        frame.setLayout(formLayout);

        Composite child = new Composite(frame, SWT.FILL);
        final GridLayout layout = new GridLayout(5, false);
        child.setLayout(layout);
        Label label = new Label(child, SWT.FLAT);
        label.setText("Network:");
        label.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
        cNetwork = new Combo(child, SWT.DROP_DOWN | SWT.READ_ONLY);

        GridData layoutData = new GridData(SWT.FILL, SWT.CENTER, true, false);
        layoutData.minimumWidth = 150;
        cNetwork.setLayoutData(layoutData);

        label = new Label(child, SWT.FLAT);
        label.setText("Drive:");
        label.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
        cDrive = new Combo(child, SWT.DROP_DOWN | SWT.READ_ONLY);

        layoutData = new GridData(SWT.FILL, SWT.CENTER, true, false);
        layoutData.minimumWidth = 150;
        cDrive.setLayoutData(layoutData);

        bCorrelate = new Button(child, SWT.PUSH);
        bCorrelate.setText("correlate");
        table = new TableViewer(frame, SWT.BORDER | SWT.FULL_SELECTION);
        FormData fData = new FormData();
        fData.left = new FormAttachment(0, 0);
        fData.right = new FormAttachment(100, 0);
        fData.top = new FormAttachment(child, 2);
        fData.bottom = new FormAttachment(100, -2);
        table.getControl().setLayoutData(fData);
        labelProvider = new TableLabelProvider();
        labelProvider.createTableColumn();
        provider = new TableContentProvider();
        table.setContentProvider(provider);

        addListeners();
        hookContextMenu();
        updateGisNode();
    }

    /**
     *add listeners on visual items
     */
    private void addListeners() {
        bCorrelate.addSelectionListener(new SelectionListener() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                correlateNetworkDrive();
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
                widgetSelected(e);
            }
        });
        table.getControl().addMouseListener(new MouseListener() {

            @Override
            public void mouseUp(MouseEvent e) {
            }

            @Override
            public void mouseDown(MouseEvent e) {
                point = new Point(e.x, e.y);
            }

            @Override
            public void mouseDoubleClick(MouseEvent e) {
            }
        });
    }

    /**
     * Creates a popup menu
     */

    private void hookContextMenu() {
        MenuManager menuMgr = new MenuManager("#PopupMenu");
        menuMgr.setRemoveAllWhenShown(true);
        menuMgr.addMenuListener(new IMenuListener() {
            public void menuAboutToShow(IMenuManager manager) {
                fillContextMenu(manager);
            }
        });
        Menu menu = menuMgr.createContextMenu(table.getControl());
        table.getControl().setMenu(menu);
        getSite().registerContextMenu(menuMgr, table);
    }

    /**
     * fills context menu
     * 
     * @param manager - menu manager
     */
    protected void fillContextMenu(IMenuManager manager) {
        Table tabl = table.getTable();
        if (point != null) {
            TableItem item = tabl.getItem(point);
            if (item != null) {
                final RowWrapper wrapper = (RowWrapper)item.getData();
                manager.add(new Action(String.format(REMOVE_CORRELATION, wrapper.getNetworkName(), wrapper.getDriveName())) {
                    @Override
                    public void run() {
                        removeCorrelation(wrapper);
                    }
                });
            }
        }
    }

    /**
     * Remove correlation
     * 
     * @param wrapper - correlation wrapper
     */
    protected void removeCorrelation(final RowWrapper wrapper) {
        Transaction tx = service.beginTx();
        try {

            Job removeCorrelationJob = new Job("Remove correlation") {

                @Override
                protected IStatus run(IProgressMonitor monitor) {
                    managerRemoveNetworkDriveCorrelation(monitor, wrapper);

                    return Status.OK_STATUS;
                }
            };
            removeCorrelationJob.schedule();
        } finally {
            tx.finish();
        }

    }

    /**
     * Remove correlation manager
     * 
     * @param monitor
     * @param wrapper - correlation wrapper
     * @return
     */
    protected void managerRemoveNetworkDriveCorrelation(IProgressMonitor monitor, RowWrapper wrapper) {
        Transaction tx = service.beginTx();
        NeoUtils.addTransactionLog(tx, Thread.currentThread(), "managerRemoveNetworkDriveCorrelation");
        try {
            wrapper.getRelation().delete();
            tx.success();
            tx.finish();
            updateInputFromDisplay();
            tx = service.beginTx();
            Node root = NeoUtils.findOrCreateSectorDriveRoot(wrapper.getDriveNode(), service, false);
            for (Relationship relation : root.getRelationships(NetworkRelationshipTypes.CHILD, Direction.OUTGOING)) {
                Node node = relation.getOtherNode(root);
                Iterable<Relationship> relationships = node.getRelationships(NetworkRelationshipTypes.SECTOR, Direction.OUTGOING);
                for (Relationship sectorRelation : relationships) {
                    if (sectorRelation.getProperty(INeoConstants.NETWORK_GIS_NAME).equals(wrapper.getNetworkName())) {
                        relation.delete();
                        break;
                    }
                }
            }
            tx.success();
        } finally {
            tx.finish();
        }
    }

    /**
     * Correlate network-drive trees correlate network and drive gis nodes
     */
    protected void correlateNetworkDrive() {
        final Node networkGis = gisNetworkNodes.get(cNetwork.getText());
        final Node driveGis = gisDriveNodes.get(cDrive.getText());
        if (networkGis == null || driveGis == null) {
            return;
        }
        Job correlateJob = new Job("correlate") {

            @Override
            protected IStatus run(IProgressMonitor monitor) {
                setNetworkDriveCorrelation(monitor, networkGis, driveGis);
                updateInputFromDisplay();

                return Status.OK_STATUS;
            }

        };
        correlateJob.schedule();
    }

    /**
     * correlation manager
     * 
     * @param monitor - IProgressMonitor
     * @param driveGis - drive gis node
     * @param networkGis - network gis node
     */
    protected void setNetworkDriveCorrelation(IProgressMonitor monitor, Node networkGis, Node driveGis) {
        Transaction tx = service.beginTx();
        NeoUtils.addTransactionLog(tx, Thread.currentThread(), "setNetworkDriveCorrelation");
        try {
            for (Relationship relation : networkGis.getRelationships(NetworkRelationshipTypes.LINKED_NETWORK_DRIVE,
                    Direction.OUTGOING)) {
                if (relation.getOtherNode(networkGis).equals(driveGis)) {
                    return;
                }
            }
            networkGis.createRelationshipTo(driveGis, NetworkRelationshipTypes.LINKED_NETWORK_DRIVE);
            Traverser traverse = driveGis.traverse(Order.DEPTH_FIRST, StopEvaluator.END_OF_GRAPH, new ReturnableEvaluator() {

                @Override
                public boolean isReturnableNode(TraversalPosition currentPos) {
                    Node node = currentPos.currentNode();
                    String type = NeoUtils.getNodeType(node, "");
                    if (!type.equals(INeoConstants.MP_TYPE_NAME)) {
                        return false;
                    }
                    return node.hasProperty(INeoConstants.SECTOR_ID_PROPERTIES);
                }
            }, NetworkRelationshipTypes.CHILD, Direction.OUTGOING, GeoNeoRelationshipTypes.NEXT, Direction.OUTGOING);
            Node sectorDriveRoot = NeoUtils.findOrCreateSectorDriveRoot(driveGis, service, false);
            for (Node node : traverse) {
                Node sectorDrive = NeoUtils.findOrCreateSectorDrive(NeoUtils.getSimpleNodeName(driveGis, null), sectorDriveRoot,
                        node, service, false);
                if (sectorDrive != null) {
                    NeoUtils.linkWithSector(networkGis, sectorDrive, null);
                }
                monitor.setTaskName("Node time " + new Date(NeoUtils.getNodeTime(node)));
            }
            tx.success();

        } catch (Exception e) {
            // TODO remove catch all exception after debug
            // e.printStackTrace();
            DriveInquirerPlugin.error(e.getLocalizedMessage(), e);
        } finally {
            tx.finish();
        }
    }

    /**
     * Forms list of drive and network
     */
    public void formGisLists() {
        Node refNode = service.getReferenceNode();
        gisDriveNodes.clear();
        gisNetworkNodes.clear();

        Transaction tx = service.beginTx();
        try {
            for (Relationship relationship : refNode.getRelationships(Direction.OUTGOING)) {
                Node node = relationship.getEndNode();
                Object type = node.getProperty(INeoConstants.PROPERTY_GIS_TYPE_NAME, "").toString();
                if (NeoUtils.isGisNode(node)) {
                    String id = NeoUtils.getSimpleNodeName(node, null);
                    if (type.equals(GisTypes.DRIVE.getHeader())) {
                        gisDriveNodes.put(id, node);
                    } else if (type.equals(GisTypes.NETWORK.getHeader())) {
                        gisNetworkNodes.put(id, node);
                    }
                }
            }
        } finally {
            tx.finish();
        }

    }

    /**
     *update drive combobox
     */
    public void updateGisNode() {
        formGisLists();
        changeLists(cDrive, gisDriveNodes);
        changeLists(cNetwork, gisNetworkNodes);
        table.setInput("");
    }

    /**
     *Change list of gis items
     * 
     * @param cList - list
     * @param gisMap - map of new gis items
     */
    private void changeLists(Combo cList, LinkedHashMap<String, Node> gisMap) {
        int oldInd = cList.getSelectionIndex();
        String item = oldInd >= 0 ? cList.getItem(oldInd) : null;
        String[] items = gisMap.keySet().toArray(new String[] {});
        cList.setItems(items);
        if (oldInd >= 0) {
            for (int i = 0; i < items.length; i++) {
                if (item.equals(items[i])) {
                    cList.select(i);
                    break;
                }
            }
        }
    }

    @Override
    public void setFocus() {
    }

    /**
     *fire update from Display thread
     */
    protected void updateInputFromDisplay() {
        ActionUtil.getInstance().runTask(new Runnable() {

            @Override
            public void run() {
                table.setInput("");
            }
        }, true);
    }

    /**
     * <p>
     * Label provider
     * </p>
     * 
     * @author cinkel_a
     * @since 1.0.0
     */
    private class TableLabelProvider extends LabelProvider implements ITableLabelProvider {

        private ArrayList<TableColumn> columns = new ArrayList<TableColumn>();
        /** int DEF_SIZE field */
        protected static final int DEF_SIZE = 200;

        /**
         *create column table
         */
        public void createTableColumn() {
            Table tabl = table.getTable();
            TableViewerColumn column;
            TableColumn col;
            if (columns.isEmpty()) {
                column = new TableViewerColumn(table, SWT.LEFT);
                col = column.getColumn();
                col.setText("Network");
                columns.add(col);
                col.setWidth(DEF_SIZE);
                col.setResizable(true);

                column = new TableViewerColumn(table, SWT.LEFT);
                col = column.getColumn();
                col.setText("Drive");
                columns.add(col);
                col.setWidth(DEF_SIZE);
                col.setResizable(true);

                // TODO implement other column if necessary

            }
            tabl.setHeaderVisible(true);
            tabl.setLinesVisible(true);
            table.setLabelProvider(this);
            table.refresh();
        }

        @Override
        public String getColumnText(Object element, int columnIndex) {
            RowWrapper wrapper = (RowWrapper)element;
            String result = "";
            if (columnIndex == 0) {
                return wrapper.getNetworkName();
            } else if (columnIndex == 1) {
                return wrapper.getDriveName();
            }
            return result;
        }

        @Override
        public Image getColumnImage(Object element, int columnIndex) {
            return null;
        }

    }

    /*
     * The content provider class is responsible for providing objects to the view. It can wrap
     * existing objects in adapters or simply return objects as-is. These objects may be sensitive
     * to the current input of the view, or ignore it and always show the same content (like Task
     * List, for example).
     */

    private class TableContentProvider implements IStructuredContentProvider {
        List<RowWrapper> elements = new ArrayList<RowWrapper>();

        public TableContentProvider() {
        }

        @Override
        public Object[] getElements(Object inputElement) {
            return elements.toArray(new RowWrapper[0]);
        }

        @Override
        public void dispose() {
        }

        @Override
        public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
            elements.clear();
            if (newInput == null) {
                return;
            }
            Traverser linkedNetworkTraverser = NeoUtils.getLinkedNetworkTraverser(service);
            for (Node node : linkedNetworkTraverser) {
                for (Relationship relation : node.getRelationships(NetworkRelationshipTypes.LINKED_NETWORK_DRIVE,
                        Direction.OUTGOING)) {
                    elements.add(new RowWrapper(node, relation));
                }
            }
        }
    }

    /**
     * <p>
     * Wrapper of one row of table
     * </p>
     * 
     * @author Cinkel_A
     * @since 1.0.0
     */
    private class RowWrapper {

        private String networkName;
        private final Node networkNode;
        private final Node driveNode;
        private String driveName;
        private final Relationship relation;

        /**
         * Constructor
         * 
         * @param node network node
         * @param relation - relation
         */
        public RowWrapper(Node node, Relationship relation) {
            this.networkNode = node;
            this.relation = relation;
            Transaction tx = service.beginTx();
            try {
                networkName = NeoUtils.getSimpleNodeName(node, "");
                driveNode = relation.getOtherNode(node);
                driveName = NeoUtils.getSimpleNodeName(driveNode, "");

            } finally {
                tx.finish();
            }
        }

        /**
         * @return Returns the networkName.
         */
        public String getNetworkName() {
            return networkName;
        }

        /**
         * @return Returns the driveName.
         */
        public String getDriveName() {
            return driveName;
        }

        /**
         * @return Returns the networkNode.
         */
        public Node getNetworkNode() {
            return networkNode;
        }

        /**
         * @return Returns the driveNode.
         */
        public Node getDriveNode() {
            return driveNode;
        }

        /**
         * @return Returns the relation.
         */
        public Relationship getRelation() {
            return relation;
        }

    }

}
