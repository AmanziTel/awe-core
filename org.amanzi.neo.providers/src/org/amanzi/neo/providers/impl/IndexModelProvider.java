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

import org.amanzi.neo.models.IIndexModel;
import org.amanzi.neo.models.IModel;
import org.amanzi.neo.models.impl.IndexModel;
import org.amanzi.neo.nodeproperties.IGeneralNodeProperties;
import org.amanzi.neo.providers.IIndexModelProvider;
import org.amanzi.neo.providers.impl.internal.AbstractModelProvider;
import org.amanzi.neo.services.IIndexService;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Nikolay Lagutko (nikolay.lagutko@amanzitel.com)
 * @since 1.0.0
 */
public class IndexModelProvider extends AbstractModelProvider<IndexModel, IIndexModel> implements IIndexModelProvider {

    private final IIndexService indexService;

    private final IGeneralNodeProperties generalNodeProperties;

    public IndexModelProvider(final IIndexService indexService, final IGeneralNodeProperties generalNodeProperties) {
        this.indexService = indexService;
        this.generalNodeProperties = generalNodeProperties;
    }

    @Override
    public IIndexModel getIndexModel(final IModel model) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected IndexModel createInstance() {
        return new IndexModel(generalNodeProperties, indexService);
    }

    @Override
    protected Class< ? extends IIndexModel> getModelClass() {
        return IndexModel.class;
    }

}
