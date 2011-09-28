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
public class CellReselection implements IXmlTag {

    private String probeId;
    private Calendar cellReselReq;
    private Calendar cellReselAccept;
    private Long locationAreaBefore;
    private Long LocationAreaAfter;
    private InconclusiveElement inconclusive;
    private Calendar calendar;

    @Override
    public String getType() {
        return ChildTypes.CELL_RESEL.getId();
    }

    @Override
    public void setValueByTagType(String tagName, Object value) {
        if (tagName.equals(LoaderConstants.PROBE_ID)) {
            probeId = value.toString();
        } else if (tagName.equals(LoaderConstants.CELL_RESEL_ACCEPT)) {
            calendar= Calendar.getInstance();
            calendar.setTimeInMillis(getTime(value.toString()));
            setCellReselAccept(calendar);
        } else if (tagName.equals(LoaderConstants.CELL_RESEL_REQ)) {
            calendar= Calendar.getInstance();
            calendar.setTimeInMillis(getTime(value.toString()));
            setCellReselReq(calendar);
        } else if (tagName.equals(LoaderConstants.LOCATION_AREA_BEFORE)) {
            locationAreaBefore = Long.parseLong(value.toString());
        } else if (tagName.equals(LoaderConstants.LOCATION_AREA_AFTER)) {
            LocationAreaAfter = Long.parseLong(value.toString());
        } else if (tagName.equals(ChildTypes.INCONCLUSIVE.getId())) {
            inconclusive = (InconclusiveElement)value;
        }
    }

    @Override
    public Object getValueByTagType(String tagName) {
        if (tagName.equals(LoaderConstants.PROBE_ID)) {
            return probeId;
        } else if (tagName.equals(LoaderConstants.CELL_RESEL_ACCEPT)) {
            return cellReselAccept;
        } else if (tagName.equals(LoaderConstants.CELL_RESEL_REQ)) {
            return cellReselReq;
        } else if (tagName.equals(LoaderConstants.LOCATION_AREA_BEFORE)) {
            return locationAreaBefore;
        } else if (tagName.equals(LoaderConstants.LOCATION_AREA_AFTER)) {
            return LocationAreaAfter;
        } else if (tagName.equals(ChildTypes.INCONCLUSIVE.getId())) {
            return inconclusive;
        }
        return null;
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

    public String getProbeId() {
        return probeId;
    }

    public void setProbeId(String probeId) {
        this.probeId = probeId;
    }

    public Calendar getCellReselReq() {
        return cellReselReq;
    }

    public void setCellReselReq(Calendar cellReselReq) {
        this.cellReselReq = cellReselReq;
    }

    public Calendar getCellReselAccept() {
        return cellReselAccept;
    }

    public void setCellReselAccept(Calendar cellReselAccept) {
        this.cellReselAccept = cellReselAccept;
    }

    public Long getLocationAreaBefore() {
        return locationAreaBefore;
    }

    public void setLocationAreaBefore(Long locationAreaBefore) {
        this.locationAreaBefore = locationAreaBefore;
    }

    public Long getLocationAreaAfter() {
        return LocationAreaAfter;
    }

    public void setLocationAreaAfter(Long locationAreaAfter) {
        LocationAreaAfter = locationAreaAfter;
    }

    public InconclusiveElement getInconclusive() {
        return inconclusive;
    }

    public void setInconclusive(InconclusiveElement inconclusive) {
        this.inconclusive = inconclusive;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((LocationAreaAfter == null) ? 0 : LocationAreaAfter.hashCode());
        result = prime * result + ((calendar == null) ? 0 : calendar.hashCode());
        result = prime * result + ((cellReselAccept == null) ? 0 : cellReselAccept.hashCode());
        result = prime * result + ((cellReselReq == null) ? 0 : cellReselReq.hashCode());
        result = prime * result + ((inconclusive == null) ? 0 : inconclusive.hashCode());
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
        if (!(obj instanceof CellReselection)) {
            return false;
        }
        CellReselection other = (CellReselection)obj;
        if (LocationAreaAfter == null) {
            if (other.LocationAreaAfter != null) {
                return false;
            }
        } else if (!LocationAreaAfter.equals(other.LocationAreaAfter)) {
            return false;
        }
       
        if (cellReselAccept == null) {
            if (other.cellReselAccept != null) {
                return false;
            }
        } else if (!cellReselAccept.equals(other.cellReselAccept)) {
            return false;
        }
        if (cellReselReq == null) {
            if (other.cellReselReq != null) {
                return false;
            }
        } else if (!cellReselReq.equals(other.cellReselReq)) {
            return false;
        }
        if (inconclusive == null) {
            if (other.inconclusive != null) {
                return false;
            }
        } else if (!inconclusive.equals(other.inconclusive)) {
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
