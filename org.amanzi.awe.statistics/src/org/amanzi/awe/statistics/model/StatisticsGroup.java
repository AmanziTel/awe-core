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

package org.amanzi.awe.statistics.model;

import org.amanzi.awe.statistics.enumeration.StatisticsNodeTypes;
import org.amanzi.neo.services.exceptions.DatabaseException;
import org.amanzi.neo.services.exceptions.IllegalNodeDataException;
import org.amanzi.neo.services.model.impl.DriveModel;
import org.apache.log4j.Logger;
import org.neo4j.graphdb.Node;

/**
 * <p>
 * StatisticsGroup entity. Can be instantiated only from {@link AggregatedStatistics}. play role of
 * storage for {@link StatisticsRow}
 * </p>
 * 
 * @author Vladislav_Kondratenko
 * @since 1.0.0
 */
public class StatisticsGroup extends AbstractEntity {
    private static final Logger LOGGER = Logger.getLogger(StatisticsGroup.class);

    /**
     * constructor for instantiation
     * 
     * @param parent
     * @param current
     * @param type
     */
    StatisticsGroup(Node parent, Node current) {
        super(parent, current, StatisticsNodeTypes.S_GROUP);
    }

    /**
     * constructor for instantiation
     * 
     * @param existed
     * @throws DatabaseException
     */
    StatisticsGroup(Node existed) throws DatabaseException {
        super(existed, StatisticsNodeTypes.S_GROUP);
    }

    /**
     * try to find S_ROW node by timestamp if not exist- create new one;
     * 
     * @param timestamp
     * @return
     * @throws DatabaseException
     * @throws IllegalNodeDataException
     */
    public StatisticsRow getSRow(Long timestamp) throws DatabaseException, IllegalNodeDataException {
        if (timestamp == null) {
            LOGGER.error("timestamp element is null.");
            throw new IllegalArgumentException("timestamp element is null");
        }
        Node sGroupd = rootNode;
        Node srowNode = statisticService.findNodeInChain(sGroupd, DriveModel.TIMESTAMP, timestamp);
        if (srowNode == null) {
            srowNode = statisticService.createSRow(sGroupd, timestamp, false);
        }
        return new StatisticsRow(rootNode, srowNode);
    }
}
