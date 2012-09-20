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

}
