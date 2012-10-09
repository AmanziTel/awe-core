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

import java.util.ArrayList;
import java.util.List;

import org.amanzi.awe.nem.properties.manager.NetworkPropertiesManager;
import org.amanzi.awe.nem.properties.manager.NetworkProperty;
import org.amanzi.awe.nem.ui.widgets.PropertyColumns;
import org.amanzi.awe.nem.ui.properties.PropertyContainer;
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

    }

    private static final int COLUMN_WIDTH = 100;

    private List<PropertyContainer> properties;

    private String type;

    public PropertyTable(Composite parent, int style, String type) {
        super(parent, style);
        this.type = type;
    }

    public void initialize() {
        getTable().setLayoutData(new GridData(GridData.FILL_BOTH));
        setContentProvider(new TableContentProvider());

        createColumns();

        fillTable();

        getTable().setHeaderVisible(true);
        getTable().setLinesVisible(true);
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

    /**
    *
    */
    private void fillTable() {
        properties = new ArrayList<PropertyContainer>();
        for (NetworkProperty property : NetworkPropertiesManager.getInstance().getProperties(type)) {
            properties.add(new PropertyContainer(property));
        }
        this.setInput(properties);

    }
}
