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

import org.amanzi.awe.statistics.period.Period;
import org.amanzi.awe.ui.view.widget.internal.AbstractAWEWidget;
import org.amanzi.awe.views.statistics.table.StatisticsTable.StatisticsTableListener;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Nikolay Lagutko (nikolay.lagutko@amanzitel.com)
 * @since 1.0.0
 */
public class StatisticsTable extends AbstractAWEWidget<Composite, StatisticsTableListener> {

    public interface StatisticsTableListener extends AbstractAWEWidget.IAWEWidgetListener {

    }

    private TableViewer tableViewer;

    private final StatisticsTableProvider contentProvider = new StatisticsTableProvider();

    /**
     * @param parent
     * @param style
     * @param listener
     */
    protected StatisticsTable(Composite parent, StatisticsTableListener listener) {
        super(parent, SWT.NONE, listener);
    }

    @Override
    protected Composite createWidget(Composite parent, int style) {
        Composite composite = new Composite(parent, style);
        composite.setLayout(new GridLayout(1, false));

        tableViewer = new TableViewer(parent, SWT.BORDER | SWT.FULL_SELECTION | SWT.H_SCROLL | SWT.V_SCROLL);
        tableViewer.setContentProvider(contentProvider);

        return composite;
    }

    public void setPeriod(Period period) {
        contentProvider.setPeriod(period);
    }
}
