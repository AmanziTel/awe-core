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
package org.amanzi.awe.views.neighbours.views;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.amanzi.awe.catalog.neo.GeoNeo;
import org.amanzi.awe.catalog.neo.NeoCatalogPlugin;
import org.amanzi.awe.catalog.neo.upd_layers.events.UpdatePropertiesAndMapEvent;
import org.amanzi.awe.catalog.neo.upd_layers.events.UpdatePropertiesEvent;
import org.amanzi.awe.views.neighbours.NeighboursPlugin;
import org.amanzi.awe.views.neighbours.RelationWrapper;
import org.amanzi.neo.core.INeoConstants;
import org.amanzi.neo.core.enums.GeoNeoRelationshipTypes;
import org.amanzi.neo.core.enums.NetworkRelationshipTypes;
import org.amanzi.neo.core.enums.NetworkSiteType;
import org.amanzi.neo.core.enums.NodeTypes;
import org.amanzi.neo.core.icons.IconManager;
import org.amanzi.neo.core.service.NeoServiceProvider;
import org.amanzi.neo.core.service.listener.INeoServiceProviderListener;
import org.amanzi.neo.core.utils.NeoUtils;
import org.amanzi.neo.core.utils.PropertyHeader;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableColorProvider;
import org.eclipse.jface.viewers.ITableFontProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.part.ViewPart;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.ReturnableEvaluator;
import org.neo4j.graphdb.StopEvaluator;
import org.neo4j.graphdb.TraversalPosition;
import org.neo4j.graphdb.Traverser;
import org.neo4j.graphdb.Traverser.Order;

/**
 * <p>
 * Neighbour view
 * </p>
 * 
 * @author Cinkel_A
 * @since 1.0.0
 */
public class NeighboursView extends ViewPart implements INeoServiceProviderListener {
    //TODO ZNN need main solution for using class NeoServiceProviderListener instead of interface INeoServiceProviderListener  

    private GraphDatabaseService graphDatabaseService=NeoServiceProvider.getProvider().getService();
    /** String SHOW_NEIGHBOUR field */
    private static final String SHOW_NEIGHBOUR = "show neighbour relation '%s' > '%s' on map";
    private static final String SHOW_SERVE = "show all '%s' neighbours on map";
    private static final String SHOW_SERVE_2G = "show 2G '%s' neighbours on map";
    private static final String SHOW_SERVE_3G = "show 3G '%s' neighbours on map";

    /** String ROLLBACK field */
    private static final String ROLLBACK = "Rollback";

    /** String COMMIT field */
    private static final String COMMIT = "Commit";

    /**
	 * The ID of the view as specified by the extension.
	 */
	public static final String ID = "org.amanzi.awe.views.neighbours.views.NeighboursView";


	private TableViewer viewer;
    private Node network;
    private Node gis = null;
    private ViewContentProvider provider;
    private boolean editMode = false;
    private Combo neighbour;
    private Node centeredNode;
    private ViewLabelProvider labelProvider;

    private List<String> integerProperties = new ArrayList<String>();

    private List<String> doubleProperties = new ArrayList<String>();

    private Button rollback;

    private Button commit;

    protected Point point = null;
    private RelationWrapper selectedServe;
    private RelationWrapper selectedNeighbour;
    private Font fontNormal;
    private Font fontSelected;
    private boolean autoZoom = true;
    private Comparator<RelationWrapper> comparator;
    private int sortOrder = 0;
    private Color color1;
    private Color color2;
    private Collection<Node> input;

	/*
	 * The content provider class is responsible for
	 * providing objects to the view. It can wrap
	 * existing objects in adapters or simply return
	 * objects as-is. These objects may be sensitive
	 * to the current input of the view, or ignore
	 * it and always show the same content 
	 * (like Task List, for example).
	 */
	 
	class ViewContentProvider implements IStructuredContentProvider {
        private static final int MAX_FIELD = 1000;
        private Collection<Node> input;
        private RelationWrapper[] elements = new RelationWrapper[0];


