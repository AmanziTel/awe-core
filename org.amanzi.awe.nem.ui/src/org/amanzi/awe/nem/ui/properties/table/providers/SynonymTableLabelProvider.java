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

import org.amanzi.awe.nem.export.SynonymsWrapper;
import org.amanzi.awe.nem.ui.properties.table.SynonymsTableColumns;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.graphics.Image;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Vladislav_Kondratenko
 * @since 1.0.0
 */
public class SynonymTableLabelProvider implements ITableLabelProvider {

    @Override
    public void addListener(final ILabelProviderListener listener) {
        // TODO Auto-generated method stub

    }

    @Override
    public void dispose() {
        // TODO Auto-generated method stub

    }

    @Override
    public Image getColumnImage(final Object element, final int columnIndex) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getColumnText(final Object element, final int columnIndex) {
        SynonymsTableColumns column = SynonymsTableColumns.findByIndex(columnIndex);
        SynonymsWrapper wrapper = (SynonymsWrapper)element;
        switch (column) {
        case TYPE:
            return wrapper.getType();
        case PROPERTY:
            return wrapper.getProperty();
        case HEADER:
            return wrapper.getHeader();
        default:
            break;
        }
        return null;
    }

    @Override
    public boolean isLabelProperty(final Object element, final String property) {
        return false;
    }

    @Override
    public void removeListener(final ILabelProviderListener listener) {
        // TODO Auto-generated method stub

    }

}
