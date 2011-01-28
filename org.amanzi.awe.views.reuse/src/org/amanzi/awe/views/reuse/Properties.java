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

import java.awt.Color;

/**
 * TODO Purpose of 
 * <p>
 *
 * </p>
 * @author Kasnitskij_V
 * @since 1.0.0
 */
public enum Properties {

    ALL_RXQUAL_FULL("all_rxqual_full", new double[] {5.0, 1.0, 2.0}, new Color[] {Color.GREEN, Color.YELLOW, Color.RED}),
    ALL_RXQUAL_SUB("all_rxqual_sub", new double[] {5.0, 1.0, 2.0}, new Color[] {Color.GREEN, Color.YELLOW, Color.RED}),
    ALL_RXLEV_FULL_DBM("all_rxlev_full_dbm", new double[] {25.0, 5.0, 5.0, 10.0, 9.0, 57.0}, new Color[] {Color.RED, Color.ORANGE, Color.BLUE, Color.CYAN, Color.YELLOW, Color.GREEN}),
    ALL_RXLEV_SUB_DBM("all_rxlev_sub_dbm", new double[] {25.0, 5.0, 5.0, 10.0, 9.0, 57.0}, new Color[] {Color.RED, Color.ORANGE, Color.BLUE, Color.CYAN, Color.YELLOW, Color.GREEN});
    
    private final String value;
    private final double[] ranges;
    private final Color[] colors;
    
    /**
     * Constructor 
     * 
     * @param value string value
     * @param ranges array of ranges
     * @param colors array of colors
     */
    private Properties(String value, double[] ranges, Color[] colors) {
        this.value = value;
        this.ranges = ranges;
        this.colors = colors;
    }
    
    /**
     * Find enum by value
     *
     * @param value string value
     * @return enum with necessary value or null
     */
    public static Properties fingEnumByValue(String value) {
        if (value == null) {
            return null;
        }
        for (Properties enums : Properties.values()) {
            if (enums.value.equals(value)) {
                return enums;
            }
        }
        return null;
    }
    
    /**
     * Gets the ranges
     * 
     * @return Returns the ranges.
     */
    public double[] getRanges() {
        return ranges;
    }
    
    /**
     * Gets the colors
     *
     * @return Returns the colors.
     */
    public Color[] getColors() {
        return colors;
    }

    @Override
    public String toString() {
        return value;
    }
}
