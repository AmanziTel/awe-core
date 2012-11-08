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

package org.amanzi.awe.nem.ui.properties.table;

import java.util.List;

import org.amanzi.awe.nem.export.SynonymsWrapper;
import org.amanzi.awe.nem.ui.properties.table.editors.SynonymsCellEditor;
import org.amanzi.awe.nem.ui.properties.table.providers.SynonymTableLabelProvider;
import org.amanzi.awe.nem.ui.properties.table.providers.SynonymsTableContentProvider;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Vladislav_Kondratenko
 * @since 1.0.0
 */
public class SynonymsTable extends TableViewer {

    /**
     * @param parent
     */
    public SynonymsTable(final Composite parent) {
        super(parent);
        // TODO Auto-generated constructor stub
    }

    /**
     * @param column
     */
    private TableViewerColumn createColumn(final SynonymsTableColumns column) {
        TableViewerColumn tableColumn = new TableViewerColumn(this, SWT.NONE);
        tableColumn.getColumn().setText(column.getName());
        if (column.equals(SynonymsTableColumns.HEADER)) {
            tableColumn.setEditingSupport(new SynonymsCellEditor(this));
        }
        return tableColumn;
    }

    /**
     * @param synonyms
     */
    public void init(final List<SynonymsWrapper> synonyms) {
        final TableLayout tableLayout = new TableLayout();
        for (SynonymsTableColumns column : SynonymsTableColumns.values()) {
            createColumn(column);
            tableLayout.addColumnData(new ColumnWeightData(1));
        }
        this.setContentProvider(new SynonymsTableContentProvider());
        this.setLabelProvider(new SynonymTableLabelProvider());
        this.getTable().setLayout(tableLayout);
        this.getTable().setHeaderVisible(true);
        this.getTable().setLinesVisible(true);
        this.setInput(synonyms);

    }
}
