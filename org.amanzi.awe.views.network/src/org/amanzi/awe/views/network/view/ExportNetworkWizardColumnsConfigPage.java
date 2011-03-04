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
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.amanzi.neo.services.DatasetService;
import org.amanzi.neo.services.INeoConstants;
import org.amanzi.neo.services.NeoServiceFactory;
import org.amanzi.neo.services.enums.GeoNeoRelationshipTypes;
import org.amanzi.neo.services.enums.INodeType;
import org.amanzi.neo.services.enums.NodeTypes;
import org.amanzi.neo.services.statistic.IPropertyHeader;
import org.amanzi.neo.services.statistic.PropertyHeader;
import org.amanzi.neo.services.statistic.internal.PropertyHeaderImpl;
import org.eclipse.jface.dialogs.DialogPage;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Path;
import org.neo4j.graphdb.traversal.TraversalDescription;
import org.neo4j.kernel.Traversal;
import org.neo4j.kernel.Uniqueness;

/**
 * <p>
 * Second page of export network wizard
 * </p>
 * .
 * 
 * @author Saelenchits_N
 * @since 1.0.0
 */
public class ExportNetworkWizardColumnsConfigPage extends WizardPage {

    private Group main;
    private TableViewer viewer;
    private final List<RowWr> propertyList = new ArrayList<RowWr>();
    private final int colIndexType = 0;
    private final int colIndexProperty = 1;
    private final int colIndexColumn = 2;
    private static final Color BAD_COLOR = new Color(null, 255, 0, 0);
    private static final Color SKIP_COLOR = new Color(null, 150, 150, 150);
    private Node rootNode;

    /**
     * Instantiates a new export network wizard columns config page.
     * 
     * @param pageName the page name
     */
    protected ExportNetworkWizardColumnsConfigPage(String pageName, String title) {
        super(pageName, title, null);
        setDescription(getNormalDescription());
        validate();
    }
    
    @Override
    public void createControl(Composite parent) {
        main = new Group(parent, SWT.NULL);
        main.setLayout(new GridLayout(3, false));
        main.setText("Properties");

        viewer = new TableViewer(main, SWT.BORDER | SWT.FULL_SELECTION);
        TableContentProvider provider = new TableContentProvider();
        createTableColumn();
        viewer.setContentProvider(provider);

        GridData layoutData = new GridData(SWT.FILL, SWT.FILL, true, false, 3, 1);
        viewer.getControl().setLayoutData(layoutData);

        layoutData.grabExcessVerticalSpace = true;
        layoutData.heightHint = 300;
        viewer.setInput(new Object[0]);

        setControl(main);
    }

    /**
     * Creates the table column.
     */
    private void createTableColumn() {
        Table table = viewer.getTable();
        TableViewerColumn column;
        TableColumn col;

        column = new TableViewerColumn(viewer, SWT.RIGHT);
        col = column.getColumn();
        col.setText("Node type");
        col.setWidth(70);
        col.setResizable(true);
        column.setLabelProvider(new ColLabelProvider(colIndexType));
        column.setEditingSupport(new TableEditableSupport(viewer, colIndexType));

        column = new TableViewerColumn(viewer, SWT.RIGHT);
        col = column.getColumn();
        col.setText("Property name");
        col.setWidth(200);
        col.setResizable(true);
        column.setLabelProvider(new ColLabelProvider(colIndexProperty));
        column.setEditingSupport(new TableEditableSupport(viewer, colIndexProperty));

        column = new TableViewerColumn(viewer, SWT.RIGHT);
        col = column.getColumn();
        col.setText("File column name");
        col.setWidth(200);
        col.setResizable(true);
        column.setLabelProvider(new ColLabelProvider(colIndexColumn));
        column.setEditingSupport(new TableEditableSupport(viewer, colIndexColumn));

        table.setHeaderVisible(true);
        table.setLinesVisible(true);

        viewer.refresh();

    }
    
