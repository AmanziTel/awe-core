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

/**
 * <p>
 * Data for external UMTS cells.
 * </p>
 * @author Shcharbatsevich_A
 * @since 1.0.0
 */
public class ExternalCellData extends AbstractTagData{
    
    private List<SectorData> sectors;

    /**
     * Constructor.
     * @param aClass
     * @param aDistName
     * @param anId
     */
    public ExternalCellData(String aDistName, String anId) {
        super(NokiaDataConstants.MO_EXCC, aDistName, anId);
        sectors = new ArrayList<SectorData>();
    }

    /**
     * @return Returns the sectors.
     */
    public List<SectorData> getSectors() {
        return sectors;
    }
    
    /**
     * Add sector data
     *
     * @param sector SectorData
     */
    public void addSectror(SectorData sector){
        sectors.add(sector);
    }
}
