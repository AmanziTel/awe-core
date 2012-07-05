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

package org.amanzi.neo.services.impl.statistics.internal;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Nikolay Lagutko (nikolay.lagutko@amanzitel.com)
 * @since 1.0.0
 */
public class PropertyVault {

    private enum ClassType {
        INTEGER {
            @Override
            public boolean canConvert(final ClassType anotherClass) {
                switch (anotherClass) {
                case FLOAT:
                case LONG:
                    return true;
                default:
                    return super.canConvert(anotherClass);
                }
            }
        },
        STRING, FLOAT, LONG;

        public boolean canConvert(final ClassType anotherClass) {
            switch (anotherClass) {
            case STRING:
                return true;
            default:
                return false;
            }
        }
    }

    private final String propertyName;

    private boolean isChanged;

    private Map<Object, Integer> values = new HashMap<Object, Integer>();

    private ClassType classType;

    public PropertyVault(final String propertyName) {
        this.propertyName = propertyName;

        isChanged = false;
    }

    public void index(final Object value) {

    }

    public Set<Object> getValues() {
        return null;
    }

    public int getValueCount(final Object value) {
        return 0;
    }

    public boolean isChanged() {
        return isChanged;
    }

    public void setChanged(final boolean isChanged) {
        this.isChanged = isChanged;
    }

    protected ClassType defineClass(final Object value) {
        return null;
    }

    protected void updateToNewClass(final ClassType classType) {
        Map<Object, Integer> newValues = new HashMap<Object, Integer>();

        for (Entry<Object, Integer> oldValuesEntry : values.entrySet()) {
            Object updatedValue = updateToNewClass(oldValuesEntry.getKey(), classType);
            newValues.put(updatedValue, oldValuesEntry.getValue());
        }

        values = newValues;
    }

    private Object updateToNewClass(final Object value, final ClassType classType) {
        switch (classType) {
        case STRING:
            return value.toString();
        case FLOAT:
            if (value instanceof Number) {
                return ((Number)value).floatValue();
            }
            break;
        case LONG:
            if (value instanceof Number) {
                return ((Number)value).longValue();
            }
        default:
            break;
        }

        return null;
    }
}
