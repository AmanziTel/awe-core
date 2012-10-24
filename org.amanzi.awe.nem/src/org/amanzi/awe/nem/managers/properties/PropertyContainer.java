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

package org.amanzi.awe.nem.managers.properties;


/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Vladislav_Kondratenko
 * @since 1.0.0
 */
public class PropertyContainer implements Comparable<PropertyContainer> {

    private Object value;

    private String name;

    private KnownTypes type;

    public PropertyContainer(final String name, final KnownTypes type) {
        this.name = name;
        this.type = type;
        value = type.getDefaultValue();
    }

    /**
     * @param string
     * @param string2
     * @param id
     */
    public PropertyContainer(final String name, final KnownTypes type, final Object value) {
        this(name, type);
        this.value = value;
    }

    @Override
    public int compareTo(final PropertyContainer o) {
        return name.compareToIgnoreCase(o.getName());
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        PropertyContainer other = (PropertyContainer)obj;
        if (name == null) {
            if (other.name != null) {
                return false;
            }
        } else if (!name.equals(other.name)) {
            return false;
        }
        return true;
    }

    /**
     * @return Returns the name.
     */
    public String getName() {
        return name;
    }

    /**
     * @return Returns the type.
     */
    public KnownTypes getType() {
        return type;
    }

    /**
     * @return Returns the defaultValue.
     */
    public Object getValue() {
        return value;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (name == null ? 0 : name.hashCode());
        return result;
    }

    /**
     * @param name The name to set.
     */
    public void setName(final String name) {
        this.name = name;
    }

    /**
     * @param type The type to set.
     */
    public void setType(final KnownTypes type) {
        this.type = type;
    }

    /**
     * @param defaultValue The defaultValue to set.
     */
    public void setValue(final Object defaultValue) {
        this.value = defaultValue;
    }

}
