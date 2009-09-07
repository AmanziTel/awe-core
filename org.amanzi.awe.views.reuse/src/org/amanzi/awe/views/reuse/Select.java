/* AWE - Amanzi Wireless Explorer
 * http://awe.amanzi.org
 * (C) 2008-2009, AmanziTel AB
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation;
 * version 3.0 of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 */
package org.amanzi.awe.views.reuse;

/**
 * <p>
 * Selection type of 'ms' node
 * </p>
 * 
 * @author Cinkel_A
 * @since 1.1.0
 */
public enum Select {

    /** Select node with max value */
    MAX("max"),
    /** Select node with min value */
    MIN("min"),
    /** Select AVERAGE value of nodes */
    AVERAGE("average"),
    /** Select EXISTS algorithm */
    EXISTS("exists"),
    /** Select FIRST value of nodes */
    FIRST("first");
    private final String value;

    /**
     * Constructor
     * 
     * @param value - string value
     */
    private Select(String value) {
        this.value = value;
    }

    /**
     * Find enum by value
     * 
     * @param value string value
     * @return enum with necessary value or null
     */
    public static Select findSelectByValue(String value) {
        if (value == null) {
            return null;
        }
        for (Select selection : Select.values()) {
            if (selection.value.equals(value)) {
                return selection;
            }
        }
        return null;
    }

    /**
     * gets enums as array of its values
     * 
     * @return string array of values
     */
    public static String[] getEnumAsStringArray() {
        Select[] enums = Select.values();
        String[] result = new String[enums.length];
        for (int i = 0; i < enums.length; i++) {
            result[i] = enums[i].value;
        }
        return result;
    }

    @Override
    public String toString() {
        return value;
    }
}
