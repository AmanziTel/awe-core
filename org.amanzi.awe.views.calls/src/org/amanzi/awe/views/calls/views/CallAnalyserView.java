package org.amanzi.awe.views.calls.views;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.amanzi.awe.views.calls.AggregateCall;
import org.amanzi.awe.views.calls.CallHandler;
import org.amanzi.awe.views.calls.CallTimePeriods;
import org.amanzi.neo.core.enums.CallProperties;
import org.amanzi.neo.core.enums.DriveTypes;
import org.amanzi.neo.core.enums.GeoNeoRelationshipTypes;
import org.amanzi.neo.core.enums.ProbeCallRelationshipType;
import org.amanzi.neo.core.service.NeoServiceProvider;
import org.amanzi.neo.core.utils.NeoUtils;
import org.amanzi.neo.core.utils.Pair;
import org.amanzi.neo.index.MultiPropertyIndex;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
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
import org.eclipse.swt.widgets.DateTime;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.part.ViewPart;
import org.neo4j.api.core.Direction;
import org.neo4j.api.core.NeoService;
import org.neo4j.api.core.Node;
import org.neo4j.api.core.Relationship;
import org.neo4j.api.core.Transaction;
import org.neo4j.api.core.Traverser;

/**
 * <p>
 * Call Analyser view
 * </p>
 * 
 * @author Cinkel_A
 * @since 1.0.0
 */
public class CallAnalyserView extends ViewPart {

    // row labels
    private static final String LBL_DRIVE = "Drive:";
    private static final String LBL_PROBE = "Probe";
    private static final String LBL_START_TIME = "Start time";
    private static final String LBL_END_TIME = "End time";
    private static final String LBL_PERIOD = "Period";
    // column name
    private static final String COL_PERIOD = "Period";
    private static final String COL_SETUP_FULL = "Setup Full time";
    private static final String COL_FAILURE_COUNT = "Failure count";
    private static final String COL_SUCCESS_COUNT = "Success count";
    private static final String COL_DIRECTION_INCOMING = "Incoming call (count)";
    private static final String COL_DIRECTION_OUTGOING = "Outgoing call (count)";
    private static final String COL_SETUP_MIN = "Setup Min time";
    private static final String COL_SETUP_MAX = "Setup Max time";
    private static final String COL_TERMINATE_FULL = "Terminate Full time";
    private static final String COL_TERMINATE_MIN = "Terminate Min time";
    private static final String COL_TERMINATE_MAX = "Terminate Max time";
    public static final String COL_SETUP_AVG = "Setup Average time";
    public static final String COL_TERMINATE_AVG = "Terminate Average time";
    /**
     * The ID of the view as specified by the extension.
     */
    public static final String ID = "org.amanzi.awe.views.call.views.CallAnalyserView";

    private static final int MIN_FIELD_WIDTH = 50;
    public static final int DEF_SIZE = 100;
    private static final String KEY_ALL = "ALL";
    public static final int MAX_TABLE_LEN = 500;
    private List<ColumnHeaders> columnHeaders = new ArrayList<ColumnHeaders>();
    private LinkedHashMap<String, Node> callDataset = new LinkedHashMap<String, Node>();
    private LinkedHashMap<String, Node> probeCallDataset = new LinkedHashMap<String, Node>();

    private Combo cDrive;

    private TableViewer tableViewer;
    private Combo cProbe;
    private DateTime dateStart;
    private ViewContentProvider provider;
    private ViewLabelProvider labelProvider;
    private Combo cPeriod;
    private DateTime dateEnd;
    private Long beginDriveTime;
    private Long endDriveTime;
    private Long startTime;
    private Long endTime;

    /*
     * The content provider class is responsible for providing objects to the view. It can wrap
     * existing objects in adapters or simply return objects as-is. These objects may be sensitive
     * to the current input of the view, or ignore it and always show the same content (like Task
     * List, for example).
     */

