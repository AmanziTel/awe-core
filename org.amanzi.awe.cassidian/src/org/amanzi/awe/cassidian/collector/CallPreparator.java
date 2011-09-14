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

package org.amanzi.awe.cassidian.collector;

import java.text.SimpleDateFormat;
import java.util.List;

import org.amanzi.awe.cassidian.constants.LoaderConstants;
import org.amanzi.awe.cassidian.enums.CallProperties.CallResult;
import org.amanzi.awe.cassidian.enums.CallProperties.CallType;
import org.amanzi.awe.cassidian.structure.AbstactMsg;
import org.amanzi.awe.cassidian.structure.AbstractTOCTTC;
import org.amanzi.awe.cassidian.structure.CellReselection;
import org.amanzi.awe.cassidian.structure.EventsElement;
import org.amanzi.awe.cassidian.structure.GPSData;
import org.amanzi.awe.cassidian.structure.GroupAttach;
import org.amanzi.awe.cassidian.structure.Handover;
import org.amanzi.awe.cassidian.structure.InconclusiveElement;
import org.amanzi.awe.cassidian.structure.ItsiAttach;
import org.amanzi.awe.cassidian.structure.Ntpq;
import org.amanzi.awe.cassidian.structure.ProbeIDNumberMap;
import org.amanzi.awe.cassidian.structure.SendMsg;
import org.amanzi.awe.cassidian.structure.TOCElement;
import org.apache.commons.lang.StringUtils;

/**
 * prepare call before save
 * <p>
 * </p>
 * 
 * @author Kondratenko_Vladislav
 * @since 1.0.0
 */
public class CallPreparator {
    private SimpleDateFormat dateFormatinName = new SimpleDateFormat("hh:mm:ss");
    private CallCollector collector;

    /**
     * initialize required variables;
     */
    public CallPreparator() {
        super();

        getCallCollector();
    }

    private CallCollector getCallCollector() {
        if (collector == null) {
            collector = new CallCollector();
        }
        return collector;
    }

    public void resetCollector() {
        collector = null;
        collector = new CallCollector();
    }

    /**
     * extract calls from parsed file
     */
    public CallCollector extractCallsFromEvents(List<ProbeIDNumberMap> probeList, List<EventsElement> eventList,
            List<GPSData> gpsDatas, List<Ntpq> ntpqs) {
        List<EventsElement> eventsCollector;
        String prevCallId = "";
        String phoneNumber = "";
        resetCollector();

        for (EventsElement event : eventList) {
            AbstractTOCTTC tocttc = event.getTocttc();
            AbstactMsg msg = event.getSendRecieveMsg();
            ItsiAttach itsiAttach = event.getItsiAttach();
            CallType callType = null;
            AbstractCall call = null;
            Handover handover = event.getHandover();
            CallResult result = null;
            InconclusiveElement inconclusiveElement = null;
            GroupAttach groupAttach = event.getGroupAttach();
            CellReselection cellResel = event.getCellReselection();
            if (tocttc != null) {
                Integer hook = tocttc.getHook();
                Integer simplex = tocttc.getSimplex();
                Integer priority = null;
                phoneNumber = getPhoneNumberByProbeId(probeList, tocttc.getProbeID());
                if (tocttc instanceof TOCElement) {
                    priority = ((TOCElement)tocttc).getPriority();
                    inconclusiveElement = tocttc.getInconclusive();
                    callType = getCallType(hook, simplex, priority);
                    result = getCallResult(inconclusiveElement);
                    call = createCallByType(callType);
                    call.setId(tocttc.getProbeID() + "_" + dateFormatinName.format(((TOCElement)tocttc).getConfigTime().getTime()));
                    dateFormatinName.format(1309277667600l);
                    call.setCallType(callType);
                    prevCallId = call.getId();
                    Integer ct = tocttc.getCauseForTermination();
                    if (ct != null && ct != 1) {
                        result = CallResult.FAILURE;
                    }
                }

            } else if (msg != null) {
                phoneNumber = getPhoneNumberByProbeId(probeList, msg.getProbeId());
                if (msg instanceof SendMsg) {
                    callType = getCallType(msg.getMsgType());
                    call = createCallByType(callType);
                    inconclusiveElement = msg.getInconclusive();
                    result = getCallResult(inconclusiveElement);
                    call.setId(msg.getProbeId() + "_" + dateFormatinName.format(((SendMsg)msg).getSendTime().getTime()));
                    prevCallId = msg.getProbeId();
                    call.setCallType(callType);
                }
            } else if (handover != null) {
                phoneNumber = getPhoneNumberByProbeId(probeList, handover.getProbeId());
                call = createCallByType(CallType.ITSI_HO);
                inconclusiveElement = handover.getInconclusive();
                call.setPhoneNumber(phoneNumber);
                result = getCallResult(inconclusiveElement);
                call.setId(handover.getProbeId() + "_" + dateFormatinName.format(handover.getHoReq().getTime()));
                call.setCallType(callType);
            } else if (cellResel != null) {
                phoneNumber = getPhoneNumberByProbeId(probeList, cellResel.getProbeId());
                call = createCallByType(CallType.ITSI_CC);
                call.setId(cellResel.getProbeId() + "_" + dateFormatinName.format(cellResel.getCellReselReq().getTime()));
                inconclusiveElement = cellResel.getInconclusive();
                result = getCallResult(inconclusiveElement);
                call.setCallType(callType);
            } else if (itsiAttach != null) {
                phoneNumber = getPhoneNumberByProbeId(probeList, itsiAttach.getProbeId());
                call = createCallByType(CallType.ITSI_ATTACH);
                call.setId(itsiAttach.getProbeId() + "_" + dateFormatinName.format(itsiAttach.getItsiAttReq().getTime()));
                inconclusiveElement = itsiAttach.getInconclusive();
                result = getCallResult(inconclusiveElement);
                call.setCallType(callType);
            } else if (groupAttach != null) {
                collector.addMemberToGroupAttach(groupAttach);
            }

            if (result != null) {
                if (inconclusiveElement != null) {
                    call.setInconclusive(true);
                    call.setErrCode(inconclusiveElement.getErrCode());
                }
                call.setCallResult(result);
            }

            if (call != null) {
                if (!gpsDatas.isEmpty()) {
                    collector.setGpsData(gpsDatas.get(0).getCompleteGpsDataList());
                }
                if (ntpqs != null) {
                    collector.setNtpq(ntpqs);
                }
                call.setPhoneNumber(phoneNumber);
                eventsCollector = collector.findInCacheOrCreate(call).getEventsCollector();
                if (!eventsCollector.isEmpty()) {
                    if (!eventsCollector.contains(event)) {
                        eventsCollector.add(event);
                    }
                } else {
                    eventsCollector.add(event);
                    call.setEventsCollector(eventsCollector);
                }
            } else {
                if (!StringUtils.isEmpty(prevCallId)) {
                    call = collector.findCallInCacheById(prevCallId);
                    eventsCollector = call.getEventsCollector();
                    eventsCollector.add(event);
                }
            }
        }
        collector.runPropertyPreporator();
        return collector;
    }

