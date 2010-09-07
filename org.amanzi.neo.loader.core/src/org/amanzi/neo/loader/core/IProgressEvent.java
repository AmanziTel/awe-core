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

package org.amanzi.neo.loader.core;

/**
 * TODO Purpose of 
 * <p>
 *
 * </p>
 * @author Saelenchits_N
 * @since 1.0.0
 */
public interface IProgressEvent {

    /**
     * Gets the process name.
     *
     * @return the process name
     */
    String getProcessName();
    
    /**
     * Gets the percentage.
     *
     * @return the percentage
     */
    double getPercentage();

    /**
     * Cancel process.
     */
    void cancelProcess();
    
    /**
     * Checks if is canseled.
     *
     * @return true, if is canseled
     */
    boolean isCanseled();
    
}
