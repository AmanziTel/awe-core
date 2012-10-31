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

package org.amanzi.awe.correlation.provider.impl;

import org.amanzi.awe.correlation.model.CorrelationNodeTypes;
import org.amanzi.awe.correlation.model.ICorrelationModel;
import org.amanzi.awe.correlation.model.impl.CorrelationModel;
import org.amanzi.awe.correlation.nodeproperties.ICorrelationProperties;
import org.amanzi.awe.correlation.provider.ICorrelationModelProvider;
import org.amanzi.awe.correlation.service.ICorrelationService;
import org.amanzi.neo.models.exceptions.DuplicatedModelException;
import org.amanzi.neo.models.exceptions.ModelException;
import org.amanzi.neo.models.impl.internal.AbstractModel;
import org.amanzi.neo.models.measurement.IMeasurementModel;
import org.amanzi.neo.models.network.INetworkModel;
import org.amanzi.neo.nodeproperties.IGeneralNodeProperties;
import org.amanzi.neo.nodeproperties.INetworkNodeProperties;
import org.amanzi.neo.nodeproperties.ITimePeriodNodeProperties;
import org.amanzi.neo.nodetypes.INodeType;
import org.amanzi.neo.providers.impl.internal.AbstractNamedModelProvider;
import org.amanzi.neo.services.INodeService;
import org.amanzi.neo.services.exceptions.ServiceException;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.log4j.Logger;
import org.neo4j.graphdb.Node;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Vladislav_Kondratenko
 * @since 1.0.0
 */
public class CorrelationModelProvider extends AbstractNamedModelProvider<ICorrelationModel, INetworkModel, CorrelationModel>
        implements
            ICorrelationModelProvider {

    @SuppressWarnings("unused")
    private static final class CorrelationModelKey implements IKey {

        private final INetworkModel networkModel;

        private final IMeasurementModel measurementModel;

        private final String correlationProperty;

        private final String correlatedProperty;

        /**
         * @param networkModel
         * @param measurementModel
         * @param correlatedProperty
         * @param correlationProperty
         */
        public CorrelationModelKey(final INetworkModel networkModel, final IMeasurementModel measurementModel,
                final String correlationProperty, final String correlatedProperty) {
            super();
            this.networkModel = networkModel;
            this.measurementModel = measurementModel;
            this.correlationProperty = correlationProperty;
            this.correlatedProperty = correlatedProperty;
        }

        @Override
        public boolean equals(final Object o) {
            return EqualsBuilder.reflectionEquals(this, o, true);
        }

        @Override
        public int hashCode() {
            return HashCodeBuilder.reflectionHashCode(this, true);
        }

    }

    private static final Logger LOGGER = Logger.getLogger(CorrelationModelProvider.class);

    private final INetworkNodeProperties networkNodeProperties;

    private final IGeneralNodeProperties generalNodeProperties;

    private final ICorrelationService correlationService;

    private final INodeService nodeService;

    private final ICorrelationProperties correlationProperties;

    private final ITimePeriodNodeProperties timePeriodNodePropertie;

    /**
     * @param networkNodeProperties
     * @param measurementNodeProperties
     * @param generalNodeProperties
     * @param correlationService
     * @param nodeService
     */
    public CorrelationModelProvider(final INetworkNodeProperties networkNodeProperties,
            final IGeneralNodeProperties generalNodeProperties, final ICorrelationProperties correlationProperties,
            final ICorrelationService correlationService, final INodeService nodeService,
            final ITimePeriodNodeProperties timePeriodNodeProperties) {
        super(nodeService, generalNodeProperties);
        this.networkNodeProperties = networkNodeProperties;
        this.generalNodeProperties = generalNodeProperties;
        this.correlationService = correlationService;
        this.nodeService = nodeService;
        this.correlationProperties = correlationProperties;
        this.timePeriodNodePropertie = timePeriodNodeProperties;
    }

    @Override
    public ICorrelationModel createCorrelationModel(final INetworkModel networkModel, final IMeasurementModel correlatedModel,
            final String correlationProperty, final String correlatedProperty) throws ModelException {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(getStartLogStatement("createCorrelation", networkModel, correlatedModel));
        }

        if (findCorrelationModel(networkModel, correlatedModel, correlationProperty, correlatedProperty) != null) {
            throw new DuplicatedModelException(getModelClass(), "correlation_model", networkModel);
        }

        final IKey key = new CorrelationModelKey(networkModel, correlatedModel, correlationProperty, correlatedProperty);

        CorrelationModel result = null;

        final AbstractModel parentModel = (AbstractModel)networkModel;
        final AbstractModel measurementModel = (AbstractModel)correlatedModel;

        try {
            final Node correlationRoot = correlationService.createCorrelationModelNode(parentModel.getRootNode(),
                    measurementModel.getRootNode(), correlationProperty, correlatedProperty);

            if (correlationRoot != null) {
                result = initializeFromNode(correlationRoot);

                initializeCorrelationModel(result, networkModel, correlatedModel);

                addToCache(result, key);
            }
        } catch (final ServiceException e) {
            processException("Error on searching for a CorrelationModel Model <" + networkModel + ", " + correlatedModel + ">", e);
        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(getFinishLogStatement("createCorrelation"));
        }

        return result;
    }

    @Override
    protected CorrelationModel createInstance() {
        return new CorrelationModel(correlationService, nodeService, generalNodeProperties, networkNodeProperties,
                correlationProperties, timePeriodNodePropertie);
    }

    @Override
    public ICorrelationModel findCorrelationModel(final INetworkModel networkModel, final IMeasurementModel correlatedModel,
            final String correlationProperty, final String correlatedProperty) throws ModelException {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(getStartLogStatement("find correlation model", networkModel, correlatedModel));
        }

        final IKey key = new CorrelationModelKey(networkModel, correlatedModel, correlationProperty, correlatedProperty);

        CorrelationModel result = getFromCache(key);

        if (result == null) {
            LOGGER.info("Creating new Correlation Model <" + networkModel + ", " + correlatedModel + "> in Database");

            final AbstractModel parentModel = (AbstractModel)networkModel;
            final AbstractModel measurementModel = (AbstractModel)correlatedModel;

            try {
                final Node correlationRoot = correlationService.findCorrelationModelNode(parentModel.getRootNode(),
                        measurementModel.getRootNode(), correlationProperty, correlatedProperty);

                if (correlationRoot != null) {
                    result = initializeFromNode(correlationRoot);

                    initializeCorrelationModel(result, networkModel, correlatedModel);

                    addToCache(result, key);
                }
            } catch (final ServiceException e) {
                processException("Error on searching for a Correlation Model <" + networkModel + ", " + correlatedModel + ">", e);
            }
        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(getFinishLogStatement("getCorrelation"));
        }

        return result;
    }

    @Override
    protected Class< ? extends ICorrelationModel> getModelClass() {
        return CorrelationModel.class;
    }

    @Override
    protected INodeType getModelType() {
        return CorrelationNodeTypes.CORRELATION_MODEL;
    }

    /**
     * @param result
     * @param networkModel
     * @param measurementModel
     */
    private void initializeCorrelationModel(final CorrelationModel result, final INetworkModel networkModel,
            final IMeasurementModel measurementModel) {
        result.setCorrelatedModels(networkModel, measurementModel);

    }

}
