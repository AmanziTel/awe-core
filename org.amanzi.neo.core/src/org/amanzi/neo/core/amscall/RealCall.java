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

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.amanzi.neo.core.enums.CallProperties.CallType;

// TODO: Auto-generated Javadoc
/**
 * <p>
 * </p>
 * .
 * 
 * @author TsAr
 * @since 1.0.0
 */
public class RealCall extends AmsCall implements IRealCall {

    /** The lq list. */
    List<Float> lqList = new ArrayList<Float>();

    /** The delay list. */
    List<Float> delayList = new LinkedList<Float>();

    private Long setupEnd;

    private Long terminationEnd;

    private Long terminationBegin;

    private Long setupBegin;

    @Override
    public void setCallType(CallType callType) {
        assert callType == null || callType == CallType.INDIVIDUAL || callType == CallType.GROUP || callType == CallType.EMERGENCY
                || callType == CallType.HELP;
        super.setCallType(callType);
    }
    /**
     * Adds the lq.
     * 
     * @param lq the lq
     */
    @Override
    public void addLq(float lq) {
        lqList.add(lq);
    }

    /**
     * Adds the delay.
     * 
     * @param delay the delay
     */
    @Override
    public void addDelay(float delay) {
        delayList.add(delay);
    }

    /**
     * Gets the call duration.
     * 
     * @return the call duration
     */
    @Override
    public Long getCallDuration() {
        return terminationEnd == null || setupBegin == null ? null : terminationEnd - setupBegin;
    }

    /**
     * Gets the setup duration.
     * 
     * @return the setup duration
     */
    @Override
    public Long getSetupDuration() {
        return setupEnd == null || setupBegin == null ? null : setupEnd - setupBegin;
    }

    /**
     * Gets the termination duration.
     * 
     * @return the termination duration
     */
    @Override
    public Long getTerminationDuration() {
        return terminationEnd == null || terminationBegin == null ? null : terminationEnd - terminationBegin;
    }


    @Override
    public float[] getDelay() {
        float[] result = new float[delayList.size()];
        for (int i = 0; i < result.length; i++) {
            result[i] = delayList.get(i);
        }
        return result;
    }

    @Override
    public float[] getLq() {
        float[] result = new float[lqList.size()];
        for (int i = 0; i < result.length; i++) {
            result[i] = lqList.get(i);
        }
        return result;
    }

    /**
     * @param property
     */
    public void setCallSetupEndTime(Long property) {
        this.setupEnd = property;
    }

    /**
     * @param property
     */
    public void setCallTerminationEnd(Long property) {
        this.terminationEnd = property;
    }

    /**
     * @param disconnectTime
     */
    public void setCallTerminationBegin(Long disconnectTime) {
        this.terminationBegin = disconnectTime;
    }

    /**
     * @param property
     */
    public void setCallSetupBeginTime(Long property) {
        this.setupBegin = property;
    }



    // @Override
    // public Node saveCall(GraphDatabaseService service) {
    // Transaction tx = NeoUtils.beginTx(service);
    // try {
    // Node result= super.saveCall(service);
    // if (setupDuration==null||setupDuration<0){
    // result.setProperty(CallProperties.SETUP_DURATION.getId(), -1);
    // }else{
    // result.setProperty(CallProperties.SETUP_DURATION.getId(),setupDuration);
    // }
    //        
    // return result;
    // } finally {
    // NeoUtils.finishTx(tx);
    // }
    // }

}
