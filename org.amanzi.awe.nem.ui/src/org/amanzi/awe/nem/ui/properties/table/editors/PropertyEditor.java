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

package org.amanzi.awe.nem.ui.properties.table.editors;

import org.amanzi.awe.nem.managers.properties.PropertyContainer;
import org.amanzi.awe.nem.ui.properties.table.PropertyColumns;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Vladislav_Kondratenko
 * @since 1.0.0
 */
public class PropertyEditor extends EditingSupport {

    private PropertyColumns column;

    /**
     * @param viewer
     */
    public PropertyEditor(ColumnViewer viewer, PropertyColumns column) {
        super(viewer);
        this.column = column;
    }

    @Override
    protected CellEditor getCellEditor(Object element) {
        switch (column) {
        case DEFAULT_VALUE:
            return new TextCellEditor(((TableViewer)getViewer()).getTable());
        default:
            break;
        }
        return null;
    }

    @Override
    protected boolean canEdit(Object element) {
        switch (column) {
        case DEFAULT_VALUE:
            return true;
        default:
            break;
        }
        return false;
    }

    @Override
    protected Object getValue(Object element) {
        PropertyContainer property = (PropertyContainer)element;
        switch (column) {
        case DEFAULT_VALUE:
            return property.getDefaultValue();
        default:
            break;
        }
        return property;
    }

    @Override
    protected void setValue(Object element, Object value) {
        PropertyContainer target = (PropertyContainer)element;
        String newValue = (String)value;
        switch (column) {
        case DEFAULT_VALUE:
            target.setDefaultValue(newValue);
            getViewer().update(element, null);
        default:
            break;
        }

    }

}
