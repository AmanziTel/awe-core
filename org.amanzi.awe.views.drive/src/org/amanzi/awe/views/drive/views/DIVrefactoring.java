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
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.TreeMap;

import net.refractions.udig.ui.PlatformGIS;
import net.refractions.udig.ui.graphics.Glyph;

import org.amanzi.neo.core.INeoConstants;
import org.amanzi.neo.core.enums.GisTypes;
import org.amanzi.neo.core.enums.NetworkRelationshipTypes;
import org.amanzi.neo.core.enums.NodeTypes;
import org.amanzi.neo.core.service.NeoServiceProvider;
import org.amanzi.neo.core.utils.NeoUtils;
import org.amanzi.neo.core.utils.Pair;
import org.amanzi.neo.core.utils.PropertyHeader;
import org.amanzi.neo.index.MultiPropertyIndex;
import org.amanzi.neo.loader.internal.NeoLoaderPlugin;
import org.amanzi.neo.preferences.DataLoadPreferences;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.IPreferenceStore;
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
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DateTime;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Slider;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;
import org.geotools.brewer.color.BrewerPalette;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.LogarithmicAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYBarRenderer;
import org.jfree.data.Range;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.time.Millisecond;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.AbstractIntervalXYDataset;
import org.jfree.data.xy.AbstractXYDataset;
import org.jfree.experimental.chart.swt.ChartComposite;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.ReturnableEvaluator;
import org.neo4j.graphdb.StopEvaluator;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.TraversalPosition;
import org.neo4j.graphdb.Traverser.Order;

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
    private static final long SLIDER_STEP = 1000;// 1 sek
    private static final String CHART_TITLE = "";
    private static final String LOG_LABEL = "Logarithmic counts";
    private static final String PALETTE_LABEL = "Palette";
    protected static final String EVENT = "event_type";
    private static final String ALL_EVENTS = "all events";
    
    /** Color PROPERTY fields */
    private static final Color[] PROPERTY_COLORS = new Color[]{Color.red,Color.black,Color.blue,
                                                               Color.green,Color.magenta,Color.yellow};

    private MultiPropertyIndex<Long> timestampIndex = null;
    private ArrayList<String> eventList;
    
    private LinkedHashMap<String, Node> gisDriveNodes;
    private final TreeMap<String, List<String>> propertyLists = new TreeMap<String, List<String>>();
    private List<String> currentProperies = new ArrayList<String>(0);;
    
    private Combo cDrive;
    private Combo cEvent;
    private Combo cPropertyList;
    private JFreeChart chart;
    private ChartCompositeImpl chartFrame;
    private EventDataset eventDataset;
    private TableViewer table;
    private TableLabelProvider labelProvider;
    private TableContentProvider provider;
    private Slider slider;
    private Composite buttonLine;
    private Button bLeft;
    private Button bLeftHalf;
    private Button bRight;
    private Button bRightHalf;
    private Button bReport;
    private Label lLogarithmic;
    private Button bLogarithmic;
    private Label lPalette;
    private Combo cPalette;
    
    private DateAxis domainAxis;
    private List<LogarithmicAxis> axisLogs;
    private List<ValueAxis> axisNumerics;
    private List<TimeDataset> xydatasets;

    private int currentIndex;
    private Long beginGisTime;
    private Long endGisTime;
    private Long selectedTime;
    private DateTime dateStart;
    private Long dateStartTimestamp;

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
        

        chart = createChart();
        chartFrame = new ChartCompositeImpl(frame, SWT.NONE, chart, true);
        fData = new FormData();
        fData.top = new FormAttachment(child, 2);
        fData.left = new FormAttachment(0, 2);
        fData.right = new FormAttachment(100, -2);
        fData.bottom = new FormAttachment(100, -130);

        chartFrame.setLayoutData(fData);
        
        slider = new Slider(frame, SWT.NONE);
        slider.setValues(MIN_FIELD_WIDTH, 0, 300, 1, 1, 1);
        fData = new FormData();
        fData.left = new FormAttachment(0, 0);
        fData.right = new FormAttachment(100, 0);
        fData.top = new FormAttachment(chartFrame, 2);
        slider.setLayoutData(fData);
        slider.pack();
        table = new TableViewer(frame, SWT.BORDER | SWT.FULL_SELECTION);
        fData = new FormData();
        fData.left = new FormAttachment(0, 0);
        fData.right = new FormAttachment(100, 0);
        fData.top = new FormAttachment(slider, 2);
        fData.bottom = new FormAttachment(100, -30);
        table.getControl().setLayoutData(fData);

        labelProvider = new TableLabelProvider();
        labelProvider.createTableColumn();
        provider = new TableContentProvider();
        table.setContentProvider(provider);

        buttonLine = new Composite(frame, SWT.NONE);
        fData = new FormData();
        fData.left = new FormAttachment(0, 2);
        fData.right = new FormAttachment(100, -2);
        fData.bottom = new FormAttachment(100, -2);
        buttonLine.setLayoutData(fData);
        formLayout = new FormLayout();
        buttonLine.setLayout(formLayout);

        bLeft = new Button(buttonLine, SWT.PUSH);
        bLeft.setText("<<");
        bLeftHalf = new Button(buttonLine, SWT.PUSH);
        bLeftHalf.setText("<");

        bRight = new Button(buttonLine, SWT.PUSH);
        bRight.setText(">>");
        bRightHalf = new Button(buttonLine, SWT.PUSH);
        bRightHalf.setText(">");

        bReport = new Button(buttonLine, SWT.PUSH);
        bReport.setText("Report");
        
        FormData formData = new FormData();
        formData.left = new FormAttachment(0, 5);
        bLeft.setLayoutData(formData);

        formData = new FormData();
        formData.left = new FormAttachment(bLeft, 5);
        bLeftHalf.setLayoutData(formData);

        formData = new FormData();
        formData.right = new FormAttachment(100, -5);
        bRight.setLayoutData(formData);

        formData = new FormData();
        formData.right = new FormAttachment(bRight, -5);
        bRightHalf.setLayoutData(formData);

        lLogarithmic = new Label(buttonLine, SWT.NONE);
        lLogarithmic.setText(LOG_LABEL);
        bLogarithmic = new Button(buttonLine, SWT.CHECK);
        bLogarithmic.setSelection(false);
        lPalette = new Label(buttonLine, SWT.NONE);
        lPalette.setText(PALETTE_LABEL);
        cPalette = new Combo(buttonLine, SWT.DROP_DOWN | SWT.READ_ONLY);
        cPalette.setItems(PlatformGIS.getColorBrewer().getPaletteNames());
        cPalette.select(0);

        FormData dCombo = new FormData();
        dCombo.left = new FormAttachment(bLeftHalf, 10);
        dCombo.top = new FormAttachment(bLeftHalf, 0, SWT.CENTER);
        bLogarithmic.setLayoutData(dCombo);

        FormData dLabel = new FormData();
        dLabel.left = new FormAttachment(bLogarithmic, 2);
        dLabel.top = new FormAttachment(bLogarithmic, 5, SWT.CENTER);
        lLogarithmic.setLayoutData(dLabel);

        dCombo = new FormData();
        dCombo.left = new FormAttachment(lLogarithmic, 10);
        dCombo.top = new FormAttachment(cPalette, 5, SWT.CENTER);
        lPalette.setLayoutData(dCombo);

        dCombo = new FormData();
        dCombo.left = new FormAttachment(lPalette, 2);
        cPalette.setLayoutData(dCombo);

        FormData dReport = new FormData();
        dReport.left = new FormAttachment(cPalette, 2);
        bReport.setLayoutData(dReport);

        setsVisible(false);

        init();
    }

    /**
     * Creates the Chart based on a dataset
     */
    private JFreeChart createChart() {

        XYBarRenderer xyarearenderer = new EventRenderer();
        eventDataset = new EventDataset();
        NumberAxis rangeAxis = new NumberAxis("Events");
        rangeAxis.setVisible(false);
        domainAxis = new DateAxis("Time");
        XYPlot xyplot = new XYPlot(eventDataset, domainAxis, rangeAxis, xyarearenderer);
        
        xydatasets = new ArrayList<TimeDataset>();

        xyplot.setDomainCrosshairVisible(true);
        xyplot.setDomainCrosshairLockedOnData(false);
        xyplot.setRangeCrosshairVisible(false);
        
        JFreeChart jfreechart = new JFreeChart(CHART_TITLE, JFreeChart.DEFAULT_TITLE_FONT, xyplot, true);

        ChartUtilities.applyCurrentTheme(jfreechart);
        jfreechart.getTitle().setVisible(false);
        
        axisNumerics = new ArrayList<ValueAxis>(0);
        axisLogs = new ArrayList<LogarithmicAxis>(0);
        xyplot.getRenderer(0).setSeriesPaint(0, new Color(0, 0, 0, 0));
        for (int i = 0; i < getCurrentPropertyCount(); i++) {
            ValueAxis axisNumeric = xyplot.getRangeAxis(i);
            LogarithmicAxis axisLog = new LogarithmicAxis(axisNumeric.getLabel());
            axisLog.setAllowNegativesFlag(true);
            axisLog.setAutoRange(true);
            
            Color color = getColorForProperty(i);
            axisLog.setTickLabelPaint(color);
            axisLog.setLabelPaint(color);
            axisNumeric.setTickLabelPaint(color);
            axisNumeric.setLabelPaint(color);
            
            axisNumerics.add(axisNumeric);
            axisLogs.add(axisLog);
            xyplot.getRenderer(i).setSeriesPaint(0, color);
        }
        
        return jfreechart;

    }
    /**
     *
     */
    private void init() {
        cDrive.setItems(getDriveItems());
        
        formPropertyList();
        cPropertyList.setItems(propertyLists.keySet().toArray(new String[0]));
        
        initializeIndex(cDrive.getText());
        
        initEvents();

    }
    
    /**
     *
     */
    private void initEvents() {
        Transaction tx = NeoUtils.beginTransaction();
        try {
            Node gis = getGisDriveNode();
            if (gis == null) {
                return;
            }
            currentIndex = cDrive.getSelectionIndex();
            PropertyHeader propertyHeader = new PropertyHeader(gis);
            Collection<String> events = propertyHeader.getEvents();
            eventList = new ArrayList<String>();
            eventList.add(ALL_EVENTS);
            if (events != null) {
                eventList.addAll(events);
            }
            cEvent.setItems(eventList.toArray(new String[0]));
            cEvent.select(0);
            String[] array = propertyHeader.getNumericFields();
            // for(Combo property : cProperties){
            // property.setItems(array);
            // if(array.length > 0){
            // property.select(0);
            // }
            // }
            initializeIndex(cDrive.getText());
            Pair<Long, Long> minMax = NeoUtils.getMinMaxTimeOfDataset(gis, null);
            beginGisTime = minMax.getLeft();
            endGisTime = minMax.getRight();
            selectedTime = beginGisTime;
            slider.setMaximum((int)((endGisTime - beginGisTime) / SLIDER_STEP));
            slider.setSelection(0);
            selectedTime = beginGisTime;
            setBeginTime(beginGisTime);
            chart.getXYPlot().setDomainCrosshairValue(selectedTime);

        } finally {
            tx.finish();
        }
    }

    private Color getColorForProperty(int propNum){
        return PROPERTY_COLORS[propNum];        
    }
    

    /**
     * get iterable of necessary mp nodes
     * 
     * @param root - root
     * @param beginTime - begin time
     * @param length - end time
     * @return
     */
    private Iterable<Node> getNodeIterator(final Long beginTime, final Long length) {
        return timestampIndex.searchTraverser(new Long[] {beginTime}, new Long[] {beginTime + length + 1});
    }
    /**
     * Initialized Timestamp index for dataset
     * 
     * @param datasetName name of dataset
     */
    private void initializeIndex(String datasetName) {
        try {
            timestampIndex = NeoUtils.getTimeIndexProperty(datasetName);
            timestampIndex.initialize(NeoServiceProvider.getProvider().getService(), null);
        } catch (IOException e) {
            throw (RuntimeException)new RuntimeException().initCause(e);
        }
    }
    
    /**
     *Preparing existing property lists for display 
     */
    private void formPropertyList() {
        propertyLists.clear();
        String val = getPreferenceStore().getString(DataLoadPreferences.PROPERY_LISTS);
        String[] lists = val.split(DataLoadPreferences.CRS_DELIMETERS);
        if (lists.length > 1 && lists.length % 2 != 0) {
            displayErrorMessage("Exception while parsing property lists data");
        }
        for(int i = 0;i<lists.length;i++){
            propertyLists.put(lists[i], Arrays.asList(lists[i++].split(",")));
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
        GraphDatabaseService service = NeoServiceProvider.getProvider().getService();
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
                updateEvent();
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
                widgetSelected(e);
            }
        });
        cPropertyList.addSelectionListener(new SelectionListener() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                updatePropertyList();
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
                widgetSelected(e);
            }
        });
    }
    
    /**
     *
     */
    protected void updateEvent() {
        String propertyName = cEvent.getText();
        if (!propertyName.equals(eventDataset.getPropertyName())) {
            eventDataset.propertyName = propertyName;
            eventDataset.update();
            // eventDataset.updateDataset(propertyName, dataset.get(currentIndex), beginTime,
            // sLength.getSelection(), propertyName);
        }
        // TODO should we update map if only event was changed?
        fireEventUpdateChart();
    }

    /**
     *
     */
    private void fireEventUpdateChart() {
    }

    /**
     *
     */
    protected void updatePropertyList() {
        currentProperies = propertyLists.get(cPropertyList.getText());
        if (currentProperies == null) {
            currentProperies = new ArrayList<String>(0);
        }
        updateDatasets();
        updateChart();
    }

    protected void updateDatasets() {
        XYPlot xyplot = chart.getXYPlot();
        for (int i = 1; i <= xydatasets.size(); i++) {
            xyplot.setDataset(i, null);
            xyplot.setRenderer(i, null);
            xyplot.setRangeAxis(i, null);
            xyplot.setRangeAxisLocation(i, null);
        }
        xydatasets.clear();

        // int start = xydatasets.size();
        //        
        // for(int i=start; i<currentPropertyCount;i++){
        // TimeDataset xydataset = new TimeDataset();
        // StandardXYItemRenderer standardxyitemrenderer = new StandardXYItemRenderer();
        // standardxyitemrenderer.setBaseShapesFilled(true);
        // int number = MAX_PROP_COUNT-i;
        // xyplot.setDataset(number,xydataset);
        // xyplot.setRenderer(number, standardxyitemrenderer);
        // NumberAxis numberaxis = new NumberAxis(getPropertyYAxisName(i));
        // numberaxis.setAutoRangeIncludesZero(false);
        // xyplot.setRangeAxis(number, numberaxis);
        // xyplot.setRangeAxisLocation(number, AxisLocation.BOTTOM_OR_LEFT);
        // xyplot.mapDatasetToRangeAxis(number, number);
        // xydatasets.add(xydataset);
        // }
        //        
        // for(int i=start; i<currentPropertyCount; i++){
        // ValueAxis axisNumeric = xyplot.getRangeAxis(MAX_PROP_COUNT-i);
        // LogarithmicAxis axisLog = new LogarithmicAxis(axisNumeric.getLabel());
        // axisLog.setAllowNegativesFlag(true);
        // axisLog.setAutoRange(true);
        //            
        // Color color = getColorForProperty(i);
        // axisLog.setTickLabelPaint(color);
        // axisLog.setLabelPaint(color);
        // axisNumeric.setTickLabelPaint(color);
        // axisNumeric.setLabelPaint(color);
        //            
        // axisNumerics.add(axisNumeric);
        // axisLogs.add(axisLog);
        // xyplot.getRenderer(MAX_PROP_COUNT-i).setSeriesPaint(0, color);
        // }
        //        
        //        
        // for(String property : currentProperies){
        //            
        //            
        //            
        //            
        //            
        // propertyName = cProperties.get(i).getText();
        // TimeDataset xydataset = xydatasets.get(i);
        // if (!propertyName.equals(xydataset.getPropertyName())) {
        // xydataset.updateDataset(propertyName, beginTime, sLength.getSelection(), propertyName);
        // }
        // }
    }

    /**
     *update chart
     */
    private void updateChart() {
        // if(cDrive.getText().isEmpty()){
        // return;
        // }
        // Node gis = getGisDriveNode();
        // String event = cEvent.getText();
        // if (gis == null || event.isEmpty() || hasEmptyProperties()) {
        // setsVisible(false);
        // }
        // chart.getTitle().setVisible(false);
        // // chart.setTitle(CHART_TITLE + " " + NeoUtils.getSimpleNodeName(root, ""));
        // Integer length = sLength.getSelection();
        // Long time = getBeginTime();
        // Date date = new Date(time);
        // domainAxis.setMinimumDate(date);
        // domainAxis.setMaximumDate(new Date(time + length * 1000 * 60));
        //
        // if(xydatasets.size()<currentPropertyCount){
        // addDatasets();
        // }
        //        
        // for(int i=0; i<currentPropertyCount; i++){
        // TimeDataset xydataset = xydatasets.get(i);
        // String property = cProperties.get(i).getText();
        // xydataset.updateDataset(property, time, length, property);
        // }
        // eventDataset.updateDataset(cEvent.getText(), time, length, cEvent.getText());
        // setsVisible(true);
        // fireEventUpdateChart();
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
    /**
     * @return
     */
    private String getPropertyYAxisName(int propNum) {
        return "";
    }
    
    public IPreferenceStore getPreferenceStore() {
        return NeoLoaderPlugin.getDefault().getPreferenceStore();
    }
    
    /**
     * get event color
     * 
     * @param node node
     * @return color
     */
    public Color getEventColor(Node node) {
        String event = node.getProperty(EVENT, ALL_EVENTS).toString();
        int alpha = 0;
        if (ALL_EVENTS.equals(eventDataset.propertyName) || event.equals(eventDataset.propertyName)) {
            alpha = 255;
        }
        int i = eventList.indexOf(event);
        if (i < 0) {
            i = 0;
        }
        BrewerPalette palette = PlatformGIS.getColorBrewer().getPalette(cPalette.getText());
        Color[] colors = palette.getColors(palette.getMaxColors());
        int index = i % colors.length;
        Color color = colors[index];
        return new Color(color.getRed(), color.getGreen(), color.getBlue(), alpha);
    }

    /**
     * get gis node
     * 
     * @return node
     */
    private Node getGisDriveNode() {
        return gisDriveNodes == null ? null : gisDriveNodes.get(cDrive.getText());
    }

    private int getCurrentPropertyCount() {
        return 0;
    }

    /**
     * Sets begin time
     * 
     * @param time - time
     */
    @SuppressWarnings("deprecation")
    private void setBeginTime(Long time) {
        dateStartTimestamp = time;
        Date date = new Date(time);
        dateStart.setHours(date.getHours());
        dateStart.setMinutes(date.getMinutes());
        dateStart.setSeconds(date.getSeconds());
    }

    /**
     * Gets index of crosshair data item
     * 
     * @param xydataset
     * @param crosshair
     * @return index or null
     */
    private Integer getCrosshairIndex(TimeDataset dataset, Number crosshair) {
        return getCrosshairIndex(dataset.collection, crosshair);
    }

    private Integer getCrosshairIndex(TimeSeriesCollection collection, Number crosshair) {
        if (crosshair == null) {
            return null;
        }
        int[] item = collection.getSurroundingItems(0, crosshair.longValue());
        Integer result = null;
        if (item[0] >= 0) {
            result = item[0];
        }
        // else if (item[1] >= 0) {
        // result = item[1];
        // }
        return result;
    }

    /**
     * @param time
     * @return
     */
    public Long getPreviousTime(Long time) {
        XYPlot xyplot = (XYPlot)chart.getPlot();
        ValueAxis valueaxis = xyplot.getDomainAxis();
        Range range = valueaxis.getRange();

        return time == null ? null : (long)Math.max(time - 1000, range.getLowerBound());
    }

    /**
     * @param time
     * @return
     */
    public Long getNextTime(Long time) {
        XYPlot xyplot = (XYPlot)chart.getPlot();
        ValueAxis valueaxis = xyplot.getDomainAxis();
        Range range = valueaxis.getRange();

        return time == null ? null : (long)Math.min(time + 1000, range.getUpperBound());
    }

    @Override
    public void setFocus() {
    }
//    TODO border
   
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
    private class EventDataset extends AbstractIntervalXYDataset {
        /** long serialVersionUID field */
        private static final long serialVersionUID = 1L;
        
        private Long beginTime;
        private Long length;
        private TimeSeries series;
        private TimeSeriesCollection collection;
        private String propertyName;

        /**
         * @return Returns the propertyName.
         */
        public String getPropertyName() {
            return propertyName;
        }

        /**
         * update dataset with new data
         * 
         * @param name - dataset name
         * @param root - root node
         * @param beginTime - begin time
         * @param length - length
         * @param propertyName - property name
         * @param event - event value
         */
        public void updateDataset(String name, Long beginTime, int length, String propertyName) {
            this.beginTime = beginTime;
            this.length = (long)length * 1000 * 60;
            this.propertyName = propertyName;
            collection = new TimeSeriesCollection();
            createSeries(name, propertyName);
            collection.addSeries(series);
            this.fireDatasetChanged();
        }

        /**
         * update dataset
         */
        public void update() {
            if (collection.getSeriesCount() > 0) {
                collection.getSeries(0).setKey(propertyName);
            }
            this.fireDatasetChanged();
        }

        /**
         * constructor
         */
        public EventDataset() {
            super();
            beginTime = null;
            length = null;
            series = null;
            collection = new TimeSeriesCollection();
            propertyName = null;
        }

        /**
         * Create time series
         * 
         * @param name name of serie
         * @param propertyName property name
         */
        protected void createSeries(String name, String propertyName) {
            Transaction tx = NeoUtils.beginTransaction();
            try {
                series = new TimeSeries(name);
                Iterator<Node> nodeIterator = getNodeIterator(beginTime, length).iterator();
                while (nodeIterator.hasNext()) {
                    Node node = nodeIterator.next();
                    Long time = NeoUtils.getNodeTime(node);
                    node = getSubNode(node, propertyName);
                    if (node == null) {
                        continue;
                    }

                    series.addOrUpdate(new Millisecond(new Date(time)), node.getId());
                }
            } finally {
                tx.finish();
            }
        }

        public Node getSubNode(Node node, final String propertyName) {
            Iterator<Node> iterator = node.traverse(Order.DEPTH_FIRST, StopEvaluator.DEPTH_ONE, new ReturnableEvaluator() {

                @Override
                public boolean isReturnableNode(TraversalPosition currentPos) {
                    Node node = currentPos.currentNode();
                    boolean result = node.hasProperty(EVENT);
                    return result;
                }
            }, NetworkRelationshipTypes.CHILD, Direction.OUTGOING).iterator();
            return iterator.hasNext() ? iterator.next() : null;
        }

        @Override
        public int getSeriesCount() {
            return collection.getSeriesCount();
        }

        @Override
        @SuppressWarnings("unchecked")
        public Comparable getSeriesKey(int i) {
            return collection.getSeriesKey(i);
        }

        @Override
        public Number getEndX(int i, int j) {
            return collection.getEndX(i, j);
        }

        @Override
        public Number getEndY(int i, int j) {
            return 1;
        }

        @Override
        public Number getStartX(int i, int j) {
            return collection.getStartX(i, j);
        }

        @Override
        public Number getStartY(int i, int j) {
            return 1;
        }

        @Override
        public int getItemCount(int i) {
            return collection.getItemCount(i);
        }

        @Override
        public Number getX(int i, int j) {
            return collection.getX(i, j);
        }

        @Override
        public Number getY(int i, int j) {
            return 1;
        }
    }

    /**
     * 
     * <p>
     * temporary class for avoid bug: if anchor is set - the crosshair do not change by slider changing 
     *  remove if more correctly way will be found
     * </p>
     * @author Cinkel_A
     * @since 1.0.0
     */
public static class ChartCompositeImpl extends ChartComposite{

    
    public ChartCompositeImpl(Composite frame, int none, JFreeChart chart, boolean b) {
        super(frame,none,chart,b);
    }
    /**
     * drop anchor;
     */
    public void dropAnchor(){
        setAnchor(null);
    }
}
/**
 * <p>
 * Time dataset Now it simple wrapper of TimeSeriesCollection But if cache is not possible need
 * be refactored for use database access
 * </p>
 * 
 * @author Cinkel_A
 * @since 1.0.0
 */
private class TimeDataset extends AbstractXYDataset implements CategoryDataset {

    /** long serialVersionUID field */
    private static final long serialVersionUID = 1L;
    
    private Long beginTime;
    private Long length;
    private TimeSeries series;
    private TimeSeriesCollection collection;
    private String propertyName;

    /**
     * @return Returns the propertyName.
     */
    public String getPropertyName() {
        return propertyName;
    }

    /**
     * update dataset with new data
     * 
     * @param name - dataset name
     * @param root - root node
     * @param beginTime - begin time
     * @param length - length
     * @param propertyName - property name
     * @param event - event value
     */
    public void updateDataset(String name, Long beginTime, int length, String propertyName) {
        this.beginTime = beginTime;
        this.length = (long)length * 1000 * 60;
        this.propertyName = propertyName;
        collection = new TimeSeriesCollection();
        createSeries(name, propertyName);
        collection.addSeries(series);
        this.fireDatasetChanged();
    }

    /**
     * constructor
     */
    public TimeDataset() {
        super();
        beginTime = null;
        length = null;
        series = null;
        collection = new TimeSeriesCollection();
        propertyName = null;
    }

    /**
     * Create time series
     * 
     * @param name name of serie
     * @param propertyName property name
     */
    protected void createSeries(String name, String propertyName) {
        Transaction tx = NeoUtils.beginTransaction();
        try {
            series = new TimeSeries(name);
            Iterator<Node> nodeIterator = getNodeIterator(beginTime, length).iterator();
            while (nodeIterator.hasNext()) {
                Node node = nodeIterator.next();
                Long time = NeoUtils.getNodeTime(node);
                node = getSubNode(node, propertyName);
                if (node == null) {
                    continue;
                }

                series.addOrUpdate(new Millisecond(new Date(time)), node.getId());
            }
        } finally {
            tx.finish();
        }
    }

    /**
     * get necessary subnodes of mp node
     * 
     * @param node - node
     * @param event - event value
     * @param propertyName - property name
     * @return subnode
     */
    public Node getSubNode(Node node, final String propertyName) {
        Iterator<Node> iterator = node.traverse(Order.DEPTH_FIRST, StopEvaluator.DEPTH_ONE, new ReturnableEvaluator() {

            @Override
            public boolean isReturnableNode(TraversalPosition currentPos) {
                Node node = currentPos.currentNode();
                return node.hasProperty(propertyName);
            }
        }, NetworkRelationshipTypes.CHILD, Direction.OUTGOING).iterator();
        return iterator.hasNext() ? iterator.next() : null;
    }


    @Override
    public int getSeriesCount() {
        return collection.getSeriesCount();
    }

    @Override
    @SuppressWarnings("unchecked")
    public Comparable getSeriesKey(int i) {
        return collection.getSeriesKey(i);
    }

    @Override
    public int getItemCount(int i) {
        return collection.getItemCount(i);
    }

    @Override
    public Number getX(int i, int j) {
        return collection.getX(i, j);
    }

    @Override
    public Number getY(int i, int j) {
        return (Number)NeoServiceProvider.getProvider().getService().getNodeById(collection.getY(i, j).longValue())
                .getProperty(propertyName);
    }

    @Override
    public int getColumnIndex(Comparable comparable) {
        return 0;
    }

    @Override
    public Comparable getColumnKey(int i) {
        return null;
    }

    @Override
    public List getColumnKeys() {
        return null;
    }

    @Override
    public int getRowIndex(Comparable comparable) {
        return 0;
    }

    @Override
    public Comparable getRowKey(int i) {
        return null;
    }

    @Override
    public List getRowKeys() {
        return null;
    }

    @Override
    public Number getValue(Comparable comparable, Comparable comparable1) {
        return null;
    }

    @Override
    public int getColumnCount() {
        return 0;
    }

    @Override
    public int getRowCount() {
        return 0;
    }

    @Override
    public Number getValue(int i, int j) {
        return null;
    }

}

    private class TableLabelProvider extends LabelProvider implements ITableLabelProvider {

        private final ArrayList<TableColumn> columns = new ArrayList<TableColumn>();
        /** int DEF_SIZE field */
        protected static final int DEF_SIZE = 150;

        @Override
        public Image getColumnImage(Object element, int columnIndex) {
            NodeWrapper wr = provider.nodeWrapper;
            if (columnIndex == 3 && wr.nEvents.get((Integer)element) != null) {
                Color eventColor = getEventColor(wr.nEvents.get((Integer)element));
                return Glyph.palette(new Color[] {eventColor}).createImage();
            }
            return getImage(element);
        }

        public void refreshTable() {
            Table tabl = table.getTable();
            TableColumn col = getColumn(0);
            col.setText("Time");
            col.setWidth(DEF_SIZE);
            col.setResizable(true);

            for (int i = 1; i <= getCurrentPropertyCount(); i++) {
                col = getColumn(i);
                col.setText(Messages.DriveInquirerView_label_property + (i));
                col.setWidth(DEF_SIZE);
                col.setResizable(true);
            }

            col = getColumn(getCurrentPropertyCount() + 1);
            col.setText("events");
            columns.add(col);
            col.setWidth(DEF_SIZE);
            col.setResizable(true);
            for (int i = getCurrentPropertyCount() + 1; i < getCurrentPropertyCount() + 1; i++) {
                col = getColumn(i);
                col.setText("");
                col.setWidth(DEF_SIZE);
                col.setResizable(true);
            }
            tabl.setHeaderVisible(true);
            tabl.setLinesVisible(true);
            table.setLabelProvider(this);
            table.refresh();
        }

        private TableColumn getColumn(int colNum) {
            if (colNum < columns.size()) {
                return columns.get(colNum);
            }
            TableColumn column = new TableViewerColumn(table, SWT.LEFT).getColumn();
            columns.add(column);
            return column;
        }

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
                col.setText("Time");
                columns.add(col);
                col.setWidth(DEF_SIZE);
                col.setResizable(true);

                for (int i = 0; i < getCurrentPropertyCount(); i++) {
                    column = new TableViewerColumn(table, SWT.LEFT);
                    col = column.getColumn();
                    col.setText(Messages.DriveInquirerView_label_property + (i + 1));
                    columns.add(col);
                    col.setWidth(DEF_SIZE);
                    col.setResizable(true);
                }

                column = new TableViewerColumn(table, SWT.LEFT);
                col = column.getColumn();
                col.setText("events");
                columns.add(col);
                col.setWidth(DEF_SIZE);
                col.setResizable(true);

            }
            tabl.setHeaderVisible(true);
            tabl.setLinesVisible(true);
            table.setLabelProvider(this);
            table.refresh();
        }

        @Override
        public String getColumnText(Object element, int columnIndex) {
            NodeWrapper wr = provider.nodeWrapper;
            int index = (Integer)element;
            SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");
            if (columnIndex == 0) {
                Long time = wr.time.get(index);
                return time == null ? "" : df.format(new Date(time));
            }
            if (columnIndex <= getCurrentPropertyCount() && wr.nProperties.get(columnIndex - 1)[index] != null) {
                return wr.nProperties.get(columnIndex - 1)[index].getProperty(wr.propertyNames.get(columnIndex - 1), "").toString();
            }
            if (columnIndex == getCurrentPropertyCount() + 1) {
                if (wr.nEvents.get(index) != null) {
                    return wr.nEvents.get(index).getProperty(EVENT, "").toString();
                }
            }
            return "";
        }

    }

    /*
     * The content provider class is responsible for providing objects to the view. It can wrap
     * existing objects in adapters or simply return objects as-is. These objects may be sensitive
     * to the current input of the view, or ignore it and always show the same content (like Task
     * List, for example).
     */

    private class TableContentProvider implements IStructuredContentProvider {

        private final NodeWrapper nodeWrapper = new NodeWrapper();

        public TableContentProvider() {
        }

        @Override
        public Object[] getElements(Object inputElement) {
            return new Integer[] {0, 1, 2};
        }

        @Override
        public void dispose() {
        }

        @Override
        public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
            // if (newInput == null) {
            // return;
            // }
            // labelProvider.refreshTable();
            // Double crosshair = ((XYPlot)chart.getPlot()).getDomainCrosshairValue();
            // nodeWrapper.propertyNames.clear();
            // nodeWrapper.propertyNames.addAll(propertyLists.get(cPropertyList.getText()));
            // for(int i=0; i<getCurrentPropertyCount(); i++){
            // nodeWrapper.propertyNames.add(cProperties.get(i).getText());
            // changeName(labelProvider.columns.get(i+1), nodeWrapper.propertyNames.get(i));
            // }
            // nodeWrapper.eventName = cEvent.getText();
            // changeName(labelProvider.columns.get(getCurrentPropertyCount()+1),
            // nodeWrapper.eventName);
            // for (int i = 0; i < 2; i++) {
            // nodeWrapper.nEvents[i] = null;
            // for(int j=0; j<getCurrentPropertyCount();j++){
            // nodeWrapper.nProperties.get(j)[i] = null;
            // }
            // nodeWrapper.time[i] = null;
            // }
            // if (crosshair < 0.1) {
            // return;
            // }
            // nodeWrapper.time.set(1, crosshair.longValue());
            // nodeWrapper.time.set(0,getPreviousTime(nodeWrapper.time.get(1)));
            // nodeWrapper.time.set(2,getNextTime(nodeWrapper.time.get(1)));
            // for(int i=0; i<getCurrentPropertyCount(); i++){
            // fillProperty(crosshair, xydatasets.get(i).collection, nodeWrapper.nProperties.get(i),
            // nodeWrapper.time);
            // }
            // fillProperty(crosshair, eventDataset.collection, nodeWrapper.nEvents,
            // nodeWrapper.time);

        }

        /**
         * @param tableColumn
         * @param name
         */
        private void changeName(TableColumn tableColumn, String name) {
            if (!tableColumn.getText().equals(name)) {
                tableColumn.setText(name);
            }
        }

        /**
         * @param crosshair
         * @param dataset
         * @param nodes
         */
        private void fillProperty(double crosshair, TimeSeriesCollection dataset, Node[] nodes, Long[] time) {
            Integer index1 = getCrosshairIndex(dataset, time[1]);
            if (index1 != null) {
                nodes[1] = NeoUtils.getNodeById(dataset.getSeries(0).getDataItem(index1).getValue().longValue());
                if (index1 > 0) {
                    nodes[0] = NeoUtils.getNodeById(dataset.getSeries(0).getDataItem(index1 - 1).getValue().longValue());
                }
                if (index1 + 1 < dataset.getSeries(0).getItemCount()) {
                    nodes[2] = NeoUtils.getNodeById(dataset.getSeries(0).getDataItem(index1 + 1).getValue().longValue());
                }
            }
        }

    }

    private class NodeWrapper {
        List<String> propertyNames = new ArrayList<String>(getCurrentPropertyCount());
        String eventName;
        List<Long> time = new ArrayList<Long>();
        List<Node[]> nProperties = new ArrayList<Node[]>(getCurrentPropertyCount());
        List<Node> nEvents = new ArrayList<Node>();;
    
    /**
     * 
     */
        public NodeWrapper() {
            for (int i = 0; i < getCurrentPropertyCount(); i++) {
                nProperties.add(new Node[3]);
            }
    }
}

}

