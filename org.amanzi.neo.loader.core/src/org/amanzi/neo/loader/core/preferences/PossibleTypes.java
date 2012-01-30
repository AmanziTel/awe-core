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

package org.amanzi.neo.loader.core.preferences;

import org.amanzi.neo.loader.core.saver.AbstractSaver;
import org.apache.commons.lang.StringUtils;

/**
 * @author Vladislav_Kondratenko
 */
public enum PossibleTypes {

    DOUBLE(Double.class) {
        @Override
        public Object internalParse(String text) {
            return Double.parseDouble(text);
        }
    }, 
    FLOAT(Float.class) {
        @Override
        public Object internalParse(String text) {
            return Float.parseFloat(text);
        }
    }, 
    INTEGER(Integer.class) {
        @Override
        public Object internalParse(String text) {
            return Integer.parseInt(text);
        }
    }, 
    LONG(Long.class) {
        @Override
        public Object internalParse(String text) {
            return Long.parseLong(text);
        }
    }, 
    STRING(String.class) {
        @Override
        public Object internalParse(String text) {
            return text.toString();
        }
    }, 
    BOOLEAN(Boolean.class){
        @Override
        public Object internalParse(String text) {
            return Boolean.parseBoolean(text);
        }        
    },
    AUTO(null) {
        @Override
        public Object internalParse(String text) {
            return AbstractSaver.autoParse(text);
        }
    };
    
    private final static String NULL_STRING = "NULL";
    
    private final static String MINUS_STRING = "-";
    
    private Class<?> originalClass;
    
    private PossibleTypes(Class<?> originalClass) {
        this.originalClass = originalClass;
    }
    
    public Object parse(String text) {
        if (!StringUtils.isEmpty(text)) {
            if (!(text.equalsIgnoreCase(NULL_STRING) ||
                text.equals(MINUS_STRING))) {
                return internalParse(text);
            }
        }
        
        return null;
    }
    
    protected abstract Object internalParse(String text);
    
    public static PossibleTypes getType(Class<?> clazz) {
        for (PossibleTypes singleType : values()) {
            if (clazz.equals(singleType.originalClass)) {
                return singleType;
            }
        }        
        return null;
    }

}
