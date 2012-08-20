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

package org.amanzi.awe.statistics.dto.impl;

import org.amanzi.awe.statistics.dto.IStatisticsCell;
import org.amanzi.neo.impl.dto.DataElement;
import org.neo4j.graphdb.Node;

/**
 * TODO Purpose of
 * <p>
 *
 * </p>
 * @author Nikolay Lagutko (nikolay.lagutko@amanzitel.com)
 * @since 1.0.0
 */
public class StatisticsCell extends DataElement implements IStatisticsCell {

    private Object value;

    public StatisticsCell(final Node node) {
        super(node);
    }

    @Override
    public Object getValue() {
        return value;
    }

    public void setValue(final Object value) {
        this.value = value;
    }

}
