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

package org.amanzi.awe.charts.model.impl;

import java.util.ArrayList;
import java.util.List;

import org.amanzi.awe.charts.model.IRangeAxis;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.google.common.collect.Iterables;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Vladislav_Kondratenko
 * @since 1.0.0
 */
public class RangeAxisContainer implements IRangeAxis {

    private final static Logger LOGGER = Logger.getLogger(RangeAxisContainer.class);

    private List<String> cells = new ArrayList<String>();

    private String name;

    public RangeAxisContainer(String name, Iterable<String> cells) {
        if (StringUtils.isEmpty(name)) {
            LOGGER.error("name can't be null");
        }
        this.name = name;
        Iterables.addAll(this.cells, cells);

    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Iterable<String> getCellsNames() {
        return cells;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((cells == null) ? 0 : cells.hashCode());
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
        RangeAxisContainer other = (RangeAxisContainer)obj;
        if (cells == null) {
            if (other.cells != null)
                return false;
        } else if (!cells.containsAll(other.cells))
            return false;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "RangeAxisContainer [cells=" + cells.toArray() + ", name=" + name + "]";
    }

    @Override
    public boolean isInCellList(String cell) {
        return cells.contains(cell);
    }

}