    @Override
    public IWizardPage getNextPage() {
        ExportNetworkWizardColumnsConfigPage currentPage = null;
        ArrayList<Boolean> checkboxStates = ExportNetworkWizard.getSavingDataPage().getCheckBoxesState();
        ArrayList<Integer> indexes = new ArrayList<Integer>();
        int index = 0;
        for (Boolean state : checkboxStates) {
            if (state == true) {
                indexes.add(index);
            }
            index++;
        }
        for (Integer ind : indexes) {
            System.out.println(ind);
        }
        System.out.println(ExportNetworkWizard.getCurrentIndex());
        for (Boolean state : checkboxStates) {
            System.out.println(state);
        }
        if (ExportNetworkWizard.getCurrentIndex() > indexes.size() - 1) {
            return getWizard().getPage(ExportNetworkWizard.PROPERTY_CSV);
        }
        if (ExportNetworkWizard.getCurrentIndex() < 0) {
            ExportNetworkWizard.setCurrentIndex(0);
        }
        try {
            currentPage = ExportNetworkWizard.getAvailablePages().get(indexes.get(ExportNetworkWizard.getCurrentIndex()));
        }
        catch (IndexOutOfBoundsException e) {
            return getWizard().getPage(ExportNetworkWizard.PROPERTY_CSV);
        }
        ExportNetworkWizard.setCurrentIndex(ExportNetworkWizard.getCurrentIndex() + 2);
        
        return currentPage;
    }
    
    @Override
    public IWizardPage getPreviousPage() {
        ExportNetworkWizard.setCurrentIndex(ExportNetworkWizard.getCurrentIndex() - 1);
        return super.getPreviousPage();
    }

    /*
     * The content provider class is responsible for providing objects to the view. It can wrap
     * existing objects in adapters or simply return objects as-is. These objects may be sensitive
     * to the current input of the view, or ignore it and always show the same content (like Taskc
     * List, for example).
     */
    private class TableContentProvider implements IStructuredContentProvider {

        @Override
        public Object[] getElements(Object inputElement) {
            return propertyList.toArray(new RowWr[0]);
        }

        @Override
        public void dispose() {
        }

