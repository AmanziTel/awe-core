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
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

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
   // private Class<?> klass = this.getClass();

    /**
     * constructor
     * 
     * @param type
     */
    public StatisticsVault(String type) {
        this.type = type;
        this.count = 0;
    }

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
    public void index() {
    }

    @Override
    public void parse() {
    }
}
