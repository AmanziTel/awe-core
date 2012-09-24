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

package org.amanzi.awe.statistics.service.impl;

import java.text.MessageFormat;
import java.util.Iterator;

import org.amanzi.awe.statistics.model.DimensionType;
import org.amanzi.awe.statistics.model.StatisticsNodeType;
import org.amanzi.awe.statistics.service.IStatisticsService;
import org.amanzi.neo.nodeproperties.ITimePeriodNodeProperties;
import org.amanzi.neo.services.INodeService;
import org.amanzi.neo.services.exceptions.DatabaseException;
import org.amanzi.neo.services.exceptions.ServiceException;
import org.amanzi.neo.services.impl.NodeService.NodeServiceRelationshipType;
import org.amanzi.neo.services.impl.internal.AbstractService;
import org.apache.commons.lang3.StringUtils;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Path;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.traversal.Evaluation;
import org.neo4j.graphdb.traversal.Evaluator;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Nikolay Lagutko (nikolay.lagutko@amanzitel.com)
 * @since 1.0.0
 */
public class StatisticsService extends AbstractService implements IStatisticsService {

    public static final String STATISTICS_NAME_PATTERN = "{0} - {1}";

    private static final String GROUP_NAME_PATTERN = "{0} - {1}";

    private final INodeService nodeService;

    private final ITimePeriodNodeProperties timePeriodNodeProperties;

    /**
     * <p>
     * </p>
     * 
     * @author Vladislav_Kondratenko
     * @since 1.0.0
     */
    private static final class TimeRangeEvaluator implements Evaluator {
        /** Node node field */
        private long endTime;
        private long startTime;
        private String startTimeName;
        private String endTimeName;

        /**
         * @param node
         */
        private TimeRangeEvaluator(String startTimeName, long startTime, String endTimeName, long endTime) {
            this.startTimeName = startTimeName;
            this.endTimeName = endTimeName;
            this.startTime = startTime;
            this.endTime = endTime;
        }

        @Override
        public Evaluation evaluate(final Path path) {
            boolean toContinue = true;
            boolean include = true;
            if (path.lastRelationship() != null) {
                Relationship relation = path.lastRelationship();
                Node node = relation.getEndNode();
                long nodeStartTime = (Long)node.getProperty(startTimeName);
                long nodeEndTime = (Long)node.getProperty(endTimeName);
                if (nodeStartTime >= startTime && nodeEndTime <= endTime) {
                    toContinue = true;
                    include = toContinue;
                } else {
                    include = false;
                }

            } else {
                include = false;
            }

            return Evaluation.of(include, toContinue);
        }
    }

    public static enum StatisticsRelationshipType implements RelationshipType {
        STATISTICS, TIME_DIMENSION, PROPERTY_DIMENSION, SOURCE;
    }

    /**
     * @param graphDb
     * @param generalNodeProperties
     */
    public StatisticsService(final GraphDatabaseService graphDb, final INodeService nodeService,
            ITimePeriodNodeProperties timePeriodNodeProperties) {
        super(graphDb, null);
        this.nodeService = nodeService;
        this.timePeriodNodeProperties = timePeriodNodeProperties;
    }

    @Override
    public Node findStatisticsNode(final Node parentNode, final String templateName, final String aggregationPropertyName)
            throws ServiceException {
        assert parentNode != null;
        assert !StringUtils.isEmpty(templateName);
        assert !StringUtils.isEmpty(aggregationPropertyName);

        String statisticsName = MessageFormat.format(STATISTICS_NAME_PATTERN, templateName, aggregationPropertyName);

        return nodeService.getChildByName(parentNode, statisticsName, StatisticsNodeType.STATISTICS,
                StatisticsRelationshipType.STATISTICS);
    }

    public Iterator<Node> findAllStatisticsNode(Node parentNode) throws ServiceException {
        assert parentNode != null;
        return nodeService.getChildren(parentNode, StatisticsNodeType.STATISTICS, StatisticsRelationshipType.STATISTICS);
    }

