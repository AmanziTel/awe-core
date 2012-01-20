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

/**
 * @author Vladislav_Kondratenko
 */
public enum PossibleTypes {

    DOUBLE(Double.class) {
        @Override
        public Object parse(String text) {
            return Double.parseDouble(text);
        }
    }, 
    FLOAT(Float.class) {
        @Override
        public Object parse(String text) {
            return Float.parseFloat(text);
        }
    }, 
    INTEGER(Integer.class) {
        @Override
        public Object parse(String text) {
            return Integer.parseInt(text);
        }
    }, 
    LONG(Long.class) {
        @Override
        public Object parse(String text) {
            return Long.parseLong(text);
        }
    }, 
    STRING(String.class) {
        @Override
        public Object parse(String text) {
            return text.toString();
        }
    }, 
    BOOLEAN(Boolean.class){
        @Override
        public Object parse(String text) {
            return Boolean.parseBoolean(text);
        }        
    },
    AUTO(null) {
        @Override
        public Object parse(String text) {
            return AbstractSaver.autoParse(text);
        }
    };
    
    private Class<?> originalClass;
    
    private PossibleTypes(Class<?> originalClass) {
        this.originalClass = originalClass;
    }
    
    public abstract Object parse(String text);
    
    public static PossibleTypes getType(Class<?> clazz) {
        for (PossibleTypes singleType : values()) {
            if (clazz.equals(singleType.originalClass)) {
                return singleType;
            }
        }        
        return null;
    }

}
