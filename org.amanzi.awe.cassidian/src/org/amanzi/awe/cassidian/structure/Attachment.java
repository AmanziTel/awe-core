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
 * describe attachment tag
 * </p>
 * 
 * @author Kondratenko_V
 * @since 1.0.0
 */
public class Attachment implements IXmlTag {

    private Integer groupType;
    private Long gssi;

    @Override
    public String getType() {
        return ChildTypes.ATTACHMENT.getId();
    }

    @Override
    public void setValueByTagType(String tagName, Object value) {
        if (LoaderConstants.GROUP_TYPE.equals(tagName)) {
            groupType = Integer.parseInt(value.toString());
        } else if (LoaderConstants.GSSI.equals(tagName)) {
            gssi = Long.parseLong(value.toString());
        }
    }

    @Override
    public Object getValueByTagType(String tagName) {
        if (LoaderConstants.GROUP_TYPE.equals(tagName)) {
            return groupType;
        } else if (LoaderConstants.GSSI.equals(tagName)) {
            return gssi;
        }
        return null;
    }

    public Integer getGroupType() {
        return groupType;
    }

    public void setGroupType(Integer groupType) {
        this.groupType = groupType;
    }

    public Long getGssi() {
        return gssi;
    }

    public void setGssi(Long gssi) {
        this.gssi = gssi;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((groupType == null) ? 0 : groupType.hashCode());
        result = prime * result + ((gssi == null) ? 0 : gssi.hashCode());
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
        if (!(obj instanceof Attachment)) {
            return false;
        }
        Attachment other = (Attachment)obj;
        if (groupType == null) {
            if (other.groupType != null) {
                return false;
            }
        } else if (!groupType.equals(other.groupType)) {
            return false;
        }
        if (gssi == null) {
            if (other.gssi != null) {
                return false;
            }
        } else if (!gssi.equals(other.gssi)) {
            return false;
        }
        return true;
    }

}