    class ViewContentProvider implements IStructuredContentProvider {
        private List<PeriodWrapper> elements = new ArrayList<PeriodWrapper>();

        public void inputChanged(Viewer v, Object oldInput, Object newInput) {
            elements.clear();
            if (newInput == null || !(newInput instanceof InputWrapper)) {
                return;
            }
            InputWrapper inputWr = (InputWrapper)newInput;
            if (!inputWr.isCorrectInput()) {
                return;
            }
            NeoService service = NeoServiceProvider.getProvider().getService();
            Transaction tx = service.beginTx();
            try {
                String indexPart = inputWr.getIndexName();
                if (inputWr.probe != null) {
                    elements.add(new PeriodWrapper(inputWr.beginTime, inputWr.endTime, indexPart));
                } else {
                    long timeEnd;
                    CallTimePeriods period = inputWr.periods;
                    Long beginTime;
                    timeEnd = period.getFirstTime(beginDriveTime);
                    do {
                        beginTime = timeEnd;
                        timeEnd = Math.min(inputWr.endTime, period.addPeriod(timeEnd));
                        elements.add(new PeriodWrapper(beginTime, timeEnd - 1, indexPart));
                    } while (timeEnd < inputWr.endTime && elements.size() < MAX_TABLE_LEN);
                }
            } finally {
                tx.finish();
            }

        }

        public void dispose() {
        }

        public Object[] getElements(Object parent) {
            return elements.toArray(new PeriodWrapper[0]);
        }
    }

    /**
     * <p>
     * Table Label provider
     * </p>
     * 
     * @author Cinkel_A
     * @since 1.0.0
     */
    class ViewLabelProvider extends LabelProvider implements ITableLabelProvider {

        public String getColumnText(Object obj, int index) {
            if (obj instanceof PeriodWrapper) {
                PeriodWrapper period = (PeriodWrapper)obj;
                period.calculate();
                if (index == 0) {
                    return getTimePeriod(period);
                } else {
                    return columnHeaders.get(index).getValue(period);
                }

            } else {
                return getText(obj);
            }
        }

        public Image getColumnImage(Object obj, int index) {
            return null;
        }

