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

package org.amanzi.awe.views.network.wrapper;

import org.amanzi.awe.ui.tree.wrapper.ITreeWrapper;
import org.amanzi.awe.ui.tree.wrapper.factory.impl.AbstractModelWrapperFactory;
import org.amanzi.neo.models.network.INetworkModel;
import org.amanzi.neo.providers.INetworkModelProvider;
import org.amanzi.neo.providers.IProjectModelProvider;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Nikolay Lagutko (nikolay.lagutko@amanzitel.com)
 * @since 1.0.0
 */
public class NetworkTreeWrapperFactory extends AbstractModelWrapperFactory<INetworkModel, INetworkModelProvider> {

    /**
     * @param provider
     * @param projectModelProvider
     */
    public NetworkTreeWrapperFactory(final INetworkModelProvider provider, final IProjectModelProvider projectModelProvider) {
        super(provider, projectModelProvider);
    }

    @Override
    protected ITreeWrapper createTreeWrapper(final INetworkModel model) {
        return new NetworkTreeWrapper(model);
    }

}
