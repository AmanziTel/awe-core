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

package org.amanzi.awe.filters.impl.internal;

import org.amanzi.awe.filters.IFilter;
import org.amanzi.neo.dto.IDataElement;

/**
 * TODO Purpose of
 * <p>
 *
 * </p>
 * @author Nikolay Lagutko (nikolay.lagutko@amanzitel.com)
 * @since 1.0.0
 */
public abstract class AbstractFilter<T extends Object> implements IFilter {

    private final String propertyName;

    protected AbstractFilter(final String propertyName) {
        this.propertyName = propertyName;
    }

    @SuppressWarnings("unchecked")
    protected T getElementValue(final IDataElement element) {
        return (T)element.get(propertyName);
    }
}
