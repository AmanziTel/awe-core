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

import org.amanzi.awe.correlation.model.ICorrelationModel;
import org.amanzi.awe.correlation.model.impl.CorrelationModel;
import org.amanzi.awe.correlation.provider.ICorrelationModelProvider;
import org.amanzi.awe.correlation.service.ICorrelationService;
import org.amanzi.neo.models.exceptions.DuplicatedModelException;
import org.amanzi.neo.models.exceptions.ModelException;
import org.amanzi.neo.models.impl.internal.AbstractModel;
import org.amanzi.neo.models.measurement.IMeasurementModel;
import org.amanzi.neo.models.network.INetworkModel;
import org.amanzi.neo.nodeproperties.IGeneralNodeProperties;
import org.amanzi.neo.nodeproperties.IMeasurementNodeProperties;
import org.amanzi.neo.nodeproperties.INetworkNodeProperties;
import org.amanzi.neo.providers.impl.internal.AbstractModelProvider;
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
public class CorrelationModelProvider extends AbstractModelProvider<CorrelationModel, ICorrelationModel>
        implements
            ICorrelationModelProvider {

    @SuppressWarnings("unused")
    private final static class CorrelationModelKey implements IKey {

        private final INetworkModel networkModel;

        private final IMeasurementModel measurementModel;

        /**
         * @param networkModel
         * @param measurementModel
         */
        public CorrelationModelKey(final INetworkModel networkModel, final IMeasurementModel measurementModel) {
            super();
            this.networkModel = networkModel;
            this.measurementModel = measurementModel;
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

    private final IMeasurementNodeProperties measurementNodeProperties;

    private final IGeneralNodeProperties generalNodeProperties;

    private final ICorrelationService correlationService;

    private final INodeService nodeService;

    /**
     * @param networkNodeProperties
     * @param measurementNodeProperties
     * @param generalNodeProperties
     * @param correlationService
     * @param nodeService
     */
    public CorrelationModelProvider(final INetworkNodeProperties networkNodeProperties,
            final IMeasurementNodeProperties measurementNodeProperties, final IGeneralNodeProperties generalNodeProperties,
            final ICorrelationService correlationService, final INodeService nodeService) {
        super();
        this.networkNodeProperties = networkNodeProperties;
        this.measurementNodeProperties = measurementNodeProperties;
        this.generalNodeProperties = generalNodeProperties;
        this.correlationService = correlationService;
        this.nodeService = nodeService;
    }

    @Override
    public ICorrelationModel createCorrelationModel(final INetworkModel networkModel, final IMeasurementModel correlatedModel)
            throws ModelException {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(getStartLogStatement("createDistribution", networkModel, correlatedModel));
        }

        if (findCorrelationModel(networkModel, correlatedModel) != null) {
            throw new DuplicatedModelException(getModelClass(), "correlation_model", networkModel);
        }

        final IKey key = new CorrelationModelKey(networkModel, correlatedModel);

        CorrelationModel result = null;

        final AbstractModel parentModel = (AbstractModel)networkModel;
        final AbstractModel measurementModel = (AbstractModel)correlatedModel;

        try {
            final Node distributionRoot = correlationService.createCorrelationModelNode(parentModel.getRootNode(),
                    measurementModel.getRootNode());

            if (distributionRoot != null) {
                result = initializeFromNode(distributionRoot);

                initializeCorrelationModel(result, networkModel, correlatedModel);

                addToCache(result, key);
            }
        } catch (final ServiceException e) {
            processException("Error on searching for a CorrelationModel Model <" + networkModel + ", " + correlatedModel + ">", e);
        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(getFinishLogStatement("createDistribution"));
        }

        return result;
    }

    @Override
    protected CorrelationModel createInstance() {
        return new CorrelationModel(correlationService, nodeService, generalNodeProperties, networkNodeProperties,
                measurementNodeProperties);
    }

    @Override
    public ICorrelationModel findCorrelationModel(final INetworkModel networkModel, final IMeasurementModel correlatedModel)
            throws ModelException {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(getStartLogStatement("find correlation model", networkModel, correlatedModel));
        }

        final IKey key = new CorrelationModelKey(networkModel, correlatedModel);

        CorrelationModel result = getFromCache(key);

        if (result == null) {
            LOGGER.info("Creating new Correlation Model <" + networkModel + ", " + correlatedModel + "> in Database");

            final AbstractModel parentModel = (AbstractModel)networkModel;
            final AbstractModel measurementModel = (AbstractModel)correlatedModel;

            try {
                final Node correlationRoot = correlationService.findCorrelationModelNode(parentModel.getRootNode(),
                        measurementModel.getRootNode());

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
            LOGGER.debug(getFinishLogStatement("getDistribution"));
        }

        return result;
    }

    @Override
    protected Class< ? extends ICorrelationModel> getModelClass() {
        return CorrelationModel.class;
    }

    /**
     * @param result
     * @param networkModel
     * @param measurementModel
     */
    private void initializeCorrelationModel(final CorrelationModel result, final INetworkModel networkModel,
            final IMeasurementModel measurementModel) {
        result.setMeasurementModel(networkModel, measurementModel);

    }

}
