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

package org.amanzi.neo.providers.impl;

import org.amanzi.neo.models.exceptions.ModelException;
import org.amanzi.neo.models.impl.internal.AbstractModel;
import org.amanzi.neo.models.impl.statistics.PropertyStatisticsModel;
import org.amanzi.neo.models.statistics.IPropertyStatisticalModel;
import org.amanzi.neo.models.statistics.IPropertyStatisticsModel;
import org.amanzi.neo.nodeproperties.IGeneralNodeProperties;
import org.amanzi.neo.providers.IPropertyStatisticsModelProvider;
import org.amanzi.neo.providers.impl.internal.AbstractModelProvider;
import org.amanzi.neo.services.IPropertyStatisticsService;
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
public class PropertyStatisticsModelProvider extends AbstractModelProvider<PropertyStatisticsModel, IPropertyStatisticsModel>
        implements
            IPropertyStatisticsModelProvider {

    private static final Logger LOGGER = Logger.getLogger(PropertyStatisticsModelProvider.class);

    private final IPropertyStatisticsService statisticsService;

    private final IGeneralNodeProperties generalNodeProperties;

    public PropertyStatisticsModelProvider(final IPropertyStatisticsService statisticsService,
            final IGeneralNodeProperties generalNodeProperties) {
        super();
        this.statisticsService = statisticsService;
        this.generalNodeProperties = generalNodeProperties;
    }

    @Override
    public IPropertyStatisticsModel getPropertyStatistics(final IPropertyStatisticalModel parent) throws ModelException {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(getStartLogStatement("getPropertyStatistics", parent));
        }

        PropertyStatisticsModel result = null;

        if (parent instanceof AbstractModel) {
            Node parentNode = ((AbstractModel)parent).getRootNode();
            IKey key = new NodeKey(parentNode);
            result = getFromCache(key);
            if (result == null) {
                result = createInstance();
                result.initialize(parentNode);

                addToCache(result, key);
            }
        } else {
            throw new IllegalArgumentException("Cannot use not AbstractModel as a parent");
        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(getFinishLogStatement("getPropertyStatistics"));
        }

        return result;
    }

    @Override
    protected PropertyStatisticsModel createInstance() {
        return new PropertyStatisticsModel(generalNodeProperties, statisticsService);
    }

}
