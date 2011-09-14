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
import java.util.TimeZone;

/**
 * <p>
 * Abstract wrapper for toc ttc
 * </p>
 * 
 * @author Kondratenko_V
 * @since 1.0.0
 */
public abstract class AbstractTOCTTC implements IXmlTag {

    protected String probeID;
    protected Calendar calendar = Calendar.getInstance();
    protected Integer hook;
    protected Integer simplex;
    protected Integer causeForTermination;
    protected Calendar connectTime;
    protected Calendar releaseTime;

    protected List<PESQResultElement> pesqResult = new LinkedList<PESQResultElement>();
    protected InconclusiveElement inconclusive;


    public void setPesqResultMember(PESQResultElement member) {
        pesqResult.add(member);
    }

    /**
     * get date from document and parse it into a long format
     * 
     * @param stringData
     * @return
     * @throws ParseException
     */
    protected Long getTime(String stringData) throws ParseException {
        if (stringData == null) {
            return null;
        }
        int i = stringData.lastIndexOf(':');
        StringBuilder time = new StringBuilder(stringData.substring(0, i)).append(stringData.substring(i + 1, stringData.length()));
       
        long time2 = dateFormatWithTimeZone.parse(time.toString()).getTime();
        return time2;
    }

    /**
     * convert date to xml document format
     * 
     * @param stringData
     * @return
     * @throws ParseException
     */
    public String getTimeiInXMLformat(Calendar calendar) {
        try {
            Date calendarDate = calendar.getTime();
            String calendarString = dateFormatWithTimeZone.format(calendarDate);
            int i = calendarString.lastIndexOf('+');
            StringBuilder time = new StringBuilder(calendarString.substring(0, i += 3)).append(":").append(
                    calendarString.substring(i, calendarString.length()));

            return time.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
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
    public void setProbeId(String probeID) {
        this.probeID = probeID;
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

    public String getCalendarString(Calendar d) {
        return dateFormatWithTimeZone.format(d);
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

    public InconclusiveElement getInconclusive() {
        return inconclusive;
    }

    public void setInconclusive(InconclusiveElement inconclusive) {
        this.inconclusive = inconclusive;
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

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((calendar == null) ? 0 : calendar.hashCode());
        result = prime * result + ((causeForTermination == null) ? 0 : causeForTermination.hashCode());
        result = prime * result + ((connectTime == null) ? 0 : connectTime.hashCode());
        result = prime * result + ((hook == null) ? 0 : hook.hashCode());
        result = prime * result + ((inconclusive == null) ? 0 : inconclusive.hashCode());
        result = prime * result + ((pesqResult == null) ? 0 : pesqResult.hashCode());
        result = prime * result + ((probeID == null) ? 0 : probeID.hashCode());
        result = prime * result + ((releaseTime == null) ? 0 : releaseTime.hashCode());
        result = prime * result + ((simplex == null) ? 0 : simplex.hashCode());
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
        if (!(obj instanceof AbstractTOCTTC)) {
            return false;
        }
        AbstractTOCTTC other = (AbstractTOCTTC)obj;
        
        if (causeForTermination == null) {
            if (other.causeForTermination != null) {
                return false;
            }
        } else if (!causeForTermination.equals(other.causeForTermination)) {
            return false;
        }
        if (connectTime == null) {
            if (other.connectTime != null) {
                return false;
            }
        } else if (!connectTime.equals(other.connectTime)) {
            return false;
        }
        if (hook == null) {
            if (other.hook != null) {
                return false;
            }
        } else if (!hook.equals(other.hook)) {
            return false;
        }
        if (inconclusive == null) {
            if (other.inconclusive != null) {
                return false;
            }
        } else if (!inconclusive.equals(other.inconclusive)) {
            return false;
        }
        if (pesqResult == null) {
            if (other.pesqResult != null) {
                return false;
            }
        } else if (!pesqResult.equals(other.pesqResult)) {
            return false;
        }
        if (probeID == null) {
            if (other.probeID != null) {
                return false;
            }
        } else if (!probeID.equals(other.probeID)) {
            return false;
        }
        if (releaseTime == null) {
            if (other.releaseTime != null) {
                return false;
            }
        } else if (!releaseTime.equals(other.releaseTime)) {
            return false;
        }
        if (simplex == null) {
            if (other.simplex != null) {
                return false;
            }
        } else if (!simplex.equals(other.simplex)) {
            return false;
        }
        return true;
    }
}
