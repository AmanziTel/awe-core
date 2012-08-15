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

package org.amanzi.awe.statistics.model.impl;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

import org.amanzi.awe.statistics.dto.IStatisticsGroup;
import org.amanzi.awe.statistics.dto.IStatisticsRow;
import org.amanzi.awe.statistics.dto.impl.StatisticsGroup;
import org.amanzi.awe.statistics.model.IStatisticsModel;
import org.amanzi.awe.statistics.model.StatisticsNodeType;
import org.amanzi.awe.statistics.nodeproperties.IStatisticsNodeProperties;
import org.amanzi.awe.statistics.service.DimensionType;
import org.amanzi.awe.statistics.service.IStatisticsService;
import org.amanzi.awe.statistics.service.impl.StatisticsService;
import org.amanzi.awe.statistics.service.impl.StatisticsService.StatisticsRelationshipType;
import org.amanzi.neo.models.exceptions.ModelException;
import org.amanzi.neo.models.impl.internal.AbstractModel;
import org.amanzi.neo.nodeproperties.IGeneralNodeProperties;
import org.amanzi.neo.nodeproperties.ITimePeriodNodeProperties;
import org.amanzi.neo.services.INodeService;
import org.amanzi.neo.services.exceptions.ServiceException;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.log4j.Logger;
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
public class StatisticsModel extends AbstractModel implements IStatisticsModel {

    private static final Logger LOGGER = Logger.getLogger(StatisticsModel.class);

    private final ITimePeriodNodeProperties timePeriodNodeProperties;

    private final IStatisticsNodeProperties statisticsNodeProperties;

    private String templateName;

    private String aggregatedProperty;

    private final IStatisticsService statisticsService;

    private final Map<Pair<String, String>, IStatisticsGroup> statisticsGroupCache = new HashMap<Pair<String,String>, IStatisticsGroup>();

    private final Map<DimensionType, Map<String, Node>> statisticsLevelCache = new HashMap<DimensionType, Map<String,Node>>();

    /**
     * @param nodeService
     * @param generalNodeProperties
     */
    public StatisticsModel(final IStatisticsService statisticsService, final INodeService nodeService, final IGeneralNodeProperties generalNodeProperties,
            final ITimePeriodNodeProperties timePeriodNodeProperties, final IStatisticsNodeProperties statisticsNodeProperties) {
        super(nodeService, generalNodeProperties);

        this.statisticsService = statisticsService;
        this.timePeriodNodeProperties = timePeriodNodeProperties;
        this.statisticsNodeProperties = statisticsNodeProperties;
    }

    @Override
    public void initialize(final Node rootNode) throws ModelException {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(getStartLogStatement("initialize", rootNode));
        }

