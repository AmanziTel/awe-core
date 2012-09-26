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

package org.amanzi.awe.distribution.model.type.impl.internal;

import java.awt.Color;

import org.amanzi.awe.distribution.model.type.IRange;
import org.amanzi.awe.filters.IFilter;

/**
 * TODO Purpose of
 * <p>
 *
 * </p>
 * @author Nikolay Lagutko (nikolay.lagutko@amanzitel.com)
 * @since 1.0.0
 */
public class SimpleRange implements IRange {

    private final String name;

    private final IFilter filter;

    public SimpleRange(final String name, final IFilter filter) {
        this.name = name;
        this.filter = filter;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public IFilter getFilter() {
        return filter;
    }

    @Override
    public Color getColor() {
        return null;
    }

}
