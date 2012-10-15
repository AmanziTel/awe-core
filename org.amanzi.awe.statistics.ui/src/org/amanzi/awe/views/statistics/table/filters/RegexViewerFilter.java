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

import org.amanzi.awe.views.statistics.table.filters.dialog.KpiComboViewerWidget;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

/**
 * <p>
 * </p>
 * 
 * @author Vladislav_Kondratenko
 * @since 1.0.0
 */
public class RegexViewerFilter extends ViewerFilter {

    private String filter;

    public RegexViewerFilter() {
    }

    public void setFilterText(String text) {
        this.filter = text;
    }

    @Override
    public boolean select(Viewer viewer, Object parentElement, Object element) {
        if ((filter == null) || filter.isEmpty()) {
            return true;
        }
        String elem = ((String)element);
        if (elem.equals(KpiComboViewerWidget.SELECT_ALL_ITEM)) {
            return true;
        }
        return FilterUtils.getInstatnce().match(filter, elem);
    }

}
