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

package org.amanzi.awe.views.statistics.table;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.amanzi.awe.statistics.dto.IStatisticsCell;
import org.amanzi.awe.statistics.dto.IStatisticsGroup;
import org.amanzi.awe.statistics.dto.IStatisticsRow;
import org.amanzi.awe.statistics.model.DimensionType;
import org.amanzi.awe.statistics.model.IStatisticsModel;
import org.amanzi.awe.ui.events.impl.ShowElementsOnMap;
import org.amanzi.awe.ui.events.impl.ShowInViewEvent;
import org.amanzi.awe.ui.manager.AWEEventManager;
import org.amanzi.awe.ui.manager.EventChain;
import org.amanzi.awe.ui.view.widget.internal.AbstractAWEWidget;
import org.amanzi.awe.views.statistics.table.StatisticsTable.IStatisticsTableListener;
import org.amanzi.awe.views.statistics.table.filters.dialog.FilterDialogEvent;
import org.amanzi.awe.views.statistics.table.filters.dialog.FilteringDialog;
import org.amanzi.awe.views.statistics.table.filters.dialog.FilteringDialog.IFilterDialogListener;
import org.amanzi.neo.core.period.Period;
import org.amanzi.neo.dto.IDataElement;
import org.amanzi.neo.models.exceptions.ModelException;
import org.amanzi.neo.models.measurement.IMeasurementModel;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.custom.TableCursor;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

import com.google.common.collect.Iterables;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Nikolay Lagutko (nikolay.lagutko@amanzitel.com)
 * @since 1.0.0
 */
