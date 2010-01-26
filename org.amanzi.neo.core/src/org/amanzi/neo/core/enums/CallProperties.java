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

/**
 * <p>
 * Enum of all Calls properties
 * </p>
 * 
 * @author Cinkel_A
 * @since 1.0.0
 */
public enum CallProperties {
    SETUP_DURATION("setupDuration"), CALL_TYPE("callType"), CALL_DIRECTION("callDirection");
    private final String id;

    /**
     * constructor
     * 
     * @param id - property name
     */
    private CallProperties(String id) {
        this.id = id;
    }

    /**
     * @return Returns the id.
     */
    public String getId() {
        return id;
    }

}