    /**
 * 
 */
    String getPhoneNumberByProbeId(List<ProbeIDNumberMap> probes, String probeId) {
        for (ProbeIDNumberMap probe : probes) {
            if (probe.getProbeId().equals(probeId)) {
                return probe.getPhoneNumber().toString();
            }
        }
        return "";
    }

    /**
     * get result by inconclusive
     * 
     * @param inconclusive
     * @return
     */
    private CallResult getCallResult(InconclusiveElement inconclusive) {
        if (inconclusive != null && inconclusive.getErrCode() != null) {
            return CallResult.FAILURE;
        } else {
            return CallResult.SUCCESS;
        }
    }

    /**
     * get SDS, TSM calltype
     * 
     * @param msgtype
     * @return
     */
    private CallType getCallType(Integer msgtype) {
        if (msgtype.equals(LoaderConstants.SDS_MSG_TYPE)) {
            return CallType.SDS;
        } else if (msgtype.equals(LoaderConstants.TSM_MSG_TYPE)) {
            return CallType.TSM;
        }
        return null;
    }

    /**
     * get individual ,help,group, emergency calltype
     * 
     * @param hook
     * @param simplex
     * @param priority
     * @return
     */
    private CallType getCallType(Integer hook, Integer simplex, Integer priority) {
        if (hook == LoaderConstants.INDIVIDUAL_HELP_HOOK && simplex == LoaderConstants.INDIVIDUAL_HELP_SIMPLEX) {

            if (priority != null && priority == LoaderConstants.HELP_PRIORITY) {
                return CallType.HELP;
            }
            return CallType.INDIVIDUAL;
        } else if (hook == LoaderConstants.GROUP_EMERGENCY_HOOK && simplex == LoaderConstants.GROUP_EMERGENCY_SIMPLEX) {
            if (priority != null) {
                if (priority >= LoaderConstants.EMER_PRIORITY) {
                    return CallType.EMERGENCY;
                }

            }
            return CallType.GROUP;
        }

        return null;
    }

    /**
     * create call instance by call type
     * 
     * @param type
     * @return
     */
    private AbstractCall createCallByType(CallType type) {
        if (type == null) {
            return null;
        }
        switch (type) {
        case INDIVIDUAL:
        case GROUP:
        case EMERGENCY:
        case HELP:
            return new RealCall();
        case ITSI_ATTACH:
            return new ItsiAttachCall();
        case SDS:
        case TSM:
            return new MessageCall();
        case ITSI_HO:
            return new HandoverCall();
        case ITSI_CC:
            return new CellReselCall();
        }
        return null;
    }
}
