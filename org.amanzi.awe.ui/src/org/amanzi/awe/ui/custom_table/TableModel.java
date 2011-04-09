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

package org.amanzi.awe.ui.custom_table;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.ISafeRunnable;
import org.eclipse.core.runtime.ListenerList;
import org.eclipse.jface.util.SafeRunnable;
import org.eclipse.jface.viewers.ILazyContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

/**
 * <p>
 *
 * </p>
 * @author TsAr
 * @since 1.0.0
 */
public abstract class TableModel {
    ListenerList listeners = new ListenerList();

    public abstract ILazyContentProvider getContentProvider();

    public abstract int getRowsCount();

    public abstract boolean canSort();

    public abstract void sortData(int columnId, int direction);

    public abstract void updateColumn(Table table, TableColumn column, int columnId);

    public void hideColumn(Table table, TableColumn column, int columnId) {
        // do nothing in initial implementation
    }

    public TableViewerColumn defineColumn(final TableViewer viewer, final int columnId) {
        TableViewerColumn columnviewer = createColumn(viewer, columnId);
        final Table table = viewer.getTable();
        Listener sortListener = new Listener() {

            public void handleEvent(Event e) {
                if (canSort()) {
                    // determine new sort column and direction
                    TableColumn sortColumn = table.getSortColumn();
                    TableColumn currentColumn = (TableColumn)e.widget;
                    int dir = table.getSortDirection();
                    if (sortColumn == currentColumn) {
                        dir = dir == SWT.UP ? SWT.DOWN : SWT.UP;
                    } else {
                        table.setSortColumn(currentColumn);
                        dir = SWT.UP;
                    }
                    // sort the data based on column and direction
                    sortData(columnId, dir);
                    // update data displayed in table
                    table.setSortDirection(dir);
                    table.clearAll();
                } else {
                    table.setSortDirection(SWT.NONE);
                }
            }
        };
        columnviewer.getColumn().addListener(SWT.Selection, sortListener);
        return columnviewer;
    }

    public TableViewerColumn createColumn(TableViewer viewer, int columnId) {
        return new TableViewerColumn(viewer, SWT.NONE, columnId);
        // TODO should override for set labelProvider
    }

    public abstract int getColumnsCount();
    public void addModelChangeListener(IModelChangeListener listener) {
        Assert.isNotNull(listener);
        listeners.add(listener);
    }

    public void removeModelChangeListener(IModelChangeListener listener) {
        listeners.remove(listener);
    }

    protected void fireEvent(final IModelChangeEvent event) {
        for (final Object listener : listeners.getListeners()) {
            SafeRunnable.run(new ISafeRunnable() {

                @Override
                public void run() throws Exception {
                    ((IModelChangeListener)listener).handleEvent(event);
                }

                @Override
                public void handleException(Throwable exception) {
                }
            });
        }

    }
}
