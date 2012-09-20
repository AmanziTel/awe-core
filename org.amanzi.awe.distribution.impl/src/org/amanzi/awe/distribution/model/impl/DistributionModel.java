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

package org.amanzi.awe.distribution.model.impl;

import org.amanzi.awe.distribution.model.IDistributionModel;
import org.amanzi.awe.distribution.model.bar.IDistributionBar;
import org.amanzi.awe.distribution.model.type.IDistributionType;
import org.amanzi.neo.models.exceptions.ModelException;
import org.amanzi.neo.models.impl.internal.AbstractAnalyzisModel;
import org.amanzi.neo.models.statistics.IPropertyStatisticalModel;
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
public class DistributionModel extends AbstractAnalyzisModel<IPropertyStatisticalModel> implements IDistributionModel {

    /**
     * @param nodeService
     * @param generalNodeProperties
     */
    protected DistributionModel(final INodeService nodeService, final IGeneralNodeProperties generalNodeProperties) {
        super(nodeService, generalNodeProperties);
        // TODO Auto-generated constructor stub
    }

    @Override
    public void finishUp() throws ModelException {
        // TODO Auto-generated method stub

    }

    @Override
    public IDistributionType< ? > getDistributionType() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Iterable<IDistributionBar> getDistributionBars() throws ModelException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public int getDistributionBarsCount() throws ModelException {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public void setCurrent(final boolean isCurrent) throws ModelException {
        // TODO Auto-generated method stub

    }

    @Override
    public void updateBar(final IDistributionBar bar) throws ModelException {
        // TODO Auto-generated method stub

    }
}