        @SuppressWarnings("unchecked")
        public void inputChanged(Viewer v, Object oldInput, Object newInput) {
            //Lagutko, 9.10.2009, if newInput is null than AWE closes and no need to update view
            if (newInput == null) {
                return;
            }
            clearSelection();
            if (!(newInput instanceof Collection< ? >)) {
                input = new ArrayList<Node>(0);
                network = null;
            } else {
                input = (Collection<Node>)newInput;
                if (input == null) {
                    input = new ArrayList<Node>(0);
                    network = null;
                } else {
                    network = input.iterator().hasNext() ? NeoUtils.findNodeByChild(input.iterator().next(),
                            NodeTypes.NETWORK.getId()) : null;
                    if (network == null) {
                        input = new ArrayList<Node>(0);
                    } else {
                        gis = network.getSingleRelationship(GeoNeoRelationshipTypes.NEXT, Direction.INCOMING).getOtherNode(network);
                    }
                }
            }
            initializeLayer(gis);
            updateNeighbourList(network);
            if (neighbour.getItemCount() > 0) {
                neighbour.select(0);
                neighbourSelectionChange();
            }
            elements = getElements2(input);
            sort();
        }
		public void dispose() {
		}
		public Object[] getElements(Object parent) {
            return elements;
        }

        public RelationWrapper[] getElements2(Object parent) {
            final RelationWrapper[] emptyArray = new RelationWrapper[0];
            Node neighbour = getNeighbour();
            if (neighbour == null || input == null) {
                return emptyArray;
            }
            List<RelationWrapper> results = new ArrayList<RelationWrapper>();
            // Transaction tx = NeoUtils.beginTransaction();
            // try {
            Iterator<Relationship> iterator = new InputIterator(input, neighbour);
            int count = 0;
            while (iterator.hasNext() && ++count < MAX_FIELD) {
                Relationship relation = iterator.next();
                results.add(new RelationWrapper(relation));
            }
            return results.toArray(emptyArray);
		}



        /**
         * <p>
         * Iterator of relationships
         * </p>
         * 
         * @author Cinkel_A
         * @since 1.0.0
         */
        public class InputIterator implements Iterator<Relationship> {

            private final String name;
            private final Iterator<Node> iterator1;
            private Iterator<Node> nodeIterator;
            private Iterator<Relationship> iterator2;

            /**
             * @param input
             * @param neighbour
             */
            public InputIterator(Collection<Node> input, Node neighbour) {
                name = NeoUtils.getSimpleNodeName(neighbour, null);
                iterator1 = input.iterator();
                nodeIterator = new ArrayList<Node>().iterator();
                iterator2 = new ArrayList<Relationship>().iterator();
            }

            @Override
            public boolean hasNext() {
                if (iterator2.hasNext()) {
                    return true;
                }
                createIterator2();
                return iterator2.hasNext();
            }

            /**
             * create Relationship iterator
             */
            private void createIterator2() {
                createNodeIterator();
                List<Relationship> result = new ArrayList<Relationship>();
                while (nodeIterator.hasNext()) {
                    Node mainNode = nodeIterator.next();
                    for (Relationship relation : mainNode.getRelationships(NetworkRelationshipTypes.NEIGHBOUR,
                            Direction.OUTGOING)) {
                        if (NeoUtils.getNeighbourName(relation, "").equals(name)) {
                            result.add(relation);
                        }
                    }
                    if (!result.isEmpty()) {
                        break;
                    }
                }
                iterator2 = result.iterator();
            }

            /**
             * create node iterator
             */
            private void createNodeIterator() {
                if (nodeIterator.hasNext()) {
                    return;
                }
                if (iterator1.hasNext()) {
                    Node mainNode = iterator1.next();
                    nodeIterator = mainNode.traverse(Order.DEPTH_FIRST, StopEvaluator.END_OF_GRAPH, new ReturnableEvaluator() {

                        @Override
                        public boolean isReturnableNode(TraversalPosition currentPos) {
                                            return currentPos.currentNode().hasRelationship(NetworkRelationshipTypes.NEIGHBOUR,
                                                    Direction.OUTGOING);
                        }
                    }, NetworkRelationshipTypes.CHILD, Direction.OUTGOING, GeoNeoRelationshipTypes.CHILD, Direction.OUTGOING)
                            .iterator();
                }
            }

