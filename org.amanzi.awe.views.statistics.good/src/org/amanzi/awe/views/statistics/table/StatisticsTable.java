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

import org.amanzi.awe.statistics.model.IStatisticsModel;
import org.amanzi.awe.statistics.period.Period;
import org.amanzi.awe.ui.view.widget.internal.AbstractAWEWidget;
import org.amanzi.awe.views.statistics.table.StatisticsTable.IStatisticsTableListener;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;

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

    private final StatisticsTableProvider contentProvider = new StatisticsTableProvider();

    private IStatisticsModel model;

    /**
     * @param parent
     * @param style
     * @param listener
     */
    public StatisticsTable(Composite parent, IStatisticsTableListener listener) {
        super(parent, SWT.NONE, listener);
    }

    @Override
    protected Composite createWidget(Composite parent, int style) {
        Composite composite = new Composite(parent, style);
        composite.setLayout(new GridLayout(1, false));

        tableViewer = new TableViewer(parent, SWT.BORDER | SWT.FULL_SELECTION | SWT.H_SCROLL | SWT.V_SCROLL);
        tableViewer.setContentProvider(contentProvider);

        initializeTable(tableViewer.getTable());

        return composite;
    }

    public void setPeriod(Period period) {
        contentProvider.setPeriod(period);
    }

    private void initializeTable(Table table) {
        table.setLinesVisible(true);
        table.setHeaderVisible(true);
    }

    public void updateStatistics(IStatisticsModel model) {
        tableViewer.setInput(model);
        if ((model != null) && !model.equals(this.model)) {
            updateTable(tableViewer.getTable());
        }
    }

    private void updateTable(Table table) {
        clearTable(table);

    }

    private void clearTable(Table table) {
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumn(i).dispose();
        }
    }
}
