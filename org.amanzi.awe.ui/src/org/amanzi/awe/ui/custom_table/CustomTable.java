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

import java.util.ArrayList;

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.part.ViewPart;

/**
 * <p>
 * Custom table view part
 * </p>
 * 
 * @author TsAr
 * @since 1.0.0
 */
public class CustomTable<E extends TableModel> extends ViewPart implements IModelChangeListener {
    private ArrayList<TableColumn> columns = new ArrayList<TableColumn>();
    private final E model;
    private final int style;
    private TableViewer viewer;

    /**
     * Instantiates a new custom table.
     * 
     * @param model the model
     * @param style the style
     */
    public CustomTable(E model, int style) {
        this.model = model;
        this.style = style;
    }

    @Override
    public void createPartControl(Composite parent) {
        viewer = new TableViewer(parent, SWT.VIRTUAL | style);
        viewer.setContentProvider(model.getContentProvider());
        formColumns();
        model.addModelChangeListener(this);
    }

    @Override
    public void dispose() {
        model.removeModelChangeListener(this);
        super.dispose();
    }

    @Override
    public void setFocus() {
        viewer.getTable().setFocus();
    }

    @Override
    public void handleEvent(IModelChangeEvent event) {
        formColumns();
        if (event.getType() == ChangeModelType.CONTENT) {
            Object data = event.getData();
            viewer.setInput(data);
        }
    }

    private void formColumns() {
        final Table table = viewer.getTable();
        table.setVisible(false);

        int columnCount = model.getColumnsCount();
        for (int i = 0; i < columns.size() && i < columnCount; i++) {
            model.updateColumn(table, columns.get(i), i);
        }
        while (columns.size() < columnCount) {
            TableViewerColumn col = model.defineColumn(viewer, columns.size());
            columns.add(col.getColumn());
        }
        for (int i = columnCount; i < columns.size(); i++) {
            TableColumn tableColumn = columns.get(i);
            tableColumn.setWidth(0);
            model.hideColumn(table, tableColumn, i);
        }
        updateTable(table);
        table.setItemCount(model.getRowsCount());
        table.setVisible(true);
    }


    public void updateTable(final Table table) {
        table.setHeaderVisible(true);
        table.setLinesVisible(true);
    }

    /**
     * @return Returns the viewer.
     */
    public TableViewer getViewer() {
        return viewer;
    }

}
