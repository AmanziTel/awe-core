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

import java.awt.Color;
import java.awt.Paint;
import java.awt.Shape;
import java.util.LinkedHashMap;
import java.util.TreeMap;

import org.amanzi.neo.core.INeoConstants;
import org.amanzi.neo.core.enums.GisTypes;
import org.amanzi.neo.core.enums.NodeTypes;
import org.amanzi.neo.core.service.NeoServiceProvider;
import org.amanzi.neo.core.utils.NeoUtils;
import org.amanzi.neo.loader.internal.NeoLoaderPlugin;
import org.amanzi.neo.preferences.DataLoadPreferences;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;
import org.jfree.chart.renderer.xy.XYBarRenderer;
import org.neo4j.api.core.Direction;
import org.neo4j.api.core.NeoService;
import org.neo4j.api.core.Node;
import org.neo4j.api.core.Relationship;
import org.neo4j.api.core.Transaction;

/**
 * TODO Purpose of 
 * <p>
 *  Temp class for refactoring org.amanzi.awe.views.drive.views.DriveInquirerView
 * </p>
 * @author Saelenchits_N
 * @since 1.0.0
 */
public class DIVrefactoring  extends ViewPart{
    private static final int MIN_FIELD_WIDTH = 50;
    
    private LinkedHashMap<String, Node> gisDriveNodes;
    private final TreeMap<String,String> propertyLists = new TreeMap<String,String>();
    
    private Combo cDrive;
    private Combo cEvent;
    private Combo cPropertyList;
//    private JFreeChart chart;
//    private ChartCompositeImpl chartFrame;
//    private Composite addPropBar;

    @Override
    public void createPartControl(Composite parent) {
        Composite frame = new Composite(parent, SWT.FILL);
        FormLayout formLayout = new FormLayout();
        formLayout.marginHeight = 0;
        formLayout.marginWidth = 0;
        formLayout.spacing = 0;
        frame.setLayout(formLayout);
        
        Composite child = new Composite(frame, SWT.FILL);
        FormData fData = new FormData();
        fData.top = new FormAttachment(0, 2);
        fData.left = new FormAttachment(0, 2);
        fData.right = new FormAttachment(100, -2);

        child.setLayoutData(fData);
        final GridLayout layout = new GridLayout(10, false);
        child.setLayout(layout);
        Label label = new Label(child, SWT.FLAT);
        label.setText(Messages.DriveInquirerView_label_drive);
        label.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
        cDrive = new Combo(child, SWT.DROP_DOWN | SWT.READ_ONLY);

        GridData layoutData = new GridData(SWT.FILL, SWT.CENTER, true, false);
        layoutData.minimumWidth = MIN_FIELD_WIDTH;
        cDrive.setLayoutData(layoutData);

        label = new Label(child, SWT.FLAT);
        label.setText(Messages.DriveInquirerView_label_event);
        label.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
        cEvent = new Combo(child, SWT.DROP_DOWN | SWT.READ_ONLY);

        layoutData = new GridData(SWT.FILL, SWT.CENTER, true, false);
        layoutData.minimumWidth = MIN_FIELD_WIDTH;
        cEvent.setLayoutData(layoutData);

        label = new Label(child, SWT.NONE);
        label.setText("Property lists");
        label.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
        cPropertyList = new Combo(child, SWT.DROP_DOWN | SWT.READ_ONLY);
        layoutData = new GridData(SWT.FILL, SWT.CENTER, true, false);
        layoutData.minimumWidth = MIN_FIELD_WIDTH;
        cPropertyList.setLayoutData(layoutData);
        
//        chart = createChart();
//        chartFrame = new ChartCompositeImpl(frame, SWT.NONE, chart, true);
//        fData = new FormData();
//        fData.top = new FormAttachment(addPropBar, 2);
//        fData.left = new FormAttachment(0, 2);
//        fData.right = new FormAttachment(100, -2);
//        fData.bottom = new FormAttachment(100, -130);
//
//        chartFrame.setLayoutData(fData);
        
        
        init();
    }

