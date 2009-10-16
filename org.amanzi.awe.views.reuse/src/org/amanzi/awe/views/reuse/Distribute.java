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
 * @since 1.1.0
 */
public enum Distribute {
    /** auto column groups*/
    AUTO("auto"), 
    /** column groups by integer value*/
    INTEGERS("integers"), 
    /** group by 10 columns */
    I10("10"),
    /** group by 20 columns */
    I20("20"),
    /** group by 50 columns */
    I50("50");
    private final String value;

    /**
     * Constructor
     * 
     * @param value - string value
     */
    private Distribute(String value) {
        this.value = value;
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

    @Override
    public String toString() {
        return value;
    }
}
