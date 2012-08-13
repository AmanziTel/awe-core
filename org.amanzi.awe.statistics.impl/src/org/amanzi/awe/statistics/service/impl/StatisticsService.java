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

import java.util.Iterator;

import org.amanzi.awe.statistics.model.StatisticsNodeType;
import org.amanzi.awe.statistics.nodeproperties.IStatisticsNodeProperties;
import org.amanzi.awe.statistics.service.IStatisticsService;
import org.amanzi.neo.services.INodeService;
import org.amanzi.neo.services.exceptions.DatabaseException;
import org.amanzi.neo.services.exceptions.DuplicatedNodeException;
import org.amanzi.neo.services.exceptions.ServiceException;
import org.amanzi.neo.services.impl.internal.AbstractService;
import org.apache.commons.lang3.StringUtils;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.RelationshipType;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Nikolay Lagutko (nikolay.lagutko@amanzitel.com)
 * @since 1.0.0
 */
public class StatisticsService extends AbstractService implements IStatisticsService {

    private final INodeService nodeService;

    private final IStatisticsNodeProperties statisticsNodeProperties;

    public static enum StatisticsRelationshipType implements RelationshipType {
        STATISTICS, TIME_DIMENSION, PROPERTY_DIMENSION;
    }

    /**
     * @param graphDb
     * @param generalNodeProperties
     */
    public StatisticsService(GraphDatabaseService graphDb, INodeService nodeService,
            IStatisticsNodeProperties statisticsNodeProperties) {
        super(graphDb, null);
        this.nodeService = nodeService;
        this.statisticsNodeProperties = statisticsNodeProperties;
    }

    @Override
    public Node findStatisticsNode(Node parentNode, String templateName, String aggregationPropertyName) throws ServiceException {
        assert parentNode != null;
        assert !StringUtils.isEmpty(templateName);
        assert !StringUtils.isEmpty(aggregationPropertyName);

        Node result = null;

        boolean throwDuplicatedException = false;

        try {
            Iterator<Node> nodes = nodeService
                    .getChildrenTraversal(StatisticsNodeType.STATISTICS, StatisticsRelationshipType.STATISTICS)
                    .evaluator(new PropertyEvaluator(statisticsNodeProperties.getTemplateNameProperty(), templateName))
                    .evaluator(
                            new PropertyEvaluator(statisticsNodeProperties.getAggregationPropertyNameProperty(),
                                    aggregationPropertyName)).traverse(parentNode).nodes().iterator();

            if (nodes.hasNext()) {
                result = nodes.next();

                if (nodes.hasNext()) {
                    throwDuplicatedException = true;
                }
            }
        } catch (Exception e) {
            throw new DatabaseException(e);
        }

        if (throwDuplicatedException) {
            throw new DuplicatedNodeException(statisticsNodeProperties.getTemplateNameProperty(), templateName);
        }

        return result;
    }

}