    /**
     * Creates the Chart based on a dataset
     */
//    private JFreeChart createChart() {
//
//        XYBarRenderer xyarearenderer = new EventRenderer();
//        eventDataset = new EventDataset();
//        NumberAxis rangeAxis = new NumberAxis("Events");
//        rangeAxis.setVisible(false);
//        domainAxis = new DateAxis("Time");
//        XYPlot xyplot = new XYPlot(eventDataset, domainAxis, rangeAxis, xyarearenderer);
//        
//        xydatasets = new ArrayList<TimeDataset>(MAX_PROP_COUNT);
//        for(int i=0; i<currentPropertyCount;i++){
//            TimeDataset xydataset = new TimeDataset();
//            StandardXYItemRenderer standardxyitemrenderer = new StandardXYItemRenderer();
//            standardxyitemrenderer.setBaseShapesFilled(true);
//            int number = MAX_PROP_COUNT-i;
//            xyplot.setDataset(number,xydataset);
//            xyplot.setRenderer(number, standardxyitemrenderer);
//            NumberAxis numberaxis = new NumberAxis(getPropertyYAxisName(i));
//            numberaxis.setAutoRangeIncludesZero(false);
//            xyplot.setRangeAxis(number, numberaxis);
//            xyplot.setRangeAxisLocation(number, AxisLocation.BOTTOM_OR_LEFT);
//            xyplot.mapDatasetToRangeAxis(number, number);
//            xydatasets.add(xydataset);
//        }
//
//        xyplot.setDomainCrosshairVisible(true);
//        xyplot.setDomainCrosshairLockedOnData(false);
//        xyplot.setRangeCrosshairVisible(false);
//        
// 
//        
//        JFreeChart jfreechart = new JFreeChart(CHART_TITLE, JFreeChart.DEFAULT_TITLE_FONT, xyplot, true);
//
//        ChartUtilities.applyCurrentTheme(jfreechart);
//        jfreechart.getTitle().setVisible(false);
//        
//        axisNumerics = new ArrayList<ValueAxis>(MAX_PROP_COUNT);
//        axisLogs = new ArrayList<LogarithmicAxis>(MAX_PROP_COUNT);
//        xyplot.getRenderer(0).setSeriesPaint(0, new Color(0, 0, 0, 0));
//        for(int i=0; i<currentPropertyCount; i++){
//            ValueAxis axisNumeric = xyplot.getRangeAxis(MAX_PROP_COUNT-i);
//            LogarithmicAxis axisLog = new LogarithmicAxis(axisNumeric.getLabel());
//            axisLog.setAllowNegativesFlag(true);
//            axisLog.setAutoRange(true);
//            
//            Color color = getColorForProperty(i);
//            axisLog.setTickLabelPaint(color);
//            axisLog.setLabelPaint(color);
//            axisNumeric.setTickLabelPaint(color);
//            axisNumeric.setLabelPaint(color);
//            
//            axisNumerics.add(axisNumeric);
//            axisLogs.add(axisLog);
//            xyplot.getRenderer(MAX_PROP_COUNT-i).setSeriesPaint(0, color);
//        }
//        
//        return jfreechart;
//
//    }
    /**
     *
     */
    private void init() {
        cDrive.setItems(getDriveItems());
        
        formPropertyList();
        cPropertyList.setItems(propertyLists.keySet().toArray(new String[0]));
        
    }
    
    /**
     *Preparing existing property lists for display 
     */
    private void formPropertyList() {
        propertyLists.clear();
        String val = getPreferenceStore().getString(DataLoadPreferences.PROPERY_LISTS);
        String[] lists = val.split(DataLoadPreferences.CRS_DELIMETERS);
        if(lists.length%2!=0){
            displayErrorMessage("Exception while parsing property lists data");
        }
        for(int i = 0;i<lists.length;i++){
            propertyLists.put(lists[i], lists[i++]);
        }
    }
    /**
     * Displays error message instead of throwing an exception
     * 
     * @param e exception thrown
     */
    private void displayErrorMessage(final String e) {
        final Display display = PlatformUI.getWorkbench().getDisplay();
        display.asyncExec(new Runnable() {

            @Override
            public void run() {
                MessageDialog.openError(display.getActiveShell(), "Operation exception", e);
            }

        });
    }
    
