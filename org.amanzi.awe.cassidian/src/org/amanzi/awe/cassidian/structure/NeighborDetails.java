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
 * describe neighborDetails tag
 * </p>
 * 
 * @author Kondratenko_V
 * @since 1.0.0
 */
public class NeighborDetails implements IXmlTag {

    private double frequency;
    private int rssi;
    private int c2;

    @Override
    public String getType() {
        return ChildTypes.NEIGHBOR_DETAILS.getId();
    }

    @Override
    public Object getValueByTagType(String tagName) {
        if (LoaderConstants.FREQUENCY.equals(tagName)) {
            return frequency;
        } else if (LoaderConstants.RSSI.equals(tagName)) {
            return rssi;
        } else if (LoaderConstants.C2.equals(tagName)) {
            return c2;
        }
        return null;
    }

    @Override
    public void setValueByTagType(String tagName, Object value) {
        if (LoaderConstants.FREQUENCY.equals(tagName)) {
            frequency = Double.parseDouble(value.toString());
        } else if (LoaderConstants.RSSI.equals(tagName)) {
            rssi = Integer.parseInt(value.toString());
        } else if (LoaderConstants.C2.equals(tagName)) {
            c2 = Integer.parseInt(value.toString());
        }
    }

    public double getFrequency() {
        return frequency;
    }

    public void setFrequency(double frequency) {
        this.frequency = frequency;
    }

    public int getRssi() {
        return rssi;
    }

    public void setRssi(int rssi) {
        this.rssi = rssi;
    }

    public int getC2() {
        return c2;
    }

    public void setC2(int c2) {
        this.c2 = c2;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + c2;
        long temp;
        temp = Double.doubleToLongBits(frequency);
        result = prime * result + (int)(temp ^ (temp >>> 32));
        result = prime * result + rssi;
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
        if (!(obj instanceof NeighborDetails)) {
            return false;
        }
        NeighborDetails other = (NeighborDetails)obj;
        if (c2 != other.c2) {
            return false;
        }
        if (Double.doubleToLongBits(frequency) != Double.doubleToLongBits(other.frequency)) {
            return false;
        }
        if (rssi != other.rssi) {
            return false;
        }
        return true;
    }

}