            @Override
            public Relationship next() {
                return iterator2.next();
            }

            @Override
            /**
             * Method do not support
             */
            public void remove() {
                throw new UnsupportedOperationException();
            }

        }

        /**
         *
         */
        public void sort() {
            Arrays.sort(elements, comparator);
            if (elements.length < 1) {
                return;
            }
            Color color = color1;
            elements[0].setColor(color);
            for (int i = 1; i < elements.length; i++) {
                if (comparator.compare(elements[i - 1], elements[i]) != 0) {
                    color = color == color1 ? color2 : color1;
                }
                elements[i].setColor(color);
            }
        }
	}

    class ViewLabelProvider extends LabelProvider implements ITableLabelProvider, ITableFontProvider, ITableColorProvider {
        /** int DEF_SIZE field */
        protected static final int DEF_SIZE = 100;
        private final ArrayList<String> columns = new ArrayList<String>();
        

        public String getColumnText(Object obj, int index) {
            // Transaction tx = NeoUtils.beginTransaction();
            // try {
            RelationWrapper relation = (RelationWrapper)obj;
            if (index == 0) {
                return NeoUtils.getSimpleNodeName(relation.getServeNode(), "");
            } else if (index == 1) {
                NetworkSiteType networkSiteType = NetworkSiteType.getNetworkSiteType(NeoUtils.getParent(graphDatabaseService, relation.getServeNode()), graphDatabaseService);
                return networkSiteType==null?"":networkSiteType.getId();
            } else if (index == 2) {
                return NeoUtils.getSimpleNodeName(relation.getNeighbourNode(), "");
            } else if (index == 3) {
                NetworkSiteType networkSiteType = NetworkSiteType.getNetworkSiteType(NeoUtils.getParent(graphDatabaseService, relation.getNeighbourNode()), graphDatabaseService);
                return networkSiteType==null?"":networkSiteType.getId();
            }else {
                return relation.getRelation().getProperty(columns.get(index), "").toString();
            }
        }

        public Image getColumnImage(Object obj, int index) {
            return getImage(obj);
		}


