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

package org.amanzi.neo.model.distribution.types.ranges.impl;

import java.awt.Color;

import org.amanzi.neo.model.distribution.IRange;
import org.amanzi.neo.services.filters.ISimpleFilter;

/**
 * Simple Range for default Distributions
 * 
 * Contains only Name and Filter, but not a pre-defined Color
 * 
 * @author gerzog
 * @since 1.0.0
 */
public class SimpleRange implements IRange {
    
    /*
     * Name of this Range
     */
    private String name;
    
    /*
     * Filter for this Range
     */
    private ISimpleFilter filter;
    
    /**
     * Default constructor
     * 
     * @param name
     * @param filter
     */
    public SimpleRange(String name, ISimpleFilter filter) {
        this.name = name;
        this.filter = filter;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public ISimpleFilter getFilter() {
        return filter;
    }

    @Override
    public Color getColor() {
        return null;
    }

}
