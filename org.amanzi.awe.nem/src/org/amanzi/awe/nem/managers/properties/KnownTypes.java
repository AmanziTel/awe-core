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

import org.amanzi.awe.nem.exceptions.ParsersExceptions;
import org.apache.commons.lang3.StringUtils;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Vladislav_Kondratenko
 * @since 1.0.0
 */
public enum KnownTypes {

    STRING("String", String.class) {
        @Override
        public Object parseValue(String value) throws ParsersExceptions {
            if (StringUtils.isEmpty(value)) {
                throw new ParsersExceptions(String.format(ERROR_MESSAGE_FORMAT, value, "String"), null);
            }
            return value;
        }

        @Override
        public Object getDefaultValue() {
            return "";
        }
    },
    DOUBLE("Double", Double.class) {
        @Override
        public Object parseValue(String value) throws ParsersExceptions {
            Object result = null;
            try {
                result = Double.parseDouble(value);
            } catch (Exception e) {
                throw new ParsersExceptions(String.format(ERROR_MESSAGE_FORMAT, value, "Double"), null);
            }
            return result;
        }

        @Override
        public Object getDefaultValue() {
            return 0d;
        }
    },
    LONG("Long", Long.class) {
        @Override
        public Object parseValue(String value) throws ParsersExceptions {
            Object result = null;
            try {
                result = Long.parseLong(value);
            } catch (Exception e) {
                throw new ParsersExceptions(String.format(ERROR_MESSAGE_FORMAT, value, "Long"), null);
            }
            return result;
        }

        @Override
        public Object getDefaultValue() {
            return 0l;
        }
    },
    INTEGER("Integer", Integer.class) {
        @Override
        public Object parseValue(String value) throws ParsersExceptions {
            Object result = null;
            try {
                result = Integer.parseInt(value);
            } catch (Exception e) {
                throw new ParsersExceptions(String.format(ERROR_MESSAGE_FORMAT, value, "Integer"), null);
            }
            return result;
        }

        @Override
        public Object getDefaultValue() {
            return 0;
        }
    },
    OBJECT("OBJECT", Object.class) {
        @Override
        public Object parseValue(String value) throws ParsersExceptions {
            return value;
        }

        @Override
        public Object getDefaultValue() {
            return StringUtils.EMPTY;
        }
    };

    private static final String ERROR_MESSAGE_FORMAT = "can't parse value %s to type %s";

    private String id;

    private String errorMessage;

    private Class< ? > clazz;

    private KnownTypes(String id, Class< ? > clazz) {
        this.id = id;
        this.clazz = clazz;
    }

    public String getId() {
        return id;
    }

    /**
     * @return Returns the clazz.
     */
    public Class< ? > getClazz() {
        return clazz;
    }

    public Object parse(String value) {
        Object result;
        try {
            result = parseValue(value);
            errorMessage = null;
        } catch (ParsersExceptions e) {
            errorMessage = e.getMessage();
            result = null;
        }
        return result;
    }

    /**
     * @return Returns the errorMessage.
     */
    public String getErrorMessage() {
        return errorMessage;
    }

    public static final KnownTypes getTypeById(String id) {
        for (KnownTypes type : values()) {
            if (type.getId().equals(id)) {
                return type;
            }
        }
        return null;
    }

    public static final KnownTypes getTypeByClass(Class< ? > clazz) {
        for (KnownTypes type : values()) {
            if (type.getClazz().equals(clazz)) {
                return type;
            }
        }
        return null;
    }

    /**
     * @param value
     */
    protected abstract Object parseValue(String value) throws ParsersExceptions;

    /**
     * @return
     */
    public abstract Object getDefaultValue();

    /**
     * @param value
     * @return
     */
    public static KnownTypes defineClass(Object value) {
        if (value instanceof String) {
            return STRING;
        } else if (value instanceof Integer) {
            return INTEGER;
        } else if (value instanceof Long) {
            return LONG;
        } else if (value instanceof Double) {
            return DOUBLE;
        }
        return OBJECT;
    }
}
