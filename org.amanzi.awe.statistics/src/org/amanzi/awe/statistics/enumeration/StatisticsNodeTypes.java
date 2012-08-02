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

package org.amanzi.awe.statistics.enumeration;

import org.amanzi.neo.services.NodeTypeManager;
import org.amanzi.neo.services.enums.INodeType;

/**
 * <p>
 * Statistics node Types
 * </p>
 * 
 * @author Vladislav_Kondratenko
 * @since 1.0.0
 */
public enum StatisticsNodeTypes implements INodeType {
    STATISTICS_MODEL, S_ROW, S_CELL, DIMENSION, STATISTICS, S_GROUP, LEVEL;
    static {
        NodeTypeManager.registerNodeType(StatisticsNodeTypes.class);
    }

    @Override
    public String getId() {
        return this.name().toLowerCase();
    }

}
