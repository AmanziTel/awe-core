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

package org.amanzi.neo.model.distribution.impl;

import org.amanzi.neo.model.distribution.IDistribution;
import org.amanzi.neo.model.distribution.IDistribution.ChartType;
import org.amanzi.neo.model.distribution.IDistributionalModel;

/**
 * Manager for Distribution types
 * 
 * @author lagutko_n
 * @since 1.0.0
 */
public class DistributionManager {
    
    /*
     * Instance of Manager
     */
    private static DistributionManager manager;
    
    /**
     * Returns instance of this Manager
     */
    public static DistributionManager getManager() {
        if (manager == null) {
            manager = new DistributionManager();
        }
        
        return manager;
    }
    
    /**
     * Private constructor, to prevent non-singleton access
     */
    private DistributionManager() {
        
    }
    
    public IDistribution getDistribution(IDistributionalModel model, String property, ChartType chartType) {
        return null;
    }

}
