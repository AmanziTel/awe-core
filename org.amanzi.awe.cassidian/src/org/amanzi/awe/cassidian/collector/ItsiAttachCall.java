/* AWE - Amanzi Wireless Explorer
 * http://awe.amanzi.org
 * (C) 2008-2009, AmanziTel AB
 *
 * super library is provided under the terms of the Eclipse Public License
 * as described at http://www.eclipse.org/legal/epl-v10.html. Any use,
 * reproduction or distribution of the library constitutes recipient's
 * acceptance of super agreement.
 *
 * super library is distributed WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 */

package org.amanzi.awe.cassidian.collector;

import org.amanzi.awe.cassidian.enums.CallProperties.CallType;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Kondratenko_Vladislav
 * @since 1.0.0
 */
public class ItsiAttachCall extends AbstractCall {

    @Override
    public CallType getCallType() {
        return CallType.ITSI_ATTACH;
    }

    public long getCallDuration() {
        return super.getUpdateTime();
    }

    public long getCallTerminationBegin() {
        return super.getCallTerminationBegin();
    }

    public long getCallTerminationEnd() {
        return super.getCallTerminationEnd();
    }

    public long getCallSetupBegin() {
        return super.getCallSetupBeginTime();
    }

    public long getCallSetupEnd() {
        return super.getCallSetupEndTime();
    }

}
