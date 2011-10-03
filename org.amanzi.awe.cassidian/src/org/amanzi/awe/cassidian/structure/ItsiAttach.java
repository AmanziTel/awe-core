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

import org.amanzi.awe.cassidian.constants.ChildTypes;
import org.amanzi.awe.cassidian.constants.LoaderConstants;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Kondratenko_Vladislav
 * @since 1.0.0
 */
public class ItsiAttach implements IXmlTag {
    private Calendar calendar;
    private String probeId;
    private Calendar itsiAttReq;
    private Calendar itsiAccept;
    private Long locationAreaBefore;
    private Long locationAreaAfter;
    private InconclusiveElement inconclusive;

    @Override
    public String getType() {
        return ChildTypes.ITSI_ATTACH.getId();
    }

    @Override
    public void setValueByTagType(String tagName, Object value) {
        if (tagName.equals(LoaderConstants.PROBE_ID)) {
            probeId = value.toString();
        } else if (tagName.equals(LoaderConstants.ITSI_ATT_REQ)) {
            calendar = Calendar.getInstance();
            calendar.setTimeInMillis(getTime(value.toString()));
            setItsiAttReq(calendar);
        } else if (tagName.equals(LoaderConstants.ITSI_ATT_ACCEPT)) {
            calendar = Calendar.getInstance();
            calendar.setTimeInMillis(getTime(value.toString()));
            setItsiAccept(calendar);
        } else if (tagName.equals(LoaderConstants.LOCATION_AREA_AFTER)) {
            locationAreaAfter = Long.parseLong(value.toString());
        } else if (tagName.equals(LoaderConstants.LOCATION_AREA_BEFORE)) {
            locationAreaBefore = Long.parseLong(value.toString());
        } else if (tagName.equals(ChildTypes.INCONCLUSIVE.getId())) {
            inconclusive = (InconclusiveElement)value;
        }
    }

    @Override
    public Object getValueByTagType(String tagName) {
        if (tagName.equals(LoaderConstants.PROBE_ID)) {
            return probeId;
        } else if (tagName.equals(LoaderConstants.ITSI_ATT_REQ)) {
            return itsiAttReq;
        } else if (tagName.equals(LoaderConstants.ITSI_ATT_ACCEPT)) {
            return itsiAccept;
        } else if (tagName.equals(LoaderConstants.LOCATION_AREA_AFTER)) {
            return locationAreaAfter;
        } else if (tagName.equals(LoaderConstants.LOCATION_AREA_BEFORE)) {
            return locationAreaBefore;
        }
        return null;
    }

    public String getProbeId() {
        return probeId;
    }

    public void setProbeId(String probeId) {
        this.probeId = probeId;
    }

    

    public void setItsiAttReq(Calendar itstiAttReq) {
        this.itsiAttReq = itstiAttReq;
    }

    public Calendar getItsiAccept() {
        return itsiAccept;
    }

    public void setItsiAccept(Calendar itsiAccept) {
        this.itsiAccept = itsiAccept;
    }

    public Long getLocationAreaBefore() {
        return locationAreaBefore;
    }

    public void setLocationAreaBefore(Long locationAreaBefore) {
        this.locationAreaBefore = locationAreaBefore;
    }

    public Long getLocationAreaAfter() {
        return locationAreaAfter;
    }

    public void setLocationAreaAfter(Long locationAreaAfter) {
        this.locationAreaAfter = locationAreaAfter;
    }

    /**
     * convert date to xml format string
     * 
     * @param calendar
     * @return
     */
    public String getTimeiInXMLformat(Calendar calendar) {
        try {
            Date calendarDate = calendar.getTime();
            String calendarString = AbstractTOCTTC.dateFormatWithTimeZone.format(calendarDate);
            int i = calendarString.lastIndexOf('+');
            StringBuilder time = new StringBuilder(calendarString.substring(0, i += 3)).append(":").append(
                    calendarString.substring(i, calendarString.length()));
            // long time2 = SDF.parse(time.toString()).getTime();
            return time.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * convert date from xml format to long
     * 
     * @param stringData
     * @return
     */
    private Long getTime(String stringData) {
        try {
            if (stringData == null) {
                return null;
            }
            int i = stringData.lastIndexOf(':');
            StringBuilder time = new StringBuilder(stringData.substring(0, i)).append(stringData.substring(i + 1,
                    stringData.length()));
            long time2;

            time2 = dateFormatWithTimeZone.parse(time.toString()).getTime();
            return time2;
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }

    }

    /**
     * @return Returns the inconclusive.
     */
    public InconclusiveElement getInconclusive() {
        return inconclusive;
    }

    /**
     * @param inconclusive The inconclusive to set.
     */
    public void setInconclusive(InconclusiveElement inconclusive) {
        this.inconclusive = inconclusive;
    }

    /**
     * @return Returns the itsiAttReq.
     */
    public Calendar getItsiAttReq() {
        return itsiAttReq;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((inconclusive == null) ? 0 : inconclusive.hashCode());
        result = prime * result + ((itsiAccept == null) ? 0 : itsiAccept.hashCode());
        result = prime * result + ((itsiAttReq == null) ? 0 : itsiAttReq.hashCode());
        result = prime * result + ((locationAreaAfter == null) ? 0 : locationAreaAfter.hashCode());
        result = prime * result + ((locationAreaBefore == null) ? 0 : locationAreaBefore.hashCode());
        result = prime * result + ((probeId == null) ? 0 : probeId.hashCode());
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
        if (!(obj instanceof ItsiAttach)) {
            return false;
        }
        ItsiAttach other = (ItsiAttach)obj;
        if (inconclusive == null) {
            if (other.inconclusive != null) {
                return false;
            }
        } else if (!inconclusive.equals(other.inconclusive)) {
            return false;
        }
        if (itsiAccept == null) {
            if (other.itsiAccept != null) {
                return false;
            }
        } else if (!itsiAccept.equals(other.itsiAccept)) {
            return false;
        }
        if (itsiAttReq == null) {
            if (other.itsiAttReq != null) {
                return false;
            }
        } else if (!itsiAttReq.equals(other.itsiAttReq)) {
            return false;
        }
        if (locationAreaAfter == null) {
            if (other.locationAreaAfter != null) {
                return false;
            }
        } else if (!locationAreaAfter.equals(other.locationAreaAfter)) {
            return false;
        }
        if (locationAreaBefore == null) {
            if (other.locationAreaBefore != null) {
                return false;
            }
        } else if (!locationAreaBefore.equals(other.locationAreaBefore)) {
            return false;
        }
        if (probeId == null) {
            if (other.probeId != null) {
                return false;
            }
        } else if (!probeId.equals(other.probeId)) {
            return false;
        }
        return true;
    }

}
