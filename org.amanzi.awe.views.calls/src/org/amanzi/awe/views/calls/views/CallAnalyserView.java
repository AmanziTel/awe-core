package org.amanzi.awe.views.calls.views;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.amanzi.awe.views.calls.CallTimePeriods;
import org.amanzi.awe.views.calls.statistics.CallStatistics;
import org.amanzi.awe.views.calls.statistics.CallStatistics.StatisticsHeaders;
import org.amanzi.neo.core.INeoConstants;
import org.amanzi.neo.core.NeoCorePlugin;
import org.amanzi.neo.core.database.nodes.StatisticSelectionNode;
import org.amanzi.neo.core.enums.DriveTypes;
import org.amanzi.neo.core.enums.GeoNeoRelationshipTypes;
import org.amanzi.neo.core.service.NeoServiceProvider;
import org.amanzi.neo.core.utils.NeoUtils;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TableCursor;
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
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;
import org.neo4j.api.core.Direction;
import org.neo4j.api.core.NeoService;
import org.neo4j.api.core.Node;
import org.neo4j.api.core.ReturnableEvaluator;
import org.neo4j.api.core.StopEvaluator;
import org.neo4j.api.core.Transaction;
import org.neo4j.api.core.TraversalPosition;
import org.neo4j.api.core.Traverser;
import org.neo4j.api.core.Traverser.Order;
import org.neo4j.neoclipse.view.NeoGraphViewPart;

/**
 * <p>
 * Call Analyser view
 * </p>
 * 
 * @author Cinkel_A
 * @since 1.0.0
 */
public class CallAnalyserView extends ViewPart {
    /** String NEOGRAPH_ID field */
    private static final String NEOGRAPH_ID = "org.neo4j.neoclipse.view.NeoGraphViewPart";
    /** String DRIVE_ID field */
    private static final String DRIVE_ID = "org.amanzi.awe.views.tree.drive.views.DriveTreeView";
    /** String ERROR_VALUE field */
    private static final String ERROR_VALUE = "ERROR";
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

    private static final int MIN_FIELD_WIDTH = 150;
    public static final int DEF_SIZE = 100;
    private static final String KEY_ALL = "ALL";
    public static final int MAX_TABLE_LEN = 500;
    private List<ColumnHeaders> columnHeaders = new ArrayList<ColumnHeaders>();
    private LinkedHashMap<String, Node> callDataset = new LinkedHashMap<String, Node>();
    private LinkedHashMap<String, Node> probeCallDataset = new LinkedHashMap<String, Node>();

    private Combo cDrive;

    private TableViewer tableViewer;
    private Combo cProbe;
    // private DateTime dateStart;
    private ViewContentProvider provider;
    private ViewLabelProvider labelProvider;
    private Combo cPeriod;
    private TableCursor cursor;

