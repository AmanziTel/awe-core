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

import org.amanzi.neo.services.DatasetService;
import org.amanzi.neo.services.INeoConstants;
import org.amanzi.neo.services.NeoServiceFactory;
import org.amanzi.neo.services.enums.NetworkRelationshipTypes;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;

/**
 * TODO Purpose of 
 * <p>
 *
 * </p>
 * @author NiCK
 * @since 1.0.0
 */
public class NetworkSelectionModel {
    private Node networkNode;
    private DatasetService ds;

    public NetworkSelectionModel(Node networkNode) {
        this.networkNode = networkNode;
        ds = NeoServiceFactory.getInstance().getDatasetService();
    }

    public Node getSelectionNode(String selectionName) {
        Iterable<Relationship> rels = networkNode.getRelationships(NetworkRelationshipTypes.SELECTION);
        for (Relationship rel : rels) {
            Node selNode = rel.getEndNode();
            if (selNode.getProperty(INeoConstants.PROPERTY_NAME_NAME, "").equals(selectionName)) {
                return selNode;
            }
        }
        return ds.creteSelectionNode(networkNode, selectionName);
    }

    public void addSelection(Node selectionNode, Node sector) {
        ds.addSelection(selectionNode, sector);
    }

}
