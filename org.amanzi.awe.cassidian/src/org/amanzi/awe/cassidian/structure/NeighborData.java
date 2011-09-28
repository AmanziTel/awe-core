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
 * <p>
 * describe neighborData tag
 * </p>
 * 
 * @author Kondratenko_V
 * @since 1.0.0
 */
public class NeighborData implements IXmlTag {
    // <neighborData>
    // <probeID>PROBE006</probeID>
    // <deliveryTime>2010-05-16T02:40:26,723+00:00</deliveryTime>
    private Calendar calendar;
    private String probeId;
    private Calendar deliveryTime;
    private List<NeighborDetails> neighborDetails = new LinkedList<NeighborDetails>();

    public void addMemberToNeighborDetails(NeighborDetails data) {
        neighborDetails.add(data);
    }

    @Override
    public String getType() {
        return ChildTypes.NEIGHBOR_DATA.getId();
    }

    @Override
    public Object getValueByTagType(String tagName) {
        if (LoaderConstants.PROBE_ID.equals(tagName)) {
            return getProbeId();
        } else if (LoaderConstants.DELIVERY_TIME.equals(tagName)) {
            return deliveryTime;
        } else if (ChildTypes.NEIGHBOR_DETAILS.getId().equals(tagName)) {
            return neighborDetails;
        }
        return null;
    }

    @Override
    public void setValueByTagType(String tagName, Object value) {
        if (LoaderConstants.PROBE_ID.equals(tagName)) {
            probeId = value.toString();
        } else if (LoaderConstants.DELIVERY_TIME.equals(tagName)) {
            calendar = Calendar.getInstance();
            calendar.setTimeInMillis(getTime(value.toString()));
            setDeliveryTime(calendar);
        } else if (ChildTypes.NEIGHBOR_DETAILS.getId().equals(tagName)) {
            addMemberToNeighborDetails((NeighborDetails)value);
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

    public String getProbeId() {
        return probeId;
    }

    public void setProbeId(String probeId) {
        this.probeId = probeId;
    }

    public Calendar getDeliveryTime() {
        return deliveryTime;
    }

    public void setDeliveryTime(Calendar deliveryTime) {
        this.deliveryTime = deliveryTime;
    }

    public List<NeighborDetails> getNeighborDetails() {
        return neighborDetails;
    }

    public void setNeighborDetails(List<NeighborDetails> neighborDetails) {
        this.neighborDetails = neighborDetails;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((calendar == null) ? 0 : calendar.hashCode());
        result = prime * result + ((deliveryTime == null) ? 0 : deliveryTime.hashCode());
        result = prime * result + ((neighborDetails == null) ? 0 : neighborDetails.hashCode());
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
        if (!(obj instanceof NeighborData)) {
            return false;
        }
        NeighborData other = (NeighborData)obj;
        
        if (deliveryTime == null) {
            if (other.deliveryTime != null) {
                return false;
            }
        } else if (!deliveryTime.equals(other.deliveryTime)) {
            return false;
        }
        if (neighborDetails == null) {
            if (other.neighborDetails != null) {
                return false;
            }
        } else if (!neighborDetails.equals(other.neighborDetails)) {
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
