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

import org.amanzi.awe.statistics.enumeration.StatisticsNodeTypes;
import org.amanzi.neo.services.exceptions.DatabaseException;
import org.amanzi.neo.services.exceptions.DuplicateNodeNameException;
import org.amanzi.neo.services.exceptions.IllegalNodeDataException;
import org.apache.log4j.Logger;
import org.neo4j.graphdb.Node;

/**
 * <p>
 * store statistics for all {@link AggregatedStatistics}
 * </p>
 * 
 * @author Vladislav_Kondratenko
 * @since 1.0.0
 */
public class StatisticsLevel extends AbstractEntity {

    /*
     * logger instantiation;
     */
    private static final Logger LOGGER = Logger.getLogger(StatisticsLevel.class);

    /**
     * initialize new dimensionLevel
     * 
     * @param statisticsRoot
     * @param dimensionType
     * @throws DatabaseException
     * @throws IllegalNodeDataException
     */
    public StatisticsLevel(Node dimensionRoot, String name) throws DatabaseException, IllegalNodeDataException {
        super(StatisticsNodeTypes.LEVEL);
        initStatisticsService();
        if (dimensionRoot == null) {
            LOGGER.error("parent can't be null");
            throw new IllegalArgumentException("dimension root cann't be null");
        }
        if (!StatisticsNodeTypes.DIMENSION.getId().equals(statisticService.getType(dimensionRoot))) {
            LOGGER.error("incorrect parent type.parentNode should have Dimension Type.");
            throw new IllegalArgumentException("incorrect parent type.");
        }
        parentNode = dimensionRoot;
        if (name == null || name.isEmpty()) {
            LOGGER.error("level name can't be null or empty");
            throw new IllegalArgumentException("level name cann't be null");
        }
        this.name = name;
        rootNode = statisticService.findStatisticsLevelNode(parentNode, name);
        if (rootNode == null) {
            rootNode = statisticService.createStatisticsLevelNode(parentNode, name, false);
        }
    }

    /**
     * instantiate statistics model with existed level node
     * 
     * @param levelNode
     * @throws DatabaseException
     */
    public StatisticsLevel(Node parent, Node levelNode) throws DatabaseException {
        super(parent, levelNode, StatisticsNodeTypes.LEVEL);
        initStatisticsService();
    }

    /**
     * add source level to current Level
     * 
     * @param source
     * @throws DatabaseException
     */
    public void addSourceLevel(StatisticsLevel source) throws DatabaseException {
        if (source == null) {
            LOGGER.error("source level is null");
            throw new IllegalArgumentException("source Level is null");
        }
        statisticService.addSource(rootNode, source.getRootNode());
    }

    /**
     * try to find aggregated element
     * 
     * @param correlatedLevel
     * @return
     * @throws DatabaseException
     * @throws IllegalNodeDataException
     */
    public AggregatedStatistics getAggregateStatistics(StatisticsLevel correlatedLevel) throws DatabaseException,
            IllegalNodeDataException {
        AggregatedStatistics statistics = findAggregatedStatistics(correlatedLevel);
        if (findAggregatedStatistics(correlatedLevel) == null) {
            statistics = new AggregatedStatistics(this, correlatedLevel);
        }
        return statistics;
    }

    /**
     * create aggregated statistics element. if such element is already in database throw
     * DuplicatedNodeNameException
     * 
     * @param correlatedLevel
     * @return
     * @throws DatabaseException
     * @throws IllegalNodeDataException
     * @throws DuplicateNodeNameException
     */
    public AggregatedStatistics createAggregatedStatistics(StatisticsLevel correlatedLevel) throws DatabaseException,
            IllegalNodeDataException, DuplicateNodeNameException {
        AggregatedStatistics statistics = findAggregatedStatistics(correlatedLevel);
        if (statistics != null) {
            LOGGER.error("statistics for levels " + getName() + " " + correlatedLevel.getName() + " is already exists");
            throw new DuplicateNodeNameException();
        }
        statistics = new AggregatedStatistics(this, correlatedLevel);
        return statistics;
    }

    /**
     * find statistics model
     * 
     * @param correlatedLevel
     * @return null if not found
     */
    public AggregatedStatistics findAggregatedStatistics(StatisticsLevel correlatedLevel) {
        if (correlatedLevel == null) {
            LOGGER.error("correlatedLevel is Null");
            throw new IllegalArgumentException("correlated level can't be null");
        }
        Node aggregatedNode = statisticService.findAggregatedStatistics(rootNode, correlatedLevel.getRootNode());
        if (aggregatedNode != null) {
            return new AggregatedStatistics(aggregatedNode);
        }
        return null;
    }

    /**
     * return source level if exist. else return null
     * 
     * @return
     * @throws DatabaseException
     */
    public StatisticsLevel getSourceLevel() throws DatabaseException {
        Iterable<Node> sources = statisticService.getSources(rootNode);
        if (!sources.iterator().hasNext()) {
            return null;
        }
        return new StatisticsLevel(parentNode, sources.iterator().next());
    }

}
