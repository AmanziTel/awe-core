/* AWE - Amanzi Wireless Explorer
 * http://awe.amanzi.org
 * (C) 2008-2009, AmanziTel AB
 *
 * This library is provided under the terms of the Eclipse protected License
 * as described at http://www.eclipse.org/legal/epl-v10.html. Any use,
 * reproduction or distribution of the library constitutes recipient's
 * acceptance of this agreement.
 *
 * This library is distributed WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 */

package org.amanzi.awe.cassidian.collector;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.amanzi.awe.cassidian.enums.CallProperties.CallResult;
import org.amanzi.awe.cassidian.enums.CallProperties.CallType;
import org.amanzi.awe.cassidian.structure.AbstractTOCTTC;
import org.amanzi.awe.cassidian.structure.CellReselection;
import org.amanzi.awe.cassidian.structure.CompleteGpsDataList;
import org.amanzi.awe.cassidian.structure.EventsElement;
import org.amanzi.awe.cassidian.structure.GroupAttach;
import org.amanzi.awe.cassidian.structure.Handover;
import org.amanzi.awe.cassidian.structure.ItsiAttach;
import org.amanzi.awe.cassidian.structure.Ntpq;
import org.amanzi.awe.cassidian.structure.PESQResultElement;
import org.amanzi.awe.cassidian.structure.RecieveMsg;
import org.amanzi.awe.cassidian.structure.SendMsg;
import org.amanzi.awe.cassidian.structure.SendReport;
import org.amanzi.awe.cassidian.structure.TOCElement;
import org.amanzi.awe.cassidian.structure.TTCElement;
import org.apache.log4j.Logger;

/**
 * <p>
 * Class that calculates a general parameters of Call
 * </p>
 * 
 * @author Kondratenko_Vladislav
 * @since 1.0.0
 */
public abstract class AbstractCall implements ICall {
    private static Logger LOGGER = Logger.getLogger(AbstractCall.class);
   
    private CallResult callResult;
    protected boolean isInconclusive = false;
    protected long timestamp = 0l;
    private long callTerminationBegin = 0l;
    private long callTerminationEnd = 0l;
    private long callSetupBeginTime = 0l;
    private long callSetupEndTime = 0l;
    protected String phoneNumber = "";
    protected Integer inconclusiveCode;
    protected int errCode;
    private Long reselection = 0l;
    private float avgLq;
    private float avgDelay;
    private float minLq;
    private float minDelay;
    private float maxLq;
    private float maxDelay;
    private long setupDuration = 0l;
    private long terminationDuration = 0l;
    private long callDuration = 0l;
    private List<PESQResultElement> tocPesqList = new LinkedList<PESQResultElement>();
    private List<PESQResultElement> ttcPesqList = new LinkedList<PESQResultElement>();
    private List<AbstractTOCTTC> tocttcList = new LinkedList<AbstractTOCTTC>();
    private long messageRecievedTime;
    private long messageAcnowledgeTime;
    private long handoverTime;
    private Long updateTime;
    private CallType type;
    /**
     * listening quality based on pesq
     */
    private float[] lq = new float[0];

    /**
     * Audio delay
     */
    private float[] delay = new float[0];
    private String id;
    private List<EventsElement> eventsCollector = new LinkedList<EventsElement>();

    /**
     * @return Returns the lq.
     */
    protected float[] getLq() {
        return lq;
    }

    /**
     * @return Average value of source array
     */
    protected float getAverageFromFloatArray(float[] source) {
        float averageValue = 0.0f;
        if (source.length == 0) {
            return Float.MIN_VALUE;
        }
        for (float value : source) {
            averageValue += value;
        }
        return averageValue / source.length;
    }

    /**
     * @return Minimum value in source array
     */
    protected float getMinInSourceArray(float[] source) {
        float minValue = Float.MAX_VALUE;
        for (float value : source) {
            if (value < minValue) {
                minValue = value;
            }
        }
        return minValue;
    }

    /**
     * @return Maximum value in source array
     */
    protected float getMaxInSourceArray(float[] source) {
        float maxValue = Float.MIN_VALUE;
        for (float value : source) {
            if (value > maxValue) {
                maxValue = value;
            }
        }
        return maxValue;
    }

    /**
     * @param lq The lq to set.
     */
    protected void addLq(float lq) {
        this.lq = addToArray(this.lq, lq);
    }

    /**
     * Add new element to Array
     * 
     * @param original original Array
     * @param value value to add
     * @return changed array
     */
    protected float[] addToArray(float[] original, float value) {
        float[] result = new float[original.length + 1];
        result = Arrays.copyOf(original, result.length);
        result[result.length - 1] = value;
        return result;
    }

    /**
     * @return Returns the delay.
     */
    protected float[] getDelay() {
        return delay;
    }

