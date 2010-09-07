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
import java.util.Map;

import org.amanzi.neo.core.INeoConstants;
import org.amanzi.neo.core.enums.NodeTypes;
import org.amanzi.neo.core.utils.NeoUtils;
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

    protected static final NodeTypes[] COPY_ACTION_SUPPORTED_TYPES = new NodeTypes[] {};

    /**
     * @param selection
     */
    public CopyElementAction(IStructuredSelection selection) {
        super(selection, COPY_ACTION_SUPPORTED_TYPES, "Copy ", false);
    }

    @Override
    protected void updateNewElementType() {
        // do nothing since type of new element equals to current
    }

    @Override
    protected void createNewElement(Node sourceNode, HashMap<String, Object> properties) {
        Node parent = null;

        Transaction tx = service.beginTx();
        try {
            parent = NeoUtils.getParent(service, sourceNode);

            for (String key : sourceNode.getPropertyKeys()) {
                if (!defaultProperties.containsKey(key)) {
                    defaultProperties.put(key, sourceNode.getProperty(key));
                }
            }

//            PropertyHeader ph = new PropertyHeader(sourceNode);
//            Map<String, Object> copyPropertyes = ph.copyNetworkNode();
            
            Map<String, Object> copyPropertyes = new HashMap<String, Object>();
            for (String propertyKey : sourceNode.getPropertyKeys()) {
                copyPropertyes.put(propertyKey, sourceNode.getProperty(propertyKey));
            }
            
            copyPropertyes.remove(INeoConstants.PROPERTY_NAME_NAME);
            defaultProperties.putAll(copyPropertyes);
            tx.success();
        } finally {
            tx.finish();
        }

        if (parent != null) {
            super.createNewElement(parent, defaultProperties);
        }
    }
}