    /**
     * get Drive list
     * 
     * @return String[]
     */
    private String[] getDriveItems() {
        NeoService service = NeoServiceProvider.getProvider().getService();
        Node refNode = service.getReferenceNode();
        gisDriveNodes = new LinkedHashMap<String, Node>();

        Transaction tx = service.beginTx();
        try {
            for (Relationship relationship : refNode.getRelationships(Direction.OUTGOING)) {
                Node node = relationship.getEndNode();
                Object type = node.getProperty(INeoConstants.PROPERTY_GIS_TYPE_NAME, "").toString();
                if (NeoUtils.isGisNode(node) && type.equals(GisTypes.DRIVE.getHeader())||NodeTypes.OSS.checkNode(node)) {
                    String id = NeoUtils.getSimpleNodeName(node, null);
                    gisDriveNodes.put(id, node);
                }
            }

            return gisDriveNodes.keySet().toArray(new String[] {});
        } finally {
            tx.finish();
        }
    }
    
    /**
     *add listeners
     */
    private void addListeners() {
        cDrive.addSelectionListener(new SelectionListener() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                changeDrive();
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
                widgetDefaultSelected(e);
            }
        });
        cEvent.addSelectionListener(new SelectionListener() {

            @Override
            public void widgetSelected(SelectionEvent e) {
//                updateProperty();
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
                widgetSelected(e);
            }
        });
    }
    
    /**
     *change drive dataset
     */
    private void changeDrive() {
        if (cDrive.getSelectionIndex() < 0) {
            setsVisible(false);
        } else {
//            formPropertyLists();
//            updateChart();
        }
    }
    /**
     *update chart
     */
//    private void updateChart() {
//        if(cDrive.getText().isEmpty()){
//            return;
//        }
//        Node gis = getGisDriveNode();
//        String event = cEvent.getText();
//        if (gis == null || event.isEmpty() || hasEmptyProperties()) {
//            setsVisible(false);
//        }
//        chart.getTitle().setVisible(false);
//        // chart.setTitle(CHART_TITLE + " " + NeoUtils.getSimpleNodeName(root, ""));
//        Integer length = sLength.getSelection();
//        Long time = getBeginTime();
//        Date date = new Date(time);
//        domainAxis.setMinimumDate(date);
//        domainAxis.setMaximumDate(new Date(time + length * 1000 * 60));
//
//        if(xydatasets.size()<currentPropertyCount){
//            addDatasets();
//        }
//        
//        for(int i=0; i<currentPropertyCount; i++){
//            TimeDataset xydataset = xydatasets.get(i);
//            String property = cProperties.get(i).getText();
//            xydataset.updateDataset(property, time, length, property);
//        }
//        eventDataset.updateDataset(cEvent.getText(), time, length, cEvent.getText());
//        setsVisible(true);
//        fireEventUpdateChart();
//    }
    /**
     * set chart visible
     * 
     * @param visible - is visible?
     */
    private void setsVisible(boolean visible) {
//        chartFrame.setVisible(visible);
//        table.getControl().setVisible(visible);
//        buttonLine.setVisible(visible);
//        slider.setVisible(visible);
    }

    @Override
    public void setFocus() {
    }
    
   
    /**
     * <p>
     * Event renderer
     * </p>
     * 
     * @author Cinkel_A
     * @since 1.0.0
     */
    private class EventRenderer extends XYBarRenderer {

        /** long serialVersionUID field */
        private static final long serialVersionUID = 1L;

        public EventRenderer() {
            super();
        }

        @Override
        public Shape getItemShape(int row, int column) {
            return super.getItemShape(row, column);
        }

        @Override
        public Paint getItemFillPaint(int row, int column) {
            return super.getItemFillPaint(row, column);
        }

        @Override
        public Paint getItemPaint(int row, int column) {
//            TimeSeriesDataItem item = eventDataset.series.getDataItem(column);
//            Node node = NeoServiceProvider.getProvider().getService().getNodeById(item.getValue().longValue());
//            Color color = getEventColor(node);
            Color color = Color.GREEN;
            return color;
        }

    }
    
    /**
     * <p>
     * Dataset for event
     * </p>
     * 
     * @author Cinkel_A
     * @since 1.0.0
     */