        /**
         *create column of table init label provider of tibleView
         */
        public void createTableColumn() {
            Table tabl = tableViewer.getTable();
            TableViewerColumn column;
            TableColumn col;

            if (columnHeaders.isEmpty()) {
                column = new TableViewerColumn(tableViewer, SWT.LEFT);
                col = column.getColumn();
                col.setText(COL_PERIOD);
                columnHeaders.add(new ColumnHeaders(col, null, null));
                col.setWidth(DEF_SIZE);
                // TODO move creation of group of single property in one method
                //
                column = new TableViewerColumn(tableViewer, SWT.LEFT);
                col = column.getColumn();
                col.setText(COL_SUCCESS_COUNT);
                columnHeaders.add(new ColumnHeaders(col, CallProperties.CALL_TYPE, CallProperties.CallType.SUCCESS.toString()));
                col.setWidth(DEF_SIZE);
                //
                column = new TableViewerColumn(tableViewer, SWT.LEFT);
                col = column.getColumn();
                col.setText(COL_FAILURE_COUNT);
                columnHeaders.add(new ColumnHeaders(col, CallProperties.CALL_TYPE, CallProperties.CallType.FAILURE.toString()));
                col.setWidth(DEF_SIZE);
                //
                column = new TableViewerColumn(tableViewer, SWT.LEFT);
                col = column.getColumn();
                col.setText(COL_DIRECTION_INCOMING);
                columnHeaders.add(new ColumnHeaders(col, CallProperties.CALL_DIRECTION, CallProperties.CallDirection.INCOMING
                        .toString()));
                col.setWidth(DEF_SIZE);
                //
                column = new TableViewerColumn(tableViewer, SWT.LEFT);
                col = column.getColumn();
                col.setText(COL_DIRECTION_OUTGOING);
                columnHeaders.add(new ColumnHeaders(col, CallProperties.CALL_DIRECTION, CallProperties.CallDirection.OUTGOING
                        .toString()));
                col.setWidth(DEF_SIZE);
                //
                column = new TableViewerColumn(tableViewer, SWT.LEFT);
                col = column.getColumn();
                col.setText(COL_SETUP_FULL);
                columnHeaders.add(new ColumnHeaders(col, CallProperties.SETUP_DURATION, AggregateCall.SUM));
                col.setWidth(DEF_SIZE);
                //
                column = new TableViewerColumn(tableViewer, SWT.LEFT);
                col = column.getColumn();
                col.setText(COL_SETUP_AVG);
                columnHeaders.add(new ColumnHeaders(col, CallProperties.SETUP_DURATION, AggregateCall.AVERAGE));
                col.setWidth(DEF_SIZE);
                //
                column = new TableViewerColumn(tableViewer, SWT.LEFT);
                col = column.getColumn();
                col.setText(COL_SETUP_MIN);
                columnHeaders.add(new ColumnHeaders(col, CallProperties.SETUP_DURATION, AggregateCall.MIN));
                col.setWidth(DEF_SIZE);
                //
                column = new TableViewerColumn(tableViewer, SWT.LEFT);
                col = column.getColumn();
                col.setText(COL_SETUP_MAX);
                columnHeaders.add(new ColumnHeaders(col, CallProperties.SETUP_DURATION, AggregateCall.MAX));
                col.setWidth(DEF_SIZE);
                //
                column = new TableViewerColumn(tableViewer, SWT.LEFT);
                col = column.getColumn();
                col.setText(COL_TERMINATE_FULL);
                columnHeaders.add(new ColumnHeaders(col, CallProperties.TERMINATION_DURATION, AggregateCall.SUM));
                col.setWidth(DEF_SIZE);
                //
                column = new TableViewerColumn(tableViewer, SWT.LEFT);
                col = column.getColumn();
                col.setText(COL_TERMINATE_AVG);
                columnHeaders.add(new ColumnHeaders(col, CallProperties.TERMINATION_DURATION, AggregateCall.AVERAGE));
                col.setWidth(DEF_SIZE);
                //

                column = new TableViewerColumn(tableViewer, SWT.LEFT);
                col = column.getColumn();
                col.setText(COL_TERMINATE_MIN);
                columnHeaders.add(new ColumnHeaders(col, CallProperties.TERMINATION_DURATION, AggregateCall.MIN));
                col.setWidth(DEF_SIZE);
                //
                column = new TableViewerColumn(tableViewer, SWT.LEFT);
                col = column.getColumn();
                col.setText(COL_TERMINATE_MAX);
                columnHeaders.add(new ColumnHeaders(col, CallProperties.TERMINATION_DURATION, AggregateCall.MAX));
                col.setWidth(DEF_SIZE);
            }
            tabl.setHeaderVisible(true);
            tabl.setLinesVisible(true);
            tableViewer.setLabelProvider(this);
            tableViewer.refresh();
        }
    }

    /**
     * Gets format string of time period
     * 
     * @param period - period
     * @return String
     */
    public String getTimePeriod(PeriodWrapper period) {
        Long begin = period.beginTime;
        GregorianCalendar clBegin = new GregorianCalendar();
        clBegin.setTimeInMillis(begin);
        Long end = period.endTime;
        GregorianCalendar clEnd = new GregorianCalendar();
        clEnd.setTimeInMillis(end);
        Long len = end - begin;
        if (startTime.equals(begin) && endTime.equals(end)) {
            return "Selected time";
        }
        if (len <= 1 * 60 * 1000) {
            return String.format("%s-%s-%s:%s:%s", clBegin.get(Calendar.YEAR), clBegin.get(Calendar.MONTH), clBegin
                    .get(Calendar.DAY_OF_MONTH), clBegin.get(Calendar.HOUR_OF_DAY), clBegin.get(Calendar.MINUTE)/*
                                                                                                                 * ,
                                                                                                                 * clEnd
                                                                                                                 * .
                                                                                                                 * get
                                                                                                                 * (
                                                                                                                 * Calendar
                                                                                                                 * .
                                                                                                                 * MINUTE
                                                                                                                 * )
                                                                                                                 */);
        } else if (len <= 1 * 60 * 60 * 1000) {
            return String.format("%s-%s-%s:%s", clBegin.get(Calendar.YEAR), clBegin.get(Calendar.MONTH), clBegin
                    .get(Calendar.DAY_OF_MONTH), clBegin.get(Calendar.HOUR_OF_DAY));
        } else {
            return String.format("%s-%s-%s", clBegin.get(Calendar.YEAR), clBegin.get(Calendar.MONTH), clBegin
                    .get(Calendar.DAY_OF_MONTH));
        }
    }

