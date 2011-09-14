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

import java.io.File;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.amanzi.awe.cassidian.enums.CallProperties.CallType;
import org.amanzi.awe.cassidian.structure.CompleteGpsDataList;
import org.amanzi.awe.cassidian.structure.GroupAttach;
import org.amanzi.awe.cassidian.structure.Ntpq;
import org.amanzi.neo.loader.core.newsaver.IData;

/**
 * contains parsed calls collection
 * <p>
 * </p>
 * 
 * @author Kondratenko_Vladislav
 * @since 1.0.0
 */
public class CallCollector implements IData {
    /**
     * group individual help calls collection;
     */
    private List<RealCall> realCall;
    /**
     * itsi attach calls collection
     */
    private List<ItsiAttachCall> itsiAttachCalls;
    /**
     * sds tsm alarm calls collection
     */
    private List<MessageCall> messageCalls;
    /**
     * handovers calls collection
     */
    private List<HandoverCall> handovers;
    /**
     * cell reselections calls collection
     */
    private List<CellReselCall> cellResels;
    private File file;
    protected List<CompleteGpsDataList> gpsData;
    protected List<Ntpq> ntpq;
    protected List<GroupAttach> groupAttach;
    private Map<String, AbstractCall> callsCache;

    public List<RealCall> getRealCalls() {
        return realCall;
    }

    public void setRealCalls(List<RealCall> individuCalls) {
        this.realCall = individuCalls;
    }

    public List<ItsiAttachCall> getItsiAttachCalls() {
        return itsiAttachCalls;
    }

    public void setItsiAttachCalls(List<ItsiAttachCall> itsiAttachCalls) {
        this.itsiAttachCalls = itsiAttachCalls;
    }

    public List<MessageCall> getSDSCalls() {
        List<MessageCall> real = new LinkedList<MessageCall>();
        for (MessageCall call : messageCalls) {
            if (call.getCallType() == CallType.SDS) {
                real.add(call);
            }
        }
        return real;
    }

    public List<MessageCall> getTSMCalls() {
        List<MessageCall> real = new LinkedList<MessageCall>();
        for (MessageCall call : messageCalls) {
            if (call.getCallType() == CallType.TSM) {
                real.add(call);
            }
        }
        return real;
    }

    /**
     * add call to collection
     * 
     * @param call
     */
    private void addCallToColection(AbstractCall call) {
        if (call == null) {
            return;
        }

        if (call instanceof RealCall) {
            realCall.add((RealCall)call);
        } else if (call instanceof ItsiAttachCall) {
            itsiAttachCalls.add((ItsiAttachCall)call);
        } else if (call instanceof MessageCall) {
            messageCalls.add((MessageCall)call);
        } else if (call instanceof HandoverCall) {
            handovers.add((HandoverCall)call);
        } else if (call instanceof CellReselCall) {
            cellResels.add((CellReselCall)call);
        }
        callsCache.put(call.getId(), call);
    }

    /**
     * find call in cache by id
     * 
     * @param id
     * @return call if finded,else null
     */
    public AbstractCall findCallInCacheById(String id) {
        if (callsCache.containsKey(id)) {
            return callsCache.get(id);
        }
        return null;
    }

    /**
     * find or create call in Cache
     * 
     * @param call
     * @return call
     */
    public AbstractCall findInCacheOrCreate(AbstractCall call) {
        if (callsCache.containsKey(call.getId())) {
            return callsCache.get(call.getId());
        }
        callsCache.put(call.getId(), call);
        return callsCache.get(call.getId());
    }

    /**
     * default constructor initialize all necessaries fields
     */
    public CallCollector() {
        super();
        callsCache = new HashMap<String, AbstractCall>();
        realCall = new LinkedList<RealCall>();
        itsiAttachCalls = new LinkedList<ItsiAttachCall>();
        messageCalls = new LinkedList<MessageCall>();
        handovers = new LinkedList<HandoverCall>();
        cellResels = new LinkedList<CellReselCall>();
    }

    public List<RealCall> getHelpCalls() {
        List<RealCall> real = new LinkedList<RealCall>();
        for (RealCall call : realCall) {
            if (call.getCallType() == CallType.HELP) {
                real.add(call);
            }
        }
        return real;
    }

    public List<RealCall> getGroupCalls() {
        List<RealCall> real = new LinkedList<RealCall>();
        for (RealCall call : realCall) {
            if (call.getCallType() == CallType.GROUP) {
                real.add(call);
            }
        }
        return real;
    }

    public List<RealCall> getEmergencyCalls() {
        List<RealCall> real = new LinkedList<RealCall>();
        for (RealCall call : realCall) {
            if (call.getCallType() == CallType.EMERGENCY) {
                real.add(call);
            }
        }
        return real;
    }

    public List<RealCall> getIndividualCalls() {
        List<RealCall> real = new LinkedList<RealCall>();
        for (RealCall call : realCall) {
            if (call.getCallType() == CallType.INDIVIDUAL) {
                real.add(call);
            }
        }
        return real;
    }

    public List<HandoverCall> getHandovers() {
        return handovers;
    }

    public void setHandovers(List<HandoverCall> handovers) {
        this.handovers = handovers;
    }

    public List<CellReselCall> getCellResels() {
        return cellResels;
    }

    public void setCellResels(List<CellReselCall> cellResels) {
        this.cellResels = cellResels;
    }

    /**
     * add collected call to cache map
     */
    public void runPropertyPreporator() {
        for (String key : callsCache.keySet()) {
            callsCache.get(key).prepareCallProperties();
            addCallToColection(callsCache.get(key));
        }
    }

    public Map<String, AbstractCall> getCallsCache() {
        return callsCache;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    /**
     * @return Returns the gpsData.
     */
    protected List<CompleteGpsDataList> getGpsData() {
        return gpsData;
    }

    /**
     * @param gpsData The gpsData to set.
     */
    protected void setGpsData(List<CompleteGpsDataList> gpsData) {
        this.gpsData = gpsData;
    }

    /**
     * @return Returns the ntpq.
     */
    public List<Ntpq> getNtpq() {
        return ntpq;
    }

    /**
     * @param ntpq The ntpq to set.
     */
    public void setNtpq(List<Ntpq> ntpq) {
        this.ntpq = ntpq;
    }

    /**
     * @return Returns the groupAttach.
     */
    public List<GroupAttach> getGroupAttach() {
        return groupAttach;
    }

    /**
     * @param groupAttach The groupAttach to set.
     */
    public void setGroupAttach(List<GroupAttach> groupAttach) {
        this.groupAttach = groupAttach;
    }

    /**
     * add member to group attach list
     * 
     * @param groupattach
     */
    public void addMemberToGroupAttach(GroupAttach groupattach) {
        this.groupAttach.add(groupattach);
    }

}
