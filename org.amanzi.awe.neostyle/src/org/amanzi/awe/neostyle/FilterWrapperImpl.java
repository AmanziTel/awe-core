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

package org.amanzi.awe.neostyle;

import java.io.Serializable;

import org.amanzi.neo.services.filters.Filter;
import org.amanzi.neo.services.filters.IFilter;

/**
 * TODO Purpose of 
 * <p>
 *
 * </p>
 * @author TsAr
 * @since 1.0.0
 */
public class FilterWrapperImpl<T extends BaseNeoStyle> implements IFilterWrapper,Serializable {
    /** long serialVersionUID field */
    private static final long serialVersionUID = 957287654452727949L;
    private Filter filter;
    private T style;
    
    public FilterWrapperImpl() {
        super();
    }    

    public FilterWrapperImpl(Filter filter, T style) {
        super();
        this.filter = filter;
        this.style = style;
    }

    public void setFilter(Filter filter) {
        this.filter = filter;
    }

    public void setStyle(T style) {
        this.style = style;
    }

    @Override
    public IFilter getFilter() {
        return filter;
    }

    @Override
    public T getStyle() {
        return style;
    }

}
