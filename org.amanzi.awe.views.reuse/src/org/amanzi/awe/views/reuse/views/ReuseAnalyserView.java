package org.amanzi.awe.views.reuse.views;

import java.awt.Color;
import java.awt.Paint;
import java.awt.TextField;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import net.refractions.udig.catalog.IGeoResource;
import net.refractions.udig.project.ILayer;
import net.refractions.udig.project.IMap;
import net.refractions.udig.project.ui.ApplicationGIS;

import org.amanzi.awe.catalog.neo.GeoNeo;
import org.amanzi.awe.views.reuse.Distribute;
import org.amanzi.awe.views.reuse.Select;
import org.amanzi.neo.core.INeoConstants;
import org.amanzi.neo.core.enums.GeoNeoRelationshipTypes;
import org.amanzi.neo.core.enums.GisTypes;
import org.amanzi.neo.core.enums.MeasurementRelationshipTypes;
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
import org.neo4j.api.core.Transaction;
import org.neo4j.api.core.TraversalPosition;
import org.neo4j.api.core.Traverser;
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
    private Label lSelect;
    private Combo cSelect;
    private Label lDistribute;
    private Combo cDistribute;
    private HashMap<String, Node> members;
    protected ArrayList<String> propertyList;
    private Spinner spinAdj;
    private Label spinLabel;
    private ChartComposite chartFrame;
    private JFreeChart chart;
    private PropertyCategoryDataset dataset;
    private Node selectedGisNode = null;
    private ChartNode selectedColumn = null;
    private Object propertyValue;
    private static final Paint DEFAULT_COLOR = new Color(0.75f,0.7f,0.4f);
    private static final Paint COLOR_SELECTED = Color.RED;
    private static final Paint COLOR_LESS = Color.BLUE;
    private static final Paint COLOR_MORE = Color.GREEN;
    private static final Paint CHART_BACKGROUND = Color.WHITE;
    private static final Paint PLOT_BACKGROUND = new Color(230, 230, 230);
    private static final String SELECT_LABEL = "Select";
    private static final String DISTRIBUTE_LABEL = "Distribute";

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
        lDistribute = new Label(parent, SWT.NONE);
        lDistribute.setText(DISTRIBUTE_LABEL);
        cDistribute = new Combo(parent, SWT.DROP_DOWN | SWT.READ_ONLY);
        cDistribute.setItems(Distribute.getEnumAsStringArray());
        cDistribute.select(0);
        lSelect = new Label(parent, SWT.NONE);
        lSelect.setText(SELECT_LABEL);
        cSelect = new Combo(parent, SWT.DROP_DOWN | SWT.READ_ONLY);
        cSelect.setItems(Select.getEnumAsStringArray());
        cSelect.select(0);
        cSelect.setEnabled(false);
        lSelectedInformation = new TextField();
        spinAdj.addSelectionListener(new SelectionListener() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                if (selectedColumn != null) {
                    setSelection(selectedColumn);
                };
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
            }
        });
        dataset = new PropertyCategoryDataset();
        chart = ChartFactory.createBarChart("SWTBarChart", VALUES_DOMAIN, COUNT_AXIS, dataset, PlotOrientation.VERTICAL, false, false, false);
        CategoryPlot plot = (CategoryPlot)chart.getPlot();
        NumberAxis rangeAxis = (NumberAxis)plot.getRangeAxis();
        rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
        //Craig: Don't bother with a legend when we have only one data type
        //LegendItemCollection legends = new LegendItemCollection();
        //legends.add(new LegendItem(ROW_KEY, defaultColor));
        //plot.setFixedLegendItems(legends);
        CategoryItemRenderer renderer = new CustomRenderer();
        renderer.setBaseToolTipGenerator(new StandardCategoryToolTipGenerator());
        plot.setRenderer(renderer);
        plot.setBackgroundPaint(PLOT_BACKGROUND);
        chart.setBackgroundPaint(CHART_BACKGROUND);
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
                    Node gis = members.get(gisCombo.getText());
                    cSelect
                            .setEnabled(new GeoNeo(NeoServiceProvider.getProvider().getService(), gis).getGisType() == GisTypes.Tems);
                    formPropertyList(gis);
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
                    Node aggrNode = findOrCreateAggregateNode(members.get(gisCombo.getText()), propertyCombo.getText());
                    chartUpdate(aggrNode);
                }
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
                widgetSelected(e);
            }
        };
        SelectionListener selectComboSelectionListener = new SelectionListener() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                if (!chartFrame.isVisible()) {
                    return;
                }
                Node aggrNode = findOrCreateAggregateNode(members.get(gisCombo.getText()), propertyCombo.getText());
                chartUpdate(aggrNode);
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
                widgetSelected(e);
            }
        };
        gisCombo.addSelectionListener(gisComboSelectionListener);
        propertyCombo.addSelectionListener(propComboSelectionListener);
        cSelect.addSelectionListener(selectComboSelectionListener);
        cDistribute.addSelectionListener(selectComboSelectionListener);
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
        chart.fireChartChanged();
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
                            int colInd = columnKey == null ? 0 : dataset.getColumnIndex(columnKey);
                            int minInd = columnKey == null ? 0 : Math.max(colInd - adj, 0);
                            int maxind = columnKey == null ? 0 : Math.min(colInd + adj, dataset.getColumnCount() - 1);
                            geo.setPropertyToRefresh(aggrNode, columnNode,/* adj, */((ChartNode)dataset.getColumnKey(minInd))
                                    .getNode(), ((ChartNode)dataset.getColumnKey(maxind)).getNode());
                            layer.refresh(null);
                        }
                    } catch (IOException e) {
                        throw (RuntimeException)new RuntimeException().initCause(e);
                    }
                }
            }
        }
    }

    /**
     * Finds aggregate node or creates if nod does not exist
     * 
     * @param gisNode GIS node
     * @param propertyName name of property
     * @return necessary aggregates node
     */
    protected Node findOrCreateAggregateNode(Node gisNode, final String propertyName) {
        NeoService service = NeoServiceProvider.getProvider().getService();
        final GisTypes typeOfGis = new GeoNeo(service, gisNode).getGisType();
        final String distribute = cDistribute.getText();
        final String select = cSelect.getText();
        Transaction tx = service.beginTx();
        try {
            Iterator<Node> iterator = gisNode.traverse(Order.DEPTH_FIRST, StopEvaluator.DEPTH_ONE, new ReturnableEvaluator() {

                @Override
                public boolean isReturnableNode(TraversalPosition arg0) {
                    // necessary property name

                    return propertyName.equals(arg0.currentNode().getProperty(INeoConstants.PROPERTY_NAME_NAME, null))
                    // necessary property distribute type
                            && (distribute.equals(arg0.currentNode().getProperty(INeoConstants.PROPERTY_DISTRIBUTE_NAME, null)))
                            // network of necessary select type
                            && (typeOfGis == GisTypes.Network || select.equals(arg0.currentNode().getProperty(
                                    INeoConstants.PROPERTY_SELECT_NAME, null)));
                }
            }, NetworkRelationshipTypes.AGGREGATION, Direction.OUTGOING).iterator();
            if (iterator.hasNext()) {
                tx.success();
                return iterator.next();
            }

            Node result = service.createNode();
            result.setProperty(INeoConstants.PROPERTY_NAME_NAME, propertyName);
            result.setProperty(INeoConstants.PROPERTY_TYPE_NAME, INeoConstants.AGGREGATION_TYPE_NAME);
            result.setProperty(INeoConstants.PROPERTY_DISTRIBUTE_NAME, distribute);
            if (typeOfGis == GisTypes.Tems) {
                result.setProperty(INeoConstants.PROPERTY_SELECT_NAME, select);
            }
            gisNode.createRelationshipTo(result, NetworkRelationshipTypes.AGGREGATION);

            Distribute distributeColumn = Distribute.findEnumByValue(cDistribute.getText());
            TreeMap<Column, Integer> statistics = computeStatistics(gisNode, propertyName, distributeColumn, Select
                    .findSelectByValue(cSelect.getText()));
            Node parentNode = result;
            for (Column key : statistics.keySet()) {
                Node childNode = service.createNode();
                String nameCol;

                BigDecimal minValue = new BigDecimal(key.getMinValue());
                BigDecimal maxValue = new BigDecimal(key.getMinValue() + key.getRange());
                if (distributeColumn == Distribute.INTEGERS) {
                    nameCol = minValue.setScale(0, RoundingMode.HALF_UP).toString();
                } else if (propertyValue instanceof Integer) {
                    minValue = minValue.setScale(0, RoundingMode.HALF_UP);
                    maxValue = maxValue.setScale(0, RoundingMode.HALF_UP);
                    if (maxValue.compareTo(minValue) <= 1) {
                        nameCol = minValue.toString();
                    } else {
                        nameCol = minValue.toString() + "-" + maxValue.toString();
                    }
                } else {
                    minValue = minValue.setScale(2, RoundingMode.HALF_UP);
                    maxValue = maxValue.setScale(2, RoundingMode.HALF_UP);
                    if (key.getRange() == 0) {
                        nameCol = minValue.toString();
                    } else {
                        nameCol = "[" + minValue.toString() + ":" + maxValue.toString() + ")";
                    }
                }
                childNode.setProperty(INeoConstants.PROPERTY_TYPE_NAME, INeoConstants.COUNT_TYPE_NAME);
                childNode.setProperty(INeoConstants.PROPERTY_NAME_NAME, nameCol);
                childNode.setProperty(INeoConstants.PROPERTY_NAME_MIN_VALUE, key.getMinValue());
                childNode.setProperty(INeoConstants.PROPERTY_NAME_MAX_VALUE, key.getMinValue() + key.getRange());
                childNode.setProperty(INeoConstants.PROPERTY_VALUE_NAME, statistics.get(key));
                parentNode.createRelationshipTo(childNode, NetworkRelationshipTypes.CHILD);
                parentNode = childNode;
            }
            tx.success();
            return result;
        } finally {
            tx.finish();
            // fix bug - aggregate data of drive gis node do not save after restart application
            NeoServiceProvider.getProvider().commit();
        }
    }

    /**
     * Collect statistics on the selected property.
     * 
     * @param gisNode GIS node
     * @param propertyName name of property
     * @return
     */
    private TreeMap<Column, Integer> computeStatistics(Node gisNode, String propertyName, Distribute distribute, Select select) {
        Map<Node, Number> mpMap = new HashMap<Node, Number>();
        final GisTypes typeOfGis = new GeoNeo(NeoServiceProvider.getProvider().getService(), gisNode).getGisType();
        TreeMap<Column, Integer> result = new TreeMap<Column, Integer>();
        Traverser travers = gisNode.traverse(Order.BREADTH_FIRST, StopEvaluator.END_OF_GRAPH, new PropertyReturnableEvalvator(),
                NetworkRelationshipTypes.CHILD, Direction.OUTGOING, GeoNeoRelationshipTypes.NEXT, Direction.OUTGOING);

        Double min = null;
        Double max = null;
        propertyValue = null;
        int colCount = 0;
        Collection<Node> trav = travers.getAllNodes();
        for (Node node : trav) {
            if (node.hasProperty(propertyName)) {
                propertyValue = node.getProperty(propertyName);
                Number valueNum = (Number)propertyValue;
                if (typeOfGis == GisTypes.Tems && select != Select.EXISTS) {
                    Node mpNode = node.getSingleRelationship(NetworkRelationshipTypes.CHILD, Direction.INCOMING).getStartNode();
                    Number oldValue = mpMap.get(mpNode);
                    if (oldValue == null) {
                        if (select == Select.AVERAGE) {
                            valueNum = calculateAverageValueOfMpNode(mpNode, propertyName);
                        }
                        mpMap.put(mpNode, valueNum);
                    } else {
                        switch (select) {
                        case MAX:
                            if (oldValue.doubleValue() < valueNum.doubleValue()) {
                                mpMap.put(mpNode, valueNum);
                            }
                            break;
                        case MIN:
                            if (oldValue.doubleValue() > valueNum.doubleValue()) {
                                mpMap.put(mpNode, valueNum);
                            }
                            break;
                        case FIRST:
                            break;
                        default:
                            break;
                        }
                    }

                } else {
                    colCount++;
                    min = min == null ? ((Number)propertyValue).doubleValue() : Math
                            .min(((Number)propertyValue).doubleValue(), min);
                    max = max == null ? ((Number)propertyValue).doubleValue() : Math
                            .max(((Number)propertyValue).doubleValue(), max);
                }
            } else {
                System.out.println("No such property '" + propertyName + "' for node "
                        + (node.hasProperty("name") ? node.getProperty("name").toString() : node.toString()));
            }
        }
        if (typeOfGis == GisTypes.Tems && select != Select.EXISTS) {
            colCount = mpMap.size();
            min = null;
            max = null;
            for (Number value : mpMap.values()) {
                min = min == null ? value.doubleValue() : Math.min(value.doubleValue(), min);
                max = max == null ? value.doubleValue() : Math.max(value.doubleValue(), max);
            }
        }
        double range = 0;
        switch (distribute) {
        case I10:
            range = (max - min) / 9;
            break;
        case I50:
            range = (max - min) / 49;
            break;
        case I20:
            range = (max - min) / 19;
            break;
        case AUTO:
            range = (max - min);
            if (range >= 5 && range <= 30) {
                range = 1;
            } else if (range < 5) {
                if (propertyValue instanceof Integer) {
                    range = 1;
                } else {
                    range = range / 19;
                }
            } else {
                range = range / 19;
            }
            break;
        case INTEGERS:
            min = Math.rint(min) - 0.5;
            max = Math.rint(max) + 0.5;
            range = 1;
            break;
        default:
            break;
        }
        ArrayList<Column> keySet = new ArrayList<Column>();
        double curValue = min;
        while (curValue <= max) {
            keySet.add(new Column(curValue, range));
            curValue += range;
            if (range == 0) {
                break;
            }
        }
        if (typeOfGis == GisTypes.Network || select == Select.EXISTS) {
            for (Node node : trav) {
                if (node.hasProperty(propertyName)) {
                    double value = ((Number)node.getProperty(propertyName)).doubleValue();
                    for (Column column : keySet) {
                        if (column.containsValue(value)) {
                            Integer count = result.get(column);
                            if (count == null) {
                                result.put(column, 1);
                            } else {
                                result.put(column, count + 1);
                            }

                            break;
                        }
                    }
                } else {
                    System.out.println("No such property '" + propertyName + "' for node "
                            + (node.hasProperty("name") ? node.getProperty("name").toString() : node.toString()));
                }
            }
        } else {
            for (Number mpValue : mpMap.values()) {
                double value = mpValue.doubleValue();
                for (Column column : keySet) {
                    if (column.containsValue(value)) {
                        Integer count = result.get(column);
                        if (count == null) {
                            result.put(column, 1);
                        } else {
                            result.put(column, count + 1);
                        }
                        break;
                    }
                }
            }
        }
        return result;
    }

    /**
     * @param mpNode
     * @return
     */
    private static Number calculateAverageValueOfMpNode(Node mpNode, String properties) {
        Double result = new Double(0);
        int count = 0;
        for (Relationship relation : mpNode.getRelationships(MeasurementRelationshipTypes.CHILD, Direction.OUTGOING)) {
            Node node = relation.getEndNode();
            if (INeoConstants.HEADER_MS.equals(node.getProperty(INeoConstants.PROPERTY_TYPE_NAME, ""))) {
                result = result + ((Number)node.getProperty(properties, new Double(0))).doubleValue();
                count++;
            }
        }
        return count == 0 ? 0 : (double)result / (double)count;
    }

    /**
     * <p>
     * Information about column
     * </p>
     * 
     * @author Cinkel_A
     * @since 1.1.0
     */
    private static class Column implements Comparable<Column> {

        private Double minValue;
        private Double range;

        /**
         * Constructor
         * 
         * @param curValue - minimum number which enters into a column
         * @param range - range of column
         */
        public Column(double curValue, double range) {
            minValue = curValue;
            this.range = range;
        }

        /**
         * Returns true if value in [minValue,minValue+range);
         * 
         * @param value
         * @return
         */
        public boolean containsValue(double value) {
            return value >= minValue && (range == 0 || value < minValue + range);
        }

        /**
         * @return Returns the minValue.
         */
        public Double getMinValue() {
            return minValue;
        }

        /**
         * @return Returns the range.
         */
        public Double getRange() {
            return range;
        }

        @Override
        public int compareTo(Column o) {
            return minValue.compareTo(o.minValue);
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((minValue == null) ? 0 : minValue.hashCode());
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            Column other = (Column)obj;
            if (minValue == null) {
                if (other.minValue != null)
                    return false;
            } else if (!minValue.equals(other.minValue))
                return false;
            return true;
        }
        
    }

    /**
     * <p>
     * Wrapper of Node which comparable by it value
     * </p>
     * 
     * @author Cinkel_A
     * @since 1.1.0
     */
    private static class ComapableNode implements Comparable<ComapableNode> {
        private Number value;
        private Node node;

        /**
         * Constructor
         * 
         * @param node node
         * @param value - value of property
         */
        public ComapableNode(Node node, Number value) {
            this.node = node;
            this.value = value;
        }

        @Override
        public int compareTo(ComapableNode o) {
            return Double.compare(value.doubleValue(), o.value.doubleValue());
        }

    }
    /**
     * Creation list of property by selected node
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
        dCombo.right = new FormAttachment(20, -5);
        gisCombo.setLayoutData(dCombo);

        dLabel = new FormData(); // bind to left & text
        dLabel.left = new FormAttachment(gisCombo, 10);
        dLabel.top = new FormAttachment(propertyCombo, 5, SWT.CENTER);
        propertySelected.setLayoutData(dLabel);

        dCombo = new FormData(); // bind to label and text
        dCombo.left = new FormAttachment(propertySelected, 2);
        dCombo.top = new FormAttachment(0, 2);
        dCombo.right = new FormAttachment(50, -5);
        propertyCombo.setLayoutData(dCombo);

        dLabel = new FormData(); // bind to left & text
        dLabel.left = new FormAttachment(propertyCombo, 10);
        dLabel.top = new FormAttachment(cDistribute, 5, SWT.CENTER);
        lDistribute.setLayoutData(dLabel);

        dCombo = new FormData(); // bind to label and text
        dCombo.left = new FormAttachment(lDistribute, 2);
        dCombo.top = new FormAttachment(0, 2);
        dCombo.right = new FormAttachment(68, -5);
        cDistribute.setLayoutData(dCombo);

        dLabel = new FormData(); // bind to left & text
        dLabel.left = new FormAttachment(cDistribute, 10);
        dLabel.top = new FormAttachment(cSelect, 5, SWT.CENTER);
        lSelect.setLayoutData(dLabel);

        dCombo = new FormData(); // bind to label and text
        dCombo.left = new FormAttachment(lSelect, 2);
        dCombo.top = new FormAttachment(0, 2);
        dCombo.right = new FormAttachment(85, -5);
        cSelect.setLayoutData(dCombo);

        dLabel = new FormData(); // bind to left & text
        dLabel.left = new FormAttachment(cSelect, 10);
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
        private Double nodeKey;
        private String columnValue;

        ChartNode(Node aggrNode) {
            node = aggrNode;
            nodeKey = ((Number)aggrNode.getProperty(INeoConstants.PROPERTY_NAME_MIN_VALUE)).doubleValue();
            columnValue = aggrNode.getProperty(INeoConstants.PROPERTY_NAME_NAME, "").toString();
        }

        @Override
        public int compareTo(Object o) {
            ChartNode nodeToCompare = (ChartNode)o;
            return Double.compare(getNodeKey(), nodeToCompare.getNodeKey());
        }

        /**
         * @return Returns the node.
         */
        public Node getNode() {
            return node;
        }

        @Override
        public String toString() {
            return columnValue;
        }

        /**
         * @return Returns the nodeKey.
         */
        public Double getNodeKey() {
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
            }
            int columnIndex = dataset.getColumnIndex(selColumn);
            if (column == columnIndex) {
                return COLOR_SELECTED;
            }
            if (Math.abs(column - columnIndex) <= spinAdj.getSelection()) {
                return column > columnIndex ? COLOR_MORE : COLOR_LESS;
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