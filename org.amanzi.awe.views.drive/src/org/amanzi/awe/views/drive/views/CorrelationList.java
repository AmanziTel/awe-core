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
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;

import org.amanzi.neo.core.INeoConstants;
import org.amanzi.neo.core.enums.CorrelationRelationshipTypes;
import org.amanzi.neo.core.enums.GeoNeoRelationshipTypes;
import org.amanzi.neo.core.enums.GisTypes;
import org.amanzi.neo.core.enums.NetworkRelationshipTypes;
import org.amanzi.neo.core.enums.NodeTypes;
import org.amanzi.neo.core.service.NeoServiceProvider;
import org.amanzi.neo.core.service.listener.INeoServiceProviderListener;
import org.amanzi.neo.core.utils.ActionUtil;
import org.amanzi.neo.core.utils.NeoUtils;
import org.amanzi.neo.services.statistic.PropertyHeader;
import org.apache.log4j.Logger;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.part.ViewPart;
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

/**
 * <p>
 * Table contains list of properties of M-nodes for correlation of selected network and drive.
 * </p>
 * 
 * @author Saelenchits_N
 * @since 1.0.0
 */
public class CorrelationList extends ViewPart implements INeoServiceProviderListener {
    //TODO ZNN need main solution for using class NeoServiceProviderListener instead of interface INeoServiceProviderListener  

    private static final Logger LOGGER = Logger.getLogger(CorrelationList.class);
    /**
     * The ID of the view as specified by the extension.
     */
    public static final String ID = "org.amanzi.awe.views.drive.views.CorrelationList";
    private static final int FIX_COLUMN_COUNT = 1;
    /** int DEF_SIZE field */
    protected static final int DEF_SIZE = 100;
    private Combo cDrive;
    private Combo cNetwork;
    private TableViewer table;
    private GraphDatabaseService graphDatabaseService = NeoServiceProvider.getProvider().getService();
    private final LinkedHashMap<String, Node> gisDriveNodes = new LinkedHashMap<String, Node>();
    private final LinkedHashMap<String, Node> gisNetworkNodes = new LinkedHashMap<String, Node>();
    private TableLabelProvider labelProvider;
    private TableContentProvider provider;
    protected ArrayList<String> tableColNames = new ArrayList<String>(1);
    protected List<RowWrapper> tableData = new ArrayList<RowWrapper>(0);

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

        table = new TableViewer(frame, SWT.BORDER | SWT.FULL_SELECTION);
        FormData fData = new FormData();
        fData.left = new FormAttachment(0, 0);
        fData.right = new FormAttachment(100, 0);
        fData.top = new FormAttachment(child, 2);
        fData.bottom = new FormAttachment(100, -2);
        table.getControl().setLayoutData(fData);
        table.getControl().setVisible(false);
        labelProvider = new TableLabelProvider();
        labelProvider.createTableColumn();
        provider = new TableContentProvider();
        table.setContentProvider(provider);

