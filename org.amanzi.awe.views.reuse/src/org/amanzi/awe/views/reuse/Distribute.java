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
 * Type of column groups
 * </p>
 * 
 * @author Cinkel_A
 * @since 1.0.0
 */
public enum Distribute {
    /** auto column groups*/
    AUTO("auto","automatically"), 
    /** column groups by integer value*/
    INTEGERS("integers","as integers"), 
    /** group by 10 columns */
    I10("10","in 10 categories"),
    /** group by 20 columns */
    I20("20","in 20 categories"),
    /** group by 50 columns */
    I50("50","in 50 categories");
    private final String value;
    private final String description;

    /**
     * Constructor
     * 
     * @param value - string value
     */
    private Distribute(String value,String description) {
        this.value = value; 
        this.description = description; 
    }

    /**
     * Find enum by value
     * 
     * @param value string value
     * @return enum with necessary value or null
     */
    public static Distribute findEnumByValue(String value) {
        if (value == null) {
            return null;
        }
        for (Distribute enums : Distribute.values()) {
            if (enums.value.equals(value)) {
                return enums;
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
        Distribute[] enums = Distribute.values();
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
