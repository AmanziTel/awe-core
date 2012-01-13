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

package org.amanzi.neo.services.filters;

/**
 * Extends all methods from Filter class and add support of Name parameters
 * <p>
 * </p>
 * 
 * @author Vladislav_Kondratenko
 * @since 1.0.0
 */
public class NamedFilter extends Filter implements INamedFilter {
    /** long serialVersionUID field */
    private static final long serialVersionUID = -8231241185471872717L;
    private final String name;

    public NamedFilter(FilterType filterType, ExpressionType expressionType, String name) {
        super(filterType, expressionType);
        this.name = name;
    }

    public NamedFilter(FilterType filterType, String name) {
        super(filterType, ExpressionType.AND);
        this.name = name;
    }

    public NamedFilter(ExpressionType expressionType, String name) {
        super(FilterType.EQUALS, expressionType);
        this.name = name;
    }

    public NamedFilter(String name) {
        super(FilterType.EQUALS, ExpressionType.AND);
        this.name = name;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public int hashCode() {
        super.hashCode();
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (!super.equals(obj)) {
            return false;
        }
        if (this == obj)
            return true;
        if (!super.equals(obj))
            return false;
        if (getClass() != obj.getClass())
            return false;
        NamedFilter other = (NamedFilter)obj;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        return true;
    }
}
