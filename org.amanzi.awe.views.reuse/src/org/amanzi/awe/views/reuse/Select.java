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
package org.amanzi.awe.views.reuse;

/**
 * <p>
 * Selection type of 'ms' node
 * </p>
 * 
 * @author Cinkel_A
 * @since 1.0.0
 */
public enum Select {

    /** Select node with max value */
    MAX("max","maximum value"),
    /** Select node with min value */
    MIN("min","minimum value"),
    /** Select AVERAGE value of nodes */
    AVERAGE("average","average value"),
    /** Select EXISTS algorithm */
    EXISTS("exists","existance"),
    /** Select FIRST value of nodes */
    FIRST("first","first value");
    private final String value;
    private final String description;

    /**
     * Constructor
     * 
     * @param value - string value
     */
    private Select(String value,String description) {
        this.value = value;
        this.description = description;
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

    /**
     * Gets the description
     * @return Returns the description.
     */
    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return value;
    }
}