        /**
         *Create the table columns of the Neighbour types view.
         */
        private void createTableColumn() {
            Table table = viewer.getTable();
            TableViewerColumn column;
            TableColumn col;
            if (columns.isEmpty()) {
                column = new TableViewerColumn(viewer, SWT.LEFT);
                col = column.getColumn();
                col.setText("Serving cell");
                col.addSelectionListener(new SelectionListener() {
                    
                    @Override
                    public void widgetSelected(SelectionEvent e) {
                        sortOrder = 0;
                        if (provider != null) {
                            provider.sort();
                        }
                        viewer.refresh();
                        viewer.getTable().showSelection();
                    }
                    
                    @Override
                    public void widgetDefaultSelected(SelectionEvent e) {
                        widgetSelected(e);
                    }
                });
                columns.add(col.getText());
                col.setWidth(DEF_SIZE);
                col.setResizable(true);
                column = new TableViewerColumn(viewer, SWT.LEFT);
                col = column.getColumn();
                col.setText("Serving cell type");
                col.setWidth(DEF_SIZE);
                col.setResizable(true);
                columns.add(col.getText());
                column = new TableViewerColumn(viewer, SWT.LEFT);
                col = column.getColumn();
                col.setText("Neighbour cell");
                col.addSelectionListener(new SelectionListener() {

                    @Override
                    public void widgetSelected(SelectionEvent e) {
                        sortOrder = 1;
                        if (provider != null) {
                            provider.sort();
                        }
                        viewer.refresh();
                        viewer.getTable().showSelection();
                    }

                    @Override
                    public void widgetDefaultSelected(SelectionEvent e) {
                        widgetSelected(e);
                    }
                });
                columns.add(col.getText());
                col.setWidth(DEF_SIZE);
                col.setResizable(true);
                column = new TableViewerColumn(viewer, SWT.LEFT);
                col = column.getColumn();
                col.setText("Neighbour cell type");
                col.setWidth(DEF_SIZE);
                col.setResizable(true);
                columns.add(col.getText());
            }
            List<String> allNeighbourEditableProperties = getAllNeighbourEditableProperties();
            for (String name : allNeighbourEditableProperties) {
                if (!columns.contains(name)) {
                    Class<? extends Object> cl = doubleProperties.contains(name) ? Double.class : integerProperties.contains(name) ? Integer.class
                            : String.class;
                    int swt;
                    if (Number.class.isAssignableFrom(cl)) {
                        swt = SWT.RIGHT;
                    } else {
                        swt = SWT.LEFT;
                    }
                    column = new TableViewerColumn(viewer, swt);
                    col = column.getColumn();
                    col.setText(name);
                    columns.add(col.getText());
                    col.setWidth(DEF_SIZE);
                    col.setResizable(true);

                    column.setEditingSupport(new NeighbourEditableSupport(viewer, name, cl));
                }
            }
            for (int i = 4; i < columns.size(); i++) {
                TableColumn colum = table.getColumn(i);
                if (!allNeighbourEditableProperties.contains(columns.get(i))) {
                    colum.setWidth(0);
                    colum.setResizable(false);
                } else {
                    if (colum.getWidth() == 0) {
                        colum.setWidth(DEF_SIZE);
                        colum.setResizable(true);
                    }
                }
            }
            table.setHeaderVisible(true);
            table.setLinesVisible(true);
            viewer.setLabelProvider(this);
            viewer.refresh();
        }

        @Override
        public Font getFont(Object element, int columnIndex) {
            RelationWrapper rel = (RelationWrapper)element;
            if (columnIndex == 0) {
                return selectedServe != null && selectedServe.getServeNode().equals(rel.getServeNode()) ? fontSelected : fontNormal;
            } else if (columnIndex == 1) {
                return selectedNeighbour != null && selectedNeighbour.getNeighbourNode().equals(rel.getNeighbourNode())
                        ? fontSelected : fontNormal;
            } else {
                return fontNormal;
            }
        }

        @Override
        public Color getBackground(Object element, int columnIndex) {
            return ((RelationWrapper)element).getColor();
        }

        @Override
        public Color getForeground(Object element, int columnIndex) {
            return null;
        }
	}


	/**
	 * The constructor.
	 */
	public NeighboursView() {
	}

    /**
     *
     * @param gis2
     */
    public void initializeLayer(Node gis) {
        
    }

    /**
     *launch if neighbour selection changed
     */
    public void neighbourSelectionChange() {
        integerProperties = new ArrayList<String>();
        doubleProperties = new ArrayList<String>();
        if (neighbour.getSelectionIndex() < 0 || gis == null) {
            return;
        } else {
            PropertyHeader header = new PropertyHeader(gis);
            String neighbourName = neighbour.getText();
            String[] arrayInt = header.getNeighbourIntegerFields(neighbourName);
            if (arrayInt != null) {
                integerProperties.addAll(Arrays.asList(arrayInt));
            }
            String[] arrayDouble = header.getNeighbourDoubleFields(neighbourName);
            if (arrayDouble != null) {
                doubleProperties.addAll(Arrays.asList(arrayDouble));
            }
        }
        // update element in content provider, because neighbour list was changed
        provider.elements = provider.getElements2(input);
        if (labelProvider != null) {
            labelProvider.createTableColumn();
        }
    }

