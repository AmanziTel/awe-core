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
public class NewNodeTypeVault implements IVault {
    private List<IVault> subVaults;
    private int count;
    private String type;
    private List<NewPropertyStatistics> propertyStatisticsList = new ArrayList<NewPropertyStatistics>();

    /**
     * constructor
     */
    public NewNodeTypeVault() {
        super();
        this.count = 0;
        this.type = "";
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
    public void index() {
    }

    @Override
    public void parse() {
    }

    @Override
    public void addSubVault(IVault vault) {
        this.subVaults.add(vault);
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
    public List<NewPropertyStatistics> getPropertyStatisticsList() {
        return this.propertyStatisticsList;
    }

    @Override
    public void addPropertyStatistics(NewPropertyStatistics propStat) {
        this.propertyStatisticsList.add(propStat);
    }

}
