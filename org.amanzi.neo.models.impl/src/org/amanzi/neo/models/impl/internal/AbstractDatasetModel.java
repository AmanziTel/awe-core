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

import org.amanzi.neo.models.IIndexModel;
import org.amanzi.neo.models.exceptions.ModelException;
import org.amanzi.neo.models.statistics.IPropertyStatisticalModel;
import org.amanzi.neo.models.statistics.IPropertyStatisticsModel;
import org.amanzi.neo.nodeproperties.IGeneralNodeProperties;
import org.amanzi.neo.services.INodeService;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Nikolay Lagutko (nikolay.lagutko@amanzitel.com)
 * @since 1.0.0
 */
public abstract class AbstractDatasetModel extends AbstractNamedModel implements IPropertyStatisticalModel {

    private IIndexModel indexModel;

    private IPropertyStatisticsModel propertyStatisticsModel;

    /**
     * @param nodeService
     * @param generalNodeProperties
     */
    public AbstractDatasetModel(final INodeService nodeService, final IGeneralNodeProperties generalNodeProperties) {
        super(nodeService, generalNodeProperties);
    }

    @Override
    public void finishUp() throws ModelException {
        assert indexModel != null;
        assert propertyStatisticsModel != null;

        indexModel.finishUp();
        propertyStatisticsModel.finishUp();
    }

    /**
     * @param indexModel The indexModel to set.
     */
    public void setIndexModel(final IIndexModel indexModel) {
        this.indexModel = indexModel;
    }

    /**
     * @param propertyStatisticsModel The propertyStatisticsModel to set.
     */
    public void setPropertyStatisticsModel(final IPropertyStatisticsModel propertyStatisticsModel) {
        this.propertyStatisticsModel = propertyStatisticsModel;
    }

}
