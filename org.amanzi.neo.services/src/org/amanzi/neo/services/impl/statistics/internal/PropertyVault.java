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

import org.amanzi.neo.services.exceptions.ServiceException;
import org.amanzi.neo.services.exceptions.StatisticsConversionException;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Nikolay Lagutko (nikolay.lagutko@amanzitel.com)
 * @since 1.0.0
 */
public class PropertyVault {

    private static final int STATISTICS_LIMIT = 100;

    protected enum ClassType {
        INTEGER(Integer.class) {
            @Override
            public boolean canConvert(final ClassType anotherClass) {
                switch (anotherClass) {
                case DOUBLE:
                case LONG:
                    return true;
                default:
                    return super.canConvert(anotherClass);
                }
            }
        },
        STRING(String.class), DOUBLE(Double.class), LONG(Long.class), BOOLEAN(Boolean.class);

        private Class< ? > clazz;

        private ClassType(final Class< ? > clazz) {
            this.clazz = clazz;
        }

        public boolean canConvert(final ClassType anotherClass) {
            switch (anotherClass) {
            case STRING:
                return true;
            default:
                return false;
            }
        }

        public Class< ? > getDataClass() {
            return clazz;
        }

        public String getDataClassName() {
            return clazz.getName();
        }

        public static ClassType findByClass(final Class< ? > clazz) {
            for (ClassType singleType : values()) {
                if (singleType.clazz.equals(clazz)) {
                    return singleType;
                }
            }

            return null;
        }

        public static ClassType findByName(final String className) {
            for (ClassType singleType : values()) {
                if (singleType.clazz.getName().equals(className)) {
                    return singleType;
                }
            }

            return null;
        }
    }

    private final String propertyName;

    private boolean isChanged;

    private Map<Object, Integer> values = new HashMap<Object, Integer>();

    private ClassType classType;

    private boolean handleStatistics;

    public PropertyVault(final String propertyName) {
        this.propertyName = propertyName;

        isChanged = false;
        handleStatistics = true;
    }

    public void index(final Object value) throws ServiceException {
        if (handleStatistics) {
            if (classType == null) {
                defineClass(value);
            }

            if (!value.getClass().equals(classType.getDataClass())) {
                ClassType previousClass = classType;
                defineClass(value);

                if (!previousClass.canConvert(classType)) {
                    throw new StatisticsConversionException(previousClass.getDataClass(), classType.getDataClass(), value,
                            propertyName);
                }

                updateToNewClass(classType);
            }

            Integer valueCount = values.get(value);
            if (valueCount == null) {
                valueCount = 0;
            }
            valueCount++;

            values.put(value, valueCount);

            if (values.size() > STATISTICS_LIMIT) {
                handleStatistics = false;
                values.clear();
            }
        }

        isChanged = true;
    }

    public Set<Object> getValues() {
        return values.keySet();
    }

    public int getValueCount(final Object value) {
        return values.get(value);
    }

    public boolean isChanged() {
        return isChanged;
    }

    public void setChanged(final boolean isChanged) {
        this.isChanged = isChanged;
    }

    protected void defineClass(final Object value) {
        this.classType = ClassType.findByClass(value.getClass());
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
        case DOUBLE:
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

    public String getClassName() {
        return classType == null ? null : classType.getDataClassName();
    }

    public String getPropertyName() {
        return propertyName;
    }

    public Map<Object, Integer> getValuesMap() {
        return values;
    }

    public void setClass(final String className) {
        classType = ClassType.findByName(className);
    }

    public void addValue(final Object value, final int count) {
        values.put(value, count);
    }
}
