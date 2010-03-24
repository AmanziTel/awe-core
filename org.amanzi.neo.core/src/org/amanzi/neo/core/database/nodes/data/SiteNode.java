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

import org.amanzi.neo.core.enums.NodeTypes;
import org.neo4j.graphdb.Node;

/**
 * <p>
 *  Site node wrapper
 * </p>
 * @author Cinkel_A
 * @since 1.0.0
 */
public class SiteNode extends AbstractChildNextNode {

    /**
     * Constructor of abstract Wrapper
     * 
     * @param node wrapped Node
     */
    protected SiteNode(Node node) {
        super(node);
    }

    @Override
    public NodeTypes getNodeType() {
        return NodeTypes.SITE;
    }

}
