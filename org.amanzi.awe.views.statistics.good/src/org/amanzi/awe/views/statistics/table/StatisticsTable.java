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

import java.util.Set;

import org.amanzi.awe.statistics.model.IStatisticsModel;
import org.amanzi.awe.statistics.period.Period;
import org.amanzi.awe.ui.view.widget.internal.AbstractAWEWidget;
import org.amanzi.awe.views.statistics.table.StatisticsTable.IStatisticsTableListener;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Nikolay Lagutko (nikolay.lagutko@amanzitel.com)
 * @since 1.0.0
 */
public class StatisticsTable extends AbstractAWEWidget<Composite, IStatisticsTableListener> {

    public interface IStatisticsTableListener extends AbstractAWEWidget.IAWEWidgetListener {

    }

    private TableViewer tableViewer;

    private TableLayout tableLayout;

    private final StatisticsTableProvider contentProvider = new StatisticsTableProvider();

    private IStatisticsModel model;

    /**
     * @param parent
     * @param style
     * @param listener
     */
    public StatisticsTable(final Composite parent, final IStatisticsTableListener listener) {
        super(parent, SWT.FILL, listener);
    }

    @Override
    protected Composite createWidget(final Composite parent, final int style) {
        Composite composite = new Composite(parent, style);
        composite.setLayout(new GridLayout(1, false));

        tableViewer = new TableViewer(composite, SWT.BORDER | SWT.FULL_SELECTION | SWT.H_SCROLL | SWT.V_SCROLL);
        tableViewer.setContentProvider(contentProvider);
        tableViewer.getControl().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        initializeTable(tableViewer.getTable());

        return composite;
    }

    public void setPeriod(final Period period) {
        contentProvider.setPeriod(period);
    }

    private void initializeTable(final Table table) {
        table.setLinesVisible(true);
        table.setHeaderVisible(true);

        tableLayout = new TableLayout();
        table.setLayout(tableLayout);
    }

    public void updateStatistics(final IStatisticsModel model) {
        this.model = model;

        tableViewer.setInput(model);
        if ((model != null) && !model.equals(this.model)) {
            updateTable(tableViewer.getTable());
        }
    }

    private void updateTable(final Table table) {
        clearTable(table);

        updateColumns(table);
    }

    private void updateColumns(final Table table) {
        Set<String> columns = model.getColumns();

        int weight = (100 / columns.size()) + 2;

        createTableColumn(table, "Aggregation", weight);
        createTableColumn(table, "Total", weight);

        for (String column : columns) {
            createTableColumn(table, column, weight);
        }
    }

    private void createTableColumn(final Table table, final String text, final int weight) {
        TableColumn column = new TableColumn(table, SWT.RIGHT);
        column.setText(text);
        column.setToolTipText(text);

        tableLayout.addColumnData(new ColumnWeightData(weight, true));
    }

    private void clearTable(final Table table) {
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumn(i).dispose();
        }
    }
}
