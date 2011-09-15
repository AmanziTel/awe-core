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

package org.amanzi.neo.services.network;

import org.amanzi.neo.services.enums.INodeType;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.traversal.Evaluator;

/**
 * Interface that represents Model that can traverse through Sectors
 *
 * @author Lagutko_N
 * @since 1.0.0
 */
public interface INetworkTraversableModel {
    
    public Iterable<Node> getAllElementsByType(Evaluator filter, INodeType ... nodeTypes);

}
