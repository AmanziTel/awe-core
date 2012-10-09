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

package org.amanzi.awe.distribution.provider.impl;

import org.amanzi.awe.distribution.model.IDistributionModel;
import org.amanzi.awe.distribution.model.impl.DistributionModel;
import org.amanzi.awe.distribution.model.type.IDistributionType;
import org.amanzi.awe.distribution.properties.IDistributionNodeProperties;
import org.amanzi.awe.distribution.provider.IDistributionModelProvider;
import org.amanzi.awe.distribution.service.IDistributionService;
import org.amanzi.neo.models.exceptions.DuplicatedModelException;
import org.amanzi.neo.models.exceptions.ModelException;
import org.amanzi.neo.models.impl.internal.AbstractModel;
import org.amanzi.neo.models.statistics.IPropertyStatisticalModel;
import org.amanzi.neo.nodeproperties.IGeneralNodeProperties;
import org.amanzi.neo.providers.impl.internal.AbstractModelProvider;
import org.amanzi.neo.services.INodeService;
import org.amanzi.neo.services.exceptions.ServiceException;
import org.amanzi.neo.services.statistics.IPropertyStatisticsNodeProperties;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
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
public class DistributionModelProvider extends AbstractModelProvider<DistributionModel, IDistributionModel>
        implements
            IDistributionModelProvider {

    private static final Logger LOGGER = Logger.getLogger(DistributionModelProvider.class);

    @SuppressWarnings("unused")
    private final static class DistributionCacheKey implements IKey {

        private final IDistributionType< ? > distributionType;

        private final IPropertyStatisticalModel sourceModel;

        /**
         * @param distributionType
         * @param sourceModel
         */
        public DistributionCacheKey(final IDistributionType< ? > distributionType, final IPropertyStatisticalModel sourceModel) {
            super();
            this.distributionType = distributionType;
            this.sourceModel = sourceModel;
        }

        @Override
        public int hashCode() {
            return HashCodeBuilder.reflectionHashCode(this, true);
        }

        @Override
        public boolean equals(final Object o) {
            return EqualsBuilder.reflectionEquals(this, o, true);
        }

    }

    private final INodeService nodeService;

    private final IGeneralNodeProperties generalNodeProperties;

    private final IDistributionService distributionService;

    private final IDistributionNodeProperties distributionNodeProperties;

    private final IPropertyStatisticsNodeProperties countNodeProperties;

    public DistributionModelProvider(final INodeService nodeService, final IGeneralNodeProperties generalNodeProperties,
            final IDistributionService distributionService, final IDistributionNodeProperties distributionNodeProperties,
            final IPropertyStatisticsNodeProperties countNodeProperties) {
        super();

        this.nodeService = nodeService;
        this.generalNodeProperties = generalNodeProperties;
        this.distributionService = distributionService;
        this.distributionNodeProperties = distributionNodeProperties;
        this.countNodeProperties = countNodeProperties;
    }

    @Override
    protected DistributionModel createInstance() {
        return new DistributionModel(nodeService, generalNodeProperties, distributionService, distributionNodeProperties,
                countNodeProperties);
    }

    @Override
    protected Class< ? extends IDistributionModel> getModelClass() {
        return IDistributionModel.class;
    }

    @Override
    public IDistributionModel findDistribution(final IPropertyStatisticalModel analyzedModel,
            final IDistributionType< ? > distributionType) throws ModelException {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(getStartLogStatement("getDistribution", analyzedModel, distributionType));
        }

        final IKey key = new DistributionCacheKey(distributionType, analyzedModel);

        DistributionModel result = getFromCache(key);

        if (result == null) {
            LOGGER.info("Creating new Distribution Model <" + analyzedModel + ", " + distributionType + "> in Database");

            final AbstractModel parentModel = (AbstractModel)analyzedModel;

            try {
                final Node distributionRoot = distributionService.findDistributionNode(parentModel.getRootNode(), distributionType);

                if (distributionRoot != null) {
                    result = initializeFromNode(distributionRoot);

                    initializeDistributionModel(result, analyzedModel);

                    addToCache(result, key);
                }
            } catch (final ServiceException e) {
                processException("Error on searching for a Distribution Model <" + analyzedModel + ", " + distributionType + ">", e);
            }
        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(getFinishLogStatement("getDistribution"));
        }

        return result;
    }

    @Override
    public IDistributionModel getCurrentDistribution(final IPropertyStatisticalModel analyzedModel) throws ModelException {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(getStartLogStatement("getCurrentDistribution", analyzedModel));
        }

        DistributionModel result = null;

        final AbstractModel parentModel = (AbstractModel)analyzedModel;

        try {
            final Node distributionRoot = distributionService.getCurrentDistribution(parentModel.getRootNode());

            if (distributionRoot != null) {
                result = initializeFromNode(distributionRoot);

                initializeDistributionModel(result, analyzedModel);
            }
        } catch (final ServiceException e) {
            processException("Error on searching for a curernt Distribution Model of " + analyzedModel + "", e);
        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(getFinishLogStatement("getCurrentDistribution"));
        }

        return result;
    }

    private void initializeDistributionModel(final DistributionModel distributionModel, final IPropertyStatisticalModel sourceModel) {
        distributionModel.setSourceModel(sourceModel);
    }

    @Override
    public IDistributionModel createDistribution(final IPropertyStatisticalModel analyzedModel,
            final IDistributionType< ? > distributionType) throws ModelException {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(getStartLogStatement("createDistribution", analyzedModel, distributionType));
        }

        if (findDistribution(analyzedModel, distributionType) != null) {
            throw new DuplicatedModelException(getModelClass(), "distributionType", distributionType);
        }

        final IKey key = new DistributionCacheKey(distributionType, analyzedModel);

        DistributionModel result = null;

        final AbstractModel parentModel = (AbstractModel)analyzedModel;

        try {
            final Node distributionRoot = distributionService.createDistributionNode(parentModel.getRootNode(), distributionType);

            if (distributionRoot != null) {
                result = initializeFromNode(distributionRoot);

                initializeDistributionModel(result, analyzedModel);

                addToCache(result, key);
            }
        } catch (final ServiceException e) {
            processException("Error on searching for a Distribution Model <" + analyzedModel + ", " + distributionType + ">", e);
        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(getFinishLogStatement("createDistribution"));
        }

        return result;
    }

}
