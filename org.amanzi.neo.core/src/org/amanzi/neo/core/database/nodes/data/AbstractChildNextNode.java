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

import org.amanzi.neo.core.INeoConstants;
import org.amanzi.neo.core.database.nodes.AbstractNode;
import org.amanzi.neo.core.enums.GeoNeoRelationshipTypes;
import org.amanzi.neo.core.enums.NodeTypes;
import org.amanzi.neo.core.utils.NeoUtils;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.Traverser;

/**
 * <p>
 * Abstract wrapper of structured node.
 * </p>
 * 
 * @author Cinkel_A
 * @since 1.0.0
 */
public abstract class AbstractChildNextNode extends AbstractNode {

    /**
     * Constructor of abstract Wrapper
     * 
     * @param node wrapped Node
     */
    protected AbstractChildNextNode(Node node) {
        super(node);
//        final String nodeType = NeoUtils.getNodeType(node, null);
//        if (nodeType!=null){
//            if (!getNodeType().checkNode(node)){
//                throw new IllegalArgumentException("Wrong node type for current wrapper!");
//            }
//        }
    }

    /**
     * Add child to current node
     * 
     * @param child - child
     */
    protected void addChild(AbstractChildNextNode child) {
        NeoUtils.addChild(getUnderlyingNode(), child.getUnderlyingNode(), getLastChild(), null);
        setLastChild(child);
    }

    /**
     * Sets last child id in node
     * 
     * @param child - last child node
     */
    protected void setLastChild(AbstractChildNextNode child) {
        setParameter(INeoConstants.LAST_CHILD_ID, child.getUnderlyingNode().getId());
    }

    /**
     * Get next node
     * 
     * @return next node or null
     */
    protected Node getNextNode() {
        Relationship nextRelation = getUnderlyingNode().getSingleRelationship(GeoNeoRelationshipTypes.NEXT, Direction
                .OUTGOING);
        return nextRelation == null ? null : nextRelation.getOtherNode(getUnderlyingNode());
    }
    /**
     * Get parent node
     * 
     * @return next node or null
     */
    protected Node getParentNode() {
        return NeoUtils.getParent(null, getUnderlyingNode());
    }
    /**
     * if current node is first child, then return true, else false
     *
     * @return 
     */
    public boolean isFirstChild(){
        return getUnderlyingNode().hasRelationship(GeoNeoRelationshipTypes.CHILD, Direction.INCOMING);
    }
    /**
     * Get previous node
     * 
     * @return next node or null
     */
    protected Node getPreviousNode() {
        Relationship nextRelation = getUnderlyingNode().getSingleRelationship(GeoNeoRelationshipTypes.NEXT, Direction.INCOMING);
        return nextRelation == null ? null : nextRelation.getOtherNode(getUnderlyingNode());
    }

    /**
     * get all childs
     * 
     * @return traverser child traverser
     */
    protected Traverser getChildren() {
        return NeoUtils.getChildTraverser(getUnderlyingNode());
    }

    /**
     *get last child
     * 
     * @return Node or null if childs not exist
     */
    protected Node getLastChild() {
        Long id = (Long)getParameter(INeoConstants.LAST_CHILD_ID, null);
        if (id == null) {
            return null;
        }
        return getService().getNodeById(id);
    }

    /**
     * Get type of node
     * 
     * @return type of node
     */
    public abstract NodeTypes getNodeType();
}
