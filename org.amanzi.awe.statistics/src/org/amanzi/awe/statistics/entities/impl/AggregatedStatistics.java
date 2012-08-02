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

package org.amanzi.awe.statistics.entities.impl;

import org.amanzi.awe.statistics.enumeration.DimensionTypes;
import org.amanzi.awe.statistics.enumeration.StatisticsNodeTypes;
import org.amanzi.awe.statistics.service.StatisticsService;
import org.amanzi.neo.services.exceptions.DatabaseException;
import org.amanzi.neo.services.exceptions.DuplicateNodeNameException;
import org.amanzi.neo.services.exceptions.IllegalNodeDataException;
import org.apache.log4j.Logger;
import org.neo4j.graphdb.Node;

/**
 * <p>
 * Aggregated statistics consists of:<br>
 * <b>S_GROUP-</b> unique property value {@link StatisticsGroup}<br>
 * S_ROW -</b>{@link StatisticsRow} list of periods separated in according with
 * {@link DimensionTypes#TIME} dimension (see : {@link Dimension}) level (see :
 * {@link StatisticsLevel}) <br>
 * <b> S_CELL - </b> {@link StatisticsCell} list of templates kpi's. S_CELL- has a list of sources.
 * </p>
 * 
 * @author Vladislav_Kondratenko
 * @since 1.0.0
 */
public class AggregatedStatistics extends AbstractStorageEntity<StatisticsGroup> {
    /*
     * logger instantiation
     */
    private static final Logger LOGGER = Logger.getLogger(AggregatedStatistics.class);

    private static final String NAME_FORMAT = "%s, %s";

    protected AggregatedStatistics(StatisticsLevel firstLevel, StatisticsLevel secondLevel) throws DatabaseException,
            IllegalNodeDataException {
        super(StatisticsNodeTypes.STATISTICS);
        if (firstLevel == null || secondLevel == null) {
            LOGGER.error("can't create aggregated statistics element because of incorrect levels information");
            throw new IllegalArgumentException("StatisticsLevel elements can't be null");
        }
        String dimensionType = (String)statisticService.getNodeProperty(firstLevel.getParentNode(), StatisticsService.NAME);
        String firstLevelName = (String)firstLevel.getName();
        String secondLevelName = (String)secondLevel.getName();
        if (DimensionTypes.NETWORK.getId().equals(dimensionType)) {
            name = String.format(NAME_FORMAT, firstLevelName, secondLevelName);
            parentNode = secondLevel.getParentNode();
        } else {
            parentNode = firstLevel.getParentNode();
            name = String.format(NAME_FORMAT, secondLevelName, firstLevelName);
        }
        rootNode = statisticService.createAggregatedStatistics(firstLevel.getRootNode(), secondLevel.getRootNode(), name);
    }

    /**
     * @param aggregatedNode
     */
    protected AggregatedStatistics(Node aggregatedNode) {
        super(StatisticsNodeTypes.STATISTICS);
        if (aggregatedNode == null) {
            LOGGER.error("can't create aggregated statistics element because of null ");
            throw new IllegalArgumentException("can't create aggregated statistics element because of null ");
        }
        rootNode = aggregatedNode;
        name = (String)statisticService.getNodeProperty(aggregatedNode, StatisticsService.NAME);
    }

    @Override
    protected StatisticsGroup instantiateChild(Node rootNode, Node childNode) {
        return new StatisticsGroup(rootNode, childNode);
    }

    /**
     * add new group. if group is already exist- throws DuplicateNodeNameException;
     * 
     * @param name
     * @return
     * @throws DuplicateNodeNameException
     * @throws DatabaseException
     * @throws IllegalNodeDataException
     */
    public StatisticsGroup addGroup(String name) throws DuplicateNodeNameException, DatabaseException, IllegalNodeDataException {
        return createChildWithName(name, StatisticsNodeTypes.S_GROUP);
    }

}
