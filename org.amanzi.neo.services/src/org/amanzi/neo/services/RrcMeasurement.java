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

package org.amanzi.neo.services;

/**
 * <p>
 * Contains information about Rnc Measurement
 * </p>.
 *
 * @author tsinkel_a
 * @since 1.0.0
 */
public class RrcMeasurement {
    
    /** The scrambling. */
    String scrambling=null;
    
    /** The rscp. */
    Integer rscp=null;
    
    /** The ecNo. */
    Integer ecNo=null;
    /** The BSIC. */
    Integer bsic=null;

    /** The position. */
    private int position;

    private Integer ueTxPower;
private boolean empty;
    /**
     * Instantiates a new rrc measurement.
     */
    public RrcMeasurement() {
        super();
        empty=true;
    }

    /**
     * Instantiates a new rrc measurement.
     *
     * @param scrambling the scrambling
     * @param rscp the rscp
     * @param ecNo the ec no
     * @param bsic the bsic
     */
    public RrcMeasurement(String scrambling, Integer rscp, Integer ecNo, Integer bsic, Integer ueTxPower) {
        this();
        this.scrambling = scrambling;
        this.rscp = rscp;
        this.ecNo = ecNo;
        this.bsic = bsic;
        this.ueTxPower = ueTxPower;
        empty=false;
    }

    /**
     * Gets the scrambling.
     *
     * @return the scrambling
     */
    public String getScrambling() {
        return scrambling;
    }

    /**
     * Sets the scrambling.
     *
     * @param scrambling the new scrambling
     */
    public void setScrambling(String scrambling) {
        empty=false;
        this.scrambling = scrambling;
    }

    /**
     * Gets the rscp.
     *
     * @return the rscp
     */
    public Integer getRscp() {
        return rscp;
    }

    /**
     * Checks if is empty.
     *
     * @return true, if is empty
     */
    public boolean isEmpty() {
        return empty;
    }

    /**
     * Sets the rscp.
     *
     * @param rscp the new rscp
     */
    public void setRscp(Integer rscp) {
        empty=false;
        this.rscp = rscp;
    }

    /**
     * Gets the ec no.
     *
     * @return the ec no
     */
    public Integer getEcNo() {
        return ecNo;
    }

    /**
     * Sets the ec no.
     *
     * @param ecNo the new ec no
     */
    public void setEcNo(Integer ecNo) {
        empty=false;
        this.ecNo = ecNo;
    }

    /**
     * Gets the bsic.
     *
     * @return the bsic
     */
    public Integer getBsic() {
        return bsic;
    }

    /**
     * Sets the bsic.
     *
     * @param bsic the new bsic
     */
    public void setBsic(Integer bsic) {
        empty=false;
        this.bsic = bsic;
    }


    /**
     * Sets the position.
     *
     * @param position the new position
     */
    public void setPosition(int position) {
        empty=false;
        this.position = position;
    }

    /**
     * Gets the position.
     *
     * @return the position
     */
    public int getPosition() {
        return position;
    }

    /**
     * @return Returns the ueTxPower.
     */
    public Integer getUeTxPower() {
        return ueTxPower;
    }

    /**
     * @param ueTxPower The ueTxPower to set.
     */
    public void setUeTxPower(Integer ueTxPower) {
        empty=false;
        this.ueTxPower = ueTxPower;
    }
    
}
