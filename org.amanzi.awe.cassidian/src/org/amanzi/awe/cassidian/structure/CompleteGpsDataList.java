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
 * describe compleateGpsDataList tag
 * </p>
 * 
 * @author Kondratenko_V
 * @since 1.0.0
 */
public class CompleteGpsDataList implements IXmlTag {
    List<CompleteGpsData> completeGpsData;

    public CompleteGpsDataList() {
        completeGpsData = new LinkedList<CompleteGpsData>();
    }

    public void addMemberToCompleateGpsData(CompleteGpsData data) {
        completeGpsData.add(data);
    }

    /**
     * @return Returns the completeGpsData.
     */
    public List<CompleteGpsData> getCompleteGpsData() {
        return completeGpsData;
    }

    public void completeGpsData(CompleteGpsData member) {
        completeGpsData.add(member);
    }

    /**
     * @param completeGpsData The completeGpsData to set.
     */
    public void setCompleteGpsData(List<CompleteGpsData> completeGpsData) {
        this.completeGpsData = completeGpsData;
    }

    @Override
    public String getType() {
        return ChildTypes.COMPLEATE_GPS_DATA_LIST.getId();
    }

    @Override
    public void setValueByTagType(String tagName, Object value) {
        if (tagName.equals(ChildTypes.COMPLEATE_GPS_DATA.getId())) {
            completeGpsData.add((CompleteGpsData)value);
        }
    }

    @Override
    public Object getValueByTagType(String tagName) {
        return null;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((completeGpsData == null) ? 0 : completeGpsData.hashCode());
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
        if (!(obj instanceof CompleteGpsDataList)) {
            return false;
        }
        CompleteGpsDataList other = (CompleteGpsDataList)obj;
        if (completeGpsData == null) {
            if (other.completeGpsData != null) {
                return false;
            }
        } else if (!completeGpsData.equals(other.completeGpsData)) {
            return false;
        }
        return true;
    }

}
