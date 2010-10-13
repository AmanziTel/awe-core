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

package org.amanzi.awe.statistics.database;

import org.amanzi.awe.statistics.database.entity.DatasetStatistics;
import org.amanzi.neo.core.enums.NodeTypes;
import org.neo4j.graphdb.Node;

/**
 * Statistics Root Iterator
 * 
 * @author Pechko_E
 * @since 1.0.0
 */
public class StatisticsRootIterator extends AbstractStatisticsIterator<DatasetStatistics> {

    public StatisticsRootIterator(Node node) {
        super(node, NodeTypes.STATISTICS_ROOT);
    }

    @Override
    public DatasetStatistics next() {
        return new DatasetStatistics(nodeIterator.next());
    }

}
