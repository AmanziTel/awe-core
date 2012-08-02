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

package org.amanzi.awe.statistics.ui.view;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.amanzi.awe.statistics.entities.impl.AggregatedStatistics;
import org.amanzi.awe.statistics.entities.impl.StatisticsCell;
import org.amanzi.awe.statistics.entities.impl.StatisticsGroup;
import org.amanzi.awe.statistics.entities.impl.StatisticsRow;
import org.amanzi.awe.statistics.enumeration.Period;
import org.amanzi.awe.statistics.exceptions.StatisticsException;
import org.amanzi.awe.statistics.manager.StatisticsManager;
import org.amanzi.awe.statistics.ui.Messages;
import org.amanzi.awe.statistics.ui.StatisticsPlugin;
import org.amanzi.awe.statistics.ui.view.table.StatisticsComparator;
import org.amanzi.awe.statistics.ui.view.table.StatisticsContentProvider;
import org.amanzi.awe.statistics.ui.view.table.StatisticsLabelProvider;
import org.amanzi.awe.statistics.ui.view.table.StatisticsRowFilter;
import org.amanzi.awe.ui.util.ActionUtil;
import org.amanzi.neo.services.NetworkService.NetworkElementNodeType;
import org.amanzi.neo.services.exceptions.AWEException;
import org.amanzi.neo.services.model.IDriveModel;
import org.amanzi.neo.services.model.impl.DriveModel.DriveNodeTypes;
import org.amanzi.neo.services.model.impl.ProjectModel;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DateTime;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.part.ViewPart;

/**
 * <p>
 * Statistics view
 * </p>
 * 
 * @author Vladislav_Kondratenko
 * @since 1.0.0
 */
public class StatisticsView extends ViewPart {

    private static final ControlsFactory CONTROLS_FACTORY = ControlsFactory.getInstance();
    private static final Logger LOGGER = Logger.getLogger(StatisticsView.class);
    private static final String ASTERISK = "*";
    private static final String SEPARATOR = "----------";
    private static final String TOTAL_NAME_COLUMN = "Total";
    private static final int MAX_GROUPS_PER_CHART = 10;

    private final int INCREASE_WITH_BY_THREE = 3;
    private final int INCREASE_WITH_BY_TWO = 2;

    private static Mutex mutexRule = new Mutex();
    /*
     * components
     */
    private Label lDataset;
    private Combo cDataset;
    private Button bRefreshDatasets;
    private Label lTemplate;
    private Combo cTemplate;
    private Label lAggregation;
    private Combo cAggregation;
    private Button bBuild;

    private Label lPeriod;
    private Combo cPeriod;
    private Label lStartTime;
    private DateTime dDateStart;
    private DateTime dTimeStart;
    private Label lEndTime;
    private DateTime dDateEnd;
    private DateTime dTimeEnd;
    private Button bResetStart;
    private Button bResetEnd;
    private Button bReport;
    private Button bExport;
    private Button bChartView;
    private TableViewer tableViewer;
    private boolean isTimeChanged;
    /*
     * composites
     */
    private Composite mainComposite;
    private Composite controlComposite;
    private Composite topControlsComposite;
    private Composite bottomControlsComposite;

    private final Map<String, IDriveModel> datasets = new HashMap<String, IDriveModel>();
    /*
     * statistics manager
     */
    private final StatisticsManager statisticsManager = StatisticsManager.getInstance();
    /*
     * statistics
     */
    private AggregatedStatistics statistics;

    private Collection<String> groupNames;
    private List<String> selection;

    @Override
    public void createPartControl(Composite parent) {
        LOGGER.info("Create statistics view");
        createComposites(parent);
        createComponents();
        layoutComponents();
        fillComponents();
        addListeners();
    }

    /**
     * fill components
     */
    private void fillComponents() {
        updateDatasets();
        Collection<String> templates = statisticsManager.getAllScripts();
        cTemplate.setItems(templates.toArray(new String[templates.size()]));
    }

