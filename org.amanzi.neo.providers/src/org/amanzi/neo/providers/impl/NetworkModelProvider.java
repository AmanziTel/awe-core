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

import org.amanzi.neo.models.impl.network.NetworkModel;
import org.amanzi.neo.models.network.INetworkModel;
import org.amanzi.neo.models.network.NetworkElementType;
import org.amanzi.neo.models.project.IProjectModel;
import org.amanzi.neo.nodeproperties.IGeneralNodeProperties;
import org.amanzi.neo.nodeproperties.IGeoNodeProperties;
import org.amanzi.neo.nodeproperties.INetworkNodeProperties;
import org.amanzi.neo.nodetypes.INodeType;
import org.amanzi.neo.providers.IGISModelProvider;
import org.amanzi.neo.providers.IIndexModelProvider;
import org.amanzi.neo.providers.INetworkModelProvider;
import org.amanzi.neo.providers.IPropertyStatisticsModelProvider;
import org.amanzi.neo.providers.impl.internal.AbstractDatasetModelProvider;
import org.amanzi.neo.services.INodeService;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Nikolay Lagutko (nikolay.lagutko@amanzitel.com)
 * @since 1.0.0
 */
public class NetworkModelProvider extends AbstractDatasetModelProvider<INetworkModel, IProjectModel, NetworkModel>
        implements
            INetworkModelProvider {

    private final INetworkNodeProperties networkNodeProperties;

    /**
     * @param nodeService
     * @param generalNodeProperties
     * @param indexModelProvider
     * @param propertyStatisticsModelProvider
     */
    public NetworkModelProvider(final INodeService nodeService, final IGeneralNodeProperties generalNodeProperties,
            final IIndexModelProvider indexModelProvider, final IPropertyStatisticsModelProvider propertyStatisticsModelProvider,
            final IGeoNodeProperties geoNodeProperties, final INetworkNodeProperties networkNodeProperties,
            final IGISModelProvider gisModelProvider) {
        super(nodeService, generalNodeProperties, indexModelProvider, propertyStatisticsModelProvider, geoNodeProperties,
                gisModelProvider);
        this.networkNodeProperties = networkNodeProperties;
    }

    @Override
    protected INodeType getModelType() {
        return NetworkElementType.NETWORK;
    }

    @Override
    protected NetworkModel createInstance() {
        return new NetworkModel(getNodeService(), getGeneralNodeProperties(), getGeoNodeProperties(), networkNodeProperties);
    }

    @Override
    protected Class< ? extends INetworkModel> getModelClass() {
        return NetworkModel.class;
    }

}
