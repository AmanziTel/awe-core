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

import java.util.Iterator;
import java.util.Map;

import org.neo4j.graphdb.Node;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Kondratneko_Vladislav
 * @since 1.0.0
 */
public interface ISelectionModel extends IDataModel, IPropertyStatisticalModel, IRenderableModel {

    /**
     * find sector by name and create relation ship from rootNode to each node from iterator
     * 
     * @param sectorElement
     */
    public void linkToSector(String name);
}
