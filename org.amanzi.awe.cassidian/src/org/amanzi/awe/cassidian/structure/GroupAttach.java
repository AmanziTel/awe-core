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

import org.amanzi.awe.cassidian.constants.ChildTypes;
import org.amanzi.awe.cassidian.constants.LoaderConstants;

/**
 * TODO Purpose of
 * <p>
 * describe groupAttach tag
 * </p>
 * 
 * @author Kondratenko_V
 * @since 1.0.0
 */
public class GroupAttach implements IXmlTag {

    private String probeId;
    private Calendar groupAttachTime;
    private List<Attachment> attachment = new LinkedList<Attachment>();
    private Calendar calendar;

    @Override
    public String getType() {
        return ChildTypes.GROUP_ATTACH.getId();
    }

    @Override
    public void setValueByTagType(String tagName, Object value) {
        if (tagName.equals(LoaderConstants.PROBE_ID)) {
            probeId = value.toString();
        } else if (tagName.equals(LoaderConstants.GROUP_ATTACH_TIME)) {
            calendar = Calendar.getInstance();
            calendar.setTimeInMillis(getTime(value.toString()));
            setGroupAttachTime(calendar);
        } else if (tagName.equals(ChildTypes.ATTACHMENT.getId())) {
            addMemberToAttachment((Attachment)value);
        }
    }

    @Override
    public Object getValueByTagType(String tagName) {
        if (tagName.equals(LoaderConstants.PROBE_ID)) {
            return probeId;
        } else if (tagName.equals(LoaderConstants.GROUP_ATTACH_TIME)) {
            return groupAttachTime;
        } else if (tagName.equals(ChildTypes.ATTACHMENT.getId())) {
            return attachment;
        }
        return null;
    }

    public void addMemberToAttachment(Attachment data) {
        attachment.add(data);
    }

    /**
     * parse date to xml format string
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

    public Calendar getGroupAttachTime() {
        return groupAttachTime;
    }

    public void setGroupAttachTime(Calendar groupAttachTime) {
        this.groupAttachTime = groupAttachTime;
    }

    public List<Attachment> getAttachment() {
        return attachment;
    }

    public void setAttachment(List<Attachment> attachment) {
        this.attachment = attachment;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((attachment == null) ? 0 : attachment.hashCode());
        result = prime * result + ((calendar == null) ? 0 : calendar.hashCode());
        result = prime * result + ((groupAttachTime == null) ? 0 : groupAttachTime.hashCode());
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
        if (!(obj instanceof GroupAttach)) {
            return false;
        }
        GroupAttach other = (GroupAttach)obj;
        if (attachment == null) {
            if (other.attachment != null) {
                return false;
            }
        } else if (!attachment.equals(other.attachment)) {
            return false;
        }
        if (calendar == null) {
            if (other.calendar != null) {
                return false;
            }
        } else if (!calendar.equals(other.calendar)) {
            return false;
        }
        if (groupAttachTime == null) {
            if (other.groupAttachTime != null) {
                return false;
            }
        } else if (!groupAttachTime.equals(other.groupAttachTime)) {
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
