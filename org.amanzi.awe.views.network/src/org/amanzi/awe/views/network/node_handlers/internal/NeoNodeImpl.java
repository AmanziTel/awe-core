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

package org.amanzi.awe.views.network.node_handlers.internal;

import org.amanzi.awe.views.network.node_handlers.INeoNode;
import org.amanzi.neo.services.DatasetService;
import org.amanzi.neo.services.NeoServiceFactory;
import org.amanzi.neo.services.ui.NeoUtils;
import org.eclipse.core.runtime.IAdaptable;
import org.neo4j.graphdb.Node;

/**
 * <p>
 *Implementation of INeoNode
 * </p>
 * @author tsinkel_a
 * @since 1.0.0
 */
public class NeoNodeImpl implements INeoNode,IAdaptable {

    private final Node node;
    private String name;
    private String type;

    /**
     * @param next
     */
    public NeoNodeImpl(Node node) {
        this.node = node;
        DatasetService service = NeoServiceFactory.getInstance().getDatasetService();
         name = NeoUtils.getFormatedNodeName(node,"");
         type=service.getNodeType(node).getId();
    }

    @Override
    public String getId() {
        return type;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Node getNode() {
        return node;
    }

    @SuppressWarnings("rawtypes")
	@Override
    public Object getAdapter(Class adapter) {
        if (adapter==Node.class){
            return node;
        }
        return null;
    }

}
