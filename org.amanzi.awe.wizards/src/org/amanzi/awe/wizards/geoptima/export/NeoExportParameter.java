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

package org.amanzi.awe.wizards.geoptima.export;

import java.util.LinkedList;
import java.util.List;

import org.amanzi.neo.core.utils.export.IExportParameter;
import org.neo4j.graphdb.Node;

/**
 * <p>
 * Contains list of nodes for exporting
 * </p>
 * 
 * @author tsinkel_a
 * @since 1.0.0
 */
public class NeoExportParameter implements IExportParameter {
    List<Node> nodes = new LinkedList<Node>();

    /**
     * Adds to list new node
     *
     * @param node the node
     */
    public void addToList(Node node) {
        assert node != null;
        nodes.add(node);
    }

    /**
     * Adds  to list new node if not exist.
     *
     * @param node the node
     */
    public void addToListIfNotExist(Node node) {
        assert node != null;
        if (nodes.contains(node)) {
            return;
        }
        addToList(node);
    }
    
    //should return copy of list?
    /**
     * Gets the node list.
     *
     * @return the node list
     */
    public List<Node> getNodeList() {
        return nodes;
    } 
}
