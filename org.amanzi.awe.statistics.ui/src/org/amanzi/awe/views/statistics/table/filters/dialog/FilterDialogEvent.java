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

package org.amanzi.awe.views.statistics.table.filters.dialog;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.TableColumn;

/**
 * <p>
 * event for fireing from filter dialog
 * </p>
 * 
 * @author Vladislav_Kondratenko
 * @since 1.0.0
 */
public class FilterDialogEvent extends Event {

    private List<ViewerFilter> filters = new ArrayList<ViewerFilter>();

    private int direction;

    private final TableColumn column;

    /**
     * @param column
     * @param arg0
     * @param table
     * @param tableColumn
     * @param selection
     * @param filters
     */
    public FilterDialogEvent(final TableColumn column) {
        super();
        this.column = column;
    }

    /**
     * @return Returns the filters.
     */
    public ViewerFilter[] getFilters() {
        return filters.toArray(new ViewerFilter[filters.size()]);
    }

    /**
     * @return Returns the direction.
     */
    public int getDirection() {
        return direction;
    }

    /**
     * @param direction The direction to set.
     */
    public void setDirection(final int direction) {
        this.direction = direction;
    }

    /**
     * @param filters The filters to set.
     */
    public void setFilters(final List<ViewerFilter> filters) {
        this.filters = filters;
    }

    /**
     * @return Returns the column.
     */
    public TableColumn getColumn() {
        return column;
    }

}
