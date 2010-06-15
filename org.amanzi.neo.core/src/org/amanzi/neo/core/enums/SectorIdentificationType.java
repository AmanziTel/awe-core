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

package org.amanzi.neo.core.enums;

import org.amanzi.neo.core.INeoConstants;

/**
 * Enum that contains types of Identification of Sector
 * 
 * @author Lagutko_N
 * @since 1.0.0
 */
public enum SectorIdentificationType {
    
    CI (INeoConstants.PROPERTY_SECTOR_CI),
    LAC_CI (null),
    NAME (INeoConstants.PROPERTY_SECTOR_NAME);
    
    /*
     * Node property  
     */
    private String property;
    
    /**
     * Constructor. 
     * 
     * @param property property in node for this identification type
     */
    private SectorIdentificationType(String property) {
        this.property = property;
    }
    
    /**
     * Returns node property for this identification type
     *
     * @return node property
     */
    public String getProperty() {
        return property;
    }

}