    /**
     * This is a callback that will allow us to create the viewer and initialize it.
     */
    public void createPartControl(Composite parent) {
        Composite frame = new Composite(parent, SWT.FILL);
        FormLayout formLayout = new FormLayout();
        formLayout.marginHeight = 0;
        formLayout.marginWidth = 0;
        formLayout.spacing = 0;
        frame.setLayout(formLayout);
        // create row composite
        Composite rowComposite = new Composite(frame, SWT.FILL);
        FormData fData = new FormData();
        fData.top = new FormAttachment(0, 2);
        fData.left = new FormAttachment(0, 2);
        fData.right = new FormAttachment(100, -2);
        rowComposite.setLayoutData(fData);
        GridLayout layout = new GridLayout(10, false);
        rowComposite.setLayout(layout);
        // ------ fill row
        // drive
        Label label = new Label(rowComposite, SWT.FLAT);
        label.setText(LBL_DRIVE);
        label.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
        cDrive = new Combo(rowComposite, SWT.DROP_DOWN | SWT.READ_ONLY);
        GridData layoutData = new GridData(SWT.FILL, SWT.CENTER, true, false);
        layoutData.minimumWidth = MIN_FIELD_WIDTH;
        cDrive.setLayoutData(layoutData);
        // probe
        label = new Label(rowComposite, SWT.FLAT);
        label.setText(LBL_PROBE);
        label.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
        cProbe = new Combo(rowComposite, SWT.DROP_DOWN | SWT.READ_ONLY);
        layoutData = new GridData(SWT.FILL, SWT.CENTER, true, false);
        layoutData.minimumWidth = MIN_FIELD_WIDTH;
        cProbe.setLayoutData(layoutData);
        // Start time
        label = new Label(rowComposite, SWT.FLAT);
        label.setText(LBL_START_TIME);
        label.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
        dateStart = new DateTime(rowComposite, SWT.FILL | SWT.BORDER | SWT.TIME | SWT.LONG);
        GridData dateStartlayoutData = new GridData(SWT.FILL, SWT.CENTER, true, false);
        dateStartlayoutData.minimumWidth = 75;
        dateStart.setLayoutData(dateStartlayoutData);

        // end time
        label = new Label(rowComposite, SWT.FLAT);
        label.setText(LBL_END_TIME);
        label.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
        dateEnd = new DateTime(rowComposite, SWT.FILL | SWT.BORDER | SWT.TIME | SWT.LONG);
        dateStartlayoutData = new GridData(SWT.FILL, SWT.CENTER, true, false);
        dateStartlayoutData.minimumWidth = 75;
        dateEnd.setLayoutData(dateStartlayoutData);

        // Period
        label = new Label(rowComposite, SWT.FLAT);
        label.setText(LBL_PERIOD);
        label.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
        cPeriod = new Combo(rowComposite, SWT.DROP_DOWN | SWT.READ_ONLY);
        layoutData = new GridData(SWT.FILL, SWT.CENTER, true, false);
        layoutData.minimumWidth = MIN_FIELD_WIDTH;
        cPeriod.setLayoutData(layoutData);
        // ------- table
        tableViewer = new TableViewer(frame, SWT.BORDER | SWT.FULL_SELECTION);
        fData = new FormData();
        fData.left = new FormAttachment(0, 0);
        fData.right = new FormAttachment(100, 0);
        fData.top = new FormAttachment(rowComposite, 2);
        fData.bottom = new FormAttachment(100, -2);
        tableViewer.getControl().setLayoutData(fData);
        hookContextMenu();
        addListeners();
        initialize();
    }

