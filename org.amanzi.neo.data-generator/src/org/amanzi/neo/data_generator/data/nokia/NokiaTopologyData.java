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

package org.amanzi.neo.data_generator.data.nokia;

import java.util.ArrayList;
import java.util.List;

import org.amanzi.neo.data_generator.data.IGeneratedData;

/**
 * <p>
 * Generated data for Nokia topology.
 * </p>
 * @author Shcharbatsevich_A
 * @since 1.0.0
 */
public class NokiaTopologyData implements IGeneratedData {
    
    private ExternalCellData externalCell;
    private List<BSCData> bscList = new ArrayList<BSCData>(0);
    
    /**
     * @return Returns the bscList.
     */
    public List<BSCData> getBscList() {
        return bscList;
    }
    
    /**
     * @param bscList The bscList to set.
     */
    public void setBscList(List<BSCData> bscList) {
        this.bscList = bscList;
    }
    
    /**
     * @return Returns the externalCell.
     */
    public ExternalCellData getExternalCell() {
        return externalCell;
    }
    
    /**
     * @param externalCell The externalCell to set.
     */
    public void setExternalCell(ExternalCellData externalCell) {
        this.externalCell = externalCell;
    }
}
