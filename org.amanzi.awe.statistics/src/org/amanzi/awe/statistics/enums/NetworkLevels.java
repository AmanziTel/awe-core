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

package org.amanzi.awe.statistics.enums;

import org.amanzi.awe.statistics.engine.IHierarchyLevel;

/**
 * Enumeration for network levels
 * 
 * @author Pechko_E
 * @since 1.0.0
 */
public enum NetworkLevels implements IHierarchyLevel {
    SECTOR("sector", null, false), SITE("site", SECTOR, true), CITY("city", SITE, true), BSC("bsc", SITE, true);

    private String name;
    private NetworkLevels underlyingLevel;
    private boolean needsCorrelation;

    /**
     * Constructor
     * 
     * @param name level name
     * @param underlyingLevel underlying level
     */
    private NetworkLevels(String name, NetworkLevels underlyingLevel, boolean needsCorrelation) {
        this.name = name;
        this.underlyingLevel = underlyingLevel;
        this.needsCorrelation = needsCorrelation;
    }

    /**
     * @return Returns the name.
     */
    public String getName() {
        return name;
    }

    /**
     * @return Returns the underlyingLevel.
     */
    public NetworkLevels getUnderlyingLevel() {
        return underlyingLevel;
    }

    /**
     * @return Returns the needsCorrelation.
     */
    public boolean needsCorrelation() {
        return needsCorrelation;
    }
}