    /**
     * updates list of Neighbour
     * 
     * @param network network node
     */
    private void updateNeighbourList(Node network) {
        if (network == null) {
            neighbour.setItems(new String[0]);
            return;
        }
        // Transaction tx = NeoUtils.beginTransaction();
        List<String> neighbourName = new ArrayList<String>();
        // try{
//            Node gisNode = network.getSingleRelationship(GeoNeoRelationshipTypes.NEXT, Direction.INCOMING).getOtherNode(network);
            for (Relationship relation : network.getRelationships(NetworkRelationshipTypes.NEIGHBOUR_DATA, Direction.OUTGOING)) {
                neighbourName.add(NeoUtils.getSimpleNodeName(relation.getOtherNode(network), null));
            }
            Collections.sort(neighbourName);
            neighbour.setItems(neighbourName.toArray(new String[0]));
        // }finally{
        // tx.finish();
        // }
    }

    /**
     * This is a callback that will allow us to create the viewer and initialize it.
     */
	@Override
    public void createPartControl(Composite parent) {
        color1 = new Color(Display.getCurrent(), 240, 240, 240);
        color2 = new Color(Display.getCurrent(), 255, 255, 255);
        sortOrder = 0;
        comparator = new Comparator<RelationWrapper>() {

            @Override
            public int compare(RelationWrapper o1, RelationWrapper o2) {
                if (sortOrder == 0) {
                    // TODO cache node name in RelationWrapper?
                    String nameE1 = NeoUtils.getSimpleNodeName(o1.getServeNode(), "");
                    String nameE2 = NeoUtils.getSimpleNodeName(o2.getServeNode(), "");
                    return nameE1.compareTo(nameE2);
                } else {
                    String nameE1 = NeoUtils.getSimpleNodeName(o1.getNeighbourNode(), "");
                    String nameE2 = NeoUtils.getSimpleNodeName(o2.getNeighbourNode(), "");
                    return nameE1.compareTo(nameE2);
                }
            }
        };
        Composite child = new Composite(parent, SWT.NONE);
        final GridLayout layout = new GridLayout(6, false);
        child.setLayout(layout);

        Label label = new Label(child, SWT.FLAT);
        label.setText("Neighbour list:");
        GridData layoutData = new GridData(SWT.LEFT, SWT.CENTER, false, false);
        label.setLayoutData(layoutData);

        neighbour = new Combo(child, SWT.DROP_DOWN | SWT.READ_ONLY);
        neighbour.addSelectionListener(new SelectionListener() {
            
            @Override
            public void widgetSelected(SelectionEvent e) {
                neighbourSelectionChange();
            }
            
            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
                widgetSelected(e);
            }
        });
        layoutData = new GridData(SWT.FILL, SWT.CENTER, true, false);
        layoutData.horizontalSpan = 2;
        Label spaser = new Label(child, SWT.FLAT);
        spaser.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        neighbour.setLayoutData(layoutData);

