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

package org.amanzi.awe.views.statistics.table.filters;

import java.util.List;

import org.amanzi.awe.statistics.dto.IStatisticsRow;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

/**
 * <p>
 * filter group by name
 * </p>
 * 
 * @author Vladislav_Kondratenko
 * @since 1.0.0
 */
public class GroupsFilter extends ViewerFilter {

    private List<String> values;

    public GroupsFilter(List<String> values) {
        this.values = values;
    }

    @Override
    public boolean select(Viewer viewer, Object parentElement, Object element) {
        IStatisticsRow row = (IStatisticsRow)element;
        return values.contains(row.getStatisticsGroup().getPropertyValue());
    }

    /**
     * @return Returns the values.
     */
    public List<String> getValues() {
        return values;
    }

}
