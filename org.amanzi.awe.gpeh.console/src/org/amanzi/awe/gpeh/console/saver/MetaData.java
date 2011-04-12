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

package org.amanzi.awe.gpeh.console.saver;

import java.util.Collections;
import java.util.HashMap;

/**
 * <p>
 * MetaData provide information about meta data of structure
 * </p>
 * 
 * @author TsAr
 * @since 1.0.0
 */
public class MetaData {
    public final static String SUB_TYPE = "sub_type";
    private final String type;
    private final HashMap<String, Object> propertyMap = new HashMap<String, Object>();

    public MetaData(String type, String subType) {
        this(type, SUB_TYPE, subType);
    }

    public MetaData(String type, Object... additionalProperties) {
        this.type = type;
        if (additionalProperties != null) {
            for (int i = 0; i < additionalProperties.length - 1; i += 2) {
                propertyMap.put(String.valueOf(additionalProperties[i]), additionalProperties[i + 1]);
            }
        }
    }

    public String getType() {
        return type;
    }

    public String getSubType() {
        return (String)propertyMap.get(SUB_TYPE);
    }

    public Iterable<String> getPropertyKeys() {
        return Collections.unmodifiableCollection(propertyMap.keySet());
    }

    public Object getPropertyValues(String key) {
        return propertyMap.get(key);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((propertyMap == null) ? 0 : propertyMap.hashCode());
        result = prime * result + ((type == null) ? 0 : type.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof MetaData)) {
            return false;
        }
        MetaData other = (MetaData)obj;
        if (propertyMap == null) {
            if (other.propertyMap != null) {
                return false;
            }
        } else if (!propertyMap.equals(other.propertyMap)) {
            return false;
        }
        if (type == null) {
            if (other.type != null) {
                return false;
            }
        } else if (!type.equals(other.type)) {
            return false;
        }
        return true;
    }
}