        try {
            super.initialize(rootNode);

            templateName = getNodeService().getNodeProperty(rootNode, statisticsNodeProperties.getTemplateNameProperty(), null,
                    true);
            aggregatedProperty = getNodeService().getNodeProperty(rootNode,
                    statisticsNodeProperties.getAggregationPropertyNameProperty(), null, true);
        } catch (Exception e) {
            processException("An error occured on Statistics Model Initialization", e);
        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(getFinishLogStatement("initialize"));
        }
    }

    public void initialize(final Node parentNode, final String templateName, final String propertyName) throws ModelException {
        assert !StringUtils.isEmpty(templateName);
        assert !StringUtils.isEmpty(propertyName);

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(getStartLogStatement("initialize", parentNode, templateName, propertyName));
        }

        try {
            String statisticsName = MessageFormat.format(StatisticsService.STATISTICS_NAME_PATTERN, templateName, propertyName);

            super.initialize(parentNode, statisticsName, StatisticsNodeType.STATISTICS);

            this.templateName = templateName;
            this.aggregatedProperty = propertyName;
        } catch (Exception e) {
            processException("Exception on initializing Statistics Model", e);
        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(getFinishLogStatement("initialize"));
        }
    }

    @Override
    public void finishUp() throws ModelException {
        try {
            getNodeService().updateProperty(getRootNode(), statisticsNodeProperties.getTemplateNameProperty(), templateName);
            getNodeService().updateProperty(getRootNode(), statisticsNodeProperties.getAggregationPropertyNameProperty(),
                    aggregatedProperty);
        } catch (ServiceException e) {
            processException("Exception on finishin up Statistics Model", e);
        }
    }

    @Override
    protected RelationshipType getRelationToParent() {
        return StatisticsRelationshipType.STATISTICS;
    }

    @Override
    public IStatisticsGroup getGroup(final String period, final String propertyKey) throws ModelException {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(getStartLogStatement("getGroup", period, propertyKey));
        }

        //TODO: LN: 15.08.2012, validate input

        Pair<String, String> groupKey = new ImmutablePair<String, String>(period, propertyKey);

        IStatisticsGroup result = statisticsGroupCache.get(groupKey);

        if (result == null) {
            result = getGroupFromDatabase(period, propertyKey);

            statisticsGroupCache.put(groupKey, result);
        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(getFinishLogStatement("getGroup"));
        }

        return result;
    }

    protected IStatisticsGroup getGroupFromDatabase(final String period, final String propertyKey) throws ModelException {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(getStartLogStatement("getGroupFromDatabase", period, propertyKey));
        }

        IStatisticsGroup result = null;

        try {
            Node propertyLevel = getStatisticsLevelNode(DimensionType.PROPERTY, propertyKey);
            Node periodLevel = getStatisticsLevelNode(DimensionType.TIME, period);

            Node groupNode = statisticsService.getGroup(propertyLevel, periodLevel);

            result = createStatisticsGroup(groupNode);
        } catch (ServiceException e) {
            processException("Error on getting StatisticsGroup from Database", e);
        }


        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(getFinishLogStatement("getGroupFromDatabase"));
        }

        return result;
    }

    protected IStatisticsGroup createStatisticsGroup(final Node node) throws ModelException {
        StatisticsGroup result = null;

        try {
            String name = getNodeService().getNodeName(node);

            result = new StatisticsGroup(node);
            result.setNodeType(StatisticsNodeType.GROUP);
            result.setName(name);
        } catch (ServiceException e) {
            processException("Error on converting node to StatisticsGroup", e);
        }

        return result;
    }

    @Override
    public IStatisticsRow getStatisticsRow(final IStatisticsGroup group, final long startDate, final long endDate, final String period) {
        // TODO Auto-generated method stub
        return null;
    }

    protected Node getStatisticsLevelNode(final DimensionType dimensionType, final String key) throws ModelException {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(getStartLogStatement("getStatisticsLevelNode", dimensionType, key));
        }

        Node result = getStatisticsLevelNodeFromCache(dimensionType, key);
        if (result == null) {
            try {
                result = statisticsService.getStatisticsLevel(getRootNode(), dimensionType, key);

                addStatisticsLevelNodeToCache(dimensionType, key, result);
            } catch (ServiceException e) {
                processException("Exception on searching for Statistics Level", e);
            }
        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(getFinishLogStatement("getStatisticsLevelNode"));
        }

        return result;
    }

    protected Node getStatisticsLevelNodeFromCache(final DimensionType dimensionType, final String key) {
        Map<String, Node> dimensionCache = statisticsLevelCache.get(dimensionType);
        if (dimensionCache == null) {
            dimensionCache = new HashMap<String, Node>();
            statisticsLevelCache.put(dimensionType, dimensionCache);
        }

        return dimensionCache.get(key);
    }

    protected void addStatisticsLevelNodeToCache(final DimensionType dimensionType, final String key, final Node value) {
        Map<String, Node> dimensionCache = statisticsLevelCache.get(dimensionType);
        if (dimensionCache == null) {
            dimensionCache = new HashMap<String, Node>();
            statisticsLevelCache.put(dimensionType, dimensionCache);
        }

        dimensionCache.put(key, value);
    }
}