public class StatisticsTable extends AbstractAWEWidget<ScrolledComposite, IStatisticsTableListener>
implements
IFilterDialogListener {

    public interface IStatisticsTableListener extends AbstractAWEWidget.IAWEWidgetListener {
    }

    private final SelectionListener selectionListener = new SelectionAdapter() {

        @Override
        public void widgetSelected(final SelectionEvent e) {
            drillDown();
        }
    };

    private TableViewer tableViewer;

    private final StatisticsTableProvider contentProvider = new StatisticsTableProvider();

    private final StatisticsLabelProvider labelProvider = new StatisticsLabelProvider();

    private IStatisticsModel model;

    private Set<String> groups;

    private final List<TableColumn> columns = new ArrayList<TableColumn>();

    private Period period;

    private TableCursor cursor;

    /**
     * @param parent
     * @param style
     * @param listener
     */
    public StatisticsTable(final Composite parent, final IStatisticsTableListener listener) {
        super(parent, SWT.V_SCROLL | SWT.H_SCROLL, listener);
    }

    @Override
    protected ScrolledComposite createWidget(final Composite parent, final int style) {
        ScrolledComposite scrolledComposite = new ScrolledComposite(parent, style);
        scrolledComposite.setLayout(new GridLayout(1, false));

        Composite composite = new Composite(scrolledComposite, SWT.FILL);
        composite.setLayout(new GridLayout(1, false));
        composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        tableViewer = new TableViewer(composite, SWT.BORDER);
        tableViewer.getControl().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        tableViewer.setContentProvider(contentProvider);
        tableViewer.setLabelProvider(labelProvider);
        tableViewer.setComparator(new StatisticsViewerComparator());
        initializeTable(tableViewer.getTable());

        scrolledComposite.setContent(composite);
        scrolledComposite.setExpandHorizontal(true);
        scrolledComposite.setExpandVertical(true);
        scrolledComposite.setMinSize(parent.getSize());

        return scrolledComposite;
    }

    public void setPeriod(final Period period) {
        this.period = period;
        contentProvider.setPeriod(period);
        labelProvider.setPeriod(period);
    }

    private void initializeTable(final Table table) {
        table.setLinesVisible(true);
        table.setHeaderVisible(true);
        tableViewer.getTable().addListener(UPDATE_SORTING_LISTENER, this);
        tableViewer.getTable().addSelectionListener(selectionListener);

        cursor = new TableCursor(table, SWT.NONE);
    }

    public void updateStatistics(final IStatisticsModel model) {
        if (model != null) {
            this.model = model;
            initStatisticsGroups();
            updateTable(tableViewer.getTable());
        }
    }

    /**
     * update table sorting if direction is null reverse direction will be apply
     * 
     * @param currentColumn
     * @param direction
     */
    private void updateSorting(final TableColumn currentColumn, final Integer direction) {
        Table table = tableViewer.getTable();
        int columnNumber = columns.indexOf(currentColumn);
        Integer sortDirection = direction;
        if (sortDirection == null) {
            sortDirection = table.getSortDirection();
            sortDirection = sortDirection == SWT.UP ? SWT.DOWN : SWT.UP;
        }
        table.setSortDirection(sortDirection);
        table.setSortColumn(currentColumn);
        ((StatisticsViewerComparator)tableViewer.getComparator()).update(columnNumber, sortDirection);
        tableViewer.refresh();
    }

    /**
     *
     */
    private void initStatisticsGroups() {
        groups = new HashSet<String>();
        try {
            Iterable<IStatisticsGroup> statGroups = model.getAllStatisticsGroups(DimensionType.TIME, period.getId());
            for (IStatisticsGroup group : statGroups) {
                groups.add(group.getPropertyValue());
            }
        } catch (ModelException e) {
            // TODO KV: handle exception;
        }

    }

    private void updateTable(final Table table) {
        clearTable(table);

        updateColumns(table);

        tableViewer.setInput(model);
        table.redraw();
        tableViewer.getTable().layout(true, true);
    }

    private void updateColumns(final Table table) {
        TableLayout tableLayout = new TableLayout();

        Set<String> columns = model.getColumns();
        int weight = (100 / columns.size()) + 2;
        createTableColumn(table, tableLayout, "Aggregation", weight);
        createTableColumn(table, tableLayout, "Total", weight);

        for (String column : columns) {
            createTableColumn(table, tableLayout, column, weight);
        }
        table.setLayout(tableLayout);
    }

    private void createTableColumn(final Table table, final TableLayout tableLayout, final String text, final int weight) {
        final TableColumn column = new TableColumn(table, SWT.RIGHT);
        columns.add(column);
        column.setText(text);
        column.setToolTipText(text);
        column.addSelectionListener(new SelectionListener() {

            @Override
            public void widgetSelected(final SelectionEvent e) {
                if (table.getColumn(0).equals(column)) {
                    FilteringDialog filterDialog = new FilteringDialog(tableViewer, column, groups);
                    filterDialog.open();
                } else {
                    updateSorting(column, null);
                }
            }

            @Override
            public void widgetDefaultSelected(final SelectionEvent e) {
                widgetSelected(e);
            }
        });
        tableLayout.addColumnData(new ColumnWeightData(weight, true));
    }

    private void clearTable(final Table table) {
        for (TableColumn column : table.getColumns()) {
            column.dispose();
        }
        tableViewer.setInput(null);
    }

    @Override
    public void handleEvent(final Event event) {
        if (event instanceof FilterDialogEvent) {
            FilterDialogEvent filterEvent = (FilterDialogEvent)event;
            tableViewer.setFilters(filterEvent.getFilters());
            updateSorting(filterEvent.getColumn(), filterEvent.getDirection());
        }

    }

    private void drillDown() {
        int column = cursor.getColumn();

        if ((cursor.getRow() != null) && (cursor.getRow().getData() instanceof IStatisticsRow)) {
            drillDown((IStatisticsRow)cursor.getRow().getData(), column);
        }
    }

    private void drillDown(final IStatisticsRow row, final int column) {
        if (model != null) {
            IMeasurementModel sourceModel = model.getSourceModel();

            Set<IDataElement> elements = new HashSet<IDataElement>();

            IDataElement elementToShow = null;

            if (sourceModel != null) {
                switch (labelProvider.getCellType(row, column)) {
                case KPI:
                case SUMMARY:
                    IStatisticsCell cell = Iterables.get(row.getStatisticsCells(), column - 2);

                    Iterables.addAll(elements, cell.getSources());

                    elementToShow = cell;

                    break;
                case PERIOD:
                    Iterables.addAll(elements, row.getSources());

                    elementToShow = row;

                    break;
                case PROPERTY:
                    Iterables.addAll(elements, row.getStatisticsGroup().getSources());

                    elementToShow = row.getStatisticsGroup();

                    break;
                }

                EventChain eventChain = new EventChain(true);

                eventChain.addEvent(new ShowElementsOnMap(sourceModel, elements, this));
                eventChain.addEvent(new ShowInViewEvent(model, elementToShow, this));

                AWEEventManager.getManager().fireEventChain(eventChain);
            }
        }
    }


}
