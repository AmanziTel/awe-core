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

import java.awt.Color;

import org.amanzi.neo.services.model.IDataElement;

/**
 * TODO Purpose of 
 * <p>
 *
 * </p>
 * @author lagutko_n
 * @since 1.0.0
 */
public interface IDistributionBar extends Comparable<IDistributionBar> {

    /**
     * Returns Color of this Bar
     *
     * @return
     */
    public Color getColor();
    
    /**
     * Set Color for this bar
     *
     * @param color
     */
    public void setColor(Color color);
    
    /**
     * Number of Data Elements in this Bar
     *
     * @return
     */
    public int getCount();
    
    /**
     * Name of this Bar
     *
     * @return
     */
    public String getName();
    
    /**
     * Root Element of Bar
     *
     * @return
     */
    public IDataElement getRootElement();
}

