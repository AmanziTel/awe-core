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
import org.eclipse.jface.viewers.ColumnLabelProvider;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Vladislav_Kondratenko
 * @since 1.0.0
 */
public class PropertyLabelProvider extends ColumnLabelProvider {

    private PropertyColumns type;

    public PropertyLabelProvider(PropertyColumns type) {
        super();
        this.type = type;
    }

    @Override
    public String getText(Object element) {
        PropertyContainer property = (PropertyContainer)element;
        switch (type) {
        case NAME:
            return property.getName();
        case TYPE:
            return property.getType().getId();
        default:
            return property.getDefaultValue().toString();
        }
    }
}
