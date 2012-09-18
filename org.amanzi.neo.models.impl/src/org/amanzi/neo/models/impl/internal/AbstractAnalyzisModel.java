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

package org.amanzi.neo.models.impl.internal;

import org.amanzi.neo.models.IAnalyzisModel;
import org.amanzi.neo.models.IModel;
import org.amanzi.neo.nodeproperties.IGeneralNodeProperties;
import org.amanzi.neo.services.INodeService;

/**
 * TODO Purpose of
 * <p>
 *
 * </p>
 * @author Nikolay Lagutko (nikolay.lagutko@amanzitel.com)
 * @since 1.0.0
 */
public abstract class AbstractAnalyzisModel<S extends IModel> extends AbstractModel implements IAnalyzisModel<S> {

    private S sourceModel;

    /**
     * @param nodeService
     * @param generalNodeProperties
     */
    protected AbstractAnalyzisModel(final INodeService nodeService, final IGeneralNodeProperties generalNodeProperties) {
        super(nodeService, generalNodeProperties);
    }

    @Override
    public S getSourceModel() {
        return sourceModel;
    }

    public void setSourceModel(final S sourceModel) {
        this.sourceModel = sourceModel;
    }


}
