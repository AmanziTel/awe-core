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

package org.amanzi.awe.statistics.database.entity;

import org.amanzi.awe.statistics.database.AbstractStatisticsIterator;
import org.amanzi.neo.services.enums.NodeTypes;
import org.neo4j.graphdb.Node;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Pechko_E
 * @since 1.0.0
 */
public class StatisticsIterator extends AbstractStatisticsIterator<Statistics> {

    private DatasetStatistics datasetStatistics;

    public StatisticsIterator(Node node,DatasetStatistics datasetStatistics) {
        super(node, NodeTypes.STATISTICS);
        this.datasetStatistics=datasetStatistics;
    }

    @Override
    public Statistics next() {
        return new Statistics(nodeIterator.next(),datasetStatistics);
    }

}
