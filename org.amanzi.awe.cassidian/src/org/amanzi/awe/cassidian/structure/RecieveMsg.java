package org.amanzi.awe.cassidian.structure;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

import org.amanzi.awe.cassidian.constants.ChildTypes;
import org.amanzi.awe.cassidian.constants.LoaderConstants;

public class RecieveMsg extends AbstactMsg {
    protected Long callingNumber;
    private Calendar receiveTime;


    @Override
    public String getType() {
        return ChildTypes.RECIEVE_MSG.getId();
    }

    /**
     * 
     */
    public RecieveMsg() {
        super();
    }

    @Override
    public void setValueByTagType(String tagName, Object value) {
        if (LoaderConstants.PROBE_ID.equals(tagName)) {
            probeId = value.toString();
        } else if (LoaderConstants.CALLING_NUMBER.equals(tagName)) {
            callingNumber = Long.parseLong(value.toString());
        } else if (LoaderConstants.MSG_TYPE.equals(tagName)) {
            msgType = Integer.parseInt(value.toString());
        } else if (LoaderConstants.DATA_LENGTH.equals(tagName)) {
            dataLength = Integer.parseInt(value.toString());
        } else if (LoaderConstants.DATA_TXT.equals(tagName)) {
            dataTxt = Integer.parseInt(value.toString());
        } else if (LoaderConstants.SEND_TIME.equals(tagName)) {
            calendar= Calendar.getInstance();
            calendar.setTimeInMillis(getTime(value.toString()));
            setSendTime(calendar);
        } else if (ChildTypes.INCONCLUSIVE.getId().equals(tagName)) {
            super.inconclusive = (InconclusiveElement)value;
        }
    }

    @Override
    public Object getValueByTagType(String tagName) {
        if (LoaderConstants.PROBE_ID.equals(tagName)) {
            return probeId;
        } else if (LoaderConstants.CALLING_NUMBER.equals(tagName)) {
            return callingNumber;
        } else if (LoaderConstants.MSG_TYPE.equals(tagName)) {
            return msgType;
        } else if (LoaderConstants.DATA_LENGTH.equals(tagName)) {
            return dataLength;
        } else if (LoaderConstants.DATA_TXT.equals(tagName)) {
            return dataTxt;
        } else if (LoaderConstants.SEND_TIME.equals(tagName)) {
            return getSendTime();
        } else if (ChildTypes.INCONCLUSIVE.getId().equals(tagName)) {
            return getInconclusive();
        }
        return null;
    }

    public String getProbeId() {
        return probeId;
    }

    public void setProbeId(String probeId) {
        this.probeId = probeId;
    }

    public Long getCallingNumber() {
        return callingNumber;
    }

    public void setCallingNumber(Long calledNumber) {
        this.callingNumber = calledNumber;
    }

    public Integer getMsgType() {
        return msgType;
    }

    public void setMsgType(Integer msgType) {
        this.msgType = msgType;
    }

    public Integer getDataLength() {
        return dataLength;
    }

    public void setDataLength(Integer dataLength) {
        this.dataLength = dataLength;
    }

    public Integer getDataTxt() {
        return dataTxt;
    }

    public void setDataTxt(Integer dataTxt) {
        this.dataTxt = dataTxt;
    }

    public Calendar getSendTime() {
        return receiveTime;
    }

    public void setSendTime(Calendar sendTime) {
        this.receiveTime = sendTime;
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
        int result = super.hashCode();
        result = prime * result + ((callingNumber == null) ? 0 : callingNumber.hashCode());
        result = prime * result + ((receiveTime == null) ? 0 : receiveTime.hashCode());
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
        if (!(obj instanceof RecieveMsg)) {
            return false;
        }
        RecieveMsg other = (RecieveMsg)obj;
        if (callingNumber == null) {
            if (other.callingNumber != null) {
                return false;
            }
        } else if (!callingNumber.equals(other.callingNumber)) {
            return false;
        }
        if (receiveTime == null) {
            if (other.receiveTime != null) {
                return false;
            }
        } else if (!receiveTime.equals(other.receiveTime)) {
            return false;
        }
        return true;
    }

    /**
     * @return Returns the receiveTime.
     */
    public Calendar getReceiveTime() {
        return receiveTime;
    }

    /**
     * @param receiveTime The receiveTime to set.
     */
    public void setReceiveTime(Calendar receiveTime) {
        this.receiveTime = receiveTime;
    }

}
