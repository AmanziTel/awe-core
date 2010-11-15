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

package org.amanzi.awe.views.network.node_handlers;

/**
 * <p>
 *  INode Handler - provide work with necessary node
 * </p>
 * @author tsinkel_a
 * @since 1.0.0
 */
public interface INodeHandler {
    
    /**
     * Checks if the nessary INeoNode is parent
     *
     * @param nodeId the node id
     * @return true, if is parent
     */
    boolean isParent(INeoNode parent);
    Iterable<INeoNode>getChildrens(INeoNode parent);
    Iterable<INeoNode>getChildrensAfter(INeoNode childNode); 
//    Iterable<INeoNode>findInChilds(String findStr);
}
