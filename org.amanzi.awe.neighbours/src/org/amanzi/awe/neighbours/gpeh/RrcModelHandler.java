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
 * TODO Purpose of 
 * <p>
 *
 * </p>
 * @author tsinkel_a
 * @since 1.0.0
 */
public abstract class RrcModelHandler {

    protected Long computeTime;

    public abstract boolean haveData();

    public abstract List<Object> formLine();

    public abstract void clearData();


    public void setTime(Long computeTime) {
        this.computeTime = computeTime;
        clearData();
        
    }
    
    public abstract boolean setData(CellNodeInfo bestCell,InterfCellInfo interfCell);
}
