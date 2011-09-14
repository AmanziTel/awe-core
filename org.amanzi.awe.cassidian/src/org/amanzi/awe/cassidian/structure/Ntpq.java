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
 * describe ntpq tag
 * </p>
 * 
 * @author Kondratenko_V
 * @since 1.0.0
 */
public class Ntpq implements IXmlTag {

    private String probeID;
    private Calendar ntpqTime;
    private double offset;
    private double jitter;
    private Calendar calendar;

    public String getProbeId() {
        return probeID;
    }

    public void setProbeId(String probeID) {
        this.probeID = probeID;
    }

    public Calendar getNtpqTime() {
        return ntpqTime;
    }

    public void setNtpqTime(Calendar ntpqTime) {
        this.ntpqTime = ntpqTime;
    }

    public double getOffset() {
        return offset;
    }

    public void setOffset(double offset) {
        this.offset = offset;
    }

    public double getJitter() {
        return jitter;
    }

    public void setJitter(double jitter) {
        this.jitter = jitter;
    }

    @Override
    public String getType() {
        return ChildTypes.NTPQ.getId();
    }

    @Override
    public Object getValueByTagType(String tagName) {
        if (LoaderConstants.PROBE_ID.equals(tagName)) {
            return probeID;
        } else if (LoaderConstants.NTPQ_TIME.equals(tagName)) {
            return ntpqTime;
        } else if (LoaderConstants.OFFSET.equals(tagName)) {
            return offset;
        } else if (LoaderConstants.JITTER.equals(tagName)) {
            return jitter;
        }
        return null;
    }

    @Override
    public void setValueByTagType(String tagName, Object value) {
        if (LoaderConstants.PROBE_ID.equals(tagName)) {
            probeID = value.toString();
        } else if (LoaderConstants.NTPQ_TIME.equals(tagName)) {
            calendar= Calendar.getInstance();
            calendar.setTimeInMillis(getTime(value.toString()));
            setNtpqTime(calendar);
        } else if (LoaderConstants.OFFSET.equals(tagName)) {
            offset = Double.parseDouble(value.toString());
        } else if (LoaderConstants.JITTER.equals(tagName)) {
            jitter = Double.parseDouble(value.toString());
        }
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

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((calendar == null) ? 0 : calendar.hashCode());
        long temp;
        temp = Double.doubleToLongBits(jitter);
        result = prime * result + (int)(temp ^ (temp >>> 32));
        result = prime * result + ((ntpqTime == null) ? 0 : ntpqTime.hashCode());
        temp = Double.doubleToLongBits(offset);
        result = prime * result + (int)(temp ^ (temp >>> 32));
        result = prime * result + ((probeID == null) ? 0 : probeID.hashCode());
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
        if (!(obj instanceof Ntpq)) {
            return false;
        }
        Ntpq other = (Ntpq)obj;
       
        if (Double.doubleToLongBits(jitter) != Double.doubleToLongBits(other.jitter)) {
            return false;
        }
        if (ntpqTime == null) {
            if (other.ntpqTime != null) {
                return false;
            }
        } else if (!ntpqTime.equals(other.ntpqTime)) {
            return false;
        }
        if (Double.doubleToLongBits(offset) != Double.doubleToLongBits(other.offset)) {
            return false;
        }
        if (probeID == null) {
            if (other.probeID != null) {
                return false;
            }
        } else if (!probeID.equals(other.probeID)) {
            return false;
        }
        return true;
    }
}
