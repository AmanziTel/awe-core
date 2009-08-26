package org.amanzi.awe.views.reuse.views;

import java.awt.Color;
import java.awt.Paint;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeMap;

import net.refractions.udig.catalog.IGeoResource;
import net.refractions.udig.project.ILayer;
import net.refractions.udig.project.IMap;
import net.refractions.udig.project.ui.ApplicationGIS;

import org.amanzi.awe.catalog.neo.GeoNeo;
import org.amanzi.neo.core.INeoConstants;
import org.amanzi.neo.core.enums.GeoNeoRelationshipTypes;
import org.amanzi.neo.core.enums.NetworkRelationshipTypes;
import org.amanzi.neo.core.service.NeoServiceProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.ui.part.ViewPart;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartMouseEvent;
import org.jfree.chart.ChartMouseListener;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.LegendItem;
import org.jfree.chart.LegendItemCollection;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.entity.CategoryItemEntity;
import org.jfree.chart.labels.StandardCategoryToolTipGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.CategoryItemRenderer;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.general.AbstractDataset;
import org.jfree.experimental.chart.swt.ChartComposite;
import org.neo4j.api.core.Direction;
import org.neo4j.api.core.NeoService;
import org.neo4j.api.core.Node;
import org.neo4j.api.core.Relationship;
import org.neo4j.api.core.ReturnableEvaluator;
import org.neo4j.api.core.StopEvaluator;
import org.neo4j.api.core.TraversalPosition;
import org.neo4j.api.core.Traverser.Order;

/**
 * <p>
 * view "Reuse Analyser"
 * </p>
 * 
 * @author Cinkel_A
 * @since 1.1.0
 */
public class ReuseAnalyserView extends ViewPart {

    /** String ADJACENCY field */
    private static final String ADJACENCY = "Adjacency";
    /** String PROPERTY_LABEL field */
    private static final String PROPERTY_LABEL = "Property";
    /** String GIS_LABEL field */
    private static final String GIS_LABEL = "GIS:";
    /** String COUNT_AXIS field */
    private static final String COUNT_AXIS = "Count";
    /** String VALUES_DOMAIN field */
    private static final String VALUES_DOMAIN = "Value";
    private static final String ROW_KEY = "values";
    private Label gisSelected;
    private Combo gisCombo;
    private Label propertySelected;
    private Combo propertyCombo;
    private HashMap<String, Node> members;
    protected ArrayList<String> propertyList;
    private Spinner spinAdj;
    private Label spinLabel;
    private ChartComposite chartFrame;
    private JFreeChart chart;
    private PropertyCategoryDataset dataset;
    private Node selectedGisNode = null;
    private ChartNode selectedColumn = null;
    private static final Paint DEFAULT_COLOR = Color.CYAN;
    private static final Paint COLOR_SELECTED = Color.RED;
    private static final Paint COLOR_LESS = Color.BLUE;
    private static final Paint COLOR_MORE = Color.GREEN;

