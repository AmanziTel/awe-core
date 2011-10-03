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

import java.util.LinkedList;
import java.util.List;

import org.amanzi.awe.cassidian.constants.ChildTypes;

/**
 * <p>
 * describe gpsData tag
 * </p>
 * 
 * @author Kondratenko_V
 * @since 1.0.0
 */
public class GPSData implements IXmlTag {
    List<CompleteGpsDataList> completeGpsDataList = null;

    /**
     * @return Returns the completeGpsDataList.
     */
    public List<CompleteGpsDataList> getCompleteGpsDataList() {
        return completeGpsDataList;
    }

    public void addMember(CompleteGpsDataList member) {
        completeGpsDataList.add(member);
    }

    /**
     * @param completeGpsDataList The completeGpsDataList to set.
     */
    public void setCompleteGpsDataList(List<CompleteGpsDataList> completeGpsDataList) {
        this.completeGpsDataList = completeGpsDataList;
    }

    public GPSData() {
        completeGpsDataList = new LinkedList<CompleteGpsDataList>();
    }

    @Override
    public String getType() {
        return ChildTypes.GPSDATA.getId();
    }

    @Override
    public void setValueByTagType(String tagName, Object value) {
        if (tagName.equals(ChildTypes.COMPLEATE_GPS_DATA_LIST.getId())) {
            completeGpsDataList.add((CompleteGpsDataList)value);
        }
    }

    @Override
    public Object getValueByTagType(String tagName) {
        if (tagName.equals(ChildTypes.COMPLEATE_GPS_DATA_LIST.getId())) {
            return completeGpsDataList;
        }
        return null;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((completeGpsDataList == null) ? 0 : completeGpsDataList.hashCode());
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
        if (!(obj instanceof GPSData)) {
            return false;
        }
        GPSData other = (GPSData)obj;
        if (completeGpsDataList == null) {
            if (other.completeGpsDataList != null) {
                return false;
            }
        } else if (!completeGpsDataList.equals(other.completeGpsDataList)) {
            return false;
        }
        return true;
    }
}
