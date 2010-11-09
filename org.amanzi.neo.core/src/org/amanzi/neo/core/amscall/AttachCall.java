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

import org.amanzi.neo.services.enums.CallProperties.CallType;

/**
 * <p>
 * implement IAttachCall interface
 * </p>
 * 
 * @author TsAr
 * @since 1.0.0
 */
public class AttachCall extends AmsCall implements IAttachCall {

    public AttachCall() {
        setCallType(CallType.ITSI_ATTACH);
    }
    private Long callDuration;

    @Override
    public Long getCallDuration() {
        return callDuration;
    }

    /**
     * Sets the call duration.
     * 
     * @param callDuration the new call duration
     */
    public void setCallDuration(Long callDuration) {
        this.callDuration = callDuration;
    }

}