    public void createPartControl(Composite parent) {
        gisSelected = new Label(parent, SWT.NONE);
        gisSelected.setText(GIS_LABEL);
        gisCombo = new Combo(parent, SWT.DROP_DOWN | SWT.READ_ONLY);
        gisCombo.setItems(getGisItems());
        gisCombo.setEnabled(true);
        propertySelected = new Label(parent, SWT.NONE);
        propertySelected.setText(PROPERTY_LABEL);
        propertyCombo = new Combo(parent, SWT.DROP_DOWN | SWT.READ_ONLY);
        propertyCombo.setItems(new String[] {});
        propertyCombo.setEnabled(true);
        spinLabel = new Label(parent, SWT.NONE);
        spinLabel.setText(ADJACENCY);
        spinAdj = new Spinner(parent, SWT.NONE);
        spinAdj.setMinimum(0);
        spinAdj.setIncrement(1);
        spinAdj.setDigits(0);
        spinAdj.setSelection(1);
        dataset = new PropertyCategoryDataset();
        chart = ChartFactory.createBarChart("SWTBarChart", VALUES_DOMAIN, COUNT_AXIS, dataset, PlotOrientation.VERTICAL, true,
                true, false);
        CategoryPlot plot = (CategoryPlot)chart.getPlot();
        NumberAxis rangeAxis = (NumberAxis)plot.getRangeAxis();
        rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
        LegendItemCollection legends = new LegendItemCollection();
        legends.add(new LegendItem(ROW_KEY, DEFAULT_COLOR));
        plot.setFixedLegendItems(legends);
        CategoryItemRenderer renderer = new CustomRenderer();
        renderer.setBaseToolTipGenerator(new StandardCategoryToolTipGenerator());
        plot.setRenderer(renderer);
        // if: chartFrame = new ChartComposite(parent, 0, chart, FALSE); then font not zoomed, but
        // wrong column selection after resizing application
        chartFrame = new ChartComposite(parent, 0, chart, true);
        chartFrame.pack();
        chartFrame.setVisible(false);
        layoutComponents(parent);
        chartFrame.addChartMouseListener(new ChartMouseListener() {
            @Override
            public void chartMouseMoved(ChartMouseEvent chartmouseevent) {
            }

            @Override
            public void chartMouseClicked(ChartMouseEvent chartmouseevent) {
                if (chartmouseevent.getEntity() instanceof CategoryItemEntity) {
                    CategoryItemEntity entity = (CategoryItemEntity)chartmouseevent.getEntity();
                    Comparable columnKey = entity.getColumnKey();
                    setSelection((ChartNode)columnKey);
                } else {
                    setSelection(null);
                }
            }
        });
        SelectionListener gisComboSelectionListener = new SelectionListener() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                int selectedGisInd = gisCombo.getSelectionIndex();
                if (selectedGisInd < 0) {
                    propertyList = new ArrayList<String>();
                    chartFrame.setVisible(false);
                } else {
                    formPropertyList(members.get(gisCombo.getText()));
                }
                propertyCombo.setItems(propertyList.toArray(new String[] {}));
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e) {

            }
        };
        SelectionListener propComboSelectionListener = new SelectionListener() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                if (propertyCombo.getSelectionIndex() < 0) {
                    chartFrame.setVisible(false);
                } else {
                    Node aggrNode = formAggregatesNode(members.get(gisCombo.getText()), propertyCombo.getText());
                    chartUpdate(aggrNode);
                }
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
                widgetSelected(e);
            }
        };
        gisCombo.addSelectionListener(gisComboSelectionListener);
        propertyCombo.addSelectionListener(propComboSelectionListener);
    }

    /**
     * Updates chart with new main node
     * 
     * @param aggrNode - new node
     */
    protected void chartUpdate(Node aggrNode) {
        chart.setTitle(aggrNode.getProperty(INeoConstants.PROPERTY_NAME_NAME).toString());
        dataset.setAggrNode(aggrNode);
        setSelection(null);
        chartFrame.setVisible(true);
    }

    /**
     * Select column
     * 
     * @param columnKey - column
     */
    private void setSelection(ChartNode columnKey) {
        Node gisNode = members.get(gisCombo.getText());
        Node aggrNode = dataset.getAggrNode();
        if (selectedColumn != null) {
            selectedColumn = columnKey;
            if (selectedGisNode.equals(gisNode)) {
                fireLayerDrawEvent(gisNode, aggrNode, selectedColumn);
            } else {
                // drop old selection
                fireLayerDrawEvent(selectedGisNode, null, null);
                selectedGisNode = gisNode;
                fireLayerDrawEvent(selectedGisNode, aggrNode, selectedColumn);
            }
        } else {
            selectedColumn = columnKey;
            selectedGisNode = gisNode;
            fireLayerDrawEvent(gisNode, aggrNode, selectedColumn);
        }
    }

    /**
     * @return
     */
    private ChartNode getSelectedColumn() {
        return selectedColumn;
    }

    /**
     * fires layer redraw
     * 
     * @param columnKey property node for redraw action
     */
    protected void fireLayerDrawEvent(Node gisNode, Node aggrNode, ChartNode columnKey) {
        int adj = spinAdj.getSelection();
        Node columnNode = columnKey == null ? null : columnKey.getNode();
        for (IMap activeMap : ApplicationGIS.getOpenMaps()) {
            for (ILayer layer : activeMap.getMapLayers()) {
                IGeoResource resourse = layer.findGeoResource(GeoNeo.class);
                if (resourse != null) {
                    try {
                        GeoNeo geo = resourse.resolve(GeoNeo.class, null);
                        if (geo.getMainGisNode().equals(gisNode)) {
                            geo.setPropertyToRefresh(aggrNode, columnNode, adj);
                            layer.refresh(null);
                        }
                    } catch (IOException e) {
                        // TODO Handle IOException
                        throw (RuntimeException)new RuntimeException().initCause(e);
                    }
                }
            }
        }
    }

    /**
     * Forms a aggregates node
     * 
     * @param gisNode GIS node
     * @param propertyName name of property
     * @return aggregates node
     */
    protected Node formAggregatesNode(Node gisNode, final String propertyName) {
        Iterator<Node> iterator = gisNode.traverse(Order.DEPTH_FIRST, StopEvaluator.DEPTH_ONE, new ReturnableEvaluator() {

            @Override
            public boolean isReturnableNode(TraversalPosition arg0) {
                return propertyName.equals(arg0.currentNode().getProperty(INeoConstants.PROPERTY_NAME_NAME, null));
            }
        }, NetworkRelationshipTypes.AGGREGATION, Direction.OUTGOING).iterator();
        if (iterator.hasNext()) {
            return iterator.next();
        }
        NeoService service = NeoServiceProvider.getProvider().getService();
        Node result = service.createNode();
        result.setProperty(INeoConstants.PROPERTY_NAME_NAME, propertyName);
        result.setProperty(INeoConstants.PROPERTY_TYPE_NAME, INeoConstants.AGGREGATION_TYPE_NAME);
        gisNode.createRelationshipTo(result, NetworkRelationshipTypes.AGGREGATION);
        TreeMap<Integer, Integer> statistics = computeStatistics(gisNode, propertyName);
        Node parentNode = result;
        for (Integer key : statistics.keySet()) {
            Node childNode = service.createNode();
            childNode.setProperty(INeoConstants.PROPERTY_TYPE_NAME, INeoConstants.COUNT_TYPE_NAME);
            childNode.setProperty(INeoConstants.PROPERTY_NAME_NAME, key);
            childNode.setProperty(INeoConstants.PROPERTY_VALUE_NAME, statistics.get(key));
            parentNode.createRelationshipTo(childNode, NetworkRelationshipTypes.CHILD);
            parentNode = childNode;
        }
        return result;
    }

    /**
     * Collect statistics on the selected property.
     * 
     * @param gisNode GIS node
     * @param propertyName name of property
     * @return
     */
    private TreeMap<Integer, Integer> computeStatistics(Node gisNode, String propertyName) {
        TreeMap<Integer, Integer> result = new TreeMap<Integer, Integer>();
        Iterator<Node> iteratorProperties = gisNode.traverse(Order.BREADTH_FIRST, StopEvaluator.END_OF_GRAPH,
                new PropertyReturnableEvalvator(), NetworkRelationshipTypes.CHILD, Direction.OUTGOING,
                GeoNeoRelationshipTypes.NEXT, Direction.OUTGOING).iterator();
        while (iteratorProperties.hasNext()) {
            Node propertyNode = iteratorProperties.next();
            if (propertyNode.hasProperty(propertyName)) {
                Integer key = ((Number)propertyNode.getProperty(propertyName)).intValue();
                Integer value = result.get(key);
                if (value == null) {
                    result.put(key, 1);
                } else {
                    result.put(key, value + 1);
                }
            }
        }
        return result;
    }

    /**
     * Forms property list by selected node
     * 
     * @param node - selected node
     */
    private void formPropertyList(Node node) {
        propertyList = new ArrayList<String>();
        Iterator<Node> iteratorProperties = node.traverse(Order.BREADTH_FIRST, StopEvaluator.END_OF_GRAPH,
                new PropertyReturnableEvalvator(), NetworkRelationshipTypes.CHILD, Direction.OUTGOING,
                GeoNeoRelationshipTypes.NEXT, Direction.OUTGOING).iterator();
        if (iteratorProperties.hasNext()) {
            Node propNode = iteratorProperties.next();
            Iterator<String> iteratorProper = propNode.getPropertyKeys().iterator();
            while (iteratorProper.hasNext()) {
                String propName = iteratorProper.next();
                if (propNode.getProperty(propName) instanceof Number) {
                    propertyList.add(propName);
                }
            }
        }
        chartFrame.setVisible(false);
    }

    /**
     * Forms list of GIS nodes
     * 
     * @return array of GIS nodes
     */
    private String[] getGisItems() {
        NeoService service = NeoServiceProvider.getProvider().getService();
        Node refNode = service.getReferenceNode();
        members = new HashMap<String, Node>();
        for (Relationship relationship : refNode.getRelationships(Direction.OUTGOING)) {
            Node node = relationship.getEndNode();
            if (node.hasProperty(INeoConstants.PROPERTY_TYPE_NAME) && node.hasProperty(INeoConstants.PROPERTY_NAME_NAME)
                    && node.getProperty(INeoConstants.PROPERTY_TYPE_NAME).toString().equalsIgnoreCase(INeoConstants.GIS_TYPE_NAME)) {
                String id = node.getProperty(INeoConstants.PROPERTY_NAME_NAME).toString();
                members.put(id, node);
            }
        }
        return members.keySet().toArray(new String[] {});
    }

    /**
     * sets necessary layout
     * 
     * @param parent parent component
     */
    private void layoutComponents(Composite parent) {
        FormLayout layout = new FormLayout();
        layout.marginHeight = 0;
        layout.marginWidth = 0;
        layout.spacing = 0;
        parent.setLayout(layout);

        FormData dLabel = new FormData(); // bind to left & text
        dLabel.left = new FormAttachment(0, 5);
        dLabel.top = new FormAttachment(gisCombo, 5, SWT.CENTER);
        gisSelected.setLayoutData(dLabel);

        FormData dCombo = new FormData(); // bind to label and text
        dCombo.left = new FormAttachment(gisSelected, 2);
        dCombo.top = new FormAttachment(0, 2);
        dCombo.right = new FormAttachment(40, -5);
        gisCombo.setLayoutData(dCombo);

        dLabel = new FormData(); // bind to left & text
        dLabel.left = new FormAttachment(gisCombo, 10);
        dLabel.top = new FormAttachment(propertyCombo, 5, SWT.CENTER);
        propertySelected.setLayoutData(dLabel);

        dCombo = new FormData(); // bind to label and text
        dCombo.left = new FormAttachment(propertySelected, 2);
        dCombo.top = new FormAttachment(0, 2);
        dCombo.right = new FormAttachment(80, -5);
        propertyCombo.setLayoutData(dCombo);

        dLabel = new FormData(); // bind to left & text
        dLabel.left = new FormAttachment(propertyCombo, 10);
        dLabel.top = new FormAttachment(spinAdj, 5, SWT.CENTER);
        spinLabel.setLayoutData(dLabel);

        FormData dSpin = new FormData(); // bind to label and text
        dSpin.left = new FormAttachment(spinLabel, 5);
        dSpin.top = new FormAttachment(propertyCombo, 5, SWT.CENTER);
        spinAdj.setLayoutData(dSpin);

        FormData dChart = new FormData(); // bind to label and text
        dChart.left = new FormAttachment(0, 5);
        dChart.top = new FormAttachment(gisSelected, 10);
        dChart.bottom = new FormAttachment(100, -2);
        dChart.right = new FormAttachment(100, -5);
        chartFrame.setLayoutData(dChart);
    }

    @Override
    public void setFocus() {
    }

    /**
     * <p>
     * Implementation of ReturnableEvaluator Returns necessary MS or sector nodes
     * </p>
     * 
     * @author Cinkel_A
     * @since 1.1.0
     */
    private static final class PropertyReturnableEvalvator implements ReturnableEvaluator {
        @Override
        public boolean isReturnableNode(TraversalPosition traversalposition) {
            Node curNode = traversalposition.currentNode();
            Object type = curNode.getProperty(INeoConstants.PROPERTY_TYPE_NAME, null);
            return type != null && (INeoConstants.HEADER_MS.equals(type.toString()) || "sector".equals(type.toString()));
        }
    }

    /**
     * <p>
     * Implementation of CategoryDataset Only for mapping. Does not support complete functionality.
     * </p>
     * 
     * @author Cinkel_A
     * @since 1.1.0
     */
    private static class PropertyCategoryDataset extends AbstractDataset implements CategoryDataset {

        /** long serialVersionUID field */
        private static final long serialVersionUID = -1941659139984700171L;

        private Node aggrNode;
        private List<String> rowList = new ArrayList<String>();
        private List<ChartNode> nodeList = Collections.synchronizedList(new LinkedList<ChartNode>());

        PropertyCategoryDataset() {
            super();
            rowList = new ArrayList<String>();
            rowList.add(ROW_KEY);
            aggrNode = null;
        }

        /**
         * Gets aggregation node
         * 
         * @return aggregation node
         */
        public Node getAggrNode() {
            return aggrNode;
        }

        /**
         * Sets aggregation node
         * 
         * @param aggrNode new node
         */
        public void setAggrNode(Node aggrNode) {
            this.aggrNode = aggrNode;
            Iterator<Node> iteratorChild = aggrNode.traverse(Order.DEPTH_FIRST, StopEvaluator.END_OF_GRAPH,
                    ReturnableEvaluator.ALL_BUT_START_NODE, NetworkRelationshipTypes.CHILD, Direction.OUTGOING).iterator();
            nodeList.clear();
            while (iteratorChild.hasNext()) {
                Node node = (Node)iteratorChild.next();
                nodeList.add(new ChartNode(node));
            }
            fireDatasetChanged();
        }

        @Override
        public int getColumnIndex(Comparable comparable) {
            return nodeList.indexOf(comparable);
        }

        @Override
        public Comparable getColumnKey(int i) {
            return nodeList.get(i);
        }

        @Override
        public List getColumnKeys() {
            return nodeList;
        }

        @Override
        public int getRowIndex(Comparable comparable) {
            return 0;
        }

        @Override
        public Comparable getRowKey(int i) {
            return ROW_KEY;
        }

        @Override
        public List getRowKeys() {
            return rowList;
        }

        @Override
        public Number getValue(Comparable comparable, Comparable comparable1) {
            if (!(comparable1 instanceof ChartNode)) {
                return 0;
            }
            return ((Number)((ChartNode)comparable1).getNode().getProperty(INeoConstants.PROPERTY_VALUE_NAME)).intValue();
        }

        @Override
        public int getColumnCount() {
            return nodeList.size();
        }

        @Override
        public int getRowCount() {
            return 1;
        }

        @Override
        public Number getValue(int i, int j) {
            return getValue(i, getColumnKey(j));
        }

    }

    /**
     * <p>
     * Wrapper of chart node
     * </p>
     * 
     * @author Cinkel_A
     * @since 1.1.0
     */
    private static class ChartNode implements Comparable {
        private Node node;
        private Integer nodeKey;

        ChartNode(Node aggrNode) {
            node = aggrNode;
            nodeKey = (Integer)aggrNode.getProperty(INeoConstants.PROPERTY_NAME_NAME);
        }

        @Override
        public int compareTo(Object o) {
            ChartNode nodeToCompare = (ChartNode)o;
            return getNodeKey() - nodeToCompare.getNodeKey();
        }

        /**
         * @return Returns the node.
         */
        public Node getNode() {
            return node;
        }

        @Override
        public String toString() {
            return String.valueOf(getNodeKey());
        }

        /**
         * @return Returns the nodeKey.
         */
        public Integer getNodeKey() {
            return nodeKey;
        }
    }

    /**
     * A custom renderer that returns a different color for each items.
     */
    class CustomRenderer extends BarRenderer {

        /**
         * Returns the paint for an item. Overrides the default behaviour inherited from
         * AbstractSeriesRenderer.
         * 
         * @param row the series.
         * @param column the category.
         * @return The item color.
         */
        public Paint getItemPaint(final int row, final int column) {
            ChartNode selColumn = getSelectedColumn();
            if (selColumn == null) {
                return DEFAULT_COLOR;
            } else if (column == dataset.getColumnIndex(selColumn)) {
                return COLOR_SELECTED;
            }
            Integer thisValue = ((ChartNode)dataset.getColumnKey(column)).getNodeKey();
            Integer selectedValue = selColumn.getNodeKey();
            if (Math.abs(thisValue - selectedValue) <= spinAdj.getSelection()) {
                return thisValue > selectedValue ? COLOR_MORE : COLOR_LESS;
            }
            return DEFAULT_COLOR;
        }
    }

    /**
     * updates list of gis nodes
     */
    public void updateGisNode() {
        setSelection(null);
        String[] gisItems = getGisItems();
        gisCombo.setItems(gisItems);
        propertyCombo.setItems(new String[] {});
        chartFrame.setVisible(false);
    }
}