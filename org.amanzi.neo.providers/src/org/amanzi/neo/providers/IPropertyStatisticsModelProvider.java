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

package org.amanzi.neo.providers;

import org.amanzi.neo.models.exceptions.ModelException;
import org.amanzi.neo.models.statistics.IPropertyStatisticalModel;
import org.amanzi.neo.models.statistics.IPropertyStatisticsModel;
import org.amanzi.neo.providers.internal.IModelProvider;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Nikolay Lagutko (nikolay.lagutko@amanzitel.com)
 * @since 1.0.0
 */
public interface IPropertyStatisticsModelProvider extends IModelProvider<IPropertyStatisticsModel> {

    IPropertyStatisticsModel getPropertyStatistics(IPropertyStatisticalModel parent) throws ModelException;

}
