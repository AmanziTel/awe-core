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

package org.amanzi.neo.services.statistic;


import java.util.ArrayList;
import java.util.List;

import org.amanzi.neo.services.statistic.internal.NewPropertyStatistics;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Kruglik_A
 * @since 1.0.0
 */
public class StatisticsVault implements IVault {
    private List<IVault> subVaults = new ArrayList<IVault>();
    private int count;
    private String type;
    private List<NewPropertyStatistics> propertyStatisticsList = new ArrayList<NewPropertyStatistics>();

    /**
     * constructor
     */
    public StatisticsVault() {
        super();
        this.count = 0;
        this.type ="";
    }

    /**
     * constructor with type of vault
     * 
     * @param type
     */
    public StatisticsVault(String type) {
        this.type = type;
        this.count = 0;
    }

    @Override
    public void addSubVault(IVault subVault) {
        subVaults.add(subVault);
    }

    @Override
    public List<IVault> getSubVaults() {
        return subVaults;
    }

    @Override
    public int getCount() {
        return count;
    }

    @Override
    public String getType() {
        return type;
    }
    @Override
    public List<NewPropertyStatistics> getPropertyStatisticsList(){
        return this.propertyStatisticsList;
    }
    @Override
    public void addPropertyStatistics(NewPropertyStatistics propStat){
        this.propertyStatisticsList.add(propStat);
    }
    
    @Override
    public void setCount(int count) {
        this.count = count;
    }

    @Override
    public void setType(String type) {
        this.type = type;
    }
    
    @Override
    public void index() {
    }

    @Override
    public void parse() {
    }
}
