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

package org.amanzi.neo.core.database.nodes.data;

import org.amanzi.neo.services.enums.NodeTypes;
import org.neo4j.graphdb.Node;

/**
 * <p>
 * Gis node wrapper
 * </p>
 * 
 * @author Cinkel_A
 * @since 1.0.0
 */
public class GisNode extends AbstractChildNextNode {

    public GisNode(Node node) {
        super(node);
    }

    @Override
    public NodeTypes getNodeType() {
        return NodeTypes.GIS;
    }

    public DataRootNode getRootNode() {
        Node node = getNextNode();
        if (node == null) {
            return null;
        }
        return DataRootNode.getInstance(node);
    }
}
