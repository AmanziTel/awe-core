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

package org.amanzi.awe.statistics.provider.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.amanzi.awe.statistics.model.IStatisticsModel;
import org.amanzi.awe.statistics.model.impl.StatisticsModel;
import org.amanzi.awe.statistics.nodeproperties.IStatisticsNodeProperties;
import org.amanzi.awe.statistics.provider.IStatisticsModelProvider;
import org.amanzi.awe.statistics.service.IStatisticsService;
import org.amanzi.neo.models.exceptions.DuplicatedModelException;
import org.amanzi.neo.models.exceptions.ModelException;
import org.amanzi.neo.models.exceptions.ParameterInconsistencyException;
import org.amanzi.neo.models.impl.internal.AbstractModel;
import org.amanzi.neo.models.measurement.IMeasurementModel;
import org.amanzi.neo.models.project.IProjectModel;
import org.amanzi.neo.nodeproperties.IGeneralNodeProperties;
import org.amanzi.neo.nodeproperties.IMeasurementNodeProperties;
import org.amanzi.neo.nodeproperties.ITimePeriodNodeProperties;
import org.amanzi.neo.providers.impl.internal.AbstractModelProvider;
import org.amanzi.neo.services.INodeService;
import org.amanzi.neo.services.exceptions.ServiceException;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.neo4j.graphdb.Node;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Nikolay Lagutko (nikolay.lagutko@amanzitel.com)
 * @since 1.0.0
 */
public class StatisticsModelProvider extends AbstractModelProvider<StatisticsModel, IStatisticsModel>
        implements
            IStatisticsModelProvider {

    private static final Logger LOGGER = Logger.getLogger(StatisticsModelProvider.class);

    private final INodeService nodeService;

    private final IGeneralNodeProperties generalNodeProperties;

    private final ITimePeriodNodeProperties timePeriodNodeProperties;

    private final IStatisticsNodeProperties statisticsNodeProperties;

    private final IStatisticsService statisticsService;

    private IMeasurementNodeProperties measurementNodeProperties;

    public StatisticsModelProvider(final INodeService nodeService, final IGeneralNodeProperties generalNodeProperties,
            final ITimePeriodNodeProperties timePeriodNodeProperties, final IStatisticsNodeProperties statisticsNodeProperties,
            final IMeasurementNodeProperties measurementProperties, final IStatisticsService statisticsService) {
        this.nodeService = nodeService;
        this.generalNodeProperties = generalNodeProperties;
        this.timePeriodNodeProperties = timePeriodNodeProperties;
        this.statisticsNodeProperties = statisticsNodeProperties;
        this.statisticsService = statisticsService;
        this.measurementNodeProperties = measurementProperties;
    }

    @Override
    protected StatisticsModel createInstance() {
        return new StatisticsModel(statisticsService, nodeService, generalNodeProperties, timePeriodNodeProperties,
                statisticsNodeProperties, measurementNodeProperties);
    }

    @Override
    protected Class< ? extends IStatisticsModel> getModelClass() {
        return StatisticsModel.class;
    }

    @Override
    public IStatisticsModel find(final IMeasurementModel analyzedModel, final String template, final String propertyName)
            throws ModelException {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(getStartLogStatement("find", analyzedModel, template, propertyName));
        }

        // validate arguments
        if (analyzedModel == null) {
            throw new ParameterInconsistencyException("parent");
        }

        if (StringUtils.isEmpty(template)) {
            throw new ParameterInconsistencyException(statisticsNodeProperties.getTemplateNameProperty(), template);
        }

        if (StringUtils.isEmpty(propertyName)) {
            throw new ParameterInconsistencyException(statisticsNodeProperties.getAggregationPropertyNameProperty(), propertyName);
        }

        AbstractModel model = (AbstractModel)analyzedModel;

        IKey key = new MultiKey(new NodeKey(model.getRootNode()), new NameKey(template), new NameKey(propertyName));

        StatisticsModel result = getFromCache(key);

        if (result == null) {
            try {
                Node statisticsNode = statisticsService.findStatisticsNode(model.getRootNode(), template, propertyName);

                if (statisticsNode != null) {
                    result = initializeFromNode(statisticsNode);

                    addToCache(result, key);
                }
            } catch (ServiceException e) {
                processException("Exception on searching for a Statistics Model", e);
            }
        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(getFinishLogStatement("find"));
        }

        return result;
    }

    @Override
    public Iterable<IStatisticsModel> findAll(IMeasurementModel analyzedModel) throws ModelException {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(getStartLogStatement("find all statistics models from parent", analyzedModel));
        }

        // validate arguments
        if (analyzedModel == null) {
            throw new ParameterInconsistencyException("parent");
        }

        AbstractModel model = (AbstractModel)analyzedModel;

        List<IStatisticsModel> models = new ArrayList<IStatisticsModel>();

        try {
            Iterator<Node> statisticsNodes = statisticsService.findAllStatisticsNode(model.getRootNode());

            while (statisticsNodes.hasNext()) {
                StatisticsModel statistic = initializeFromNode(statisticsNodes.next());
                models.add(statistic);
            }

        } catch (ServiceException e) {
            processException("Exception on searching for a Statistics Models", e);
        }
        return models;
    }

    @Override
    public IStatisticsModel create(final IMeasurementModel analyzedModel, final String template, final String propertyName)
            throws ModelException {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(getStartLogStatement("create", analyzedModel, template, propertyName));
        }

        // validate arguments
        if (analyzedModel == null) {
            throw new ParameterInconsistencyException("parent");
        }

        if (StringUtils.isEmpty(template)) {
            throw new ParameterInconsistencyException(statisticsNodeProperties.getTemplateNameProperty(), template);
        }

        if (StringUtils.isEmpty(propertyName)) {
            throw new ParameterInconsistencyException(statisticsNodeProperties.getAggregationPropertyNameProperty(), propertyName);
        }

        // validate uniqueness
        if (find(analyzedModel, template, propertyName) != null) {
            throw new DuplicatedModelException(IProjectModel.class, statisticsNodeProperties.getTemplateNameProperty(), template);
        }

        AbstractModel parentModel = (AbstractModel)analyzedModel;

        StatisticsModel statisticsModel = createInstance();
        statisticsModel.initialize(parentModel.getRootNode(), template, propertyName);

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(getFinishLogStatement("create"));
        }

        return statisticsModel;
    }

}