    /**
     * @param delay The delay to set.
     */
    protected void addDelay(float delay) {
        this.delay = addToArray(this.delay, delay);
    }

    @Override
    public CallType getCallType() {
        return type;
    }

    protected void setCallResult(CallResult callResult) {
        this.callResult = callResult;
    }

    public CallResult getCallResult() {
        return callResult;
    }

    public boolean isInconclusive() {
        return isInconclusive;
    }

    protected void setInconclusive(boolean isInconclusive) {
        this.isInconclusive = isInconclusive;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    protected long getCallTerminationBegin() {
        return callTerminationBegin;
    }

    protected long getCallSetupBeginTime() {
        return callSetupBeginTime;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    protected void setCallTerminationBegin(Long callTerminationBegin) {
        this.callTerminationBegin = callTerminationBegin;
    }

    protected void setCallSetupBeginTime(Long callSetupBeginTime) {
        this.callSetupBeginTime = callSetupBeginTime;
    }

    /**
     * prepare common properties for current call
     */
    public void prepareCallProperties() {
        CallType type = getCallType();
        switch (type) {
        case INDIVIDUAL:
        case GROUP:
        case EMERGENCY:
        case HELP:
            prepareRealCallProperties();
            break;
        case SDS:
        case TSM:
        case ALARM:
            prepareMessageCall();
            break;
        case ITSI_HO:
            prepareHandoverCall();
            break;
        case ITSI_CC:
            prepareCellReselCall();
            break;
        case ITSI_ATTACH:
            prepareItsiAttachCall();
            break;
        }
    }

    public List<EventsElement> getEventsCollector() {
        return eventsCollector;
    }

    public void setEventsCollector(List<EventsElement> eventsCollector) {
        this.eventsCollector = eventsCollector;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    protected long getCallTerminationEnd() {
        return callTerminationEnd;
    }

    protected void setCallTerminationEnd(Long callTerminationEnd) {
        this.callTerminationEnd = callTerminationEnd;
    }

    protected long getCallSetupEndTime() {
        return callSetupEndTime;
    }

    protected void setCallSetupEndTime(Long callSetupEndTime) {
        this.callSetupEndTime = callSetupEndTime;
    }

    protected String getPhoneNumber() {
        return phoneNumber;
    }

    protected void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public Integer getErrCode() {
        return inconclusiveCode;
    }

    public void setErrCode(Integer errCode) {
        this.inconclusiveCode = errCode;
    }

    protected float getAverageLQ() {
        return avgLq;
    }

    protected float getAverageDelay() {
        return avgDelay;
    }

    protected float getMinLq() {
        return minLq;
    }

    protected float getMinDelay() {
        return minDelay;
    }

    protected float getMaxLq() {
        return maxLq;
    }

    protected float getMaxDelay() {
        return maxDelay;
    }

    protected long getSetupDuration() {
        return setupDuration;
    }

    protected long getTerminationDuration() {
        return terminationDuration;
    }

    protected long getCallDuration() {
        return callDuration;
    }

    /**
     * prepare common properties for Itsi attach call
     */
    private void prepareItsiAttachCall() {
        for (EventsElement event : eventsCollector) {
            try {
                if (event.getItsiAttach() instanceof ItsiAttach) {
                    timestamp = ((ItsiAttach)event.getItsiAttach()).getItsiAttReq().getTimeInMillis();
                    Long beginTime = timestamp;

                    if (beginTime != null) {
                        callSetupBeginTime = beginTime;
                        callSetupEndTime = beginTime;
                    }
                    Long endTime = ((ItsiAttach)event.getItsiAttach()).getItsiAccept().getTimeInMillis();
                    if (endTime != null) {
                        callTerminationBegin = endTime;
                        callTerminationEnd = endTime;
                    }
                }
                updateTime = callTerminationEnd - callSetupBeginTime;
            } catch (Exception e) {
                LOGGER.error("Error in " + getCallType() + "properties preparator", e);
            }

        }
    }

    /**
     * calculate common properties for TSD SDS ALARM calls
     */
    private void prepareMessageCall() {
        for (EventsElement event : eventsCollector) {
            try {
                if (event.getSendRecieveMsg() instanceof SendMsg) {
                    timestamp = ((SendMsg)event.getSendRecieveMsg()).getSendTime().getTimeInMillis();
                    callSetupBeginTime = timestamp;
                    messageAcnowledgeTime = getMaximiSendReportTime(((SendMsg)event.getSendRecieveMsg()).getSendReport())
                            - timestamp;
                } else {
                    messageRecievedTime = timestamp - ((RecieveMsg)event.getSendRecieveMsg()).getReceiveTime().getTimeInMillis();
                }
            } catch (Exception e) {
                LOGGER.error("Error in " + getCallType() + "properties preparator", e);
            }

        }
    }

    /**
     * get maximum send report time from send reports
     * 
     * @param sendReport
     */
    private long getMaximiSendReportTime(List<SendReport> sendReport) {
        long maxReportTime = 0l;
        for (SendReport report : sendReport) {
            maxReportTime = Math.max(maxReportTime, report.getReportTime().getTimeInMillis());
        }
        return maxReportTime;
    }

    /**
     * prepare common properties for group, individual, emergency,help
     */
    private void prepareRealCallProperties() {
        for (EventsElement event : eventsCollector) {
            try {
                if (event.getTocttc() instanceof TOCElement) {

                    timestamp = getMinTimestamp(event.getTocttc());
                    callSetupBeginTime = timestamp;
                    if (((TOCElement)event.getTocttc()).getDisconnectTime() != null) {
                        callTerminationBegin = ((TOCElement)event.getTocttc()).getDisconnectTime().getTimeInMillis();
                    } else {
                        callTerminationBegin = 0l;
                    }
                    if (((TOCElement)event.getTocttc()).getReleaseTime() != null) {
                        callTerminationBegin = ((TOCElement)event.getTocttc()).getReleaseTime().getTimeInMillis();
                    }
                    if ((type == CallType.GROUP || type == CallType.EMERGENCY)
                            && ((TOCElement)event.getTocttc()).getConnectTime() != null) {
                        callSetupEndTime = ((TOCElement)event.getTocttc()).getConnectTime().getTimeInMillis();
                    }
                    tocPesqList.addAll(event.getTocttc().getPesqResult());
                } else {
                    // if (((TTCElement)event.getTocttc()).getCallingNumber() != null) {
                    // phoneNumber = ((TTCElement)event.getTocttc()).getCallingNumber();
                    // }
                    long oldCallTerminationEnd = callTerminationEnd;
                    callTerminationEnd = event.getTocttc().getReleaseTime().getTimeInMillis();
                    if (oldCallTerminationEnd != 0 || oldCallTerminationEnd > callTerminationEnd) {
                        callTerminationEnd = oldCallTerminationEnd;
                    }
                    if (type == CallType.INDIVIDUAL || type == CallType.HELP) {
                        callSetupEndTime = ((TTCElement)event.getTocttc()).getConnectTime().getTimeInMillis();
                    }
                    ttcPesqList.addAll(event.getTocttc().getPesqResult());

                }

                if (!event.getTocttc().getPesqResult().isEmpty()) {
                    pesqHandler(event.getTocttc().getPesqResult());
                }
            } catch (Exception e) {
                LOGGER.error("Error in " + getCallType() + "properties preparator", e);
            }
            tocttcList.add(event.getTocttc());
        }
        callDuration = callTerminationEnd - callSetupBeginTime;
        terminationDuration = callTerminationEnd - callTerminationBegin;
        setupDuration = callSetupEndTime - callSetupBeginTime;
        avgDelay = getAverageFromFloatArray(getDelay());
        avgLq = getAverageFromFloatArray(getLq());
        minDelay = getMinInSourceArray(getDelay());
        minLq = getMinInSourceArray(getLq());
        maxDelay = getMaxInSourceArray(getDelay());
        maxLq = getMaxInSourceArray(getLq());

    }

    /**
     * calculate minimum timestamp for call
     * 
     * @param tocttc
     * @return minimum timestamp for call;
     */
    private long getMinTimestamp(AbstractTOCTTC tocttc) {
        Long timestamp = null;
        if (tocttc instanceof TOCElement) {
            TOCElement toc = (TOCElement)tocttc;
            if (toc.getConfigTime() != null) {
                timestamp = timestamp == null ? (Long)toc.getConfigTime().getTimeInMillis() : Math.min(timestamp, toc
                        .getConfigTime().getTimeInMillis());
            }
            if (toc.getConnectTime() != null) {
                timestamp = timestamp == null ? (Long)toc.getConnectTime().getTimeInMillis() : Math.min(timestamp, toc
                        .getConnectTime().getTimeInMillis());
            }
            if (toc.getDisconnectTime() != null) {
                timestamp = timestamp == null ? (Long)toc.getDisconnectTime().getTimeInMillis() : Math.min(timestamp, toc
                        .getDisconnectTime().getTimeInMillis());
            }
            if (toc.getReleaseTime() != null) {
                timestamp = timestamp == null ? (Long)toc.getReleaseTime().getTimeInMillis() : Math.min(timestamp, toc
                        .getReleaseTime().getTimeInMillis());
            }
            if (toc.getSetupTime() != null) {
                timestamp = timestamp == null ? (Long)toc.getSetupTime().getTimeInMillis() : Math.min(timestamp, toc.getSetupTime()
                        .getTimeInMillis());
            }
        } else if (tocttc instanceof TTCElement) {
            TTCElement toc = (TTCElement)tocttc;
            timestamp = timestamp == null ? (Long)toc.getAnswerTime().getTimeInMillis() : Math.min(timestamp, toc.getAnswerTime()
                    .getTimeInMillis());
            timestamp = timestamp == null ? (Long)toc.getConnectTime().getTimeInMillis() : Math.min(timestamp, toc.getConnectTime()
                    .getTimeInMillis());
            timestamp = timestamp == null ? (Long)toc.getIndicationTime().getTimeInMillis() : Math.min(timestamp, toc
                    .getIndicationTime().getTimeInMillis());
            timestamp = timestamp == null ? (Long)toc.getReleaseTime().getTimeInMillis() : Math.min(timestamp, toc.getReleaseTime()
                    .getTimeInMillis());
        }
        return timestamp;
    }

    /**
     * prepare common properties for cell reselection call
     */
    private void prepareCellReselCall() {
        CellReselection cellresel;
        for (EventsElement event : eventsCollector) {
            try {
                cellresel = event.getCellReselection();
                Long beginTime = cellresel.getCellReselReq().getTimeInMillis();
                timestamp = beginTime;
                Long endTime = cellresel.getCellReselAccept().getTimeInMillis();
                if (beginTime == null || endTime == null) {
                    setCallResult(CallResult.FAILURE);
                    reselection = -1l;
                } else {
                    reselection = endTime - beginTime;
                }
            } catch (Exception e) {
                LOGGER.error("Error in " + getCallType() + "properties preparator", e);
            }

        }
    }

    /**
     * prepare common properties for Handover call
     */
    private void prepareHandoverCall() {
        Handover handover;
        for (EventsElement event : eventsCollector) {
            try {
                handover = event.getHandover();
                Long beginTime = handover.getHoReq().getTimeInMillis();
                timestamp = beginTime;
                Long endTime = handover.getHoAccept().getTimeInMillis();
                if (beginTime == null || endTime == null) {
                    setCallResult(CallResult.FAILURE);
                    handoverTime = -1l;
                } else {
                    handoverTime = endTime - beginTime;

                }
            } catch (Exception e) {
                LOGGER.error("Error in " + getCallType() + "properties preparator", e);
            }
        }
    }

    /**
     * form delay & listening quality (lq) arrays
     * 
     * @param pesqResult
     */
    private void pesqHandler(List<PESQResultElement> pesqResult) {
        for (PESQResultElement pesq : pesqResult) {
            if (pesq.getDelay() != null) {
                addDelay(pesq.getDelay() / 1000f);
            }
            if (pesq.getPesq() != null) {
                addLq(pesq.getPesq());
            }
        }
    }

    /**
     * @return Returns the inconclusiveCode.
     */
    public Integer getInconclusiveCode() {
        return inconclusiveCode;
    }

   

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((eventsCollector == null) ? 0 : eventsCollector.hashCode());
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof AbstractCall)) {
            return false;
        }
        AbstractCall other = (AbstractCall)obj;
        if (eventsCollector == null) {
            if (other.eventsCollector != null) {
                return false;
            }
        } else if (!eventsCollector.equals(other.eventsCollector)) {
            return false;
        }
        
        if (id == null) {
            if (other.id != null) {
                return false;
            }
        } else if (!id.equals(other.id)) {
            return false;
        }
      
        return true;
    }

