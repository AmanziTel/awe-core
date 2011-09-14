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

import org.amanzi.awe.cassidian.constants.ChildTypes;
import org.amanzi.awe.cassidian.constants.LoaderConstants;

/**
 * <p>
 * Describe probeIdNumberMap tag
 * </p>
 * 
 * @author Kondratenko_V
 * @since 1.0.0
 */
public class ProbeIDNumberMap implements IXmlTag {
    private String probeId;
    private Integer phoneNumber;
    private Integer locationArea;
    private Double frequency;
    

    /**
     * @return Returns the probeId.
     */
    public String getProbeId() {
        return probeId;
    }

    /**
     * @param probeId The probeId to set.
     */
    public void setProbeId(String probeId) {
        this.probeId = probeId;
    }

    /**
     * @return Returns the phoneNumber.
     */
    public Integer getPhoneNumber() {
        return phoneNumber;
    }

    /**
     * @param phoneNumber The phoneNumber to set.
     */
    public void setPhoneNumber(Integer phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    /**
     * @return Returns the locationArea.
     */
    public Integer getLocationArea() {
        return locationArea;
    }

    /**
     * @param locationArea The locationArea to set.
     */
    public void setLocationArea(Integer locationArea) {
        this.locationArea = locationArea;
    }

    /**
     * @return Returns the frequency.
     */
    public Double getFrequency() {
        return frequency;
    }

    /**
     * @param frequency The frequency to set.
     */
    public void setFrequency(Double frequency) {
        this.frequency = frequency;
    }

    @Override
    public String getType() {
        return ChildTypes.PROBE_ID_NUMBER_MAP.getId();
    }

    @Override
    public void setValueByTagType(String tagName, Object value) {
        if (tagName.equals(LoaderConstants.PROBE_ID)) {
            probeId = value.toString();
        } else if (tagName.equals(LoaderConstants.PHONE_NUMBER)) {
            phoneNumber = Integer.parseInt(value.toString());
        } else if (tagName.equals(LoaderConstants.LOCATION_AREA)) {
            locationArea = Integer.parseInt(value.toString());
        } else if (tagName.equals(LoaderConstants.FREQUENCY)) {
            frequency = Double.parseDouble(value.toString());
        }
    }

    @Override
    public Object getValueByTagType(String tagName) {
        if (tagName.equals(LoaderConstants.PROBE_ID)) {
            return probeId;
        } else if (tagName.equals(LoaderConstants.PHONE_NUMBER)) {
            return phoneNumber;
        } else if (tagName.equals(LoaderConstants.LOCATION_AREA)) {
            return locationArea;
        } else if (tagName.equals(LoaderConstants.FREQUENCY)) {
            return frequency;
        }
        return null;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((frequency == null) ? 0 : frequency.hashCode());
        result = prime * result + ((locationArea == null) ? 0 : locationArea.hashCode());
        result = prime * result + ((phoneNumber == null) ? 0 : phoneNumber.hashCode());
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
        if (!(obj instanceof ProbeIDNumberMap)) {
            return false;
        }
        ProbeIDNumberMap other = (ProbeIDNumberMap)obj;
        
        if (locationArea == null) {
            if (other.locationArea != null) {
                return false;
            }
        } else if (!locationArea.equals(other.locationArea)) {
            return false;
        }
        if (phoneNumber == null) {
            if (other.phoneNumber != null) {
                return false;
            }
        } else if (!phoneNumber.equals(other.phoneNumber)) {
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
