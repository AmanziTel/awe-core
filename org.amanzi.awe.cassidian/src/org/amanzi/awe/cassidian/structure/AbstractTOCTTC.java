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

package org.amanzi.awe.cassidian.structure;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.amanzi.awe.cassidian.constants.ChildTypes;
import org.amanzi.awe.cassidian.constants.LoaderConstants;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Kondratenko_V
 * @since 1.0.0
 */
public abstract class AbstractTOCTTC implements IXmlTag {

    private String probeID;
    private String calledNumber;
    private Integer hook;
    private Integer simplex;
    private Integer priority;
    private Calendar configTime;
    private Calendar setupTime;
    private Calendar connectTime;
    private Calendar disconnectTime;
    private Calendar releaseTime;
    private Integer causeForTermination;

    private List<PESQResultElement> pesqResult = new LinkedList<PESQResultElement>();

    public void setValueByTagType(String tagName, Object value) {
        try {
            if (value != null) {
                if (tagName.equals(LoaderConstants.CONFIG_TIME)) {
                    calendar.setTimeInMillis(getTime(value.toString()));
                    setConfigTime(calendar);
                } else if (tagName.equals(LoaderConstants.CONNECT_TIME)) {
                    calendar.setTimeInMillis(getTime(value.toString()));
                    setConnectTime(calendar);
                } else if (tagName.equals(LoaderConstants.DISCONNECT_TIME)) {
                    calendar.setTimeInMillis(getTime(value.toString()));
                    setDisconnectTime(calendar);
                } else if (tagName.equals(LoaderConstants.HOOK)) {
                    setHook(Integer.parseInt(value.toString()));
                } else if (tagName.equals(LoaderConstants.PRIORITY)) {
                    setPriority(Integer.parseInt(value.toString()));
                } else if (tagName.equals(LoaderConstants.PROBE_ID)) {
                    setProbeID(value.toString());
                } else if (tagName.equals(LoaderConstants.RELEASE_TIME)) {
                    calendar.setTimeInMillis(getTime(value.toString()));
                    setReleaseTime(calendar);
                } else if (tagName.equals(LoaderConstants.SETUP_TIME)) {
                    calendar.setTimeInMillis(getTime(value.toString()));
                    setSetupTime(calendar);
                } else if (tagName.equals(LoaderConstants.SIMPLEX)) {
                    setSimplex(Integer.parseInt(value.toString()));
                } else if (tagName.equals(LoaderConstants.CALLED_NUMBER)) {
                    setCalledNumber(value.toString());
                } else if (tagName.equals(LoaderConstants.CAUSE_FOR_TERMINATION)) {
                    setCauseForTermination(Integer.parseInt(value.toString()));
                } else if (tagName.equalsIgnoreCase(ChildTypes.PESQ_RESULT.getId())) {
                    setPesqResultMember((PESQResultElement)value);
                }
            }
        } catch (ParseException ex) {
            ex.printStackTrace();
        }
    }

    public void setPesqResultMember(PESQResultElement member) {
        pesqResult.add(member);
    }

    public Object getValueByTagType(String tagName) {
        if (tagName.equals(LoaderConstants.CONFIG_TIME)) {

            return getConfigTime();
        } else if (tagName.equals(LoaderConstants.CONNECT_TIME)) {
            return getConnectTime();
        } else if (tagName.equals(LoaderConstants.DISCONNECT_TIME)) {
            getDisconnectTime();
        } else if (tagName.equals(LoaderConstants.HOOK)) {
            return getHook();
        } else if (tagName.equals(LoaderConstants.PRIORITY)) {
            return getPriority();
        } else if (tagName.equals(LoaderConstants.PROBE_ID)) {
            return getProbeID();
        } else if (tagName.equals(LoaderConstants.RELEASE_TIME)) {
            return getReleaseTime();
        } else if (tagName.equals(LoaderConstants.SETUP_TIME)) {
            return getSetupTime();
        } else if (tagName.equals(LoaderConstants.SIMPLEX)) {
            return getSimplex();
        } else if (tagName.equals(LoaderConstants.CALLED_NUMBER)) {
            return getCalledNumber();
        } else if (tagName.equals(LoaderConstants.CAUSE_FOR_TERMINATION)) {
            return getCauseForTermination();
        }
        return tagName;
    }

    public AbstractTOCTTC(String probeID, String calledNumber, Integer hook, Integer simplex, Integer priority,
            Calendar configTime, Calendar setupTime, Calendar connectTime, Calendar disconnectTime, Calendar releaseTime,
            Integer causeForTermination, PESQResultElement pesqResultMember) {
        this.probeID = probeID;
        this.calledNumber = calledNumber;
        this.hook = hook;
        this.simplex = simplex;
        this.priority = priority;
        this.configTime = configTime;
        this.setupTime = setupTime;
        this.connectTime = connectTime;
        this.disconnectTime = disconnectTime;
        this.releaseTime = releaseTime;
        this.causeForTermination = causeForTermination;
        this.pesqResult.add(pesqResultMember);
    }

