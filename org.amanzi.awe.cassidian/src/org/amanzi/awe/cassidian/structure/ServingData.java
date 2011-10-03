package org.amanzi.awe.cassidian.structure;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

import org.amanzi.awe.cassidian.constants.*;

/**
 * <p>
 * Describe servingData tag
 * </p>
 * 
 * @author Kondratenko_V
 * @since 1.0.0
 */
public class ServingData implements IXmlTag {

    private String probeId;
    private Calendar deliveryTime;
    private Integer rssi;
    private Integer locationArea;
    private Double frequency;
    private Integer cl;
    private Calendar calendar;

    @Override
    public String getType() {
        return ChildTypes.SERVING_DATA.getId();
    }

    @Override
    public void setValueByTagType(String tagName, Object value) {
        if (tagName.equals(LoaderConstants.PROBE_ID)) {
            probeId = value.toString();
        } else if (tagName.equals(LoaderConstants.DELIVERY_TIME)) {
            calendar= Calendar.getInstance();
            calendar.setTimeInMillis(getTime(value.toString()));
            setDeliveryTime(calendar);
        } else if (tagName.equals(LoaderConstants.LOCATION_AREA)) {
            locationArea = Integer.parseInt(value.toString());
        } else if (tagName.equals(LoaderConstants.FREQUENCY)) {
            frequency = Double.parseDouble(value.toString());
        } else if (tagName.equals(LoaderConstants.RSSI)) {
            rssi = Integer.parseInt(value.toString());
        } else if (tagName.equals(LoaderConstants.CL)) {
            cl = Integer.parseInt(value.toString());
        }
    }

    @Override
    public Object getValueByTagType(String tagName) {
        if (tagName.equals(LoaderConstants.PROBE_ID)) {
            return probeId;
        } else if (tagName.equals(LoaderConstants.DELIVERY_TIME)) {
            return deliveryTime;
        } else if (tagName.equals(LoaderConstants.LOCATION_AREA)) {
            return locationArea;
        } else if (tagName.equals(LoaderConstants.FREQUENCY)) {
            return frequency;
        } else if (tagName.equals(LoaderConstants.RSSI)) {
            return rssi;
        } else if (tagName.equals(LoaderConstants.CL)) {
            return cl;
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

    public Calendar getDeliveryTime() {
        return deliveryTime;
    }

    public void setDeliveryTime(Calendar deliveryTime) {
        this.deliveryTime = deliveryTime;
    }

    public Integer getRssi() {
        return rssi;
    }

    public void setRssi(Integer rssi) {
        this.rssi = rssi;
    }

    public Integer getLocationArea() {
        return locationArea;
    }

    public void setLocationArea(Integer locationArea) {
        this.locationArea = locationArea;
    }

    public Double getFrequency() {
        return frequency;
    }

    public void setFrequency(Double frequency) {
        this.frequency = frequency;
    }

    public Integer getCl() {
        return cl;
    }

    public void setCl(Integer cl) {
        this.cl = cl;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((calendar == null) ? 0 : calendar.hashCode());
        result = prime * result + ((cl == null) ? 0 : cl.hashCode());
        result = prime * result + ((deliveryTime == null) ? 0 : deliveryTime.hashCode());
        result = prime * result + ((frequency == null) ? 0 : frequency.hashCode());
        result = prime * result + ((locationArea == null) ? 0 : locationArea.hashCode());
        result = prime * result + ((probeId == null) ? 0 : probeId.hashCode());
        result = prime * result + ((rssi == null) ? 0 : rssi.hashCode());
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
        if (!(obj instanceof ServingData)) {
            return false;
        }
        ServingData other = (ServingData)obj;
       
        if (cl == null) {
            if (other.cl != null) {
                return false;
            }
        } else if (!cl.equals(other.cl)) {
            return false;
        }
        if (deliveryTime == null) {
            if (other.deliveryTime != null) {
                return false;
            }
        } else if (!deliveryTime.equals(other.deliveryTime)) {
            return false;
        }
        if (frequency == null) {
            if (other.frequency != null) {
                return false;
            }
        } else if (!frequency.equals(other.frequency)) {
            return false;
        }
        if (locationArea == null) {
            if (other.locationArea != null) {
                return false;
            }
        } else if (!locationArea.equals(other.locationArea)) {
            return false;
        }
        if (probeId == null) {
            if (other.probeId != null) {
                return false;
            }
        } else if (!probeId.equals(other.probeId)) {
            return false;
        }
        if (rssi == null) {
            if (other.rssi != null) {
                return false;
            }
        } else if (!rssi.equals(other.rssi)) {
            return false;
        }
        return true;
    }

}
