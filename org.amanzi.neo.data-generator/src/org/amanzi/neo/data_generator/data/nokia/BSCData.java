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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * <p>
 * Data for BSC tag.
 * </p>
 * @author Shcharbatsevich_A
 * @since 1.0.0
 */
public class BSCData extends AbstractTagData{

    private List<SiteData> sites;
    private HashMap<Integer,Set<Integer>> balFrequency;
    private HashMap<Integer,Set<Integer>> malFrequency;
    
    /**
     * Constructor.
     * @param aDistName
     * @param anId
     */
    public BSCData(String aDistName, String anId) {
        super(NokiaDataConstants.MO_BSC, aDistName, anId);
        sites = new ArrayList<SiteData>();
        balFrequency = new HashMap<Integer,Set<Integer>>();
        malFrequency = new HashMap<Integer,Set<Integer>>();
    }
    
    /**
     * @return Returns the sites.
     */
    public List<SiteData> getSites() {
        return sites;
    }
    
    /**
     * Add site.
     *
     * @param site SiteData
     */
    public void addSite(SiteData site){
        sites.add(site);
    }

    /**
     * @return Returns the balFrequency.
     */
    public HashMap<Integer, Set<Integer>> getBalFrequency() {
        return balFrequency;
    }
    
    /**
     * Add BAL frequency
     *
     * @param bal
     * @param freq
     */
    public void addBalFrequency(Integer bal,Integer freq){
       Set<Integer> freqs = balFrequency.get(bal); 
       if(freqs == null){
           freqs = new HashSet<Integer>();
           balFrequency.put(bal, freqs);
       }
       freqs.add(freq);
    }
    
    /**
     * @return Returns the malFrequency.
     */
    public HashMap<Integer, Set<Integer>> getMalFrequency() {
        return malFrequency;
    }
    
    /**
     * Add MAL frequency
     *
     * @param mal
     * @param freq
     */
    public void addMalFrequency(Integer mal,Integer freq){
       Set<Integer> freqs = malFrequency.get(mal); 
       if(freqs == null){
           freqs = new HashSet<Integer>();
           malFrequency.put(mal, freqs);
       }
       freqs.add(freq);
    }
}