    /**
     * @return Returns the handoverTime.
     */
    protected long getHandoverTime() {
        return handoverTime;
    }

    /**
     * @return Returns the lOGGER.
     */
    protected static Logger getLOGGER() {
        return LOGGER;
    }

    /**
     * @return Returns the reselection.
     */
    protected Long getReselection() {
        return reselection;
    }

    /**
     * @return Returns the updateTime.
     */
    protected Long getUpdateTime() {
        return updateTime;
    }

    /**
     * @return Returns the messageRecievedTime.
     */
    protected long getMessageRecievedTime() {
        return messageRecievedTime;
    }

    /**
     * @return Returns the messageAcnowledgeTime.
     */
    protected long getMessageAcnowledgeTime() {
        return messageAcnowledgeTime;
    }

    protected float getAvgLq() {
        return avgLq;
    }

    protected float getAvgDelay() {
        return avgDelay;
    }

    protected List<PESQResultElement> getTtcPesqList() {
        return ttcPesqList;
    }

    protected List<PESQResultElement> getTocPesqList() {
        return tocPesqList;
    }

    public void setCallType(CallType type) {
        this.type = type;
    }

    protected List<AbstractTOCTTC> getTocTTCList() {
        return tocttcList;
    }

    public void setErrCode(int errCode) {
        this.errCode = errCode;
    }

}
