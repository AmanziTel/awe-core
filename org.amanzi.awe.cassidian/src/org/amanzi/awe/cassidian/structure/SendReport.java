package org.amanzi.awe.cassidian.structure;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

import org.amanzi.awe.cassidian.constants.ChildTypes;
import org.amanzi.awe.cassidian.constants.LoaderConstants;

public class SendReport implements IXmlTag {
    private Calendar calendar;
    private Calendar reportTime;
    private Integer status;

    @Override
    public String getType() {
        return ChildTypes.SEND_REPORT.getId();
    }

    @Override
    public void setValueByTagType(String tagName, Object value) {
        if (LoaderConstants.REPORT_TIME.equals(tagName)) {
            calendar= Calendar.getInstance();
            calendar.setTimeInMillis(getTime(value.toString()));
            setReportTime(calendar);
        } else if (LoaderConstants.STATUS.equals(tagName)) {
            status = Integer.parseInt(value.toString());
        }
    }

    @Override
    public Object getValueByTagType(String tagName) {
        if (LoaderConstants.REPORT_TIME.equals(tagName)) {
            return getReportTime();
        } else if (LoaderConstants.STATUS.equals(tagName)) {
            return status;
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

    public Calendar getReportTime() {
        return reportTime;
    }

    public void setReportTime(Calendar reportTime) {
        this.reportTime = reportTime;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((calendar == null) ? 0 : calendar.hashCode());
        result = prime * result + ((reportTime == null) ? 0 : reportTime.hashCode());
        result = prime * result + ((status == null) ? 0 : status.hashCode());
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
        if (!(obj instanceof SendReport)) {
            return false;
        }
        SendReport other = (SendReport)obj;
     
        if (reportTime == null) {
            if (other.reportTime != null) {
                return false;
            }
        } else if (!reportTime.equals(other.reportTime)) {
            return false;
        }
        if (status == null) {
            if (other.status != null) {
                return false;
            }
        } else if (!status.equals(other.status)) {
            return false;
        }
        return true;
    }

}
