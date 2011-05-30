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

package org.amanzi.neo.services;

import org.neo4j.graphdb.Node;

/**
 * <p>
 * Interface for common database
 * </p>
 * 
 * @author TsAr
 * @since 1.0.0
 */
public interface IDatasetService {

    /**
     * Sets the structure of root node
     * 
     * @param root the child of project node (network.dataset,"")
     * @param structureProperty the structure - array of type id
     */
    void setStructure(Node root, String[] structureProperty);

}