//    private class EventDataset extends AbstractIntervalXYDataset {
//        /** long serialVersionUID field */
//        private static final long serialVersionUID = 1L;
//        
//        private Long beginTime;
//        private Long length;
//        private TimeSeries series;
//        private TimeSeriesCollection collection;
//        private String propertyName;
//
//        /**
//         * @return Returns the propertyName.
//         */
//        public String getPropertyName() {
//            return propertyName;
//        }
//
//        /**
//         * update dataset with new data
//         * 
//         * @param name - dataset name
//         * @param root - root node
//         * @param beginTime - begin time
//         * @param length - length
//         * @param propertyName - property name
//         * @param event - event value
//         */
//        public void updateDataset(String name, Long beginTime, int length, String propertyName) {
//            this.beginTime = beginTime;
//            this.length = (long)length * 1000 * 60;
//            this.propertyName = propertyName;
//            collection = new TimeSeriesCollection();
//            createSeries(name, propertyName);
//            collection.addSeries(series);
//            this.fireDatasetChanged();
//        }
//
//        /**
//         * update dataset
//         */
//        public void update() {
//            if (collection.getSeriesCount() > 0) {
//                collection.getSeries(0).setKey(propertyName);
//            }
//            this.fireDatasetChanged();
//        }
//
//        /**
//         * constructor
//         */
//        public EventDataset() {
//            super();
//            beginTime = null;
//            length = null;
//            series = null;
//            collection = new TimeSeriesCollection();
//            propertyName = null;
//        }
//
//        /**
//         * Create time series
//         * 
//         * @param name name of serie
//         * @param propertyName property name
//         */
//        protected void createSeries(String name, String propertyName) {
//            Transaction tx = NeoUtils.beginTransaction();
//            try {
//                series = new TimeSeries(name);
//                Iterator<Node> nodeIterator = getNodeIterator(beginTime, length).iterator();
//                while (nodeIterator.hasNext()) {
//                    Node node = nodeIterator.next();
//                    Long time = NeoUtils.getNodeTime(node);
//                    node = getSubNode(node, propertyName);
//                    if (node == null) {
//                        continue;
//                    }
//
//                    series.addOrUpdate(new Millisecond(new Date(time)), node.getId());
//                }
//            } finally {
//                tx.finish();
//            }
//        }
//
//        public Node getSubNode(Node node, final String propertyName) {
//            Iterator<Node> iterator = node.traverse(Order.DEPTH_FIRST, StopEvaluator.DEPTH_ONE, new ReturnableEvaluator() {
//
//                @Override
//                public boolean isReturnableNode(TraversalPosition currentPos) {
//                    Node node = currentPos.currentNode();
//                    boolean result = node.hasProperty(EVENT);
//                    return result;
//                }
//            }, NetworkRelationshipTypes.CHILD, Direction.OUTGOING).iterator();
//            return iterator.hasNext() ? iterator.next() : null;
//        }
//
//        @Override
//        public int getSeriesCount() {
//            return collection.getSeriesCount();
//        }
//
//        @Override
//        @SuppressWarnings("unchecked")
//        public Comparable getSeriesKey(int i) {
//            return collection.getSeriesKey(i);
//        }
//
//        @Override
//        public Number getEndX(int i, int j) {
//            return collection.getEndX(i, j);
//        }
//
//        @Override
//        public Number getEndY(int i, int j) {
//            return 1;
//        }
//
//        @Override
//        public Number getStartX(int i, int j) {
//            return collection.getStartX(i, j);
//        }
//
//        @Override
//        public Number getStartY(int i, int j) {
//            return 1;
//        }
//
//        @Override
//        public int getItemCount(int i) {
//            return collection.getItemCount(i);
//        }
//
//        @Override
//        public Number getX(int i, int j) {
//            return collection.getX(i, j);
//        }
//
//        @Override
//        public Number getY(int i, int j) {
//            return 1;
//        }
//    }
    public IPreferenceStore getPreferenceStore() {
        return NeoLoaderPlugin.getDefault().getPreferenceStore();
    }
}

