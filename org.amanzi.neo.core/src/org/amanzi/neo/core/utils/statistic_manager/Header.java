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

package org.amanzi.neo.core.utils.statistic_manager;

import java.util.HashMap;


/**
 * <p>
 *
 * </p>
 * @author Cinkel_A
 * @since 1.0.0
 */
public class Header {
    private static final int MAX_PROPERTY_VALUE_COUNT = 100; // discard
    // calculate spread after this number of data points
    int index;
    String key;
//    String name;
    Class< ? extends Object> headerTypes;
    HashMap<Class< ? extends Object>, Integer> parseTypes = new HashMap<Class< ? extends Object>, Integer>();
    Double min = Double.POSITIVE_INFINITY;
    Double max = Double.NEGATIVE_INFINITY;
    HashMap<Object, Integer> values = new HashMap<Object, Integer>();
    int parseCount = 0;
    int countALL = 0;
    // is type should be fixed?
    private final boolean typeIsFixed;
    private int notParsedValueCount;

    /**
     * Constructor
     * 
     * @param name header name
     * @param key header key
     * @param index idex of header
     */
    Header(String key, int index) {
        this.index = index;
//        this.name = name;
        this.key = key;
        headerTypes = Object.class;
        typeIsFixed = false;
        notParsedValueCount = 0;
    }

    /**
     * Constructor
     * 
     * @param name header name
     * @param key header key
     * @param index idex of header
     * @param headerTypes - knownType
     */
    Header(String name, String key, int index, Class< ? extends Object> headerTypes) {
        this.index = index;
//        this.name = name;
        this.key = key;
        this.headerTypes = headerTypes;
        typeIsFixed = true;
        notParsedValueCount = 0;
    }

    /**
     * check field by valid
     * 
     * @param field
     * @return
     */
    protected boolean invalid(String field) {
        return field == null || field.length() < 1 || field.equals("?");
    }

    /**
     * add to statistics already parsed value
     * 
     * @param value - parsed value
     * @return value or null if value do not stored in statistics
     */
    public Object indexValue(Object value) {
        Class< ? extends Object> klass = value.getClass();
        if (headerTypes == Object.class) {
            setType(klass);
            incValue(value);
        } else {
            if (headerTypes.isAssignableFrom(klass)) {
                incValue(value);
            } else {
                if (typeIsFixed) {
                    notParsedValueCount++;
                    return null;
                }
                if (klass.isAssignableFrom(headerTypes)) {
                    setType(klass);
                    incValue(value);
                } else {
                    if (Number.class.isAssignableFrom(headerTypes) && Number.class.isAssignableFrom(klass)) {
                        setType(Double.class);
                        final Double doubleValue = ((Number)value).doubleValue();
                        incValue(doubleValue);
                        return doubleValue;
                    } else {
                        notParsedValueCount++;
                        return null;
                    }
                }
            }
        }
        return value;
    }

    public Object parse(String field) {

        if (invalid(field)) {
            return null;
        }
        if (headerTypes==String.class){
            incValue(field);
            return field;
        }
        if (headerTypes == Object.class) {
            try {
                int value = Integer.parseInt(field);
                incValue(value);
                setType(Integer.class);
                return value;
            } catch (Exception e) {
                try {
                    float value = Float.parseFloat(field);
                    incValue(value);
                    setType(Float.class);
                    return value;
                } catch (Exception e2) {
                    incValue(field);
                    setType(String.class);
                    return field;
                }
            }
        }else if (Number.class.isAssignableFrom(headerTypes)){
            try {
                if (headerTypes==Float.class){
                    float value = Float.parseFloat(field);
                    incValue(value);
                }else if (headerTypes==Double.class){
                    Double value = Double.parseDouble(field);
                    incValue(value);
                }else if (headerTypes==Long.class){
                    Long value = Long.parseLong(field);
                    incValue(value);
                }
                int value = Integer.parseInt(field);
                incValue(value);
                setType(Integer.class);
                return value;
            } catch (Exception e) {
                if (typeIsFixed){
                    notParsedValueCount++;
                    return null;
                }
                try {
                    Double value = Double.parseDouble(field);
                    setType(Double.class);
                    incValue(value);
                    return value;
                } catch (Exception e2) {
                    setType(String.class);
                    incValue(field);
                    return field;
                }
            }
        }else{
            if (typeIsFixed){
                notParsedValueCount++;
                return null;
            }
            incValue(field);
            setType(String.class);
            return field;              
        }
    }

    /**
     * set type of header
     * 
     * @param klass - new type
     */
    protected void setType(Class< ? extends Object> klass) {
        headerTypes = klass;
    }

    protected Object incValue(Object value) {
        if (value != null) {
            countALL++;
            if (value instanceof Number) {
                double doubleValue = ((Number)value).doubleValue();
                min = Math.min(min, doubleValue);
                max = Math.max(max, doubleValue);
            }
        }
        if (values != null) {
            Integer count = values.get(value);
            if (count == null) {
                count = 0;
            }
            boolean discard = false;
            if (count == 0) {
                // We have a new value, so adding it will increase the size
                // of the map
                // We should perform threshold tests to decide whether to
                // drop the map or not
                if (values.size() >= MAX_PROPERTY_VALUE_COUNT) {
                    // Exceeded absolute threashold, drop map
                    System.out.println("Property values exceeded maximum count, no longer tracking value set: " + this.key);
                    discard = true;
                }
                // TODO if we do not use parse method this check will be
                // wrong
                // else if (values.size() >=
                // MIN_PROPERTY_VALUE_SPREAD_COUNT) {
                // // Exceeded minor threshold, test spread and then decide
                // float spread = (float)values.size() / (float)parseCount;
                // if (spread > MAX_PROPERTY_VALUE_SPREAD) {
                // // Exceeded maximum spread, too much property variety,
                // drop map
                // System.out.println("Property shows excessive variation, no longer tracking value set: "
                // + this.key);
                // discard = true;
                // }
                // }
            }
            if (discard) {
                // Detected too much variety in property values, stop
                // counting
                dropStats();
            } else {
                values.put(value, count + 1);
            }
        }
        return value;
    }

    /**
     * Disable statistics collection for this header. This is useful if the property is
     * undesirable in some later statistical analysis, either because it is too diverse, or it
     * is a property we can 'grouped by' during the load. Examples of excessive diversity would
     * be element names, ids, timestamps, locations. Examples of grouping by would be site
     * properties, timestamps and locations.
     */
    public void dropStats() {
        values = null;
    }
}