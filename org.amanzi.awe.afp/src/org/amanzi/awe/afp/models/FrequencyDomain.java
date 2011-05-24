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

package org.amanzi.awe.afp.models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.amanzi.awe.afp.models.parameters.ChannelType;
import org.amanzi.neo.services.filters.Filter;

/**
 * TODO Purpose of 
 * <p>
 *
 * </p>
 * @author Lagutko_N
 * @since 1.0.0
 */
public class FrequencyDomain extends Domain {
    
    private boolean isFree;
    
    private Filter filter;
    
    private int sectorCount = 0;
    
    private int trxCount = 0;
    
    private Map<ChannelType, Integer> channelCount = new HashMap<ChannelType, Integer>(0);
    
    private List<String> selectedFrequencies = new ArrayList<String>(0);
    
    /**
     * @param name
     */
    public FrequencyDomain(String name, boolean isFree) {
        super(name);
        this.isFree = isFree;
        
        for (ChannelType type : ChannelType.values()) {
            channelCount.put(type, 0);
        }
    }

    public void setFilter(Filter filter) {
        this.filter = filter;
    }
    
    public Filter getFilter() {
        return filter;
    }

    /**
     * @return Returns the sectorCount.
     */
    public int getSectorCount() {
        return sectorCount;
    }

    /**
     * @param sectorCount The sectorCount to set.
     */
    public void setSectorCount(int sectorCount) {
        this.sectorCount = sectorCount;
    }

    /**
     * @return Returns the trxCount.
     */
    public int getTrxCount() {
        return trxCount;
    }

    /**
     * @param trxCount The trxCount to set.
     */
    public void setTrxCount(int trxCount) {
        this.trxCount = trxCount;
    }
    
    public boolean isFree() {
        return isFree;
    }
    
    public Map<ChannelType, Integer> getChannelCount() {
        return channelCount;
    }

    /**
     * @return Returns the selectedFrequencies.
     */
    public List<String> getSelectedFrequencies() {
        return selectedFrequencies;
    }

    /**
     * @param selectedFrequencies The selectedFrequencies to set.
     */
    public void setSelectedFrequencies(List<String> selectedFrequencies) {
        this.selectedFrequencies = selectedFrequencies;
    }
    
}
