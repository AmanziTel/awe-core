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

import org.amanzi.awe.nem.managers.properties.PropertyContainer;
import org.amanzi.awe.nem.ui.properties.table.editors.PropertyEditor;
import org.amanzi.awe.nem.ui.properties.table.providers.PropertyLabelProvider;
import org.amanzi.awe.nem.ui.properties.table.providers.TableContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Vladislav_Kondratenko
 * @since 1.0.0
 */
public class PropertyTable extends TableViewer {

    public interface IPropertyTableListener {
        void onUpdate(String message);
    }

    private static final int COLUMN_WIDTH = 100;

    private final List<PropertyContainer> properties;

    private final IPropertyTableListener listener;

    public PropertyTable(final Composite parent, final int style, final List<PropertyContainer> properties,
            final IPropertyTableListener listener) {
        super(parent, style);
        this.properties = properties;
        this.listener = listener;
    }

    @Override
    public void add(final Object element) {
        properties.add((PropertyContainer)element);
        super.add(element);
    }

    private void createColumns() {
        for (PropertyColumns column : PropertyColumns.values()) {
            createTableColumn(column);
        }
    }

    private void createTableColumn(final PropertyColumns columnId) {
        TableViewerColumn column = new TableViewerColumn(this, SWT.NONE);
        column.setLabelProvider(new PropertyLabelProvider(columnId));
        column.getColumn().setText(columnId.getName());
        column.getColumn().setToolTipText(columnId.getName());
        column.getColumn().setWidth(COLUMN_WIDTH);
        column.setEditingSupport(new PropertyEditor(this, columnId));
    }

    private void fillTable() {
        this.setInput(properties);
    }

    public void initialize() {
        getTable().setLayoutData(new GridData(GridData.FILL_BOTH));
        setContentProvider(new TableContentProvider());

        createColumns();

        fillTable();

        getTable().setHeaderVisible(true);
        getTable().setLinesVisible(true);
    }

    @Override
    public void remove(final Object element) {
        properties.remove(element);
        super.remove(element);
    }

    @Override
    public void update(final Object element, final String[] properties) {
        listener.onUpdate(null);
        super.update(element, properties);
    }
}
