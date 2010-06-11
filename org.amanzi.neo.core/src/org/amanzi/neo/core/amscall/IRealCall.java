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

package org.amanzi.neo.core.amscall;

/**
 * <p>
 * real call interface
 * </p>
 * .
 * 
 * @author TsAr
 * @since 1.0.0
 */
public interface IRealCall extends IAmsCall {

    /**
     * Adds the lq.
     * 
     * @param lq the lq
     */
    void addLq(float lq);

    /**
     * Gets the lq.
     * 
     * @return the lq
     */
    float[] getLq();

    /**
     * Gets the delay.
     * 
     * @return the delay
     */
    float[] getDelay();

    /**
     * Adds the delay.
     * 
     * @param delay the delay
     */
    void addDelay(float delay);

    /**
     * Gets the setup duration.
     * 
     * @return the setup duration
     */
    Long getSetupDuration();

    /**
     * Gets the termination duration.
     * 
     * @return the termination duration
     */
    Long getTerminationDuration();

    /**
     * Gets the call duration.
     * 
     * @return the call duration
     */
    Long getCallDuration();
}