        @Override
        public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
        }

    }

    /**
     * The Class RowWr.
     */
    public class RowWr {
        private final String nodeType;
        private final String propertyName;
        private String columnName = "";
        private boolean isValid;

        /**
         * Instantiates a new row wr.
         * 
         * @param nodeType the node type
         * @param propertyName the property name
         * @param columnName the column name
         */
        public RowWr(String nodeType, String propertyName, String columnName) {
            this.nodeType = nodeType;
            this.propertyName = propertyName;
            this.columnName = columnName;
        }

        /**
         * Gets the value.
         * 
         * @param colIndex the column index
         * @return the value
         */
        public String getValue(int colIndex) {
            switch (colIndex) {
            case colIndexColumn:
                return columnName;
            case colIndexProperty:
                return propertyName;
            case colIndexType:
                return nodeType;
            default:
                return null;
            }
        }

        /**
         * Sets the value.
         * 
         * @param colIndex the column index
         * @param sValue the s value
         */
        public void setValue(int colIndex, String sValue) {
            if (colIndexColumn == colIndex) {
                columnName = sValue;
                validate();
            }
        }

        /**
         * Checks if is valid.
         * 
         * @return true, if is valid
         */
        public boolean isValid() {
            return isValid;
        }

        /**
         * Sets the valid.
         * 
         * @param isValid the new valid
         */
        public void setValid(boolean isValid) {
            this.isValid = isValid;
        }

    }

    /**
     * The Class ColLabelProvider.
     */
    private class ColLabelProvider extends ColumnLabelProvider {

        /** The column index. */
        private final int colInd;

        /**
         * Instantiates a new col label provider.
         * 
         * @param colInd the column index
         */
        public ColLabelProvider(int colInd) {
            super();
            this.colInd = colInd;
        }

        /**
         * Gets the foreground.
         * 
         * @param element the element
         * @return the foreground
         */
        @Override
        public Color getForeground(Object element) {
            RowWr wrapper = (RowWr)element;
            if (colInd == colIndexColumn) {
                if (!wrapper.isValid()) {
                    return BAD_COLOR;
                }
                if (wrapper.getValue(colIndexColumn).isEmpty()) {
                    return SKIP_COLOR;
                }
            }
            return null;
        }

        /**
         * Gets the text.
         * 
         * @param element the element
         * @return the text
         */
        @Override
        public String getText(Object element) {
            RowWr wrapper = (RowWr)element;
            if (colInd == colIndexColumn) {
                if (wrapper.getValue(colIndexColumn).isEmpty()) {
                    return "Not exported";
                }
            }
            return wrapper.getValue(colInd);
        }
    }

    /**
     * The Class TableEditableSupport.
     */
    public class TableEditableSupport extends EditingSupport {

        /** The editor. */
        private final TextCellEditor editor;

        /** The column index. */
        private final int colIndex;

        /**
         * Instantiates a new table editable support.
         * 
         * @param viewer the viewer
         * @param colIndex the column index
         */
        public TableEditableSupport(TableViewer viewer, int colIndex) {
            super(viewer);
            this.colIndex = colIndex;
            editor = new TextCellEditor(viewer.getTable());
        }


        @Override
        protected boolean canEdit(Object element) {
            return colIndex == colIndexColumn;
        }

        @Override
        protected CellEditor getCellEditor(Object element) {
            return editor;
        }

        @Override
        protected Object getValue(Object element) {
            return ((RowWr)element).getValue(colIndex);
        }

        @Override
        protected void setValue(Object element, Object value) {
            String sValue = ((String)value).trim();
            ((RowWr)element).setValue(colIndex, sValue);
            viewer.refresh();
        }

    }

    /**
     * Change node selection.
     * 
     * @param selectedNode the selected node
     */
    public void changeNodeSelection(Node selectedNode) {
        
        Set<String> allPropertiesWithStatistic = (Set<String>)getAvailabilityPropertiesInNetwork(selectedNode, NodeTypes.SECTOR);
        Set<String> props2 = (Set<String>)getAvailabilityPropertiesInNetwork(selectedNode, NodeTypes.NEIGHBOUR);
        Set<String> props3 = (Set<String>)getAvailabilityPropertiesInNetwork(selectedNode, NodeTypes.SITE);
        Set<String> props4 = (Set<String>)getAvailabilityPropertiesInNetwork(selectedNode, NodeTypes.CITY);
        Set<String> props5 = (Set<String>)getAvailabilityPropertiesInNetwork(selectedNode, NodeTypes.BSC);
        Set<String> props6 = (Set<String>)getAvailabilityPropertiesInNetwork(selectedNode, NodeTypes.FREQUENCY_PLAN);
        Set<String> props7 = (Set<String>)getAvailabilityPropertiesInNetwork(selectedNode, NodeTypes.TRX);
        Set<String> props8 = (Set<String>)getAvailabilityPropertiesInNetwork(selectedNode, NodeTypes.NETWORK);
        Set<String> props9 = (Set<String>)getAvailabilityPropertiesInNetwork(selectedNode, NodeTypes.STATISTICS);
        
        for (String str : allPropertiesWithStatistic) {
            System.out.println(str);
        }
        String nameOfPage = this.getName();
        ColumnsConfigPageTypes typeOfPage = ColumnsConfigPageTypes.findColumnsConfigPageTypeByName(nameOfPage);
        this.rootNode = selectedNode;
        DatasetService datasetService = NeoServiceFactory.getInstance().getDatasetService();
        
        String[] strtypes = datasetService.getSructureTypesId(rootNode);
        List<String> headers = new ArrayList<String>();
        
        // Collect all existed properties
        HashMap<String, Collection<String>> propertyMap = new HashMap<String, Collection<String>>();
        TraversalDescription descr = Traversal.description().depthFirst().uniqueness(Uniqueness.NONE).relationships(GeoNeoRelationshipTypes.CHILD, Direction.OUTGOING)
        .filter(Traversal.returnAllButStartNode());
        ArrayList<String> properties = new ArrayList<String>();
        Collection<String> coll = new TreeSet<String>();

        switch (typeOfPage) {
            case NETWORK_SECTOR_DATA:
                for (int i = 1; i < strtypes.length; i++) {
                    headers.add(strtypes[i]);
                }
                for (Path path : descr.traverse(rootNode)) {
                    Node node = path.endNode();
                    INodeType type = datasetService.getNodeType(node);
                    if (type != null && headers.contains(type.getId())) {
                        coll = propertyMap.get(type.getId());
                        if (coll == null) {
                            coll = new TreeSet<String>();
                            propertyMap.put(type.getId(), coll);
                        }
                        for (String propertyName : node.getPropertyKeys()) {
                            if (INeoConstants.PROPERTY_TYPE_NAME.equals(propertyName)) {
                                continue;
                            }
                            coll.add(propertyName);
                        }
                    }
                }
                break;
            case NEIGBOURS_DATA:
                properties.clear();
                try {
                    for (String prop : ColumnsConfigPageTypes.NEIGBOURS_DATA.getProperties()) {
                        properties.add(cleanHeader(prop));
                    }
                }
                catch (NullPointerException e) {
                    
                }
                coll.clear();
                propertyMap.put(NodeTypes.SECTOR.getId(), coll);
                
                for (String propertyName : properties) {
                    coll.add(propertyName);
                }
                break;
            case FREQUENCY_CONSTRAINT_DATA:
                properties.clear();
                try {
                    for (String prop : ColumnsConfigPageTypes.FREQUENCY_CONSTRAINT_DATA.getProperties()) {
                        properties.add(cleanHeader(prop));
                    }
                }
                catch (NullPointerException e) {
                    
                }
                coll.clear();
                propertyMap.put(NodeTypes.SECTOR.getId(), coll);
                
                for (String propertyName : properties) {
                    coll.add(propertyName);
                }
                break;
            case INTERFERENCE_MATRIX:
                properties.clear();
                try {
                    for (String prop : ColumnsConfigPageTypes.INTERFERENCE_MATRIX.getProperties()) {
                        properties.add(cleanHeader(prop));
                    }
                }
                catch (NullPointerException e) {
                    
                }
                coll.clear();
                propertyMap.put(NodeTypes.SECTOR.getId(), coll);
                
                for (String propertyName : properties) {
                    coll.add(propertyName);
                }
                break;
            case SEPARATION_CONSTRAINT_DATA:
                properties.clear();
                try {
                    for (String prop : ColumnsConfigPageTypes.SEPARATION_CONSTRAINT_DATA.getProperties()) {
                        properties.add(cleanHeader(prop));
                    }
                }
                catch (NullPointerException e) {
                    
                }
                coll.clear();
                propertyMap.put(NodeTypes.SECTOR.getId(), coll);
                
                for (String propertyName : properties) {
                    coll.add(propertyName);
                }
                break;
            case TRAFFIC_DATA:
                properties.clear();
                try {
                    for (String prop : ColumnsConfigPageTypes.TRAFFIC_DATA.getProperties()) {
                        properties.add(cleanHeader(prop));
                    }
                }
                catch (NullPointerException e) {
                    
                }
                coll.clear();
                propertyMap.put(NodeTypes.SECTOR.getId(), coll);
                
                for (String propertyName : properties) {
                    coll.add(propertyName);
                }
                break;
            case TRX_DATA:
                properties.clear();
                try {
                    for (String prop : ColumnsConfigPageTypes.TRX_DATA.getProperties()) {
                        properties.add(cleanHeader(prop));
                    }
                }
                catch (NullPointerException e) {
                    
                }
                coll.clear();
                propertyMap.put(NodeTypes.SECTOR.getId(), coll);
                
                for (String propertyName : properties) {
                    coll.add(propertyName);
                }
                break;
            default:
                break;
            
        }

        // Kasnitskij_V:
        // Create table content
        boolean isCreatedFromNode = createTableContentFromNode(datasetService, propertyMap);
        if (isCreatedFromNode == false) {
            createTableContentWithCasualHeaders(datasetService, propertyMap);
        }
        
        validate();
        if (viewer != null)
            viewer.setInput("");

    }
    
    private Iterable<String> getAvailabilityPropertiesInNetwork(Node selectedNode, NodeTypes nodeType) {
        // Kasnitskij_V:
        IPropertyHeader propertyHeader = null;
        String currentNetworkName = null;
        DatasetService datasetService = NeoServiceFactory.getInstance().getDatasetService();
        try {
            currentNetworkName = datasetService.findRootByChild((Node)selectedNode)
                                            .getProperty(INeoConstants.PROPERTY_NAME_NAME).toString();
        }
        catch (NullPointerException e) {
        }
        
        Iterable<Node> rootNodes = datasetService.getAllRootNodes().nodes();
        Node rootNode = null;
        for (Node node : rootNodes) {
            String networkName = node.getProperty(INeoConstants.PROPERTY_NAME_NAME).toString();
            if (networkName.equals(currentNetworkName)) {
                propertyHeader = new PropertyHeaderImpl(node, datasetService.getNodeName(node));
                rootNode = node;
                break;
            }
        }

        Map<String, Object> propertiesWithValues = propertyHeader.getStatisticParams(nodeType);
        if (propertiesWithValues.size() == 0) {
            propertyHeader = PropertyHeader.getPropertyStatistic(rootNode);
            propertiesWithValues = propertyHeader.getStatisticParams(nodeType);
        }
        Set<String> keysFromProperties = propertiesWithValues.keySet();
    
        return keysFromProperties;
    }
    
    /**
     * Kasnitskij_V:
     *
     * method create table with headers from saving node
     *
     * @param datasetService DatasetService
     * @param propertyMap PropertyMap
     */
    private boolean createTableContentFromNode(DatasetService datasetService, HashMap<String, Collection<String>> propertyMap) {
        boolean isExistColumnName = false;
        
        // Create table content
        Map<String, String> originalHeaders = datasetService.getOriginalFileHeaders(rootNode);
        List<RowWr> rows = new ArrayList<RowWr>();
        for (Map.Entry<String, Collection<String>> entry : propertyMap.entrySet()) {
            String type = entry.getKey();
            for (String propertyName : entry.getValue()) {
                propertyName = propertyName.toLowerCase();
                String columnName = "";
                String origHeader = originalHeaders.get(type + INeoConstants.PROPERTY_NAME_PREFIX + cleanHeader(propertyName));
                if (origHeader != null) {
                    columnName = origHeader;
                    isExistColumnName = true;
                }
                rows.add(new RowWr(type, propertyName, columnName));
            }
        }
        propertyList.clear();
        propertyList.addAll(rows);
        
        return isExistColumnName;
    }
    
    private static String cleanHeader(String header) {
        return header.replaceAll("[\\s\\-\\[\\]\\(\\)\\/\\.\\\\\\:\\#]+", "_").replaceAll("[^\\w]+", "_").replaceAll("_+", "_").replaceAll("\\_$", "").toLowerCase();
    }
    
    /**
     * Kasnitskij_V:
     *
     * method create table with casual headers
     *
     * @param datasetService DatasetService
     * @param propertyMap PropertyMap
     */
    private void createTableContentWithCasualHeaders(DatasetService datasetService, HashMap<String, Collection<String>> propertyMap) {
        
        // Create table content
        List<RowWr> rows = new ArrayList<RowWr>();
        for (Map.Entry<String, Collection<String>> entry : propertyMap.entrySet()) {
            String type = entry.getKey();
            for (String propertyName : entry.getValue()) {
                String columnName = "";
                String headerName = null;
                if (propertyName.equals("name")) {
                    headerName = ExportNetworkWizard.getMapPropertyNameHeader().get(propertyName + "_" + type);
                }
                else {
                    headerName = ExportNetworkWizard.getMapPropertyNameHeader().get(propertyName);
                }
                
                String[] headers = null;
                if (headerName != null) {
                     headers = ExportNetworkWizard.getPossibleHeaders(headerName);
                }

                if (headers != null) {
                    columnName = headers[0];
                }
                rows.add(new RowWr(type, propertyName, columnName));
            }
        }
        propertyList.clear();
        propertyList.addAll(rows);
    }

    /**
     * Validate.
     */
    private void validate() {
        setPageComplete(isValidPage());
    }

    @Override
    public boolean canFlipToNextPage() {
        return true;
    }

    /**
     * Checks if is valid page.
     * 
     * @return true, if is valid page
     */
    private boolean isValidPage() {
        for (RowWr row1 : propertyList) {
            row1.setValid(true);
            if (row1.getValue(colIndexColumn).isEmpty()) {
                continue;
            }

            for (RowWr row2 : propertyList) {
                if (!row1.equals(row2) && row1.getValue(colIndexColumn).equalsIgnoreCase(row2.getValue(colIndexColumn))) {
                    setMessage(String.format("Duplicated column name '%s'", row1.getValue(colIndexColumn)), DialogPage.ERROR);
                    row1.setValid(false);
                    row2.setValid(false);
                    return false;
                }
            }

        }
        setMessage(getNormalDescription(), DialogPage.NONE);
        return true;
    }

    /**
     * Gets the normal description.
     * 
     * @return the normal description
     */
    protected String getNormalDescription() {
        return "All non-empty properties can be exported.";
    }

    /**
     * Gets the property map.
     * 
     * @return the property map
     */
    public Map<String, Map<String, String>> getPropertyMap() {
        Map<String, Map<String, String>> propertyMap = new HashMap<String, Map<String, String>>();
        for (RowWr row : propertyList) {
            String type = row.getValue(colIndexType);
            String property = row.getValue(colIndexProperty);
            String column = row.getValue(colIndexColumn);
            if (column.isEmpty())
                continue;

            Map<String, String> propertyCol = propertyMap.get(type);
            if (propertyCol == null) {
                propertyMap.put(type, new HashMap<String, String>());
                propertyCol = propertyMap.get(type);
            }
            propertyCol.put(property, column);
        }
        return propertyMap;
    }
}
