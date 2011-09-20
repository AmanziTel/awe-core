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

package org.amanzi.neo.services;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Map;
import java.util.TreeMap;

import org.amanzi.neo.services.exceptions.AWEException;
import org.amanzi.neo.services.exceptions.FailedParseValueException;
import org.amanzi.neo.services.exceptions.UnsupportedClassException;

/**
 * <p>
 * this class stores the statistics for a particular property
 * </p>
 * 
 * @author kruglik_a
 * @since 1.0.0
 */
public class NewPropertyStatistics {

    /**
     * name of property
     */
    private String name;

    /**
     * class of property values
     */
    private Class< ? > klass;

    /**
     * map, which makes the correspondence between the value of the property and the number of such
     * values
     */
    private Map<Object, Integer> propertyMap = new TreeMap<Object, Integer>();

    /**
     * constructor with parameter name
     * 
     * @param name - name of the property, statistics which will be stored in this object
     */
    public NewPropertyStatistics(String name, Class< ? > klass) {
        this.name = name;
        this.klass = klass;
    }

    /**
     * get name of property
     * 
     * @return String name
     */
    public String getName() {
        return this.name;
    }

    /**
     * get Class of property
     * 
     * @return Class<?> klass
     */
    public Class< ? > getKlass() {
        return this.klass;
    }

    /**
     * set Class of property
     * 
     * @param klass - Class of property
     */
    public void setKlass(Class< ? > klass) {
        this.klass = klass;
    }

    /**
     * this method add property value with it count or update count if this value is already
     * contained in the map
     * 
     * @param value - property value
     * @param count - count to update for this value
     */
    public void updatePropertyMap(Object value, Integer count) {
        Integer oldCount = 0;
        if (propertyMap.containsKey(value)) {
            oldCount = propertyMap.get(value);
        }
        propertyMap.put(value, oldCount + count);
    }

    /**
     * this method get propertyMap
     * 
     * @return Map<Object, Integer> propertyMap
     */
    public Map<Object, Integer> getPropertyMap() {
        return this.propertyMap;
    }

    /**
     * parse String value to klass object
     * 
     * @param propertyValue
     * @return Object pareValue
     * @throws FailedParseValueException
     * @throws AWEException
     * @throws ParseException
     */
    public Object parseValue(String propertyValue) throws UnsupportedClassException, FailedParseValueException {
        try {
            Number numberValue = NumberFormat.getNumberInstance().parse(propertyValue);

            if (klass.equals(Integer.class)) {
                return numberValue.intValue();
            }
            if (klass.equals(Float.class)) {
                return numberValue.floatValue();
            }
            if (klass.equals(String.class)) {
                return propertyValue;
            }
            if (klass.equals(Byte.class)) {
                return numberValue.byteValue();
            }
            if (klass.equals(Short.class)) {
                return numberValue.shortValue();
            }
            if (klass.equals(Long.class)) {
                return numberValue.longValue();
            }
            if (klass.equals(Double.class)) {
                return numberValue.doubleValue();
            }
            if (klass.equals(Boolean.class)) {
                return Boolean.valueOf(propertyValue);
            }
            throw new UnsupportedClassException(klass);
        } catch (UnsupportedClassException e) {
            throw e;
        } catch (Exception e2) {
            throw new FailedParseValueException();
        }
    }
}
