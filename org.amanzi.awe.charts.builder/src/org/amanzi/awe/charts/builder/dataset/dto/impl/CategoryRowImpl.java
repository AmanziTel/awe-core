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
import java.util.Collection;
import java.util.List;

import org.amanzi.awe.charts.builder.dataset.dto.ICategoryRow;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Vladislav_Kondratenko
 * @since 1.0.0
 */
public class CategoryRowImpl implements ICategoryRow {

    private Double value = 0d;

    private List<String> groups = new ArrayList<String>();

    private String cellName;

    private int count = 0;

    public CategoryRowImpl(String cellName) {
        super();
        this.cellName = cellName;
    }

    public void addGroup(String groupName) {
        groups.add(groupName);
    }

    @Override
    public Collection<String> getGroupsNames() {
        return groups;
    }

    @Override
    public int compareTo(ICategoryRow o) {
        return cellName.compareTo(o.getName());
    }

    public void increase(Number value) {
        this.value += value.doubleValue();
        this.count++;
    }

    @Override
    public String getName() {
        return cellName;
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
        return getName();
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
        CategoryRowImpl other = (CategoryRowImpl)obj;
        if (cellName == null) {
            if (other.cellName != null)
                return false;
        } else if (!cellName.equals(other.cellName))
            return false;
        return true;
    }

}
