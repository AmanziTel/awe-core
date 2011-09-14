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
 * describe isInconclusive tag
 * </p>
 * 
 * @author Kondratenko_V
 * @since 1.0.0
 */
public class InconclusiveElement implements IXmlTag {

    private Integer errCode;
    private String reason;

    @Override
    public String getType() {
        return ChildTypes.INCONCLUSIVE.getId();
    }

    @Override
    public void setValueByTagType(String tagName, Object value) {
        if (LoaderConstants.REASON.equals(tagName)) {
            reason = value.toString();
        } else if (LoaderConstants.ERR_CODE.equals(tagName)) {
            errCode = Integer.parseInt(value.toString());
        }
    }

    @Override
    public Object getValueByTagType(String tagName) {
        if (LoaderConstants.REASON.equals(tagName)) {
            return reason;
        } else if (LoaderConstants.ERR_CODE.equals(tagName)) {
            return errCode;
        }
        return null;
    }

    public Integer getErrCode() {
        return errCode;
    }

    public void setErrCode(Integer errCode) {
        this.errCode = errCode;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((errCode == null) ? 0 : errCode.hashCode());
        result = prime * result + ((reason == null) ? 0 : reason.hashCode());
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
        if (!(obj instanceof InconclusiveElement)) {
            return false;
        }
        InconclusiveElement other = (InconclusiveElement)obj;
        if (errCode == null) {
            if (other.errCode != null) {
                return false;
            }
        } else if (!errCode.equals(other.errCode)) {
            return false;
        }
        if (reason == null) {
            if (other.reason != null) {
                return false;
            }
        } else if (!reason.equals(other.reason)) {
            return false;
        }
        return true;
    }

}
