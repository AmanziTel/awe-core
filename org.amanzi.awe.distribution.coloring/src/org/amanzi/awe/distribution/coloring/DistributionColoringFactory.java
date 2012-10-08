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

package org.amanzi.awe.distribution.coloring;

import org.amanzi.awe.distribution.coloring.internal.DistributionColoringPlugin;
import org.amanzi.awe.distribution.model.IDistributionModel;
import org.amanzi.awe.distribution.provider.IDistributionModelProvider;
import org.amanzi.awe.render.core.coloring.IColoringInterceptor;
import org.amanzi.awe.render.core.coloring.IColoringInterceptorFactory;
import org.amanzi.neo.models.IModel;
import org.amanzi.neo.models.exceptions.ModelException;
import org.amanzi.neo.models.render.IGISModel;
import org.amanzi.neo.models.statistics.IPropertyStatisticalModel;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Nikolay Lagutko (nikolay.lagutko@amanzitel.com)
 * @since 1.0.0
 */
public class DistributionColoringFactory implements IColoringInterceptorFactory {

    private final IDistributionModelProvider distributionModelProvider;

    /**
     * 
     */
    public DistributionColoringFactory() {
        this.distributionModelProvider = DistributionColoringPlugin.getDefault().getDistributionModelProvider();
    }

    @Override
    public int compareTo(final IColoringInterceptorFactory arg0) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public boolean accept(final IGISModel gisModel) {
        return getDistributionModel(gisModel) != null;
    }

    private IDistributionModel getDistributionModel(final IGISModel gisModel) {
        IDistributionModel result = null;

        final IModel model = gisModel.getSourceModel();

        if (model instanceof IPropertyStatisticalModel) {
            final IPropertyStatisticalModel distributionalModel = (IPropertyStatisticalModel)model;

            try {
                result = distributionModelProvider.getCurrentDistribution(distributionalModel);
            } catch (final ModelException e) {
                // TODO: LN: 8.10.2012, handle exception
            }
        }

        return result;
    }

    @Override
    public IColoringInterceptor createInterceptor(final IGISModel gisModel) {
        final IDistributionModel distributionModel = getDistributionModel(gisModel);

        if (distributionModel != null) {
            return new DistributionColoringInterceptor(distributionModel);
        }

        return null;
    }

}
