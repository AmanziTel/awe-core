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
 * Implement of IMessageCall interface
 * </p>
 * 
 * @author TsAr
 * @since 1.0.0
 */
public class MessageCall extends AmsCall implements IMessageCall {

    private Long messageAcknowledgeTime;
    private Long MessageReceiveTime;

    @Override
    public Long getMessageAcknowledgeTime() {
        return messageAcknowledgeTime;
    }

    @Override
    public Long getMessageReceiveTime() {
        return MessageReceiveTime;
    }

    @Override
    public void setCallType(CallType callType) {
        assert callType == null || callType == CallType.SDS || callType == callType.TSM;
        super.setCallType(callType);
    }
    /**
     * Sets the message acknowledge time.
     * 
     * @param messageAcknowledgeTime the new message acknowledge time
     */
    public void setMessageAcknowledgeTime(Long messageAcknowledgeTime) {
        this.messageAcknowledgeTime = messageAcknowledgeTime;
    }

    /**
     * Sets the message receive time.
     * 
     * @param messageReceiveTime the new message receive time
     */
    public void setMessageReceiveTime(Long messageReceiveTime) {
        MessageReceiveTime = messageReceiveTime;
    }

    /**
     * @param sendTime
     */
    public void setCallSetupBeginTime(Long sendTime) {
    }

    /**
     * @return
     */
    public Long getCallSetupBegin() {
        return null;
    }

}
