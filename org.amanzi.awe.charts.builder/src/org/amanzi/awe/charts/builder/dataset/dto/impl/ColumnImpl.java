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

package org.amanzi.awe.charts.builder.dataset.dto.impl;

import java.util.ArrayList;
import java.util.List;

import org.amanzi.awe.charts.builder.dataset.dto.IRow;
import org.amanzi.awe.charts.builder.dataset.dto.IColumn;
import org.amanzi.awe.charts.manger.ChartsManager;

/**
 * <p>
 * </p>
 * 
 * @author Vladislav_Kondratenko
 * @since 1.0.0
 */
public class ColumnImpl implements IColumn {

    private Long startDate;

    private Long endDate;

    private String name;

    private List<RowImpl> items = new ArrayList<RowImpl>();

    public ColumnImpl(Long startDate, Long endDate) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.name = ChartsManager.getInstance().getDefaultDateFormat().format(startDate);
    }

    @Override
    public int compareTo(IColumn o) {
        return startDate.compareTo(o.getStartDate());
    }

    protected void addItem(RowImpl cell) {
        items.add(cell);
    }

    protected List<RowImpl> getRows() {
        return items;
    }

    @Override
    public IRow getItemByName(String name) {
        for (IRow item : items) {
            if (item.getCellName().equals(name)) {
                return item;
            }
        }
        return null;
    }

    @Override
    public Long getStartDate() {
        return startDate;
    }

    @Override
    public Long getEndDate() {
        return endDate;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return getName();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((startDate == null) ? 0 : startDate.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        ColumnImpl other = (ColumnImpl)obj;
        if (startDate == null) {
            if (other.startDate != null)
                return false;
        } else if (!startDate.equals(other.startDate))
            return false;
        return true;
    }

}
