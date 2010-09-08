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

package org.amanzi.neo.services.statistic;

import org.amanzi.neo.services.statistic.internal.DatasetStatistic;
import org.neo4j.graphdb.Node;

/**
 * TODO Purpose of 
 * <p>
 *
 * </p>
 * @author tsinkel_a
 * @since 1.0.0
 */
public class StatisticManager {
    private StatisticManager() {
        // hide constructor
    }

    public static IStatistic getStatistic(Node root) {
        DatasetStatistic result = new DatasetStatistic(root);
        result.init();
        return result;
    }

    public static IStatistic getEmptyStatistic() {
        DatasetStatistic result = new DatasetStatistic();
        result.init();
        return result;
    }
}
