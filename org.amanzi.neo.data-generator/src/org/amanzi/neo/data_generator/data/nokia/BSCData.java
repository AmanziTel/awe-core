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
 * Data for BSC tag.
 * </p>
 * @author Shcharbatsevich_A
 * @since 1.0.0
 */
public class BSCData extends AbstractTagData{

    private List<SiteData> sites;
    
    /**
     * Constructor.
     * @param aDistName
     * @param anId
     */
    public BSCData(String aDistName, String anId) {
        super(NokiaDataConstants.MO_BSC, aDistName, anId);
        sites = new ArrayList<SiteData>();
    }
    
    /**
     * @return Returns the sites.
     */
    public List<SiteData> getSites() {
        return sites;
    }
    
    public void addSite(SiteData site){
        sites.add(site);
    }

}
