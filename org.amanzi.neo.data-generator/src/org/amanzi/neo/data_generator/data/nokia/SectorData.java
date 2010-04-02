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
 * Data for sector (BTS or EWCE) tag.
 * </p>
 * @author Shcharbatsevich_A
 * @since 1.0.0
 */
public class SectorData extends AbstractTagData{
    
    private List<SectorData> neighbors;
    private Integer azimuth;
    private Integer beamwidth;

    /**
     * @param aClass
     * @param aDistName
     * @param anId
     */
    public SectorData(String aClass, String aDistName, String anId) {
        super(aClass, aDistName, anId);
        neighbors = new ArrayList<SectorData>();
    }
    
    /**
     * @param aClass
     * @param aDistName
     * @param anId
     */
    public SectorData(String aClass, String aDistName, String anId, Integer anAzimuth, Integer aBeamwidth) {
        super(aClass, aDistName, anId);
        neighbors = new ArrayList<SectorData>();
        azimuth = anAzimuth;
        beamwidth = aBeamwidth;
    }

    /**
     * @return Returns the sectors.
     */
    public List<SectorData> getNeighbors() {
        return neighbors;
    }
    
    /**
     * Add sector data
     *
     * @param sector SectorData
     */
    public void addNeighbor(SectorData sector){
        neighbors.add(sector);
    }

    /**
     * @return Returns the azimuth.
     */
    public Integer getAzimuth() {
        return azimuth;
    }
    
    /**
     * @return Returns the beamwidth.
     */
    public Integer getBeamwidth() {
        return beamwidth;
    }
    
}
