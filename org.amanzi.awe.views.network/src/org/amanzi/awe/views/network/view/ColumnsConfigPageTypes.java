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

    NETWORK_SECTOR_DATA("Network sector data"),
    NEIGBOURS_DATA("Neighbours data"),
    FREQUENCY_CONSTRAINT_DATA("Frequency constraint data"),
    SEPARATION_CONSTRAINT_DATA("Separation constraint data"),
    TRAFFIC_DATA("Traffic data"),
    TRX_DATA("Trx data"),
    INTERFERENCE_MATRIX("Interference matrix");
    
    /**
     * Name of type
     */
    private String name;
    
    /**
     * Constructor
     * @param name
     */
    private ColumnsConfigPageTypes(String name) {
        this.name = name;
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
