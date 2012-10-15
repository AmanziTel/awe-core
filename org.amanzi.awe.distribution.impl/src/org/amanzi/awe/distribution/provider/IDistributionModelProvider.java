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

package org.amanzi.awe.distribution.provider;

import java.util.Iterator;

import org.amanzi.awe.distribution.model.IDistributionModel;
import org.amanzi.awe.distribution.model.type.IDistributionType;
import org.amanzi.neo.models.exceptions.ModelException;
import org.amanzi.neo.models.project.IProjectModel;
import org.amanzi.neo.models.statistics.IPropertyStatisticalModel;
import org.amanzi.neo.providers.internal.IModelProvider;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Nikolay Lagutko (nikolay.lagutko@amanzitel.com)
 * @since 1.0.0
 */
public interface IDistributionModelProvider extends IModelProvider<IDistributionModel> {

    IDistributionModel findDistribution(IPropertyStatisticalModel analyzedModel, IDistributionType< ? > distributionType)
            throws ModelException;

    IDistributionModel getCurrentDistribution(IPropertyStatisticalModel analyzedModel) throws ModelException;

    IDistributionModel createDistribution(IPropertyStatisticalModel analyzedModel, IDistributionType< ? > distributionType)
            throws ModelException;

    Iterator<IDistributionModel> findAll(IPropertyStatisticalModel model) throws ModelException;

    Iterator<IDistributionModel> findAll(IProjectModel projectModel) throws ModelException;

}
