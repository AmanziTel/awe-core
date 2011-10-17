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

import org.amanzi.neo.services.enums.INodeType;

/**
 * Type of Distribution 
 * 
 * @author lagutko_n
 * @since 1.0.0
 */
public interface IDistribution {
    
    /**
     * Type of Chart
     * 
     * @author lagutko_n
     * @since 1.0.0
     */
    public static enum ChartType {
        COUNTS,
        PERCENTS,
        LOGARYTHMIC,
        CDF;
    }
    
    /**
     * Returns name of this Distribution
     *
     * @return
     */
    public String getName();
    
    /**
     * Returns list of Ranges of this Distribution
     *
     * @return
     */
    public List<IRange> getRanges();
    
    /**
     * Type of Node to Analyze
     *
     * @return
     */
    public INodeType getNodeType();
    
    /**
     * Returns total number of nodes to analyse
     *
     * @return
     */
    public int getCount();

}
