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

package org.amanzi.awe.views.calls.enums;

import org.amanzi.awe.views.calls.statistics.constants.IStatisticsConstants;
import org.neo4j.graphdb.Node;

/**
 * <p>
 * Common interface for all statistics headers (both levels)
 * </p>
 * @author Shcharbatsevich_A
 * @since 1.0.0
 */
public interface IStatisticsHeader {
    
    /**
     * @return Returns the headerName.
     */
    public String getTitle();
    
    /**
     * @return Returns the headerType.
     */
    public StatisticsType getType();
    
    /**
     * Get statistics data from call (cell) by header.
     * Returns null if call does not good for this header.
     *
     * @param dataNode Node (call or cell)
     * @param inclInconclusive boolean (is include inconclusive calls)
     * @return Number
     */
    public Number getStatisticsData(Node dataNode, IStatisticsConstants constants, InclInconclusiveStates inclState);

}
