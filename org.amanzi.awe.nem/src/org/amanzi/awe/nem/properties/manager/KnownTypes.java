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

package org.amanzi.awe.nem.properties.manager;

import org.amanzi.awe.nem.properties.ParsersExceptions;
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

    STRING("String") {
        @Override
        public Object parseValue(String value) throws ParsersExceptions {
            if (StringUtils.isEmpty(value)) {
                throw new ParsersExceptions(String.format(ERROR_MESSAGE_FORMAT, value, "String"), null);
            }
            return value;
        }
    },
    DOUBLE("Double") {
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
    },
    LONG("Long") {
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
    },
    INTEGER("Integer") {
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
    };

    private static final String ERROR_MESSAGE_FORMAT = "can't parse value %s to type %s";

    private String id;

    private String errorMessage;

    private KnownTypes(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public Object parse(String value) {
        try {
            parseValue(value);
            errorMessage = null;
        } catch (ParsersExceptions e) {
            errorMessage = e.getMessage();
            value = null;
        }
        return value;
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

    /**
     * @param value
     */
    protected abstract Object parseValue(String value) throws ParsersExceptions;
}
