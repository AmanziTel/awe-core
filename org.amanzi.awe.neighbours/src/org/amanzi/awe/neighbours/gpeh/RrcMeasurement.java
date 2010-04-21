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

package org.amanzi.awe.neighbours.gpeh;

/**
 * <p>
 * Contains information about Rnc Measurement 
 * </p>
 *
 * @author tsinkel_a
 * @since 1.0.0
 */
public class RrcMeasurement {
    
    /** The scrambling. */
    Integer scrambling;
    
    /** The rscp. */
    Integer rscp;
    
    /** The ecNo. */
    Integer ecNo;
    /** The BSIC. */
    Integer bsic;

    /**
     * Instantiates a new rrc measurement.
     */
    public RrcMeasurement() {
        super();
    }

    /**
     * Instantiates a new rrc measurement.
     *
     * @param scrambling the scrambling
     * @param rscp the rscp
     * @param ecNo the ec no
     * @param bsic the bsic
     */
    public RrcMeasurement(Integer scrambling, Integer rscp, Integer ecNo,Integer bsic) {
        super();
        this.scrambling = scrambling;
        this.rscp = rscp;
        this.ecNo = ecNo;
        this.bsic = bsic;
    }

    /**
     * Gets the scrambling.
     *
     * @return the scrambling
     */
    public Integer getScrambling() {
        return scrambling;
    }

    /**
     * Sets the scrambling.
     *
     * @param scrambling the new scrambling
     */
    public void setScrambling(Integer scrambling) {
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
     * Sets the rscp.
     *
     * @param rscp the new rscp
     */
    public void setRscp(Integer rscp) {
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
        this.bsic = bsic;
    }
    
}