    @Override
    public Node getStatisticsLevel(final Node parentNode, final DimensionType dimensionType, final String propertyName)
            throws ServiceException {
        assert parentNode != null;
        assert dimensionType != null;
        assert !StringUtils.isEmpty(propertyName);

        Node result = findStatisticsLevel(parentNode, dimensionType, propertyName);
        if (result == null) {
            result = nodeService
                    .createNode(parentNode, StatisticsNodeType.LEVEL, dimensionType.getRelationshipType(), propertyName);
        }

        return result;
    }

    @Override
    public Node findStatisticsLevel(final Node parentNode, final DimensionType dimensionType, final String propertyName)
            throws ServiceException {
        assert parentNode != null;
        assert dimensionType != null;
        assert !StringUtils.isEmpty(propertyName);

        return nodeService.getChildByName(parentNode, propertyName, StatisticsNodeType.LEVEL, dimensionType.getRelationshipType());
    }

    @Override
    public Iterator<Node> findAllStatisticsLevelNode(final Node parentNode, final DimensionType dimensionType)
            throws ServiceException {
        assert parentNode != null;
        assert dimensionType != null;
        return nodeService.getChildren(parentNode, StatisticsNodeType.LEVEL, dimensionType.getRelationshipType());
    }

    @Override
    public Node getGroup(final Node propertyLevelNode, final Node periodLevelNode) throws ServiceException {
        assert propertyLevelNode != null;
        assert periodLevelNode != null;

        Node result = findGroup(propertyLevelNode, periodLevelNode);
        if (result == null) {
            result = createGroup(propertyLevelNode, periodLevelNode);
        }

        return result;
    }

    protected Node findGroup(final Node propertyLevelNode, final Node periodLevelNode) throws ServiceException {
        Node result = null;

        try {
            for (Relationship relationship : propertyLevelNode.getRelationships(StatisticsRelationshipType.TIME_DIMENSION,
                    Direction.INCOMING)) {
                if (relationship.getStartNode().equals(periodLevelNode)) {
                    result = relationship.getStartNode();
                    break;
                }
            }
        } catch (Exception e) {
            throw new DatabaseException(e);
        }

        return result;
    }

    protected Node createGroup(final Node propertyLevelNode, final Node periodLevelNode) throws ServiceException {
        String propertyLevelName = nodeService.getNodeName(propertyLevelNode);
        String periodLevelname = nodeService.getNodeName(periodLevelNode);

        String groupName = MessageFormat.format(GROUP_NAME_PATTERN, periodLevelname, propertyLevelName);

        Node result = nodeService.createNode(periodLevelNode, StatisticsNodeType.GROUP, NodeServiceRelationshipType.CHILD,
                groupName);
        nodeService.linkNodes(propertyLevelNode, result, NodeServiceRelationshipType.CHILD);

        return result;
    }

    @Override
    public void addSourceNode(final Node node, final Node sourceNode) throws ServiceException {
        assert node != null;
        assert sourceNode != null;

        nodeService.linkNodes(node, sourceNode, StatisticsRelationshipType.SOURCE);
    }

    @Override
    public Iterator<Node> getAllSources(Node rootNode) throws ServiceException {
        assert rootNode != null;
        return nodeService.getChildren(rootNode, StatisticsRelationshipType.SOURCE);
    }

    @Override
    public String getStatisticsLevelName(final Node groupNode, final DimensionType dimensionType) throws ServiceException {
        assert groupNode != null;
        assert dimensionType != null;

        try {
            Iterable<Relationship> relationships = groupNode
                    .getRelationships(Direction.INCOMING, NodeServiceRelationshipType.CHILD);

            for (Relationship singleRelationship : relationships) {
                if (singleRelationship.getStartNode().hasRelationship(Direction.INCOMING, dimensionType.getRelationshipType())) {
                    return nodeService.getNodeName(singleRelationship.getStartNode());
                }
            }
        } catch (Exception e) {
            throw new DatabaseException(e);
        }

        return null;
    }

    @Override
    public Iterator<Node> getRowsInTimeRange(Node groupNode, long startTime, long endTime) throws ServiceException {
        assert groupNode != null;

        return nodeService
                .getChildrenChainTraversal(groupNode)
                .evaluator(
                        new TimeRangeEvaluator(timePeriodNodeProperties.getStartDateTimestampProperty(), startTime,
                                timePeriodNodeProperties.getEndDateTimestampProperty(), endTime)).traverse(groupNode).nodes()
                .iterator();
    }
}
