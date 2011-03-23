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

    NETWORK_SECTOR_DATA("Network sector data", 7, new String[] {}),
    NEIGBOURS_DATA("Neighbours data", 0, new String[] {"Serving Sector", "Target Sector", "Attempts"}),
    FREQUENCY_CONSTRAINT_DATA("Frequency constraint data", 1, new String[] {"Sector", "TRX_ID", "Channel Type", "ARFCN", "Type", "Penalty"}),
    SEPARATION_CONSTRAINT_DATA("Separation constraint data", 2, new String[] {"Sector", "Separation"}),
    TRAFFIC_DATA("Traffic data", 3, new String[] {"Sector", "Traffic"}),
    TRX_DATA("Trx data", 4, new String[] {"Sector", "Subcell", "Band", "Extended", "Hopping Type", "BCCH", "HSN", "MAIO", "ARFCN"}),
    INTERFERENCE_MATRIX("Interference matrix", 5, new String[] {"Serving Sector", "Interfering Sector", "Source", "Co", "Adj"});
    
    /**
     * Name of type
     */
    private String name;
    
    /**
     * Properties of page
     */
    private String[] properties;
    
    /**
     * Index of page
     */
    private Integer index;
    
    /**
     * Constructor
     * @param name
     */
    private ColumnsConfigPageTypes(String name, Integer index, String[] properties) {
        this.name = name;
        this.index = index;
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
     * Get the index of page
     *
     * @return
     */
    public Integer getIndex() {
        return index;
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
    
    /**
     * Find enum by index
     * 
     * @param name
     * @return ColumnsConfigPageTypes
     */
    public static ColumnsConfigPageTypes findColumnsConfigPageTypeByIndex(Integer index) {
        for (ColumnsConfigPageTypes columnsConfigPageType :ColumnsConfigPageTypes.values()) {
            if (columnsConfigPageType.getIndex() == index) {
                return columnsConfigPageType;
            }
        }
        return null;
    }
    
    public static String getNameOfProperty(String searchingProperty) {
        for (ColumnsConfigPageTypes type : ColumnsConfigPageTypes.values()) {
            for (String property : type.getProperties()) {
                String prop = cleanHeader(searchingProperty);
                if (prop.equals(cleanHeader(property)))
                    return property;
            }
        }
        return "";
    }
    
    private static String cleanHeader(String header) {
        return header.replaceAll("[\\s\\-\\[\\]\\(\\)\\/\\.\\\\\\:\\#]+", "_").replaceAll("[^\\w]+", "_").replaceAll("_+", "_").replaceAll("\\_$", "").toLowerCase();
    }
}
