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

import java.util.LinkedHashMap;

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
 * <b>S_GROUP-</b> unique property value <br>
 * S_ROW -</b> list of periods separated in according with {@link DimensionTypes#TIME} dimension
 * (see : {@link Dimension}) level (see : {@link StatisticsLevel}) <br>
 * <b> S_CELL - </b> list of templates kpi's. S_CELL- has a list of sources.
 * </p>
 * 
 * @author Vladislav_Kondratenko
 * @since 1.0.0
 */
public class AggregatedStatistics extends AbstractEntity {
    /*
     * logger instantiation
     */
    private static final Logger LOGGER = Logger.getLogger(AggregatedStatistics.class);
    private LinkedHashMap<String, StatisticsGroup> groups;
    private static final String NAME_FORMAT = "%s, %s";

    AggregatedStatistics(StatisticsLevel firstLevel, StatisticsLevel secondLevel) throws DatabaseException,
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
    public AggregatedStatistics(Node aggregatedNode) {
        super(StatisticsNodeTypes.STATISTICS);
        if (aggregatedNode == null) {
            LOGGER.error("can't create aggregated statistics element because of null ");
            throw new IllegalArgumentException("can't create aggregated statistics element because of null ");
        }
        rootNode = aggregatedNode;
        name = (String)statisticService.getNodeProperty(aggregatedNode, StatisticsService.NAME);
    }

    /**
     * get s_group data Element
     * 
     * @param aggregationElement
     * @param name
     * @return
     * @throws IllegalNodeDataException
     * @throws DatabaseException
     */
    public StatisticsGroup getSGroup(String name) {
        loadChildIfNecessary();
        return groups.get(name);
    }

    /**
     * try to create new group in this statistics. if group is already exists throw
     * DuplicatedNodeNameException
     * 
     * @param timestamp
     * @return
     * @throws DuplicateNodeNameException
     * @throws DatabaseException
     * @throws IllegalNodeDataException
     */
    public StatisticsGroup createStatisticsGroup(String name) throws DuplicateNodeNameException, DatabaseException,
            IllegalNodeDataException {
        loadChildIfNecessary();
        if (groups.containsKey(name)) {
            LOGGER.error("s_group with timestamp." + name + "is already exists");
            throw new DuplicateNodeNameException();
        }
        Node statisticsRow = statisticService.createSGroup(rootNode, name, Boolean.FALSE);
        StatisticsGroup newRow = new StatisticsGroup(rootNode, statisticsRow);
        groups.put(name, newRow);
        return newRow;
    }

    /**
     * get all groups name
     * 
     * @return
     */
    public Iterable<StatisticsGroup> getAllSGroups() {
        loadChildIfNecessary();
        return groups.values();
    }

    @Override
    protected void loadChildIfNecessary() {
        if (groups == null) {
            groups = new LinkedHashMap<String, StatisticsGroup>();
            Iterable<Node> groupNodes = statisticService.getChildrenChainTraverser(rootNode);
            if (groupNodes == null) {
                return;
            }
            for (Node rowNode : groupNodes) {
                String name = (String)statisticService.getNodeProperty(rowNode, StatisticsService.NAME);
                groups.put(name, new StatisticsGroup(rootNode, rowNode));
            }
        }
    }

}
