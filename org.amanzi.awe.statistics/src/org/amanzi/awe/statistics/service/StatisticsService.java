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

package org.amanzi.awe.statistics.service;

import java.util.Iterator;

import org.amanzi.awe.statistics.enumeration.StatisticsNodeTypes;
import org.amanzi.awe.statistics.enumeration.StatisticsRelationshipTypes;
import org.amanzi.neo.services.DatasetService;
import org.amanzi.neo.services.NeoServiceFactory;
import org.amanzi.neo.services.exceptions.DatabaseException;
import org.amanzi.neo.services.exceptions.IllegalNodeDataException;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Node;

/**
 * <p>
 * Service statistics
 * </p>
 * 
 * @author Vladislav_Kondratenko
 * @since 1.0.0
 */
public class StatisticsService {
    /*
     * logger instantiation
     */
    private static final Logger LOGGER = Logger.getLogger(StatisticsService.class);
    /*
     * services instantiation
     */
    DatasetService datasetService;

    private StatisticsService() {
        if (datasetService == null) {
            datasetService = NeoServiceFactory.getInstance().getDatasetService();
        }
    }

    static StatisticsService service;

    public static StatisticsService getInstance() {
        if (service == null) {
            service = new StatisticsService();
        }
        return service;
    }

    /**
     * try to find statistic model root, if can't found - create new one
     * 
     * @param parentNode
     * @param name
     * @param type
     * @throws IllegalNodeDataException
     * @throws DatabaseException
     */
    public Node getStatistics(Node parentNode, String name) throws DatabaseException, IllegalNodeDataException {
        Node statisticRoot = findStatistic(parentNode, name);
        if (statisticRoot == null) {
            statisticRoot = createStatisticsModelRoot(parentNode, name);
        }
        return statisticRoot;
    }

    /**
     * find statistic root model
     * 
     * @param parentNode
     * @param name
     * @param type
     * @return
     */
    public Node findStatistic(Node parentNode, String name) {
        LOGGER.info("try to find statistic parent:" + parentNode + " name:" + name);
        Iterator<Node> statisticsNodes = datasetService.getFirstRelationTraverser(parentNode,
                StatisticsRelationshipTypes.STATISTICS, Direction.OUTGOING).iterator();
        while (statisticsNodes.hasNext()) {
            Node currentNode = statisticsNodes.next();
            if (currentNode.getProperty(DatasetService.NAME, StringUtils.EMPTY).equals(name)) {
                LOGGER.info("Statistic model founded parent:" + parentNode + " name:" + name);
                return currentNode;
            }
        }
        LOGGER.info("Model node not found. parent:" + parentNode + " name:" + name);
        return null;
    }

    /**
     * create statistic model root node
     * 
     * @param parent
     * @param name
     * @param type
     * @return
     * @throws DatabaseException
     * @throws IllegalNodeDataException
     */
    public Node createStatisticsModelRoot(Node parent, String name) throws DatabaseException, IllegalNodeDataException {
        LOGGER.info("create statistic model node not found. parent:" + parent + " name:" + name);
        Node newlyNode = datasetService.createNode(parent, StatisticsRelationshipTypes.STATISTICS, StatisticsNodeTypes.STATISTICS);
        datasetService.setAnyProperty(newlyNode, DatasetService.NAME, name);
        return newlyNode;
    }
}
