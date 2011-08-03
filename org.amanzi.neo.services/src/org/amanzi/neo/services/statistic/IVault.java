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

import java.util.List;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Kruglik_A
 * @since 1.0.0
 */
public interface IVault {
   
    /**
     * this method get subVaults of vault
     * @return List<IVault> subVaults
     */
    public List<IVault> getSubVaults();

    /**
     * this method get count
     * @return int count
     */
    public int getCount();

    /**
     * this method get type of vault
     * @return String type
     */
    public String getType();

    /**
     * add subVault to vault
     * @param vault - subVault
     */
    public void addSubVault(IVault vault);

    /**
     * this method set count to vault 
     * @param count
     */
    public void setCount(int count);

    /**
     * this method set type to vault
     * @param type
     */
    public void setType(String type);

    /**
     * 
     */
    public void index();

    /**
     * 
     */
    public void parse();
}