    /**
     * initialize startup parameters
     */
    private void initialize() {
        labelProvider = new ViewLabelProvider();
        labelProvider.createTableColumn();
        provider = new ViewContentProvider();
        tableViewer.setContentProvider(provider);
        tableViewer.setInput(0);
        formPeriods();
        formCallDataset();
    }

    /**
     *forms period list
     */
    private void formPeriods() {
        cPeriod.setItems(new String[] {CallTimePeriods.HOURLY.getId(), CallTimePeriods.DAILY.getId(),
                CallTimePeriods.WEEKLY.getId(), CallTimePeriods.MONTHLY.getId()});
        cPeriod.select(0);
    }

    /**
     *form call dataset list
     */
    private void formCallDataset() {
        callDataset.clear();
        callDataset = NeoUtils.getAllDatasetNodesByType(DriveTypes.AMS_CALLS, NeoServiceProvider.getProvider().getService());
        cDrive.setItems(callDataset.keySet().toArray(new String[0]));

    }

    /**
     *add listeners
     */
    private void addListeners() {
        cDrive.addSelectionListener(new SelectionListener() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                formPropertyList();
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
                widgetSelected(e);
            }
        });
        cProbe.addSelectionListener(new SelectionListener() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                changeProbe();
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
                widgetSelected(e);
            }
        });
        cPeriod.addSelectionListener(new SelectionListener() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                changePeriod();
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
                widgetSelected(e);
            }
        });
        dateStart.addFocusListener(new FocusListener() {

            @Override
            public void focusLost(FocusEvent e) {
                changeDate();
            }

            @Override
            public void focusGained(FocusEvent e) {
            }
        });
        dateStart.addKeyListener(new KeyListener() {

            @Override
            public void keyReleased(KeyEvent e) {
                if (e.keyCode == '\r' || e.keyCode == SWT.KEYPAD_CR) {
                    changeDate();
                }
            }

            @Override
            public void keyPressed(KeyEvent e) {
            }
        });
        dateStart.addFocusListener(new FocusListener() {

            @Override
            public void focusLost(FocusEvent e) {
                changeDate();
            }

            @Override
            public void focusGained(FocusEvent e) {
            }
        });
        dateEnd.addKeyListener(new KeyListener() {

            @Override
            public void keyReleased(KeyEvent e) {
                if (e.keyCode == '\r' || e.keyCode == SWT.KEYPAD_CR) {
                    changeDate();
                }
            }

            @Override
            public void keyPressed(KeyEvent e) {
            }
        });
    }

    /**
     *change start or end time
     */
    protected void changeDate() {
        updateTable();
    }

    /**
     *change period
     */
    protected void changePeriod() {
        updateTable();
    }

    /**
     *change probe
     */
    protected void changeProbe() {
        Node probe = probeCallDataset.get(cProbe.getText());
        cPeriod.setEnabled(probe == null);
        updateTable();
    }

    /**
     *update table if has correct InputWrapper
     */
    private void updateTable() {
        InputWrapper wrapper = createInputWrapper();
        if (wrapper.isCorrectInput()) {
            tableViewer.setInput(wrapper);
        }
    }

    /**
     * create InputWrapper depends of user choices
     * 
     * @return InputWrapper
     */
    private InputWrapper createInputWrapper() {
        return new InputWrapper(startTime, endTime, probeCallDataset.get(cProbe.getText()), callDataset.get(cDrive.getText()),
                CallTimePeriods.findById(cPeriod.getText()));
    }

    /**
     *forms property list depends of selected dataset
     */
    protected void formPropertyList() {
        Node drive = callDataset.get(cDrive.getText());
        formProbeCall(drive);

    }

    /**
     * forms call probe depends of dataset
     * 
     * @param drive - drive dataset
     */
    private void formProbeCall(Node drive) {
        probeCallDataset.clear();
        if (drive != null) {
            probeCallDataset.put(KEY_ALL, null);
            Pair<Long, Long> minMax;
            NeoService service = NeoServiceProvider.getProvider().getService();
            Transaction tx = service.beginTx();
            try {
                for (Relationship relation : drive.getRelationships(ProbeCallRelationshipType.PROBE_DATASET, Direction.OUTGOING)) {
                    Node probeCall = relation.getOtherNode(drive);
                    probeCallDataset.put(NeoUtils.getNodeName(probeCall), probeCall);
                }
                minMax = NeoUtils.getMinMaxTimeOfDataset(drive.getSingleRelationship(GeoNeoRelationshipTypes.NEXT,
                        Direction.INCOMING).getOtherNode(drive), service);
            } finally {
                tx.finish();
            }
            beginDriveTime = minMax.getLeft();
            endDriveTime = minMax.getRight();
            setStartTime(beginDriveTime);
            setEndTime(endDriveTime);

        }
        cProbe.setItems(probeCallDataset.keySet().toArray(new String[0]));
    }

    /**
     * Sets end time
     * 
     * @param end - end time
     */
    private void setEndTime(Long end) {
        endTime = Math.min(endDriveTime, end);
        Date date = new Date(endTime);
        dateEnd.setHours(date.getHours());
        dateEnd.setMinutes(date.getMinutes());
        dateEnd.setSeconds(date.getSeconds());
    }

    /**
     * Sets start time
     * 
     * @param start - start time
     */
    private void setStartTime(Long start) {
        startTime = Math.max(beginDriveTime, start);
        Date date = new Date(startTime);
        dateStart.setHours(date.getHours());
        dateStart.setMinutes(date.getMinutes());
        dateStart.setSeconds(date.getSeconds());
    }

    // TODO implement if necessary
    private void hookContextMenu() {
        // MenuManager menuMgr = new MenuManager("#PopupMenu");
        // menuMgr.setRemoveAllWhenShown(true);
        // menuMgr.addMenuListener(new IMenuListener() {
        // public void menuAboutToShow(IMenuManager manager) {
        // CallAnalyserView.this.fillContextMenu(manager);
        // }
        // });
        // Menu menu = menuMgr.createContextMenu(viewer.getControl());
        // viewer.getControl().setMenu(menu);
        // getSite().registerContextMenu(menuMgr, viewer);
    }

    private void fillContextMenu(IMenuManager manager) {

    }

    /**
     * Passing the focus request to the viewer's control.
     */
    public void setFocus() {
        tableViewer.getControl().setFocus();
    }

    /**
     * <p>
     * Column header - contains information about columns
     * </p>
     * 
     * @author Cinkel_A
     * @since 1.0.0
     */
    private class ColumnHeaders {
        private final AggregateCall aggregateType;
        private final CallProperties property;
        private TableColumn column;
        private Object countProperties;

        /**
         * constructor
         * 
         * @param column - TableColumn
         * @param property - handled property
         * @param aggregateType aggregation type
         */
        public ColumnHeaders(TableColumn column, CallProperties property, AggregateCall aggregateType) {
            this.column = column;
            this.property = property;
            this.aggregateType = aggregateType;
            countProperties = null;
        }

        /**
         * constructor - only for string properties property.needMappedCount() must be true
         * 
         * @param column TableColumn
         * @param property handled property
         * @param properties - property value
         */
        public ColumnHeaders(TableColumn column, CallProperties property, Object properties) {
            this(column, property, AggregateCall.COUNT);
            this.countProperties = properties;
            if (!property.needMappedCount()) {
                // TODO add description
                throw new IllegalArgumentException();
            }

        }

        /**
         * get value depends PeriodWrapper
         * 
         * @param wr - PeriodWrapper
         * @return statistic value
         */
        public String getValue(PeriodWrapper wr) {
            if (property == null || aggregateType == null) {
                return getTimePeriod(wr);
            } else {
                CallHandler handler = wr.getHandler(property);
                if (handler != null) {
                    if (countProperties != null) {
                        return String.valueOf(handler.getMappedCount(countProperties));
                    } else {
                        return String.valueOf(handler.getAggregateValue(aggregateType));
                    }
                }
                return "";
            }
        }
    }

    /**
     * <p>
     * Period wrapper contains information about calculated period
     * </p>
     * 
     * @author Cinkel_A
     * @since 1.0.0
     */
    private static class PeriodWrapper {
        private final Object monitor = new Object();
        private boolean calculated = false;
        private long beginTime;
        private long endTime;
        private Map<CallProperties, CallHandler> handlers = new HashMap<CallProperties, CallHandler>();
        private final String indexPartName;

        /**
         * Constructor
         * 
         * @param beginTime - begin time
         * @param endTime - end time
         * @param indexPartName - index name
         */
        public PeriodWrapper(long beginTime, long endTime, String indexPartName) {
            super();
            this.beginTime = beginTime;
            this.endTime = endTime;
            this.indexPartName = indexPartName;
            initHandlers();
        }

        /**
         * initialize handlers
         */
        private void initHandlers() {
            handlers.clear();
            for (CallProperties properties : CallProperties.values()) {
                if (properties.isAnalysed()) {
                    CallHandler handler = new CallHandler(properties);
                    handlers.put(properties, handler);
                }
            }
        }

        /**
         *calculate statistic
         */
        public void calculate() {
            if (!calculated) {
                synchronized (monitor) {
                    if (!calculated) {
                        NeoService service = NeoServiceProvider.getProvider().getService();
                        Transaction tx = service.beginTx();
                        try {
                            MultiPropertyIndex<Long> index = NeoUtils.getTimeIndexProperty(indexPartName);
                            index.initialize(service, null);
                            Traverser traverser = index.searchTraverser(new Long[] {beginTime}, new Long[] {endTime});
                            for (Node node : traverser) {
                                for (CallHandler handler : handlers.values()) {
                                    handler.analyseNode(node);
                                }
                            }
                        } catch (Exception e) {
                            throw (RuntimeException)new RuntimeException().initCause(e);
                        } finally {
                            tx.finish();
                        }
                        calculated = true;
                    }
                }
            }
        }

        /**
         * get CallHandler
         * 
         * @param property - property
         * @return
         */
        public CallHandler getHandler(CallProperties property) {
            return handlers.get(property);
        }
    }

    /**
     * <p>
     * InputWrapper contains information about input
     * </p>
     * 
     * @author Cinkel_A
     * @since 1.0.0
     */
    public static class InputWrapper {
        private long beginTime;
        private long endTime;
        private Node probe;
        private Node drive;
        private CallTimePeriods periods;

        /**
         * constructor
         * 
         * @param beginTime - start time
         * @param endTime - end time
         * @param probe - probe call node
         * @param drive - call dataset node
         * @param periods - periods
         */
        public InputWrapper(long beginTime, long endTime, Node probe, Node drive, CallTimePeriods periods) {
            super();
            this.beginTime = beginTime;
            this.endTime = endTime;
            this.probe = probe;
            this.drive = drive;
            this.periods = periods;
        }

        /**
         * get index name
         * 
         * @return
         */
        public String getIndexName() {
            return NeoUtils.getNodeName(probe != null ? probe : drive);
        }

        /**
         * check
         * 
         * @return true if InputWrapper contains correct information
         */
        public boolean isCorrectInput() {
            return beginTime <= endTime && drive != null && (probe != null || periods != null);
        }

    }

}