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

import java.util.Calendar;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Kondratenko_Vladislav
 * @since 1.0.0
 */
public abstract class AbstactMsg implements IXmlTag {
    protected String probeId;
    protected Integer msgType;
    protected Integer dataLength;
    protected Integer dataTxt;
    protected InconclusiveElement inconclusive;
    protected Calendar calendar;

    public InconclusiveElement getInconclusive() {
        return inconclusive;
    }

    public void setInconclusive(InconclusiveElement inconclusive) {
        this.inconclusive = inconclusive;
    }

    public String getProbeId() {
        return probeId;
    }

    public void setProbeId(String probeId) {
        this.probeId = probeId;
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

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((calendar == null) ? 0 : calendar.hashCode());
        result = prime * result + ((dataLength == null) ? 0 : dataLength.hashCode());
        result = prime * result + ((dataTxt == null) ? 0 : dataTxt.hashCode());
        result = prime * result + ((inconclusive == null) ? 0 : inconclusive.hashCode());
        result = prime * result + ((msgType == null) ? 0 : msgType.hashCode());
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
        if (!(obj instanceof AbstactMsg)) {
            return false;
        }
        AbstactMsg other = (AbstactMsg)obj;
      
        if (dataLength == null) {
            if (other.dataLength != null) {
                return false;
            }
        } else if (!dataLength.equals(other.dataLength)) {
            return false;
        }
        if (dataTxt == null) {
            if (other.dataTxt != null) {
                return false;
            }
        } else if (!dataTxt.equals(other.dataTxt)) {
            return false;
        }
        if (inconclusive == null) {
            if (other.inconclusive != null) {
                return false;
            }
        } else if (!inconclusive.equals(other.inconclusive)) {
            return false;
        }
        if (msgType == null) {
            if (other.msgType != null) {
                return false;
            }
        } else if (!msgType.equals(other.msgType)) {
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
