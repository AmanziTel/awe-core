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

package org.amanzi.neo.services.model;

import org.amanzi.neo.services.exceptions.AWEException;


/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Kondratneko_Vladislav
 * @since 1.0.0
 */
public interface ISelectionModel extends IDataModel {

    /**
     * creates link to current element
     * 
     * @param element
     */
    public void linkToSector(IDataElement element) throws AWEException;

    /**
     * Returns number of Nodes in current Selection Model
     * 
     * @return
     */
    public int getSelectedNodesCount();

    /**
     * Check existing of selection link
     * 
     * @param element
     * @return true if link exist
     */
    public boolean isExistSelectionLink(IDataElement element);

    /**
     * Delete selection link
     * 
     * @param element
     */
    void deleteSelectionLink(IDataElement element);
}
