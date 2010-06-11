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
 * Implement Handover call interface
 * </p>
 * 
 * @author TsAr
 * @since 1.0.0
 */
public class HandoverCall extends AmsCall implements IHandoverCall {

    /**
     * Instantiates a new handover call.
     */
    public HandoverCall() {
        setCallType(CallType.ITSI_HO);
    }

    private Long handovertime;

    @Override
    public Long getHandovertime() {
        return handovertime;
    }

    /**
     * Sets the handovertime.
     * 
     * @param handovertime the new handovertime
     */
    public void setHandovertime(Long handovertime) {
        this.handovertime = handovertime;
    }

}
