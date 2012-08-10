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

import org.amanzi.awe.statistics.model.IStatisticsModel;
import org.amanzi.awe.statistics.model.StatisticsNodeType;
import org.amanzi.awe.statistics.nodeproperties.IStatisticsNodeProperties;
import org.amanzi.awe.statistics.service.impl.StatisticsService.StatisticsRelationshipType;
import org.amanzi.neo.models.exceptions.ModelException;
import org.amanzi.neo.models.impl.internal.AbstractModel;
import org.amanzi.neo.nodeproperties.IGeneralNodeProperties;
import org.amanzi.neo.nodeproperties.ITimePeriodNodeProperties;
import org.amanzi.neo.services.INodeService;
import org.amanzi.neo.services.exceptions.ServiceException;
import org.apache.commons.lang3.StringUtils;
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

    private static final String STATISTICS_NAME_PATTERN = "{0} - {1}";

    private final ITimePeriodNodeProperties timePeriodNodeProperties;

    private final IStatisticsNodeProperties statisticsNodeProperties;

    private String templateName;

    private String aggregatedProperty;

    /**
     * @param nodeService
     * @param generalNodeProperties
     */
    public StatisticsModel(INodeService nodeService, IGeneralNodeProperties generalNodeProperties,
            ITimePeriodNodeProperties timePeriodNodeProperties, IStatisticsNodeProperties statisticsNodeProperties) {
        super(nodeService, generalNodeProperties);

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

    public void initialize(Node parentNode, String templateName, String propertyName) throws ModelException {
        assert !StringUtils.isEmpty(templateName);
        assert !StringUtils.isEmpty(propertyName);

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(getStartLogStatement("initialize", parentNode, templateName, propertyName));
        }

        try {
            String statisticsName = MessageFormat.format(STATISTICS_NAME_PATTERN, templateName, propertyName);

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
}