        commit = new Button(child, SWT.BORDER | SWT.PUSH);
        commit.addSelectionListener(new SelectionListener() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                NeighboursPlugin.getDefault().commit();
                updateDirty(false);
                viewer.refresh();
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
                widgetSelected(e);
            }
        });
        commit.setToolTipText(COMMIT);
        commit.setImage(IconManager.getIconManager().getCommitImage());

        rollback = new Button(child, SWT.BORDER | SWT.PUSH);
        rollback.setImage(IconManager.getIconManager().getRollbackImage());
        rollback.addSelectionListener(new SelectionListener() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                NeighboursPlugin.getDefault().rollback();
                updateDirty(false);
                viewer.refresh();
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
                widgetSelected(e);
            }
        });
        rollback.setToolTipText(ROLLBACK);

        updateDirty(false);
        viewer = new TableViewer(child, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION);
        GridData data = new GridData(GridData.FILL_BOTH | GridData.GRAB_HORIZONTAL | GridData.GRAB_VERTICAL);
        data.horizontalSpan = layout.numColumns;
        viewer.getControl().setLayoutData(data);
        labelProvider = new ViewLabelProvider();
        viewer.setLabelProvider(labelProvider);
        labelProvider.createTableColumn();
        provider = new ViewContentProvider();
        viewer.setContentProvider(provider);


        // viewer.setComparator(comparator);
        getSite().setSelectionProvider(viewer);
        viewer.getControl().addMouseListener(new MouseListener() {

            @Override
            public void mouseUp(MouseEvent e) {
            }

            @Override
            public void mouseDown(MouseEvent e) {
                point = new Point(e.x, e.y);
            }

            @Override
            public void mouseDoubleClick(MouseEvent e) {
                Table table = viewer.getTable();
                Point point = new Point(e.x, e.y);
                TableItem item = table.getItem(point);
                if (item != null) {
                    if (item.getBounds(0).contains(point)) {
                        showServe((RelationWrapper)item.getData());
                    } else if (item.getBounds(1).contains(point)) {
                        showNeighbour((RelationWrapper)item.getData());
                    }
                }
            }
        });
        fontNormal = viewer.getTable().getFont();
        FontData[] fd = fontNormal.getFontData();
        fd[0].setStyle(SWT.BOLD);
        // TODO dispose font resources in plugin stop()?
        fontSelected = new Font(fontNormal.getDevice(), fd);
        hookContextMenu();
        // hookDoubleClickAction();
	}

    /**
     * @param relationWrapper
     */
    protected void showNeighbour(RelationWrapper relationWrapper) {
        setNeighbourSelection(relationWrapper);
        HashMap<String, Object> properties = new HashMap<String, Object>();
        properties.put(GeoNeo.NEIGH_MAIN_NODE, null);
        properties.put(GeoNeo.NEIGH_NAME, relationWrapper.toString());
        properties.put(GeoNeo.NEIGH_RELATION, relationWrapper.getRelation());
        properties.put(GeoNeo.NEIGH_TYPE, null);
        UpdatePropertiesAndMapEvent event = getLayerEvent(properties, relationWrapper.getNeighbourNode(), relationWrapper.getServeNode());
        NeoCatalogPlugin.getDefault().getLayerManager().sendUpdateMessage(event);
    }

    /**
     * @param relationWrapper
     */
    private void setNeighbourSelection(RelationWrapper relationWrapper) {
        selectedNeighbour = relationWrapper;
        selectedServe = null;
        viewer.refresh();
    }

    /**
     * @param relationWrapper
     */
    protected void showServe(RelationWrapper relationWrapper) {
        setServeSelection(relationWrapper);
        HashMap<String, Object> properties = new HashMap<String, Object>();
        properties.put(GeoNeo.NEIGH_MAIN_NODE, relationWrapper.getServeNode());
        properties.put(GeoNeo.NEIGH_NAME, relationWrapper.toString());
        properties.put(GeoNeo.NEIGH_RELATION, null);
        properties.put(GeoNeo.NEIGH_TYPE, null);
        UpdatePropertiesAndMapEvent event = getLayerEvent(properties, relationWrapper.getServeNode(), relationWrapper.getServeNode());
        NeoCatalogPlugin.getDefault().getLayerManager().sendUpdateMessage(event);
    }

    /**
     * @param relationWrapper
     */
    private void setServeSelection(RelationWrapper relationWrapper) {
        selectedServe = relationWrapper;
        selectedNeighbour = null;
        viewer.refresh();
    }

    /**
     * @param b
     */
    private void updateDirty(boolean dirty) {
        editMode = dirty;
        commit.setEnabled(editMode);
        rollback.setEnabled(editMode);

    }

    /**
     * get all Neighbour properties
     * 
     * @return list of properties name
     */
    private List<String> getAllNeighbourEditableProperties() {
        List<String> result = new ArrayList<String>();
        if (gis == null || neighbour.getSelectionIndex() < 0) {
            return result;
        }
        String[] array = new PropertyHeader(gis).getNeighbourAllFields(neighbour.getText());
        return array == null ? result : Arrays.asList(array);
    }

    /**
	 * Passing the focus request to the viewer's control.
	 */
	@Override
    public void setFocus() {
		viewer.getControl().setFocus();
	}

    /**
     * Sets Input of nodes
     * 
     * @param inputNodes input nodes
     */
    public void setInput(Collection<Node> inputNodes) {
        input = inputNodes;
        viewer.setInput(inputNodes);
    }

    /**
     * gets selected Neighbour node
     * 
     * @return node
     */
    private Node getNeighbour() {
        if (gis == null || neighbour.getSelectionIndex() < 0) {
            return null;
        }
        return NeoUtils.findNeighbour(NeoUtils.findRoot(gis, NeoServiceProvider.getProvider().getService()), neighbour.getText());
    }

    /**
     * <p>
     * Support edit cells
     * </p>
     * 
     * @author Cinkel_A
     * @since 1.0.0
     */
    public class NeighbourEditableSupport extends EditingSupport {

        private final String name;
        private final Class< ? > valueClass;
        private final CellEditor editor;
        private String value;

        /**
         * Constructoe
         * 
         * @param viewer
         * @param name
         * @param class1
         */
        public NeighbourEditableSupport(TableViewer viewer, String name, Class< ? > valueClass) {
            super(viewer);
            this.name = name;
            this.valueClass = valueClass;
            editor = new TextCellEditor(viewer.getTable());
        }

        @Override
        protected boolean canEdit(Object element) {
            return true;
        }

        @Override
        protected CellEditor getCellEditor(Object element) {
            return editor;
        }

        @Override
        protected Object getValue(Object element) {
            value = ((RelationWrapper)element).getRelation().getProperty(name, "").toString();
            return value;
        }

        @Override
        protected void setValue(Object element, Object value) {
            try {
                if (this.value.equals(value)) {
                    return;
                }
                Object valueToSave;
                if (valueClass.isAssignableFrom(Double.class)) {
                    valueToSave = Double.parseDouble(value.toString());
                } else if (valueClass.isAssignableFrom(Integer.class)) {
                    valueToSave = Integer.parseInt(value.toString());
                } else {
                    valueToSave = value.toString();
                }
                ((RelationWrapper)element).getRelation().setProperty(name, valueToSave);
                updateDirty(true);
                getViewer().update(element, null);
            } catch (NumberFormatException e) {
            }

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
                fillContextMenu(manager);
            }
        });
        Menu menu = menuMgr.createContextMenu(viewer.getControl());
        viewer.getControl().setMenu(menu);
        getSite().registerContextMenu(menuMgr, viewer);
    }

    /**
     * fills context menu
     * 
     * @param manager - menu manager
     */
    protected void fillContextMenu(IMenuManager manager) {
        Table table = viewer.getTable();
        if (point != null) {
        TableItem item = table.getItem(point);
        if (item != null) {
            if (item.getBounds(0).contains(point)||item.getBounds(1).contains(point)) {
                    fillServMenu(manager, (RelationWrapper)item.getData());
            } else if (item.getBounds(2).contains(point)||item.getBounds(3).contains(point)) {
                    fillNeighMenu(manager, (RelationWrapper)item.getData());
            }
        }
    }
    }

    /**
     * fill Neighbour menu
     * 
     * @param manager - menu manager
     * @param data - data
     */
    private void fillNeighMenu(IMenuManager manager, final RelationWrapper data) {
        String serv = NeoUtils.getNodeName(data.getServeNode());
        String neigh = NeoUtils.getNodeName(data.getNeighbourNode());
        manager.add(new Action(String.format(SHOW_NEIGHBOUR, serv, neigh)) {
            @Override
            public void run() {
                showNeighbour(data);
            }
        });
    }

    /**
     *Fill Serve menu
     * 
     * @param manager - menu manager
     * @param data - data
     */
    private void fillServMenu(IMenuManager manager, final RelationWrapper data) {
        String serv = NeoUtils.getNodeName(data.getServeNode());
        manager.add(new Action(String.format(SHOW_SERVE, serv)) {
            @Override
            public void run() {
                showServe(data);
            }
        });
        manager.add(new Action(String.format(SHOW_SERVE_2G, serv)) {
            @Override
            public void run() {
                showServe(data,NetworkSiteType.SITE_2G);
            }
        });
        manager.add(new Action(String.format(SHOW_SERVE_3G, serv)) {
            @Override
            public void run() {
                showServe(data,NetworkSiteType.SITE_3G);
            }
        });
    }

    /**
     *
     * @param relationWrapper
     * @param siteType
     */
    protected void showServe(RelationWrapper relationWrapper, NetworkSiteType siteType) {
        setServeSelection(relationWrapper);
        HashMap<String, Object> properties = new HashMap<String, Object>();
        properties.put(GeoNeo.NEIGH_MAIN_NODE, relationWrapper.getServeNode());
        properties.put(GeoNeo.NEIGH_NAME, relationWrapper.toString());
        properties.put(GeoNeo.NEIGH_RELATION, null);
        properties.put(GeoNeo.NEIGH_TYPE, siteType);
        UpdatePropertiesAndMapEvent event = getLayerEvent(properties, relationWrapper.getServeNode(), relationWrapper.getServeNode());
        NeoCatalogPlugin.getDefault().getLayerManager().sendUpdateMessage(event);
    }
    
    private UpdatePropertiesAndMapEvent getLayerEvent(HashMap<String, Object> properties, Node selection, Node center){
        UpdatePropertiesAndMapEvent event = new UpdatePropertiesAndMapEvent(gis, properties, false);
        boolean needCentered = needCentered(center);
        event.setSelection(Collections.singleton(selection));
        event.setNeedCentered(needCentered);
        if(needCentered){
            centeredNode = center;
            if (!gis.getProperty(INeoConstants.PROPERTY_CRS_TYPE_NAME, "").toString().equalsIgnoreCase(("projected"))) {
                autoZoom = false;
            }
            event.setAutoZoom(autoZoom);
            if(autoZoom){
                autoZoom = false; // only zoom first time, then rely on user to control zoom
            }
            event.setCoords(getCoords(center));
        }
        return event;
    }

    /**
     * @param center
     * @return
     */
    private boolean needCentered(Node center) {

        return centeredNode == null || !centeredNode.equals(center);
    }

    // TODO move to utility class (copy from NetworkTreeView)
    /**
     * get coordinates of selected node if node do not contains coordinates, try to find it in
     * parent node
     * 
     * @param node node
     * @return
     */
    private static double[] getCoords(Node node) {
        for (int i = 0; i <= 1; i++) {
            if (node.hasProperty(INeoConstants.PROPERTY_LAT_NAME)) {
                if (node.hasProperty(INeoConstants.PROPERTY_LON_NAME)) {
                    try {
                        return new double[] {(Float)node.getProperty(INeoConstants.PROPERTY_LON_NAME),
                                (Float)node.getProperty(INeoConstants.PROPERTY_LAT_NAME)};
                    } catch (ClassCastException e) {
                        return new double[] {(Double)node.getProperty(INeoConstants.PROPERTY_LON_NAME),
                                (Double)node.getProperty(INeoConstants.PROPERTY_LAT_NAME)};
                    }
                }
            }
            // try to up by 1 lvl
            Traverser traverse = node.traverse(Traverser.Order.BREADTH_FIRST, StopEvaluator.DEPTH_ONE,
                    ReturnableEvaluator.ALL_BUT_START_NODE, NetworkRelationshipTypes.CHILD, Direction.BOTH);
            if (traverse.iterator().hasNext()) {
                node = traverse.iterator().next();
            } else {
                return null;
            }
        }
        return null;

    }

    @Override
    public void dispose() {
        clearSelection();
        super.dispose();
    }

    /**
     *
     */
    private void clearSelection() {
        HashMap<String, Object> properties = new HashMap<String, Object>();
        properties.put(GeoNeo.NEIGH_MAIN_NODE, null);
        properties.put(GeoNeo.NEIGH_NAME, null);
        properties.put(GeoNeo.NEIGH_RELATION, null);
        properties.put(GeoNeo.NEIGH_TYPE, null);
        NeoCatalogPlugin.getDefault().getLayerManager().sendUpdateMessage(new UpdatePropertiesEvent(gis, properties, false));
    }

    /**
     * update view
     */
    public void updateView() {
        if (input != null) {
            setInput(input);
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

}
