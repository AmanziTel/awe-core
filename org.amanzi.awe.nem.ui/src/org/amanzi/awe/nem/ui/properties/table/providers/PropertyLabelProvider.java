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

package org.amanzi.awe.nem.ui.properties.table.providers;

import org.amanzi.awe.nem.managers.properties.PropertyContainer;
import org.amanzi.awe.nem.ui.properties.table.PropertyColumns;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableColorProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Vladislav_Kondratenko
 * @since 1.0.0
 */
public class PropertyLabelProvider implements ITableLabelProvider, ITableColorProvider {

    @Override
    public void addListener(final ILabelProviderListener listener) {
        // TODO Auto-generated method stub

    }

    @Override
    public void dispose() {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean isLabelProperty(final Object element, final String property) {
        // TODO Auto-generated method stub
        return true;
    }

    @Override
    public void removeListener(final ILabelProviderListener listener) {
        // TODO Auto-generated method stub

    }

    @Override
    public Image getColumnImage(final Object element, final int columnIndex) {

        return null;
    }

    @Override
    public String getColumnText(final Object element, final int columnIndex) {
        PropertyContainer property = (PropertyContainer)element;
        PropertyColumns column = PropertyColumns.findByIndex(columnIndex);
        switch (column) {
        case NAME:
            return property.getName();
        case TYPE:
            return property.getType().getId();
        default:
            return property.getValue().toString();
        }
    }

    @Override
    public Color getForeground(final Object element, final int columnIndex) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Color getBackground(final Object element, final int columnIndex) {
        PropertyContainer property = (PropertyContainer)element;
        if (property.getName().equals("ci_lac")) {
            return new Color(Display.getCurrent(), 220, 220, 220);
        }
        return null;
    }
}
