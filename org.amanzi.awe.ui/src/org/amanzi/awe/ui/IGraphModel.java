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

package org.amanzi.awe.ui;

import java.util.Map;
import java.util.Set;

import org.eclipse.swt.graphics.RGB;
import org.neo4j.graphdb.Node;

/**
 * <p>
 *
 * </p>
 * @author TsAr
 * @since 1.0.0
 */
public interface IGraphModel {
    RGB getColor(Node visualNode);
    Set<Node> getOutgoingRelation(Node visualNode);
    /**
     *
     * @return
     */
    Map<Node, Set<Node>> getOutgoingRelationMap();
}
