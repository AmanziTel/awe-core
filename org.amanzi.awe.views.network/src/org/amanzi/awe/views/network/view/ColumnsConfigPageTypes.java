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

package org.amanzi.awe.views.network.view;

import org.apache.commons.lang.StringUtils;

/**
 * TODO Purpose of 
 * <p>
 *
 * </p>
 * @author Kasnitskij_V
 * @since 1.0.0
 */
public enum ColumnsConfigPageTypes {

    NETWORK_SECTOR_DATA("Network sector data", new String[] {}),
    NEIGBOURS_DATA("Neighbours data", new String[] {"Serving Sector", "Target Sector", "Attempts"}),
    FREQUENCY_CONSTRAINT_DATA("Frequency constraint data", new String[] {"Sector", "TRX_ID", "Channel Type", "Frequency", "Type", "Penalty"}),
    SEPARATION_CONSTRAINT_DATA("Separation constraint data", new String[] {"Sector", "Separation"}),
    TRAFFIC_DATA("Traffic data", new String[] {"Sector", "Traffic"}),
    TRX_DATA("Trx data", new String[] {"Sector", "Subcell", "Band", "Extended", "Hopping Type", "BCCH", "HSN", "MAIO", "ARFCN"}),
    INTERFERENCE_MATRIX("Interference matrix", new String[] {"Serving Sector", "Interfering Sector", "Source", "Co", "Adj"});
    
    /**
     * Name of type
     */
    private String name;
    private String[] properties;
    
    /**
     * Constructor
     * @param name
     */
    private ColumnsConfigPageTypes(String name, String[] properties) {
        this.name = name;
        this.properties = properties;
    }
    
    /**
     * Get name of type
     *
     * @return
     */
    public String getName() {
        return name;
    }
    
    /**
     * Get properties from type
     *
     * @return
     */
    public String[] getProperties() {
        return properties;
    }
    

    /**
     * Find enum by name
     * 
     * @param name
     * @return ColumnsConfigPageTypes
     */
    public static ColumnsConfigPageTypes findColumnsConfigPageTypeByName(String name) {
        if (StringUtils.isEmpty(name)){
            return null;
        }
        for (ColumnsConfigPageTypes columnsConfigPageType :ColumnsConfigPageTypes.values()) {
            if (columnsConfigPageType.getName().equals(name)) {
                return columnsConfigPageType;
            }
        }
        return null;
    }
}
