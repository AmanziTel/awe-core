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
import org.amanzi.neo.services.DatasetService;
import org.amanzi.neo.services.exceptions.DatabaseException;
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
public class StatisticsLevel extends AbstractLevelElement {

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
    StatisticsLevel(Node dimensionRoot, String name) throws DatabaseException, IllegalNodeDataException {
        super(StatisticsNodeTypes.LEVEL);
        initStatisticsService();
        if (dimensionRoot == null) {
            LOGGER.error("parent can't be null");
            throw new IllegalArgumentException("dimension root cann't be null");
        }
        if (!StatisticsNodeTypes.DIMENSION.getId().equals(statisticService.getNodeProperty(dimensionRoot, DatasetService.TYPE))) {
            LOGGER.error("incorrect parent type.arentNode should have Dimension Type.");
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
     */
    StatisticsLevel(Node levelNode) {
        super(StatisticsNodeTypes.LEVEL);
        initStatisticsService();
        if (levelNode == null) {
            LOGGER.error("level node can't be null");
            throw new IllegalArgumentException("level node cann't be null");
        }
        name = (String)statisticService.getNodeProperty(levelNode, DatasetService.NAME);
        if (name == null) {
            LOGGER.error("cann't find name property in node " + levelNode);
            throw new IllegalArgumentException("cann't find name property in node " + levelNode);
        }
        rootNode = levelNode;
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
    public AggregatedStatistics getAggregateStatisticsModel(StatisticsLevel correlatedLevel) throws DatabaseException,
            IllegalNodeDataException {
        if (correlatedLevel == null) {
            LOGGER.error("correlatedLevel is Null");
            throw new IllegalArgumentException("correlated level can't be null");
        }
        Node aggregatedNode = statisticService.findAggregatedModel(rootNode, correlatedLevel.getRootNode());
        if (aggregatedNode != null) {
            return new AggregatedStatistics(aggregatedNode);
        }
        return new AggregatedStatistics(this, correlatedLevel);
    }

    /**
     * return source level if exist. else return null
     * 
     * @return
     */
    public StatisticsLevel getSourceLevel() {
        Iterable<Node> sources = statisticService.getSources(rootNode);
        if (sources == null) {
            return null;
        }
        return new StatisticsLevel(sources.iterator().next());
    }
}
