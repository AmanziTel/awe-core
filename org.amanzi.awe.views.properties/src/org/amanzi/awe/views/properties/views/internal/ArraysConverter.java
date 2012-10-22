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

package org.amanzi.awe.views.properties.views.internal;

import java.lang.reflect.InvocationTargetException;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Vladislav_Kondratenko
 * @since 1.0.0
 */
public enum ArraysConverter {

    INTEGER_ARRAY(Integer[].class) {
        @Override
        protected Object[] getArray(final int length) {
            return new Integer[length];
        }

        @Override
        protected Class< ? > getTypedConverter() {
            return Integer.class;
        }
    },
    LONG_ARRAY(Long[].class) {
        @Override
        protected Object[] getArray(final int length) {
            return new Long[length];
        }

        @Override
        protected Class< ? > getTypedConverter() {
            return Long.class;
        }
    },
    FLOAT_ARRAY(Integer[].class) {
        @Override
        protected Object[] getArray(final int length) {
            return new Integer[length];
        }

        @Override
        protected Class< ? > getTypedConverter() {
            return Float.class;
        }
    },
    DOUBLE_ARRAY(Double[].class) {
        @Override
        protected Object[] getArray(final int length) {
            return new Double[length];
        }

        @Override
        protected Class< ? > getTypedConverter() {
            return Double.class;
        }
    },
    STRING_ARRAY(String[].class) {
        @Override
        protected Object[] getArray(final int length) {
            return new String[length];
        }

        @Override
        protected Class< ? > getTypedConverter() {
            return String.class;
        }
    },
    CHAR_ARRAY(Character[].class) {
        @Override
        protected Object[] getArray(final int length) {
            return new Character[length];
        }

        @Override
        protected Class< ? > getTypedConverter() {
            return Character.class;
        }

        @Override
        protected Object getValue(final String newStringValue) throws IllegalArgumentException, SecurityException,
                InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
            if (newStringValue.length() > 1) {
                throw new IllegalArgumentException("can't cast value " + newStringValue + " to Charracter");
            } else {
                return new Character(newStringValue.charAt(0));
            }
        }
    };

    public static ArraysConverter findByType(final Class< ? > type) {
        for (ArraysConverter converter : values()) {
            if (converter.getType().equals(type)) {
                return converter;
            }
        }
        return null;
    }

    private Class< ? > type;

    private ArraysConverter(final Class< ? > type) {
        this.type = type;
    }

    public Object[] convertToArray(final String[] stringArray) throws IllegalArgumentException, SecurityException,
            InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        Object[] newArray = getArray(stringArray.length);
        for (int i = 0; i < stringArray.length; i++) {
            String newStringValue = stringArray[i];
            newArray[i] = getValue(newStringValue);
        }
        return newArray;
    }

    protected abstract Object[] getArray(int length);

    /**
     * @return Returns the type.
     */
    protected Class< ? > getType() {
        return type;
    }

    /**
     * @return
     */
    protected abstract Class< ? > getTypedConverter();

    /**
     * @param newStringValue
     * @return
     * @throws NoSuchMethodException
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     * @throws InstantiationException
     * @throws SecurityException
     * @throws IllegalArgumentException
     */
    protected Object getValue(final String newStringValue) throws IllegalArgumentException, SecurityException,
            InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        return getTypedConverter().getConstructor(newStringValue.getClass()).newInstance(newStringValue);
    }
}
