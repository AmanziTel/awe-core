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
 * Wrapper of node with additional information
 * </p>.
 *
 * @author TsAr
 * @since 1.0.0
 */
public class WrNode {
    
    /** The original node. */
    private Node originalNode;
    
    /** The is created. */
    boolean isCreated;



    public WrNode(Node originalNode) {
        super();
        this.originalNode = originalNode;
        isCreated=false;
    }

    /**
     * Instantiates a new wr node.
     *
     * @param originalNode the original node
     * @param isCreated the is created
     */
    public WrNode(Node originalNode, boolean isCreated) {
        super();
        this.originalNode = originalNode;
        this.isCreated = isCreated;
    }

    /**
     * Gets the original node.
     *
     * @return the original node
     */
    public Node getOriginalNode() {
        return originalNode;
    }

    /**
     * Sets the original node.
     *
     * @param originalNode the new original node
     */
    public void setOriginalNode(Node originalNode) {
        this.originalNode = originalNode;
    }

    /**
     * Checks if is created.
     *
     * @return true, if is created
     */
    public boolean isCreated() {
        return isCreated;
    }

    /**
     * Sets the created.
     *
     * @param isCreated the new created
     */
    public void setCreated(boolean isCreated) {
        this.isCreated = isCreated;
    }

}
