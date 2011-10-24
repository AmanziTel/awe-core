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

import java.util.List;

import org.amanzi.neo.services.exceptions.AWEException;
import org.amanzi.neo.services.model.IModel;
import org.eclipse.core.runtime.IProgressMonitor;

/**
 * Distribution Model
 * 
 * @author grigoreva_a
 * @since 1.0.0
 */
public interface IDistributionModel extends IModel {
    
    /**
     * Returns Type of this Distribution
     *
     * @return
     */
    public IDistribution<?> getDistributionType();
    
    /**
     * Returns List of Distribution Bars
     *
     * @return
     */
    public List<IDistributionBar> getDistributionBars() throws AWEException;
    
    /**
     * Returns List of Distribution Bars 
     *
     * @param monitor
     * @return
     */
    public List<IDistributionBar> getDistributionBars(IProgressMonitor monitor) throws AWEException;
    
    /**
     * Updates info about Distribution Bar in Database
     *
     * @param bar 
     */
    public void updateBar(IDistributionBar bar);
    
    /**
     * Set this model as current/not-current distribution model of analyzed model
     *
     * @param isCurrent is this model a current model
     */
    public void setCurrent(boolean isCurrent) throws AWEException;

}
