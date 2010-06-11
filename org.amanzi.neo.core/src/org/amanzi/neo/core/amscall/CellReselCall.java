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

package org.amanzi.neo.core.amscall;

import org.amanzi.neo.core.enums.CallProperties.CallType;

/**
 * <p>
 * Implement of cell reselection time
 * </p>
 * .
 * 
 * @author TsAr
 * @since 1.0.0
 */
public class CellReselCall extends AmsCall implements ICellReselCall {

    /**
     * Instantiates a new cell resel call.
     */
    public CellReselCall() {
        setCallType(CallType.ITSI_CC);
    }

    /** The reselection time. */
    private Long reselectionTime;

    /**
     * Gets the reselection time.
     * 
     * @return the reselection time
     */
    @Override
    public Long getReselectionTime() {
        return reselectionTime;
    }

    /**
     * Sets the reselection time.
     * 
     * @param reselectionTime the new reselection time
     */
    public void setReselectionTime(Long reselectionTime) {
        this.reselectionTime = reselectionTime;
    }

}
