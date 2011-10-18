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

package org.amanzi.neo.model.distribution;

import org.amanzi.neo.services.model.IPropertyStatisticalModel;

/**
 * Interface that describes model that can be analysed with Distribution
 * 
 * @author grigoreva_a
 * @since 1.0.0
 */
public interface IDistributionalModel extends IPropertyStatisticalModel {
    
    /**
     * Returns a Distribution Model by it's type
     *
     * @param nodeType type of Node to analyse
     * @param propertyName name of property for Distribution
     * @param distributionType type of Distribution
     * @return 
     */
    public IDistributionModel getDistributionModel(IDistribution<?> distributionType);

}
