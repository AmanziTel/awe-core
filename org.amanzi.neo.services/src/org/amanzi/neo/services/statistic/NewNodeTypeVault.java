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

import java.util.HashMap;
import java.util.Map;

import org.amanzi.neo.services.statistic.internal.NewPropertyStatistics;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Kruglik_A
 * @since 1.0.0
 */
public class NewNodeTypeVault implements IVault {
    private Map<String, IVault> subVaults = new HashMap<String, IVault>();
    private int count;
    private String type;
    private Map<String, NewPropertyStatistics> propertyStatisticsList = new HashMap<String, NewPropertyStatistics>();

    /**
     * constructor
     */
    public NewNodeTypeVault() {
        super();
        this.count = 0;
        this.type = "";

    }

    @Override
    public Map<String, IVault> getSubVaults() {
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
    public void indexProperty(String nodeType, String propName, Object propValue) {
    }

    @Override
    public Object parse(String nodeType, String propertyName, String propertyValue) {
        return null;
    }

    @Override
    public void addSubVault(IVault vault) {
        this.subVaults.put(vault.getType(), vault);
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
    public Map<String, NewPropertyStatistics> getPropertyStatisticsMap() {
        return this.propertyStatisticsList;
    }

    @Override
    public void addPropertyStatistics(NewPropertyStatistics propStat) {
        this.propertyStatisticsList.put(propStat.getName(), propStat);
    }

}
