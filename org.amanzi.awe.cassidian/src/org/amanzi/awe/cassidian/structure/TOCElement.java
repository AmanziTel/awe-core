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
/**
 * 
 * <p>
 * Describe toc tag
 * </p>
 * 
 * @author Kondratenko_V
 * @since 1.0.0
 */
import org.amanzi.awe.cassidian.constants.ChildTypes;
import org.amanzi.awe.cassidian.constants.LoaderConstants;

public class TOCElement extends AbstractTOCTTC {
    private static final String TYPE = ChildTypes.TOC.getId();

    private Integer priority;
    private Calendar configTime;
    private Calendar setupTime;
    private Calendar disconnectTime;
    private String calledNumber;

    public TOCElement() {
        super();
    }

    @Override
    public String getType() {
        return TYPE;
    }

    public void setValueByTagType(String tagName, Object value) {
        try {
            if (value != null) {
                if (tagName.equals(LoaderConstants.CONFIG_TIME)) {
                    calendar = Calendar.getInstance();
                    calendar.setTimeInMillis(getTime(value.toString()));
                    setConfigTime(calendar);
                } else if (tagName.equals(LoaderConstants.CONNECT_TIME)) {
                    calendar = Calendar.getInstance();
                    calendar.setTimeInMillis(getTime(value.toString()));
                    setConnectTime(calendar);
                } else if (tagName.equals(LoaderConstants.DISCONNECT_TIME)) {
                    calendar = Calendar.getInstance();
                    calendar.setTimeInMillis(getTime(value.toString()));
                    setDisconnectTime(calendar);
                } else if (tagName.equals(LoaderConstants.HOOK)) {
                    setHook(Integer.parseInt(value.toString()));
                } else if (tagName.equals(LoaderConstants.PRIORITY)) {
                    setPriority(Integer.parseInt(value.toString()));
                } else if (tagName.equals(LoaderConstants.PROBE_ID)) {
                    setProbeId(value.toString());
                } else if (tagName.equals(LoaderConstants.RELEASE_TIME)) {
                    calendar = Calendar.getInstance();
                    calendar.setTimeInMillis(getTime(value.toString()));
                    setReleaseTime(calendar);
                } else if (tagName.equals(LoaderConstants.SETUP_TIME)) {
                    calendar = Calendar.getInstance();
                    calendar.setTimeInMillis(getTime(value.toString()));
                    setSetupTime(calendar);
                } else if (tagName.equals(LoaderConstants.SIMPLEX)) {
                    setSimplex(Integer.parseInt(value.toString()));
                } else if (tagName.equals(LoaderConstants.CALLED_NUMBER)) {
                    calledNumber = value.toString();
                } else if (tagName.equals(LoaderConstants.CAUSE_FOR_TERMINATION)) {
                    setCauseForTermination(Integer.parseInt(value.toString()));
                } else if (tagName.equalsIgnoreCase(ChildTypes.PESQ_RESULT.getId())) {
                    setPesqResultMember((PESQResultElement)value);
                } else if (tagName.equals(ChildTypes.INCONCLUSIVE.getId())) {
                    super.inconclusive = (InconclusiveElement)value;
                }
            }
        } catch (ParseException ex) {
            ex.printStackTrace();
        }
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

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((calledNumber == null) ? 0 : calledNumber.hashCode());
        result = prime * result + ((configTime == null) ? 0 : configTime.hashCode());
        result = prime * result + ((disconnectTime == null) ? 0 : disconnectTime.hashCode());
        result = prime * result + ((priority == null) ? 0 : priority.hashCode());
        result = prime * result + ((setupTime == null) ? 0 : setupTime.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!super.equals(obj)) {
            return false;
        }
        if (!(obj instanceof TOCElement)) {
            return false;
        }
        TOCElement other = (TOCElement)obj;
        if (calledNumber == null) {
            if (other.calledNumber != null) {
                return false;
            }
        } else if (!calledNumber.equals(other.calledNumber)) {
            return false;
        }
        if (configTime == null) {
            if (other.configTime != null) {
                return false;
            }
        } else if (!configTime.equals(other.configTime)) {
            return false;
        }
        if (disconnectTime == null) {
            if (other.disconnectTime != null) {
                return false;
            }
        } else if (!disconnectTime.equals(other.disconnectTime)) {
            return false;
        }
        if (priority == null) {
            if (other.priority != null) {
                return false;
            }
        } else if (!priority.equals(other.priority)) {
            return false;
        }
        if (setupTime == null) {
            if (other.setupTime != null) {
                return false;
            }
        } else if (!setupTime.equals(other.setupTime)) {
            return false;
        }
        return true;
    }

}
