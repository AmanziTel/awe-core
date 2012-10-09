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

package org.amanzi.awe.distribution.engine;

import org.amanzi.awe.distribution.model.IDistributionModel;
import org.amanzi.awe.distribution.model.type.IDistributionType;
import org.amanzi.neo.models.exceptions.ModelException;
import org.amanzi.neo.models.statistics.IPropertyStatisticalModel;
import org.eclipse.core.runtime.IProgressMonitor;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Nikolay Lagutko (nikolay.lagutko@amanzitel.com)
 * @since 1.0.0
 */
public interface IDistributionEngine<T extends IPropertyStatisticalModel> {

    IDistributionModel build(final IDistributionType< ? > distributionType, final IProgressMonitor progressMonitor)
            throws ModelException;

}
