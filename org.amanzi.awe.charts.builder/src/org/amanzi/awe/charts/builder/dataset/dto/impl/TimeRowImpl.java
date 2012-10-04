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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.amanzi.awe.charts.builder.dataset.dto.ITimeRow;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Vladislav_Kondratenko
 * @since 1.0.0
 */
public class TimeRowImpl implements ITimeRow {

    private Map<Long, List<String>> groupsCache = new HashMap<Long, List<String>>();

    private String name;

    /**
     * @param name
     */
    public TimeRowImpl(String name) {
        super();
        this.name = name;
    }

    @Override
    public Collection<String> getGroupsForTime(long startTime) {
        return groupsCache.get(startTime);
    }

    public void addGroups(long startTime, Collection<String> groups) {
        if (groups == null) {
            return;
        }
        List<String> existedCache = groupsCache.get(startTime);
        if (existedCache == null) {
            existedCache = groupsCache.put(startTime, new ArrayList<String>());
        }
        groupsCache.get(startTime).addAll(groups);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
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
        TimeRowImpl other = (TimeRowImpl)obj;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public int compareTo(ITimeRow o) {
        return name.compareTo(o.getName());
    }

    @Override
    public String getName() {
        return name;
    }
}
