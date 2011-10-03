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
 * <p>
 * describe pesqResult tag
 * </p>
 * 
 * @author Kondratenko_V
 * @since 1.0.0
 */
public class PESQResultElement implements IXmlTag {

    public PESQResultElement(Calendar sendSampleStart, Float pesq, Long delay) {
        super();
        this.sendSampleStart = sendSampleStart;
        this.pesq = pesq;
        this.delay = delay;
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

    public PESQResultElement() {
    }

    public String getType() {
        return ChildTypes.PESQ_RESULT.getId();
    }

    private Calendar sendSampleStart;
    private Float pesq;
    private Long delay;
    private Calendar calendar;

    /**
     * @return the sendSampleStart
     */
    public Calendar getSendSampleStart() {
        return sendSampleStart;
    }

    /**
     * @param sendSampleStart the sendSampleStart to set
     */
    public void setSendSampleStart(Calendar sendSampleStart) {
        this.sendSampleStart = sendSampleStart;
    }

    /**
     * @return the pesq
     */
    public Float getPesq() {
        return pesq;
    }

    /**
     * @param pesq the pesq to set
     */
    public void setPesq(Float pesq) {
        this.pesq = pesq;
    }

    /**
     * @return the delay
     */
    public Long getDelay() {
        return delay;
    }

    /**
     * @param delay the delay to set
     */
    public void setDelay(Long delay) {
        this.delay = delay;
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
    public void setValueByTagType(String tagName, Object value) {
        if (tagName.equals(LoaderConstants.SEND_SAMPLE_START)) {
            calendar= Calendar.getInstance();
            calendar.setTimeInMillis(getTime(value.toString()));
            setSendSampleStart(calendar);
            // TODO Auto-generated method stub
        } else if (tagName.equals(LoaderConstants.DELAY)) {
            setDelay(Long.parseLong(value.toString()));
        } else if (tagName.equals(LoaderConstants.PESQ)) {
            setPesq(Float.parseFloat(value.toString()));
        }
    }

    @Override
    public Object getValueByTagType(String tagName) {
        if (tagName.equals(LoaderConstants.SEND_SAMPLE_START)) {
            return getSendSampleStart();
        } else if (tagName.equals(LoaderConstants.DELAY)) {
            return getDelay();
        } else if (tagName.equals(LoaderConstants.PESQ)) {
            return getPesq();
        } else {
            return null;
        }
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((calendar == null) ? 0 : calendar.hashCode());
        result = prime * result + ((delay == null) ? 0 : delay.hashCode());
        result = prime * result + ((pesq == null) ? 0 : pesq.hashCode());
        result = prime * result + ((sendSampleStart == null) ? 0 : sendSampleStart.hashCode());
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
        if (!(obj instanceof PESQResultElement)) {
            return false;
        }
        PESQResultElement other = (PESQResultElement)obj;
        
        if (delay == null) {
            if (other.delay != null) {
                return false;
            }
        } else if (!delay.equals(other.delay)) {
            return false;
        }
        if (pesq == null) {
            if (other.pesq != null) {
                return false;
            }
        } else if (!pesq.equals(other.pesq)) {
            return false;
        }
        if (sendSampleStart == null) {
            if (other.sendSampleStart != null) {
                return false;
            }
        } else if (!sendSampleStart.equals(other.sendSampleStart)) {
            return false;
        }
        return true;
    }
}
