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

import org.amanzi.neo.services.enums.INodeType;
import org.neo4j.graphdb.Node;

/**
 * <p>
 * This class manages access to network elements
 * </p>
 * 
 * @author grigoreva_a
 * @since 1.0.0
 */
public class NewNetworkService extends NewAbstractService {

    /**
     * @param parent
     * @param indexName
     * @param name
     * @param elementType
     * @return
     */
    public Node createNetworkElement(Node parent, String indexName, String name, INodeType elementType) {
        return null;
    }

    /**
     * @param indexName
     * @param name
     * @return
     */
    public Node findNetworkElement(String indexName, String name) {
        return null;
    }

    /**
     * @param parent
     * @param indexName
     * @param name
     * @param elementType
     * @return
     */
    public Node getNetworkElement(Node parent, String indexName, String name, INodeType elementType) {
        return null;
    }

    /**
     * @param parent
     * @param indexName
     * @param name
     * @param ci
     * @param lac
     * @return
     */
    public Node createSector(Node parent, String indexName, String name, String ci, String lac) {
        return null;
    }

    /**
     * @param indexName
     * @param name
     * @param ci
     * @param lac
     * @return
     */
    public Node findSector(String indexName, String name, String ci, String lac) {
        return null;
    }

    /**
     * @param parent
     * @param indexName
     * @param name
     * @param c
     * @param lac
     * @return
     */
    public Node getSector(Node parent, String indexName, String name, String c, String lac) {
        return null;
    }

    /**
     * @param elementType
     * @return
     */
    public Iterable<Node> findAllNetworkElements(INodeType elementType) {
        return null;
    }
}
