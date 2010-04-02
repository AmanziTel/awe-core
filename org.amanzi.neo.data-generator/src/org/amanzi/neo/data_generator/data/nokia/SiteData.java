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
 * Data for generated site.
 * </p>
 * @author Shcharbatsevich_A
 * @since 1.0.0
 */
public class SiteData extends AbstractTagData{
    
    private List<SectorData> sectors;
    private Float latitude;
    private Float longitude;

    /**
     * Constructor.
     * @param aDistName String (value for tag attribute 'distName')
     */
    public SiteData(String aDistName, String id, Float lat, Float lon) {
        super(NokiaDataConstants.MO_BCF, aDistName, id);
        sectors = new ArrayList<SectorData>();
        latitude = lat;
        longitude = lon;
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
    
    /**
     * @return Returns the latitude.
     */
    public Float getLatitude() {
        return latitude;
    }
    
    /**
     * @return Returns the longitude.
     */
    public Float getLongitude() {
        return longitude;
    }
    
}
