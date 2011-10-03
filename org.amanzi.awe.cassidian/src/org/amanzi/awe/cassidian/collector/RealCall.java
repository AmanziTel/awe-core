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

import java.util.List;

import org.amanzi.awe.cassidian.structure.AbstractTOCTTC;
import org.amanzi.awe.cassidian.structure.PESQResultElement;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Kondratenko_Vladislav
 * @since 1.0.0
 */
public class RealCall extends AbstractCall {
    Long calledPhoneNumber;

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

    public long getCallDuration() {
        return super.getCallDuration();
    }

    public long getTerminationDuration() {
        return super.getTerminationDuration();
    }

    public long getSetupDuration() {
        return super.getSetupDuration();
    }

    public float getAverageDelay() {
        return super.getAverageDelay();
    }

    public float getAverageLQ() {
        return super.getAverageLQ();
    }

    public float getMinDelay() {
        return super.getMinDelay();
    }

    public float getMinLq() {
        return super.getMinLq();
    }

    public float getMaxDelay() {
        return super.getMaxDelay();
    }

    public float getMaxLq() {
        return super.getMaxLq();
    }

    public float[] getLq() {
        return super.getLq();
    }

    public float[] getDelay() {
        return super.getDelay();
    }

    public List<PESQResultElement> getTocPesqList() {
        return super.getTocPesqList();
    }

    public List<PESQResultElement> getTtcPesqList() {
        return super.getTtcPesqList();
    }

    public List<AbstractTOCTTC> getTocTtcList() {
        return super.getTocTTCList();
    }

    public String getPhoneNumber() {
        return super.getPhoneNumber();
    }

}