        addListeners();
        updateGisNode();
    }

    /**
     *add listeners on visual items
     */
    private void addListeners() {
        cNetwork.addSelectionListener(new SelectionListener() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                networkChangeSelection();
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
                widgetDefaultSelected(e);
            }

        });

        cDrive.addSelectionListener(new SelectionListener() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                driveChangeSelection();
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
                widgetDefaultSelected(e);
            }

        });

    }

    /**
     * Listener that called when drive changed
     */
    protected void driveChangeSelection() {
        LOGGER.debug("Drive selection changed");
        tableColNames.clear();
        table.getControl().setVisible(false);
        tableData.clear();
        Node curDriveNode = gisDriveNodes.get(cDrive.getText());
        final String datasetName = curDriveNode.getProperty(INeoConstants.PROPERTY_NAME_NAME, "").toString();

        Node curNetworkNode = gisNetworkNodes.get(cNetwork.getText());
        String[] columns = PropertyHeader.getPropertyStatistic(curDriveNode).getAllFields();

        Transaction tx = graphDatabaseService.beginTx();
        try {
            Traverser correlationSectors = curNetworkNode.traverse(Order.DEPTH_FIRST, NeoUtils.getStopEvaluator(2), new ReturnableEvaluator() {

                @Override
                public boolean isReturnableNode(TraversalPosition currentPos) {
                    return currentPos.depth() == 2;
                }

            }, CorrelationRelationshipTypes.CORRELATION, Direction.OUTGOING, NetworkRelationshipTypes.CHILD, Direction.OUTGOING);

            for (Node correlationSector : correlationSectors) {
                // LOGGER.debug("correlationSector = " + correlationSector.getProperty("sector_id",
                // ""));
                // LOGGER.debug("correlationSector = " + correlationSector);
                Traverser correlatedMNodes = correlationSector.traverse(Order.DEPTH_FIRST, new StopEvaluator() {

                    @Override
                    public boolean isStopNode(TraversalPosition currentPos) {
                        if (currentPos.depth() == 1) {
                            // LOGGER.debug(currentPos.lastRelationshipTraversed().getProperty(INeoConstants.NETWORK_GIS_NAME,
                            // ""));
                            return !currentPos.lastRelationshipTraversed().getProperty(INeoConstants.NETWORK_GIS_NAME, "").equals(datasetName);
                        }
                        return NodeTypes.M.checkNode(currentPos.currentNode()) || currentPos.depth() >= 2;
                    }
                }, new ReturnableEvaluator() {

                    @Override
                    public boolean isReturnableNode(TraversalPosition currentPos) {
                        return NodeTypes.M.checkNode(currentPos.currentNode());
                    }

                }, CorrelationRelationshipTypes.CORRELATED, Direction.OUTGOING, GeoNeoRelationshipTypes.LOCATION, Direction.INCOMING);
                for (Node correlatedMNode : correlatedMNodes) {
                    tableData.add(new RowWrapper(correlationSector, correlatedMNode));
                }
            }
        } finally {
            tx.finish();
        }

        tableColNames.addAll(Arrays.asList(columns));
        labelProvider.refreshTable();
        table.getControl().setVisible(true);
    }

    /**
     * Listener that called when network changed
     */
    private void networkChangeSelection() {
        LOGGER.debug("Network selection changed");

        gisDriveNodes.clear();
        tableColNames.clear();
        tableData.clear();
        table.getControl().setVisible(false);

        Node curNetworkNode = gisNetworkNodes.get(cNetwork.getText());
        Traverser datasetsTraverser = NeoUtils.getAllCorrelatedDatasets(curNetworkNode, graphDatabaseService);

        for (Node dataset : datasetsTraverser) {
            Object type = dataset.getProperty(INeoConstants.PROPERTY_GIS_TYPE_NAME, "").toString();
            if (NeoUtils.isGisNode(dataset)) {
                String id = NeoUtils.getSimpleNodeName(dataset, null);
                if (type.equals(GisTypes.DRIVE.getHeader()) || type.equals(GisTypes.OSS.getHeader())) {
                    gisDriveNodes.put(id, dataset);
                }
            }
        }
        String[] result = gisDriveNodes.keySet().toArray(new String[0]);
        Arrays.sort(result);
        cDrive.setItems(result);
    }

    /**
     * Forms list of drive and network
     */
    public void formGisLists() {
        Node refNode = graphDatabaseService.getReferenceNode();
        // gisDriveNodes.clear();
        gisNetworkNodes.clear();

        Transaction tx = graphDatabaseService.beginTx();
        try {
            for (Relationship relationship : refNode.getRelationships(Direction.OUTGOING)) {
                Node node = relationship.getEndNode();
                Object type = node.getProperty(INeoConstants.PROPERTY_GIS_TYPE_NAME, "").toString();
                if (NeoUtils.isGisNode(node)) {
                    String id = NeoUtils.getSimpleNodeName(node, null);
                    if (type.equals(GisTypes.NETWORK.getHeader())) {
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

    public void showCurrentCorrelation(Node networkNode, Node driveNode) {
        updateGisNode();
        cNetwork.setText(networkNode.getProperty(INeoConstants.PROPERTY_NAME_NAME, "").toString());
        networkChangeSelection();
        cDrive.setText(driveNode.getProperty(INeoConstants.PROPERTY_NAME_NAME, "").toString());
        driveChangeSelection();

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
        Arrays.sort(items);
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
     * @author Saelenchits_N
     * @since 1.0.0
     */
    private class TableLabelProvider extends LabelProvider implements ITableLabelProvider {
        private final ArrayList<TableColumn> columns = new ArrayList<TableColumn>();

        /**
         *create column table
         */
        public void createTableColumn() {
            Table tabl = table.getTable();
            TableViewerColumn column;
            TableColumn col;

            column = new TableViewerColumn(table, SWT.LEFT);
            col = column.getColumn();
            col.setText("Sector ID");
            columns.add(col);
            col.setWidth(DEF_SIZE);
            col.setResizable(true);

            tabl.setHeaderVisible(true);
            tabl.setLinesVisible(true);
            table.setLabelProvider(this);
            table.refresh();
        }

        /**
         * Refresh table
         */
        public void refreshTable() {
            Table tabl = table.getTable();
            TableViewerColumn column;
            TableColumn col;

            int i = 0;
            for (; i < tableColNames.size() && i < columns.size() - FIX_COLUMN_COUNT; i++) {
                col = columns.get(i + FIX_COLUMN_COUNT);
                col.setText(tableColNames.get(i));
                col.setWidth(DEF_SIZE);
                col.setResizable(true);
            }
            if (tableColNames.size() > columns.size() - FIX_COLUMN_COUNT) {
                for (; i < tableColNames.size(); i++) {
                    column = new TableViewerColumn(table, SWT.LEFT);
                    col = column.getColumn();
                    col.setText(tableColNames.get(i));
                    columns.add(col);
                    col.setWidth(DEF_SIZE);
                    col.setResizable(true);
                }
            } else if (tableColNames.size() < columns.size() - FIX_COLUMN_COUNT) {
                i += FIX_COLUMN_COUNT;
                for (; i < columns.size(); i++) {
                    col = columns.get(i);
                    col.setWidth(0);
                    col.setResizable(false);
                }
            }

            tabl.setHeaderVisible(true);
            tabl.setLinesVisible(true);
            table.setLabelProvider(this);
            table.refresh();
        }

        @Override
        public String getColumnText(Object element, int columnIndex) {
            RowWrapper wrapper = (RowWrapper)element;
            if (columnIndex == 0) {
                return wrapper.getSectorId();
            } else {
                return wrapper.getPropertyByIndex(columnIndex);
            }
        }

        @Override
        public Image getColumnImage(Object element, int columnIndex) {
            // return columnIndex == DEL_IND ? delete : null;
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

        public TableContentProvider() {
        }

        @Override
        public Object[] getElements(Object inputElement) {
            return tableData.toArray(new RowWrapper[0]);
        }

        @Override
        public void dispose() {
        }

        @Override
        public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
        }
    }

    /**
     * <p>
     * Wrapper of one row of table
     * </p>
     * 
     * @author Saelenchits_N
     * @since 1.0.0
     */
    private class RowWrapper {

        private final Node siteCorrelationNode;
        private final Node mCorrelatedNnode;

        /**
         * Constructor
         * 
         * @param node network node
         * @param relation - relation
         */
        public RowWrapper(Node siteCorrelationNode, Node mCorrelatedNnode) {
            this.siteCorrelationNode = siteCorrelationNode;
            this.mCorrelatedNnode = mCorrelatedNnode;
        }

        /**
         * Returns property by table index
         * 
         * @param columnIndex
         * @return property
         */
        public String getPropertyByIndex(int columnIndex) {
            if (columnIndex > tableColNames.size())
                return "";
            // try {
            return mCorrelatedNnode.getProperty(tableColNames.get(columnIndex - 1), "").toString();
            // } catch (IndexOutOfBoundsException e) {
            // return e.getMessage();
            // }
        }

        /**
         * @return
         */
        public String getSectorId() {
            return siteCorrelationNode.getProperty(INeoConstants.SECTOR_ID_PROPERTIES, "").toString();
        }
    }
    
    @Override
    public void onNeoStop(Object source) {
        graphDatabaseService = null;
    }

    @Override
    public void onNeoStart(Object source) {
        graphDatabaseService = NeoServiceProvider.getProvider().getService();
    }

    @Override
    public void onNeoCommit(Object source) {
    }

    @Override
    public void onNeoRollback(Object source) {
    }

    //
    // @Override
    // public Collection<UpdateViewEventType> getType() {
    // Collection<UpdateViewEventType> spr = new HashSet<UpdateViewEventType>();
    // spr.add(UpdateViewEventType.GIS);
    // return spr;
    // }
    //
    // @Override
    // public void updateView(UpdateViewEvent event) {
    // updateGisNode();
    // }
}
