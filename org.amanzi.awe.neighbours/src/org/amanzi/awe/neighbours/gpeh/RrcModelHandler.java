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

import java.util.List;

/**
 * <p>
 *Abstract class for Rrc Model handler
 * </p>
 * @author tsinkel_a
 * @since 1.0.0
 */
public abstract class RrcModelHandler {

    protected long computeTime;

    /**
     * check - model contains data or not
     *
     * @return true, if successful
     */
    public abstract boolean haveData();

    /**
     * Form line of Object from data
     *
     * @return the list
     */
    public abstract List<Object> formLine();

    /**
     * Clear data in model
     */
    public abstract void clearData();


    /**
     * Sets the time of handling
     *
     * @param computeTime the new time
     */
    public void setTime(long computeTime) {
        this.computeTime = computeTime;
        clearData();
        
    }
    
    /**
     * Gets the compute time
     *
     * @return the compute time
     */
    public long getComputeTime() {
        return computeTime;
    }

    /**
     * Sets the data in model
     *
     * @param bestCell the best cell
     * @param interfCell the interference cell
     * @return true, if data sets successful
     */
    public abstract boolean setData(CellNodeInfo bestCell,InterfCellInfo interfCell);
}
