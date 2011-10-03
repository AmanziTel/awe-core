package org.amanzi.awe.cassidian.structure;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.amanzi.awe.cassidian.constants.ChildTypes;
import org.amanzi.awe.cassidian.constants.LoaderConstants;

public class SendMsg extends AbstactMsg {
    /**
     * 
     */
    public SendMsg() {
        super();
    }

    private Calendar sendTime;
    private Long calledNumber;
    private List<SendReport> sendReport = new LinkedList<SendReport>();


    @Override
    public String getType() {
        return ChildTypes.SEND_MSG.getId();
    }

    public void addMemberToSendReport(SendReport data) {
        sendReport.add(data);
    }

    @Override
    public void setValueByTagType(String tagName, Object value) {
        if (LoaderConstants.PROBE_ID.equals(tagName)) {
            probeId = value.toString();
        } else if (LoaderConstants.CALLED_NUMBER.equals(tagName)) {
            calledNumber = Long.parseLong(value.toString());
        } else if (LoaderConstants.MSG_TYPE.equals(tagName)) {
            msgType = Integer.parseInt(value.toString());
        } else if (LoaderConstants.DATA_LENGTH.equals(tagName)) {
            dataLength = Integer.parseInt(value.toString());
        } else if (LoaderConstants.DATA_TXT.equals(tagName)) {
            dataTxt = Integer.parseInt(value.toString());
        } else if (LoaderConstants.SEND_TIME.equals(tagName)) {
            calendar = Calendar.getInstance();
            calendar.setTimeInMillis(getTime(value.toString()));
            setSendTime(calendar);
        } else if (ChildTypes.SEND_REPORT.getId().equals(tagName)) {
            addMemberToSendReport((SendReport)value);
        } else if (ChildTypes.INCONCLUSIVE.getId().equals(tagName)) {
            super.inconclusive = (InconclusiveElement)value;
        }
    }

    @Override
    public Object getValueByTagType(String tagName) {
        if (LoaderConstants.PROBE_ID.equals(tagName)) {
            return probeId;
        } else if (LoaderConstants.CALLED_NUMBER.equals(tagName)) {
            return calledNumber;
        } else if (LoaderConstants.MSG_TYPE.equals(tagName)) {
            return msgType;
        } else if (LoaderConstants.DATA_LENGTH.equals(tagName)) {
            return dataLength;
        } else if (LoaderConstants.DATA_TXT.equals(tagName)) {
            return dataTxt;
        } else if (LoaderConstants.SEND_TIME.equals(tagName)) {
            return getSendTime();
        } else if (ChildTypes.SEND_REPORT.getId().equals(tagName)) {
            return getSendReport();
        } else if (ChildTypes.INCONCLUSIVE.getId().equals(tagName)) {
            return getInconclusive();
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

    public Calendar getSendTime() {
        return sendTime;
    }

    public void setSendTime(Calendar sendTime) {
        this.sendTime = sendTime;
    }

    public Long getCalledNumber() {
        return calledNumber;
    }

    public void setCalledNumber(Long calledNumber) {
        this.calledNumber = calledNumber;
    }

    public List<SendReport> getSendReport() {
        return sendReport;
    }

    public void setSendReport(List<SendReport> sendReport) {
        this.sendReport = sendReport;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((calledNumber == null) ? 0 : calledNumber.hashCode());
        result = prime * result + ((sendReport == null) ? 0 : sendReport.hashCode());
        result = prime * result + ((sendTime == null) ? 0 : sendTime.hashCode());
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
        if (!(obj instanceof SendMsg)) {
            return false;
        }
        SendMsg other = (SendMsg)obj;
        if (calledNumber == null) {
            if (other.calledNumber != null) {
                return false;
            }
        } else if (!calledNumber.equals(other.calledNumber)) {
            return false;
        }
        if (sendReport == null) {
            if (other.sendReport != null) {
                return false;
            }
        } else if (!sendReport.equals(other.sendReport)) {
            return false;
        }
        if (sendTime == null) {
            if (other.sendTime != null) {
                return false;
            }
        } else if (!sendTime.equals(other.sendTime)) {
            return false;
        }
        return true;
    }

}
