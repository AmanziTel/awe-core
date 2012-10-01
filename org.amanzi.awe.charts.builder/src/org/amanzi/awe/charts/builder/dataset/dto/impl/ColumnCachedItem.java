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

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.amanzi.awe.charts.builder.dataset.dto.IColumnItem;
import org.amanzi.awe.charts.manger.ChartsManager;
import org.amanzi.awe.statistics.dto.IStatisticsRow;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Vladislav_Kondratenko
 * @since 1.0.0
 */
public class ColumnCachedItem implements IColumnItem {

    private IStatisticsRow row;

    private String name;

    private Double value = 0d;

    private Set<String> groups = new HashSet<String>();

    private String cellName;

    private int count = 0;

    protected ColumnCachedItem() {

    }

    /**
     * @param row
     * @param cellName
     */
    public ColumnCachedItem(IStatisticsRow row, String cellName) {
        super();
        this.row = row;
        this.cellName = cellName;
        this.name = ChartsManager.getInstance().getDefaultDateFormat().format(row.getStartDate());
    }

    public void addGroup(String groupName) {
        groups.add(groupName);
    }

    @Override
    public Collection<String> getGroupsNames() {
        return groups;
    }

    @Override
    public IStatisticsRow getRow() {
        return row;
    }

    @Override
    public int compareTo(IColumnItem o) {
        return name.compareTo(o.getName());
    }

    public void increase(Number value) {
        this.value += value.doubleValue();
        this.count++;
    }

    @Override
    public String getCellName() {
        return cellName;
    }

    @Override
    public String getName() {
        return name;
    }

    protected int getCount() {
        return count;
    }

    protected double getValue() {
        return value;
    }

    protected void setValue(Double value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return getCellName();
    }

    /**
     * @param row The row to set.
     */
    protected void setRow(IStatisticsRow row) {
        this.row = row;
    }

    /**
     * @param name The name to set.
     */
    protected void setName(String name) {
        this.name = name;
    }

    /**
     * @param cellName The cellName to set.
     */
    protected void setCellName(String cellName) {
        this.cellName = cellName;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((cellName == null) ? 0 : cellName.hashCode());
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
        ColumnCachedItem other = (ColumnCachedItem)obj;
        if (cellName == null) {
            if (other.cellName != null)
                return false;
        } else if (!cellName.equals(other.cellName))
            return false;
        return true;
    }

}