    /**
     * update dataset combobox
     */
    private void updateDatasets() {
        Iterable<IDriveModel> datasets;
        try {
            datasets = ProjectModel.getCurrentProjectModel().findAllDriveModels();
        } catch (AWEException e) {
            LOGGER.error("can't get all drive models because of", e);
            return;
        }
        for (IDriveModel model : datasets) {
            this.datasets.put(model.getName(), model);
        }
        cDataset.setItems(this.datasets.keySet().toArray(new String[this.datasets.size()]));
    }

    /**
     * update available periods
     */
    private void updateAvailablePeriods() {
        IDriveModel model = getSelectedModel();
        long start = model.getMinTimestamp();
        long end = model.getMaxTimestamp();

        List<String> periods = Period.getAvailablePeriods(start, end);
        cPeriod.setItems(periods.toArray(new String[periods.size()]));
    }

    /**
     * add listener for controls
     */
    private void addListeners() {
        LOGGER.info("added listeners to components");
        cDataset.addSelectionListener(new SelectionListener() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                setEnabled(true, lTemplate, cTemplate);
                resetDates(dDateStart, dDateStart, getSelectedModel().getMinTimestamp());
                resetDates(dDateEnd, dTimeEnd, getSelectedModel().getMaxTimestamp());
                updateAvailablePeriods();
                updatePropertiesList();
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
                widgetSelected(e);
            }
        });
        cTemplate.addSelectionListener(new SelectionListener() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                setEnabled(true, lAggregation, cAggregation);
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
                widgetSelected(e);
            }
        });
        cAggregation.addSelectionListener(new SelectionListener() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                setEnabled(true, lPeriod, cPeriod, lStartTime, dDateStart, dTimeStart, lEndTime, dDateEnd, dTimeEnd);
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
                widgetDefaultSelected(e);
            }
        });
        cPeriod.addSelectionListener(new SelectionListener() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                setEnabled(true, bBuild);
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
                widgetDefaultSelected(e);
            }
        });
        bResetStart.addSelectionListener(new SelectionListener() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                resetDates(dDateStart, dDateStart, getSelectedModel().getMinTimestamp());
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
            }
        });
        bResetEnd.addSelectionListener(new SelectionListener() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                resetDates(dDateEnd, dTimeEnd, getSelectedModel().getMaxTimestamp());
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
            }
        });
        bBuild.addMouseListener(new MouseListener() {

            @Override
            public void mouseUp(MouseEvent e) {
                final String templateName = cTemplate.getText();
                final IDriveModel model = getSelectedModel();
                final String property = getAggregation();
                final Period period = Period.findById(cPeriod.getText());
                runStatisticsPreparation(templateName, model, property, period);
                updateTable(templateName, property, period);

            }

            @Override
            public void mouseDown(MouseEvent e) {
            }

            @Override
            public void mouseDoubleClick(MouseEvent e) {
            }
        });
        tableViewer.getTable().getShell().addListener(TableListenersType.UPDATE_BUTTON, new Listener() {

            @Override
            public void handleEvent(Event event) {
                updateButtons();
            }
        });
    }

    /**
     * update table view with new values from processed statistics. method should be invoked in
     * order {@link #runStatisticsPreparation(String, IDriveModel, String, Period)}
     * 
     * @param period
     * @param aggregation
     * @param templateName
     */
    protected void updateTable(final String templateName, final String aggregation, final Period period) {
        String status = String.format("Update table for statistics %s aggregated by propertyName %s and perio %s ", templateName,
                aggregation, period.getId());
        Job job = new Job(status) {

            @Override
            protected IStatus run(IProgressMonitor monitor) {

                ActionUtil.getInstance().runTask(new Runnable() {

                    @Override
                    public void run() {
                        Iterator<StatisticsGroup> groups = statistics.getAllChild().iterator();
                        StatisticsGroup group = groups.next();
                        groupNames.add(group.getName());
                        while (groups.hasNext()) {
                            groupNames.add(groups.next().getName());
                        }
                        selection = new ArrayList<String>(groupNames);

                        StatisticsRow row = group.getAllChild().iterator().next();
                        // dispose table
                        final Composite parent = tableViewer.getTable().getParent();
                        tableViewer.getTable().dispose();

                        // create table viewer from scratch
                        tableViewer = new TableViewer(parent);
                        final Table table = tableViewer.getTable();
                        table.setLinesVisible(true);
                        table.setHeaderVisible(true);
                        TableLayout layout = new TableLayout();
                        table.setLayout(layout);
                        Collection<StatisticsCell> values = row.getAllChild();
                        int width = (int)100.0
                                / (values.size() + (isAdditionalColumnNecessary() ? INCREASE_WITH_BY_THREE : INCREASE_WITH_BY_TWO));
                        if (isAdditionalColumnNecessary()) {
                            TableColumn column = new TableColumn(table, SWT.RIGHT);
                            column.setText(NetworkElementNodeType.SECTOR.getId());
                            column.setToolTipText(NetworkElementNodeType.SECTOR.getId());
                            layout.addColumnData(new ColumnWeightData(width, true));

                        }

                        TableColumn column = new TableColumn(table, SWT.RIGHT);
                        column.setText(getAggregation());
                        column.setToolTipText(aggregation + "(click to apply/change filter or sort)");
                        layout.addColumnData(new ColumnWeightData(width, true));

                        column = new TableColumn(table, SWT.RIGHT);
                        column.setText(TOTAL_NAME_COLUMN);
                        column.setToolTipText(TOTAL_NAME_COLUMN);
                        layout.addColumnData(new ColumnWeightData(width, true));

                        for (StatisticsCell cell : values) {
                            column = new TableColumn(table, SWT.RIGHT);
                            column.setText(cell.getName());
                            column.setImage(StatisticsPlugin.getImageDescriptor(Messages.pathToEmptyFilterImg).createImage());
                            column.setToolTipText(cell.getName());
                            layout.addColumnData(new ColumnWeightData(width, true));
                        }
                        tableViewer.getTable().addMouseListener(new MouseAdapter() {

                            @Override
                            public void mouseDown(MouseEvent e) {
                                Point point = new Point(e.x, e.y);
                                Table table = (Table)e.widget;
                                Rectangle firstRowRect = table.getItem(0).getBounds();
                                Rectangle lastRowRect = table.getItem(table.getItemCount() - NumberUtils.INTEGER_ONE).getBounds();
                                // check if a data row selected
                                if ((e.y >= firstRowRect.y) && (e.y <= (lastRowRect.y + lastRowRect.height))) {
                                    int rowNum = (e.y - firstRowRect.y) / table.getItemHeight();
                                    TableItem item = table.getItem(rowNum);
                                    for (int i = 0; i < tableViewer.getTable().getColumnCount(); i++) {
                                        if (item.getBounds(i).contains(point)) {
                                            // TODO KV: implement drill down . Show selected data on
                                            // map
                                            tableViewer.refresh();
                                            break;
                                        }
                                    }
                                }
                            }

                        });
                        addSortListeners(table);
                        table.setSortColumn(table.getColumn(0));
                        table.setSortDirection(SWT.UP);

                        tableViewer.getControl().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
                        tableViewer.setLabelProvider(new StatisticsLabelProvider(getSite().getShell().getDisplay(),
                                isAdditionalColumnNecessary()));
                        tableViewer.setContentProvider(new StatisticsContentProvider());
                        tableViewer.setComparator(new StatisticsComparator());
                        if (isTimeChanged) {
                            isTimeChanged = false;
                            tableViewer.setFilters(new ViewerFilter[] {new StatisticsRowFilter(getTime(dDateStart, dTimeStart),
                                    getTime(dDateEnd, dTimeEnd))});
                        }
                        tableViewer.setInput(statistics);
                    }
                }, true);
                return Status.OK_STATUS;
            }
        };
        job.setRule(mutexRule);
        job.schedule();
    }

    /**
     * Adds sort listeners
     * 
     * @param table table to add listeners
     */
    private void addSortListeners(final Table table) {
        for (int i = 0; i < table.getColumnCount(); i++) {
            final TableColumn col = table.getColumn(i);
            final int colNum = i;
            col.addSelectionListener(new SelectionAdapter() {

                @Override
                public void widgetSelected(SelectionEvent e) {
                    final TableColumn currentColumn = (TableColumn)e.widget;
                    boolean isAdditionalColumnNecessary = isAdditionalColumnNecessary();
                    if (colNum == (isAdditionalColumnNecessary ? 1 : 0)) {
                        SortingDialog dialog = new SortingDialog(tableViewer, groupNames, groupNames, colNum,
                                isAdditionalColumnNecessary);
                        dialog.open();
                    } else {
                        updateSorting(table, colNum, currentColumn);
                    }
                }

            });

        }
    }

    /**
     * Updates sorting
     * 
     * @param table viewer table
     * @param colNum column number
     * @param currentColumn selected column
     */
    private void updateSorting(final Table table, final int colNum, TableColumn currentColumn) {
        int sortDirection = table.getSortDirection();
        if (tableViewer.getTable().getSortColumn().equals(currentColumn)) {
            sortDirection = sortDirection == SWT.UP ? SWT.DOWN : SWT.UP;
        } else {
            sortDirection = SWT.UP;
        }
        table.setSortDirection(sortDirection);
        table.setSortColumn(currentColumn);
        ((StatisticsComparator)tableViewer.getComparator()).update(colNum, sortDirection, isAdditionalColumnNecessary());
        tableViewer.refresh();
    }

    /**
     * run statistics preparation process in new job with execution rule. this method should be
     * invoked each time when you want to rebuild statistics, and this method should be the first,
     * others job with {@link Mutex} rule will wait until this process will be finished
     * 
     * @param templateName
     * @param model
     * @param property
     * @param period
     */
    protected void runStatisticsPreparation(final String templateName, final IDriveModel model, final String property,
            final Period period) {
        Job job = new Job("Statistics calculation for model " + model.getName() + " by property " + property + " period "
                + period.getId() + " and template " + templateName) {
            @Override
            protected IStatus run(IProgressMonitor monitor) {
                try {
                    statistics = statisticsManager.processStatistics(templateName, model, property, period, monitor);
                } catch (StatisticsException e) {
                    LOGGER.error("can't build statistics because of", e);
                    // TODO: LN: 01.08.2012, return error status
                    return Status.CANCEL_STATUS;
                }
                return Status.OK_STATUS;
            }
        };
        job.setRule(mutexRule);
        job.schedule();

    }

    /**
     * update properties list
     */
    protected void updatePropertiesList() {
        IDriveModel model = datasets.get(cDataset.getText());

        // TODO KV: make sure about node type
        String[] properties = model.getAllPropertyNames(DriveNodeTypes.M);
        cAggregation.setItems(properties);
    }

    /**
     * set date time when new dataset selected
     */
    private void resetDates(DateTime date, DateTime time, Long timestamp) {
        Calendar start = Calendar.getInstance();
        start.setTimeInMillis(timestamp);
        date.setDate(start.get(Calendar.YEAR), start.get(Calendar.MONTH), start.get(Calendar.DATE));
        time.setHours(start.get(Calendar.HOUR_OF_DAY));
    }

    /**
     * create components
     */
    private void createComponents() {
        LOGGER.info("create views components");
        /*
         * top controls
         */
        lDataset = CONTROLS_FACTORY.getLabel(topControlsComposite, Messages.statisticsViewLabelDataset);
        cDataset = CONTROLS_FACTORY.getCombobox(topControlsComposite);
        bRefreshDatasets = CONTROLS_FACTORY.getButton(topControlsComposite, StringUtils.EMPTY);
        bRefreshDatasets.setImage(StatisticsPlugin.getImageDescriptor(Messages.pathToRefreshButtonImg).createImage());
        lTemplate = CONTROLS_FACTORY.getLabel(topControlsComposite, Messages.statisticsViewLabelTemplate);
        cTemplate = CONTROLS_FACTORY.getCombobox(topControlsComposite);
        lAggregation = CONTROLS_FACTORY.getLabel(topControlsComposite, Messages.statisticsViewLabelAggregation);
        cAggregation = CONTROLS_FACTORY.getCombobox(topControlsComposite);
        bBuild = CONTROLS_FACTORY.getButton(topControlsComposite, Messages.statisticsViewLabelBuild);
        /*
         * bottom controls
         */
        lPeriod = CONTROLS_FACTORY.getLabel(bottomControlsComposite, Messages.statisticsViewLabelPeriod);
        cPeriod = CONTROLS_FACTORY.getCombobox(bottomControlsComposite);
        lStartTime = CONTROLS_FACTORY.getLabel(bottomControlsComposite, Messages.statisticsViewLabelStartTime);
        dDateStart = CONTROLS_FACTORY.getDateTime(bottomControlsComposite);
        dTimeStart = CONTROLS_FACTORY.getDateTime(bottomControlsComposite);
        bResetStart = CONTROLS_FACTORY.getButton(bottomControlsComposite, Messages.statisticsViewLabelResetButton);
        lEndTime = CONTROLS_FACTORY.getLabel(bottomControlsComposite, Messages.statisticsViewLabelEndTime);
        dDateEnd = CONTROLS_FACTORY.getDateTime(bottomControlsComposite);
        dTimeEnd = CONTROLS_FACTORY.getDateTime(bottomControlsComposite);
        bResetEnd = CONTROLS_FACTORY.getButton(bottomControlsComposite, Messages.statisticsViewLabelResetButton);
        bReport = CONTROLS_FACTORY.getButton(bottomControlsComposite, Messages.statisticsViewLabelReport);
        bExport = CONTROLS_FACTORY.getButton(bottomControlsComposite, Messages.statisticsViewLabelExport);
        bChartView = CONTROLS_FACTORY.getButton(bottomControlsComposite, Messages.statisticsViewLabelChartView);
        setEnabled(false, mainComposite);
        setEnabled(true, lDataset, cDataset);
    }

    /**
     * set enable aggregation to composites elements
     * 
     * @param aggregation
     * @param composites
     */
    protected void setEnabled(boolean aggregation, Composite... composites) {
        for (Composite composite : composites) {
            for (Control element : composite.getChildren()) {
                if (element.getClass() == mainComposite.getClass()) {
                    setEnabled(aggregation, (Composite)element);
                } else {
                    setEnabled(aggregation, element);
                }
            }
        }
    }

    /**
     * set enable aggregation to controls elements
     * 
     * @param aggregation
     * @param composites
     */
    protected void setEnabled(boolean aggregation, Control... controls) {
        for (Control element : controls) {
            element.setEnabled(aggregation);
        }
    }

    /**
     * put components to composites
     */
    private void layoutComponents() {
        LOGGER.info("layout and fill components");
        Control[] topControls = {lDataset, cDataset, bRefreshDatasets, lTemplate, cTemplate, lAggregation, cAggregation, bBuild};
        Control[] bottomControls = {lPeriod, cPeriod, lStartTime, dDateStart, dTimeStart, bResetStart, lEndTime, dDateEnd,
                dTimeEnd, bResetEnd, bReport, bExport, bChartView};
        Control[] elements = new Control[topControls.length + bottomControls.length];
        System.arraycopy(topControls, NumberUtils.INTEGER_ZERO, elements, NumberUtils.INTEGER_ZERO, topControls.length);
        System.arraycopy(bottomControls, NumberUtils.INTEGER_ZERO, elements, topControls.length, bottomControls.length);

        for (Control control : elements) {
            GridData gridData = new GridData(SWT.FILL, SWT.CENTER, false, true);
            control.setLayoutData(gridData);
        }
        GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true);
        tableViewer.getTable().setLayoutData(gridData);
        tableViewer.setLabelProvider(new StatisticsLabelProvider(getSite().getShell().getDisplay(), false));
        tableViewer.setContentProvider(new StatisticsContentProvider());
        tableViewer.setInput(groupNames);
    }

    /**
     * create view composites
     * 
     * @param parent
     */
    private void createComposites(Composite parent) {
        LOGGER.info("init views composite");
        mainComposite = new Composite(parent, SWT.NONE);
        GridLayout layout = new GridLayout(1, false);
        mainComposite.setLayout(layout);

        controlComposite = new Composite(mainComposite, SWT.NONE);
        layout = new GridLayout(1, false);
        GridData gridData = new GridData(SWT.FILL, SWT.NONE, true, false);
        controlComposite.setLayout(layout);
        controlComposite.setLayoutData(gridData);

        topControlsComposite = new Composite(controlComposite, SWT.NONE);
        gridData = new GridData(SWT.FILL, SWT.NONE, true, false);
        layout = new GridLayout(8, false);
        layout.verticalSpacing = NumberUtils.INTEGER_ZERO;
        topControlsComposite.setLayout(layout);
        topControlsComposite.setLayoutData(gridData);

        bottomControlsComposite = new Composite(controlComposite, SWT.FILL);
        gridData = new GridData(SWT.FILL, SWT.NONE, true, false);
        layout = new GridLayout(13, false);
        layout.marginTop = -5;
        layout.verticalSpacing = NumberUtils.INTEGER_ZERO;
        bottomControlsComposite.setLayout(layout);
        bottomControlsComposite.setLayoutData(gridData);

        // ------- table
        tableViewer = new TableViewer(mainComposite, SWT.BORDER | SWT.FULL_SELECTION | SWT.H_SCROLL | SWT.V_SCROLL);

    }

    /**
     * @return
     */
    public String getAggregation() {
        String text = cAggregation.getText();
        return text.startsWith(ASTERISK) ? text.substring(1) : text;
    }

    /**
     * Checks if additional column is necessary
     * 
     * @return true if 'sector' is selected for aggregation and it's a network level
     */
    private boolean isAdditionalColumnNecessary() {
        return getAggregation().equals(NetworkElementNodeType.SECTOR.getId())
                && (cAggregation.getSelectionIndex() < cAggregation.indexOf(SEPARATOR));
    }

    @Override
    public void setFocus() {
    }

    /**
     * Rule for job execution. contain rule which allow to execute jobs in order
     * 
     * @author Vladislav_Kondratenko
     */
    private static class Mutex implements ISchedulingRule {

        @Override
        public boolean contains(ISchedulingRule rule) {
            return (rule == this);
        }

        @Override
        public boolean isConflicting(ISchedulingRule rule) {
            return (rule == this);
        }

    }

    /**
     * return selected dataet;
     * 
     * @return
     */
    private IDriveModel getSelectedModel() {
        return datasets.get(cDataset.getText());
    }

    /**
     * get time from date controls
     * 
     * @param dateField
     * @param timeField
     * @return
     */
    private Long getTime(DateTime dateField, DateTime timeField) {
        Calendar calendar = new GregorianCalendar();
        calendar.setTimeInMillis(0L);
        calendar.set(dateField.getYear(), dateField.getMonth(), dateField.getDay(), timeField.getHours(), timeField.getMinutes());
        return calendar.getTimeInMillis();
    }

    /**
     * Enables and disables report button according to selection
     */
    private void updateButtons() {
        bExport.setEnabled(true);
        if (selection != null) {
            bReport.setEnabled(selection.size() < MAX_GROUPS_PER_CHART);
        } else {
            bReport.setEnabled(groupNames.size() < MAX_GROUPS_PER_CHART);
        }
    }
}
