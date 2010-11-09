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

package org.amanzi.awe.views.network.view.actions;

import java.util.HashMap;

import org.amanzi.neo.services.INeoConstants;
import org.amanzi.neo.services.enums.NetworkRelationshipTypes;
import org.amanzi.neo.services.enums.NodeTypes;
import org.amanzi.neo.services.ui.NeoUtils;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Lagutko_N
 * @since 1.0.0
 */
public class CopyElementAction extends NewElementAction {

    protected static final NodeTypes[] COPY_ACTION_SUPPORTED_TYPES = new NodeTypes[] {NodeTypes.NETWORK};

    /**
     * @param selection
     */
    public CopyElementAction(IStructuredSelection selection) {
        super(selection, COPY_ACTION_SUPPORTED_TYPES, "Copy ", false);
    }


    @Override
    protected void createNewElement(Node parentElement, HashMap<String, Object> properties) {
        // Node parent = null;

        Transaction tx = service.beginTx();
        try {

            Node child = service.createNode();

            for (String key : selectedNode.getPropertyKeys()) {
                if (!child.hasProperty(key))
                    child.setProperty(key, selectedNode.getProperty(key));
            }

            // for (String key : properties.keySet()) {
            // if (!child.hasProperty(key))
            // child.setProperty(key, properties.get(key));
            // }
            Node parent = NeoUtils.getParent(service, parentElement);
            child.setProperty(INeoConstants.PROPERTY_NAME_NAME, properties.get(INeoConstants.PROPERTY_NAME_NAME));
            setType(child);

            if (type == NodeTypes.SECTOR) {
                parent.createRelationshipTo(child, NetworkRelationshipTypes.CHILD);
            } else {
                NeoUtils.addChild(parent, child, null, service);
                parent.createRelationshipTo(child, NetworkRelationshipTypes.CHILD);
            }

            postCreating(child);

            tx.success();
        } catch (Exception e) {
            tx.failure();
        } finally {
            tx.finish();
        }
    }
}