    // private DateTime dateEnd;


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
                for (Node sRow : inputWr.getSrowTraverser(service)) {
                    elements.add(new PeriodWrapper(sRow));

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
                return columnHeaders.get(index).getValue(period);
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
                columnHeaders.add(new ColumnHeaders(col, null));
                col.setWidth(DEF_SIZE);
                // TODO move creation of group of single property in one method
                //
                for (StatisticsHeaders columnHeader : StatisticsHeaders.values()) {
                    column = new TableViewerColumn(tableViewer, SWT.LEFT);
                    col = column.getColumn();
                    col.setText(columnHeader.getTitle());
                    columnHeaders.add(new ColumnHeaders(col, columnHeader));
                    col.setWidth(DEF_SIZE);                   
                }
            }
            tabl.setHeaderVisible(true);
            tabl.setLinesVisible(true);
            tableViewer.setLabelProvider(this);
            tableViewer.refresh();
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
        GridLayout layout = new GridLayout(6, false);
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
        // // Start time
        // label = new Label(rowComposite, SWT.FLAT);
        // label.setText(LBL_START_TIME);
        // label.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
        // dateStart = new DateTime(rowComposite, SWT.FILL | SWT.BORDER | SWT.TIME | SWT.LONG);
        // GridData dateStartlayoutData = new GridData(SWT.FILL, SWT.CENTER, true, false);
        // dateStartlayoutData.minimumWidth = 75;
        // dateStart.setLayoutData(dateStartlayoutData);
        //
        // // end time
        // label = new Label(rowComposite, SWT.FLAT);
        // label.setText(LBL_END_TIME);
        // label.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
        // dateEnd = new DateTime(rowComposite, SWT.FILL | SWT.BORDER | SWT.TIME | SWT.LONG);
        // dateStartlayoutData = new GridData(SWT.FILL, SWT.CENTER, true, false);
        // dateStartlayoutData.minimumWidth = 75;
        // dateEnd.setLayoutData(dateStartlayoutData);

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
        cursor = new TableCursor(tableViewer.getTable(), SWT.DefaultSelection);
        tableViewer.addSelectionChangedListener(new ISelectionChangedListener() {

            @Override
            public void selectionChanged(SelectionChangedEvent event) {
                if (event.getSelection() instanceof IStructuredSelection) {
                    IStructuredSelection selections = (IStructuredSelection)event.getSelection();
                    Object selRow = selections.getFirstElement();
                    if (selRow != null && selRow instanceof PeriodWrapper) {
                        PeriodWrapper wr = (PeriodWrapper)selRow;
                        int columnId = cursor.getColumn();
                        if (columnId == 0) {
                            select(wr.sRow);
                            return;
                        }
                        ColumnHeaders header = columnHeaders.get(columnId);
                        final String nodeName = header.header.getTitle();
                        Node cellNode = null;
                        Transaction tx = NeoServiceProvider.getProvider().getService().beginTx();
                        try {
                            Iterator<Node> iterator = NeoUtils.getChildTraverser(wr.sRow, new ReturnableEvaluator() {

                                @Override
                                public boolean isReturnableNode(TraversalPosition currentPos) {
                                    return NeoUtils.getNodeName(currentPos.currentNode()).equals(nodeName);
                                }
                            }).iterator();
                            cellNode = iterator.hasNext() ? iterator.next() : null;
                        } finally {
                            tx.finish();
                        }
                        if (cellNode != null) {
                            select(cellNode);
                        }
                    }
                }
            }
        });
        hookContextMenu();
        addListeners();
        initialize();
    }

    /**
     * @param sRow
     */
    protected void select(Node node) {
        //TODO refactor
        IViewPart viewNetwork;
        InputWrapper wr = (InputWrapper)tableViewer.getInput();
        StructuredSelection selection = new StructuredSelection(new Object[] {new StatisticSelectionNode(node, wr.periodNode)});
        try {
            viewNetwork = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView(DRIVE_ID);
        } catch (PartInitException e) {
            NeoCorePlugin.error(e.getLocalizedMessage(), e);
            viewNetwork = null;
        }
        try {
            if (viewNetwork != null) {
                Viewer networkView = (Viewer)viewNetwork.getSite().getSelectionProvider();
                networkView.setSelection(selection, true);
                // viewNetwork.setFocus();
            }
        } catch (Exception e1) {
            // TODO Handle Exception
            e1.printStackTrace();
        }
         selection = new StructuredSelection(new Object[] {node});
        try {
            IViewPart viewNeoGraph;
            try {
                viewNeoGraph = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().findView(NEOGRAPH_ID);
            } catch (Exception e) {
                NeoCorePlugin.error(e.getLocalizedMessage(), e);
                viewNeoGraph = null;
            }
            if (viewNeoGraph != null) {
                NeoGraphViewPart view = (NeoGraphViewPart)viewNeoGraph;
                view.showNode(node);
                view.setFocus();
                view.getViewSite().getSelectionProvider().setSelection(selection);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
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
        // formPeriods();
        formCallDataset();
    }

    /**
     *forms period list
     * 
     * @param statistics
     */
    private void formPeriods(CallStatistics statistics) {
        List<String> periods=new ArrayList<String>();
        if (statistics!=null){
            CallTimePeriods[] allPeriods = CallTimePeriods.values();
            for (int i = 0; i <= statistics.getHighPeriod().ordinal(); i++) {
                periods.add(allPeriods[i].getId());
            }
        }
        cPeriod.setItems(periods.toArray(new String[0]));
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
        // dateStart.addFocusListener(new FocusListener() {
        //
        // @Override
        // public void focusLost(FocusEvent e) {
        // changeDate();
        // }
        //
        // @Override
        // public void focusGained(FocusEvent e) {
        // }
        // });
        // dateStart.addKeyListener(new KeyListener() {
        //
        // @Override
        // public void keyReleased(KeyEvent e) {
        // if (e.keyCode == '\r' || e.keyCode == SWT.KEYPAD_CR) {
        // changeDate();
        // }
        // }
        //
        // @Override
        // public void keyPressed(KeyEvent e) {
        // }
        // });
        // dateStart.addFocusListener(new FocusListener() {
        //
        // @Override
        // public void focusLost(FocusEvent e) {
        // changeDate();
        // }
        //
        // @Override
        // public void focusGained(FocusEvent e) {
        // }
        // });
        // dateEnd.addKeyListener(new KeyListener() {
        //
        // @Override
        // public void keyReleased(KeyEvent e) {
        // if (e.keyCode == '\r' || e.keyCode == SWT.KEYPAD_CR) {
        // changeDate();
        // }
        // }
        //
        // @Override
        // public void keyPressed(KeyEvent e) {
        // }
        // });
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
        return new InputWrapper(probeCallDataset.get(cProbe.getText()), callDataset.get(cDrive.getText()),
                CallTimePeriods.findById(cPeriod.getText()));
    }

    /**
     *forms property list depends of selected dataset
     */
    protected void formPropertyList() {
        Node drive = callDataset.get(cDrive.getText());
        if (drive == null) {
            return;
        }
        try {
            CallStatistics statistics = new CallStatistics(drive, NeoServiceProvider.getProvider().getService());
            formProbeCall(drive);
            formPeriods(statistics);
        } catch (IOException e) {
            // TODO Handle IOException
            throw (RuntimeException)new RuntimeException().initCause(e);
        }

    }

    /**
     * forms call probe depends of dataset
     * 
     * @param drive - drive dataset
     */
    private void formProbeCall(Node drive) {
        probeCallDataset.clear();
        if (drive != null) {
            NeoService service = NeoServiceProvider.getProvider().getService();
            Transaction tx = service.beginTx();
            try {
                Collection<Node> allProbesOfDataset = NeoUtils.getAllProbesOfDataset(drive);
                for (Node probe : allProbesOfDataset) {
                    probeCallDataset.put(NeoUtils.getNodeName(probe), probe);
                }
            } finally {
                tx.finish();
            }
        }
        cProbe.setItems(probeCallDataset.keySet().toArray(new String[0]));
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

        private TableColumn column;
        private final StatisticsHeaders header;


        /**
         * constructor - only for string properties property.needMappedCount() must be true
         * 
         * @param column TableColumn
         * @param properties - property value
         */
        public ColumnHeaders(TableColumn column, StatisticsHeaders header) {
            this.column = column;
            this.header = header;
        }

        /**
         * get value depends PeriodWrapper
         * 
         * @param wr - PeriodWrapper
         * @return statistic value
         */
        public String getValue(PeriodWrapper wr) {
            if (header == null) {
                return NeoUtils.getNodeName(wr.sRow);
            } else {
                return wr.getValue(header);
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
        private final Node sRow;
        private Map<StatisticsHeaders, String> mappedValue = new HashMap<StatisticsHeaders, String>();

        /**
         * Constructor
         * 
         * @param beginTime - begin time
         * @param endTime - end time
         * @param indexPartName - index name
         */
        public PeriodWrapper(Node sRow) {
            super();
            this.sRow = sRow;
            mappedValue.clear();
            for (Node node : NeoUtils.getChildTraverser(sRow)) {
                String name = NeoUtils.getNodeName(node);
                StatisticsHeaders header = StatisticsHeaders.findById(name);
                if (header != null) {
                    mappedValue.put(header, node.getProperty(INeoConstants.PROPERTY_VALUE_NAME, ERROR_VALUE).toString());
                }
            }
        }

        /**
         * @param header
         * @return
         */
        public String getValue(StatisticsHeaders header) {
            String string = mappedValue.get(header);
            return string == null ? ERROR_VALUE : string;
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
        private Node probe;
        private Node drive;
        private CallTimePeriods periods;
        private Node periodNode;

        /**
         * constructor
         * 
         * @param probe - probe call node
         * @param drive - call dataset node
         * @param periods - periods
         */
        public InputWrapper(Node probe, Node drive, CallTimePeriods periods) {
            super();
            this.probe = probe;
            this.drive = drive;
            this.periods = periods;
        }

        /**
         * @return
         */
        public Traverser getSrowTraverser(NeoService service) {
             
            try {
                CallStatistics statistic = new CallStatistics(drive, service);
                periodNode = statistic.getPeriodNode(periods);
                if (periodNode==null){
                    return NeoUtils.emptyTraverser(probe);
                }
                return NeoUtils.getChildTraverser(periodNode, new ReturnableEvaluator() {
                    
                    @Override
                    public boolean isReturnableNode(TraversalPosition currentPos) {
                        return currentPos.currentNode().traverse(Order.DEPTH_FIRST, StopEvaluator.DEPTH_ONE, new ReturnableEvaluator() {

                            @Override
                            public boolean isReturnableNode(TraversalPosition currentPos) {
                                return currentPos.currentNode().equals(probe);
                            }
                        }, GeoNeoRelationshipTypes.SOURCE, Direction.OUTGOING).iterator().hasNext();
                    }
                });
            } catch (IOException e) {
                NeoCorePlugin.error(e.getLocalizedMessage(), e);
                return NeoUtils.emptyTraverser(probe);
            } 
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
            return drive != null && probe != null && periods != null;
        }

    }

}