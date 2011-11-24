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
package org.amanzi.neo.loader.core.newsaver;

/**
 * store signal info
 * 
 * @author Vladislav_Kondratenko
 */
public class Signal {
    // StringUtils.EMPTY + channel + "\t" + pn_code;
    private String chan_code = "";
    /**
     * at 0 index store Math.pow(10.0, ((ec_io) / 10.0));
     */
    private float[] chan_array = new float[2];

    /**
     * create class instance
     */
    public Signal() {
    }

    public float[] getChanarray() {
        return chan_array;
    }

    public String getChanCode() {
        return chan_code;
    }

    public void setChanCode(String chan_code) {
        this.chan_code = chan_code;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((chan_code == null) ? 0 : chan_code.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Signal other = (Signal)obj;
        if (chan_code == null) {
            if (other.chan_code != null)
                return false;
        } else if (!chan_code.equals(other.chan_code))
            return false;
        return true;
    }
}
