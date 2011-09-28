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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.amanzi.awe.cassidian.constants.ChildTypes;
import org.amanzi.awe.cassidian.constants.LoaderConstants;

/**
 * <p>
 * describe mptSync tag
 * </p>
 * 
 * @author Kondratenko_V
 * @since 1.0.0
 */
public class MPTSync implements IXmlTag {
    private Calendar calendar = Calendar.getInstance();
    private String probeId;
    private Calendar mptSyncTime;
    private List<Object> probeList = new ArrayList<Object>();
    private Long syncId;
    private Long timeOut;

    /**
     * add date or calendar value to probelist
     */
    public void addMemberToProbeList(Object value) {
        if (getTimeWithoutZone(value.toString()) == null) {
            String[] valueStrings = value.toString().split(",");
            probeList.addAll(Arrays.asList(valueStrings));
        } else {
            calendar = Calendar.getInstance();
            calendar.setTimeInMillis(getTimeWithoutZone(value.toString()));
            probeList.add(calendar);
        }

    }

    @Override
    public String getType() {
        return ChildTypes.MPT_SYNC.getId();
    }

    @Override
    public Object getValueByTagType(String tagName) {
        return null;
    }

    @Override
    public void setValueByTagType(String tagName, Object value) {
        if (LoaderConstants.PROBE_ID.equals(tagName)) {
            probeId = value.toString();
        } else if (LoaderConstants.SYNC_ID.equals(tagName)) {
            syncId = Long.parseLong(value.toString());
        } else if (LoaderConstants.TIMEOUT.equals(tagName)) {
            timeOut = Long.parseLong(value.toString());
        } else if (LoaderConstants.MPT_SYNC_TIME.equals(tagName)) {
            calendar = Calendar.getInstance();
            calendar.setTimeInMillis(getTime(value.toString()));
            setMptSyncTime(calendar);
        } else if (LoaderConstants.PROBE_LIST.equals(tagName)) {
            addMemberToProbeList(value);
        }
    }

    public String getProbeId() {
        return probeId;
    }

    public void setProbeId(String probeId) {
        this.probeId = probeId;
    }

    public Calendar getMptSyncTime() {
        return mptSyncTime;
    }

    public void setMptSyncTime(Calendar mptSyncTime) {
        this.mptSyncTime = mptSyncTime;
    }

    public List<Object> getProbeList() {
        return probeList;
    }

    public void setProbeList(List<Object> probeList) {
        this.probeList = probeList;
    }

    public Long getSyncId() {
        return syncId;
    }

    public void setSyncId(Long syncId) {
        this.syncId = syncId;
    }

    public Long getTimeOut() {
        return timeOut;
    }

    public void setTimeOut(Long timeOut) {
        this.timeOut = timeOut;
    }

    /**
     * convert date to xml format string without time zone
     * 
     * @param calendar
     * @return
     */
    public String getTimeInXmlFormatWithioutZone(Calendar calendar) {
        Date calendarDate = calendar.getTime();
        String calendarString = dateFormatWithoutTimeZone.format(calendarDate);
        return calendarString;
    }

    /**
     * convert date to xml format string with timezone
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

            return time.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * convert date from xml format(without time zone) to long
     * 
     * @param stringData
     * @return
     */
    private Long getTimeWithoutZone(String stringData) {
        try {
            if (stringData == null) {
                return null;
            }

            long time2;

            time2 = dateFormatWithoutTimeZone.parse(stringData).getTime();
            return time2;
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            return null;
        }

    }

    /**
     * convert date from xml format(with timezone) to long
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
        result = prime * result + ((mptSyncTime == null) ? 0 : mptSyncTime.hashCode());
        result = prime * result + ((probeId == null) ? 0 : probeId.hashCode());
        result = prime * result + ((probeList == null) ? 0 : probeList.hashCode());
        result = prime * result + ((syncId == null) ? 0 : syncId.hashCode());
        result = prime * result + ((timeOut == null) ? 0 : timeOut.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        MPTSync other = (MPTSync)obj;
        if (mptSyncTime == null) {
            if (other.mptSyncTime != null)
                return false;
        } else if (!mptSyncTime.equals(other.mptSyncTime))
            return false;
        if (probeId == null) {
            if (other.probeId != null)
                return false;
        } else if (!probeId.equals(other.probeId))
            return false;
        if (probeList == null) {
            if (other.probeList != null)
                return false;
        } else if (!probeList.equals(other.probeList))
            return false;
        if (syncId == null) {
            if (other.syncId != null)
                return false;
        } else if (!syncId.equals(other.syncId))
            return false;
        if (timeOut == null) {
            if (other.timeOut != null)
                return false;
        } else if (!timeOut.equals(other.timeOut))
            return false;
        return true;
    }
}