    /**
     * get time from document and parse it into a long format
     * 
     * @param stringData
     * @return
     * @throws ParseException
     */
    private Long getTime(String stringData) throws ParseException {
        if (stringData == null) {
            return null;
        }
        int i = stringData.lastIndexOf(':');
        StringBuilder time = new StringBuilder(stringData.substring(0, i)).append(stringData.substring(i + 1, stringData.length()));
        long time2 = SDF.parse(time.toString()).getTime();
        return time2;
    }

    /**
     * set time from xml document format
     * 
     * @param stringData
     * @return
     * @throws ParseException
     */
    public String getTimeiInXMLformat(Calendar calendar) {
        try {
            Date calendarDate = calendar.getTime();
            String calendarString = SDF.format(calendarDate);
            int i = calendarString.lastIndexOf('+');
            StringBuilder time = new StringBuilder(calendarString.substring(0, i += 3)).append(":").append(
                    calendarString.substring(i, calendarString.length()));

            return time.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public AbstractTOCTTC() {
        this.probeID = null;
        this.calledNumber = null;
        this.hook = null;
        this.simplex = null;
        this.priority = null;
        this.configTime = null;
        this.setupTime = null;
        this.connectTime = null;
        this.disconnectTime = null;
        this.releaseTime = null;
        this.causeForTermination = null;

    }

    /**
     * @return the pesqResult
     */
    public List<PESQResultElement> getPesqResult() {
        return pesqResult;
    }

    /**
     * @param pesqResult the pesqResult to set
     */
    public void setPesqResult(List<PESQResultElement> pesqResult) {
        this.pesqResult.addAll(pesqResult);
    }

    public void addPesqMember(PESQResultElement pesqResultMember) {
        this.pesqResult.add(pesqResultMember);
    }

    public void removePesqMember(PESQResultElement pesqResultMember) {
        this.pesqResult.remove(pesqResultMember);
    }

    public PESQResultElement getPESQMember(PESQResultElement pesqResultMember) {
        return this.pesqResult.get(pesqResult.indexOf(pesqResultMember));
    }

    /**
     * @return the probeID
     */
    public String getProbeID() {
        return probeID;
    }

    /**
     * @param probeID the probeID to set
     */
    public void setProbeID(String probeID) {
        this.probeID = probeID;
    }

    /**
     * @return the calledNumber
     */
    public String getCalledNumber() {
        return calledNumber;
    }

    /**
     * @param calledNumber the calledNumber to set
     */
    public void setCalledNumber(String calledNumber) {
        this.calledNumber = calledNumber;
    }

    /**
     * @return the hook
     */
    public Integer getHook() {
        return hook;
    }

    /**
     * @param hook the hook to set
     */
    public void setHook(Integer hook) {
        this.hook = hook;
    }

    /**
     * @return the simplex
     */
    public Integer getSimplex() {
        return simplex;
    }

    /**
     * @param simplex the simplex to set
     */
    public void setSimplex(Integer simplex) {
        this.simplex = simplex;
    }

    /**
     * @return the priority
     */
    public Integer getPriority() {
        return priority;
    }

    /**
     * @param priority the priority to set
     */
    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    /**
     * @return the configTime
     */
    public Calendar getConfigTime() {
        return configTime;
    }

    public String getCalendarString(Calendar d) {
        return SDF.format(d);
    }

    /**
     * @param configTime the configTime to set
     */
    public void setConfigTime(Calendar configTime) {

        this.configTime = configTime;

    }

    /**
     * @return the setupTime
     */
    public Calendar getSetupTime() {
        return setupTime;
    }

    /**
     * @param setupTime the setupTime to set
     */
    public void setSetupTime(Calendar setupTime) {
        this.setupTime = setupTime;
    }

    /**
     * @return the connectTime
     */
    public Calendar getConnectTime() {
        return connectTime;
    }

    /**
     * @param connectTime the connectTime to set
     */
    public void setConnectTime(Calendar connectTime) {
        this.connectTime = connectTime;
    }

    /**
     * @return the disconnectTime
     */
    public Calendar getDisconnectTime() {
        return disconnectTime;
    }

    /**
     * @param disconnectTime the disconnectTime to set
     */
    public void setDisconnectTime(Calendar disconnectTime) {
        this.disconnectTime = disconnectTime;
    }

    /**
     * @return the releaseTime
     */
    public Calendar getReleaseTime() {
        return releaseTime;
    }

    /**
     * @param releaseTime the releaseTime to set
     */
    public void setReleaseTime(Calendar releaseTime) {
        this.releaseTime = releaseTime;
    }

    /**
     * @return the causeForTermination
     */
    public Integer getCauseForTermination() {
        return causeForTermination;
    }

    /**
     * @param causeForTermination the causeForTermination to set
     */
    public void setCauseForTermination(Integer causeForTermination) {
        this.causeForTermination = causeForTermination;
    }

    public abstract String getType();
}
