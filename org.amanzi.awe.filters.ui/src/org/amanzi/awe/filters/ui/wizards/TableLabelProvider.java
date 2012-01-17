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

package org.amanzi.awe.filters.ui.wizards;

import java.io.Serializable;

import org.amanzi.neo.services.enums.INodeType;
import org.amanzi.neo.services.filters.IFilter;
import org.amanzi.neo.services.model.impl.RenderableModel.GisModel;
import org.eclipse.jface.viewers.ColumnLabelProvider;

/**
 * @author Vladislav_Kondratenko
 */
public class TableLabelProvider extends ColumnLabelProvider {
    private String filterDescription;
    private int columnName;

    public TableLabelProvider(int columnName) {
        this.columnName = columnName;
    }

    @Override
    public String getText(Object element) {
        filterDescription = "";
        GisModel model = null;
        switch (columnName) {
        case 1:
            model = (GisModel)element;
            return model.getName();
        case 2:
            model = (GisModel)element;
            return model.getFilter().getName();
        case 3:
            model = (GisModel)element;
            return getFilterDescription(model.getFilter());
        default:
            break;
        }
        return super.getText(element);
    }

    /**
     * collect filters property to description;
     * 
     * @param filter
     * @return
     */
    private String getFilterDescription(IFilter filter) {
        Serializable value = filter.getValue();
        String expression = filter.getExpressionType().name();
        String filterType = filter.getFilterType().name();
        INodeType nodeType = filter.getNodeType();
        String propertyName = filter.getPropertyName();
        filterDescription += propertyName;
        if (nodeType != null) {
            filterDescription += " from " + nodeType.getId() + " " + filterType + " " + value;;
        } else {
            filterDescription += " " + filterType + " " + value;
        }
        if (filter.getUnderlyingFilter() != null) {
            filterDescription += " " + expression + " ";
            getFilterDescription(filter.getUnderlyingFilter());
        }
        return filterDescription;
    }
}
