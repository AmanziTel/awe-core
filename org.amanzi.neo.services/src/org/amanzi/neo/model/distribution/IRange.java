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

import org.amanzi.neo.services.filters.ISimpleFilter;

/**
 * Single Range of Distribution 
 * 
 * @author lagutko_n
 * @since 1.0.0
 */
public interface IRange {
    
    /**
     * Returns name of this Range
     *
     * @return
     */
    public String getName();
    
    /**
     * Returns Filter of this Range
     *
     * @return
     */
    public ISimpleFilter getFilter();
    
    /**
     * Returns Color of this Range
     *
     * @return
     */
    public Color getColor();

}
