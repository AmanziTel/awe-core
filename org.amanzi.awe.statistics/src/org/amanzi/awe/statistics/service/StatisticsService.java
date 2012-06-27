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

import org.amanzi.awe.statistics.enumeration.Period;
import org.amanzi.awe.statistics.enumeration.StatisticsNodeTypes;
import org.amanzi.awe.statistics.enumeration.StatisticsRelationshipTypes;
import org.amanzi.neo.services.DatasetService;
import org.amanzi.neo.services.DatasetService.DatasetRelationTypes;
import org.amanzi.neo.services.NeoServiceFactory;
import org.amanzi.neo.services.exceptions.DatabaseException;
import org.amanzi.neo.services.exceptions.IllegalNodeDataException;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.RelationshipType;

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
     * find statistic root model
     * 
     * @param parentNode
     * @param name
     * @param type
     * @return
     */
    public Node findStatistic(Node parentNode, String name) throws IllegalArgumentException {
        return findFirstRelationshipNode(parentNode, DatasetService.NAME, name, StatisticsRelationshipTypes.STATISTICS);
    }

    /**
     * method for search required node by firstRelationship
     * 
     * @param parentNode
     * @param propertyName
     * @param propertyValue
     * @param relType
     * @return
     */
    private Node findFirstRelationshipNode(Node parentNode, String propertyName, String propertyValue, RelationshipType relType) {
        LOGGER.info("try to find node with propetyName:" + propertyName + " value:" + propertyValue + " from parent:" + parentNode
                + " by relationship: " + relType);
        Iterator<Node> statisticsNodes = datasetService.getFirstRelationTraverser(parentNode, relType, Direction.OUTGOING)
                .iterator();
        while (statisticsNodes.hasNext()) {
            Node currentNode = statisticsNodes.next();
            if (currentNode.getProperty(propertyName, StringUtils.EMPTY).equals(propertyValue)) {
                LOGGER.info("node founded propetyName:" + propertyName + " value:" + propertyValue + " from parent:" + parentNode
                        + " by relationship: " + relType);
                return currentNode;
            }
        }
        LOGGER.info("node not found. propetyName:" + propertyName + " value:" + propertyValue + " from parent:" + parentNode
                + " by relationship: " + relType);
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
    public Node createStatisticsModelRoot(Node parent, String name) throws DatabaseException {
        LOGGER.info("create statistic model node not found. parent:" + parent + " name:" + name);
        Node newlyNode = datasetService.createNode(parent, StatisticsRelationshipTypes.STATISTICS, StatisticsNodeTypes.STATISTICS);
        try {
            datasetService.setAnyProperty(newlyNode, DatasetService.NAME, name);
        } catch (IllegalNodeDataException e) {
            LOGGER.error("Unexpected exception thrown", e);
            // cann't be thrown
        }
        return newlyNode;
    }

    /**
     * try to find period statistics model node, if not found- create new one
     * 
     * @param rootNode
     * @param period
     * @return
     * @throws DatabaseException
     */
    public Node getPeriod(Node rootNode, Period period) throws DatabaseException {
        Node findedPeriod = findFirstRelationshipNode(rootNode, DatasetService.NAME, period.getId(), DatasetRelationTypes.CHILD);
        if (findedPeriod == null) {
            createPeriodNode(rootNode, period);
        }
        return findedPeriod;
    }

    /**
     * create period statistic model node
     * 
     * @param rootNode
     * @param period
     * @return
     * @throws DatabaseException
     */
    private Node createPeriodNode(Node rootNode, Period period) throws DatabaseException {
        LOGGER.info("create period statistic model node not found. parent:" + rootNode + " name:" + period);
        Node newlyNode = datasetService.createNode(rootNode, DatasetRelationTypes.CHILD, StatisticsNodeTypes.PERIOD_STATISTICS);
        try {
            datasetService.setAnyProperty(newlyNode, DatasetService.NAME, period.getId());
        } catch (IllegalNodeDataException e) {
            LOGGER.error("Unexpected exception thrown", e);
        }
        return newlyNode;
    }

    /**
     * create SOURCE relationship between periodNode and sourceNOde
     * 
     * @param periodNode
     * @param underline
     * @throws DatabaseException
     */
    public void addSource(Node periodNode, Node sourceNode) throws DatabaseException {
        LOGGER.info("add source:" + sourceNode + " to period:" + periodNode);
        datasetService.createRelationship(periodNode, sourceNode, StatisticsRelationshipTypes.SOURCE);
    }

    /**
     * return sources nodes;
     * 
     * @param parentNode
     * @return
     */
    public Iterable<Node> getSources(Node parentNode) {
        return datasetService.getFirstRelationTraverser(parentNode, StatisticsRelationshipTypes.SOURCE, Direction.OUTGOING);
    }

}
