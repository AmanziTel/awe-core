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
import org.amanzi.awe.cassidian.constants.ChildTypes;
import org.amanzi.awe.cassidian.constants.LoaderConstants;

/**
 * <p>
 * Describe ttc tag
 * </p>
 * 
 * @author Kondratenko_V
 * @since 1.0.0
 */
public class TTCElement extends AbstractTOCTTC {

    private Calendar indicationTime;
    private Calendar answerTime;
    private String callingNumber;

    public TTCElement() {
        super();
    }

    public Calendar getIndicationTime() {
        return indicationTime;
    }

    public void setIndicationTime(Calendar indicationTime) {
        this.indicationTime = indicationTime;
    }

    public Calendar getAnswerTime() {
        return answerTime;
    }

    public void setAnswerTime(Calendar answerTime) {
        this.answerTime = answerTime;
    }

    public Calendar getConnectTime() {
        return connectTime;
    }

    public void setConnectTime(Calendar connectTime) {
        this.connectTime = connectTime;
    }

    public Calendar getReleaseTime() {
        return releaseTime;
    }

    public void setReleaseTime(Calendar releaseTime) {
        this.releaseTime = releaseTime;
    }

    public void setValueByTagType(String tagName, Object value) {
        try {
            if (value != null) {
                if (tagName.equals(LoaderConstants.ANSWER_TIME)) {
                    calendar = Calendar.getInstance();
                    calendar.setTimeInMillis(getTime(value.toString()));
                    setAnswerTime(calendar);
                } else if (tagName.equals(LoaderConstants.INDICATION_TIME)) {
                    calendar = Calendar.getInstance();
                    calendar.setTimeInMillis(getTime(value.toString()));
                    setIndicationTime(calendar);
                } else if (tagName.equals(LoaderConstants.CONNECT_TIME)) {
                    calendar = Calendar.getInstance();
                    calendar.setTimeInMillis(getTime(value.toString()));
                    setConnectTime(calendar);
                } else if (tagName.equals(LoaderConstants.HOOK)) {
                    setHook(Integer.parseInt(value.toString()));
                } else if (tagName.equals(LoaderConstants.PROBE_ID)) {
                    setProbeId(value.toString());
                } else if (tagName.equals(LoaderConstants.RELEASE_TIME)) {
                    calendar = Calendar.getInstance();
                    calendar.setTimeInMillis(getTime(value.toString()));
                    setReleaseTime(calendar);
                } else if (tagName.equals(LoaderConstants.SIMPLEX)) {
                    setSimplex(Integer.parseInt(value.toString()));
                } else if (tagName.equals(LoaderConstants.CALLING_NUMBER)) {
                    callingNumber = value.toString();
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

        if (tagName.equals(LoaderConstants.ANSWER_TIME)) {
            return answerTime;
        } else if (tagName.equals(LoaderConstants.INDICATION_TIME)) {
            return indicationTime;
        } else if (tagName.equals(LoaderConstants.HOOK)) {
            return hook;
        } else if (tagName.equals(LoaderConstants.PROBE_ID)) {
            return probeID;
        } else if (tagName.equals(LoaderConstants.RELEASE_TIME)) {
            return releaseTime;
        } else if (tagName.equals(LoaderConstants.SIMPLEX)) {
            return simplex;
        } else if (tagName.equals(LoaderConstants.CALLING_NUMBER)) {
            return callingNumber;
        } else if (tagName.equals(LoaderConstants.CAUSE_FOR_TERMINATION)) {
            return causeForTermination;
        } else if (tagName.equalsIgnoreCase(ChildTypes.PESQ_RESULT.getId())) {
            return pesqResult;
        } else if (tagName.equals(ChildTypes.INCONCLUSIVE.getId())) {
            return inconclusive;
        }

        return null;
    }

    public String getCallingNumber() {
        return callingNumber;
    }

    public void setCallingNumber(String callingNumber) {
        this.callingNumber = callingNumber;
    }

    @Override
    public String getType() {
        return ChildTypes.TTC.getId();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((answerTime == null) ? 0 : answerTime.hashCode());
        result = prime * result + ((callingNumber == null) ? 0 : callingNumber.hashCode());
        result = prime * result + ((indicationTime == null) ? 0 : indicationTime.hashCode());
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
        if (!(obj instanceof TTCElement)) {
            return false;
        }
        TTCElement other = (TTCElement)obj;
        if (answerTime == null) {
            if (other.answerTime != null) {
                return false;
            }
        } else if (!answerTime.equals(other.answerTime)) {
            return false;
        }
        if (callingNumber == null) {
            if (other.callingNumber != null) {
                return false;
            }
        } else if (!callingNumber.equals(other.callingNumber)) {
            return false;
        }
        if (indicationTime == null) {
            if (other.indicationTime != null) {
                return false;
            }
        } else if (!indicationTime.equals(other.indicationTime)) {
            return false;
        }
        return true;
    }

}
