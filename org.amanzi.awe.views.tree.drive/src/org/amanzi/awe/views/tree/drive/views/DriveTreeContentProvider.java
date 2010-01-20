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
package org.amanzi.awe.views.tree.drive.views;

import org.amanzi.awe.awe.views.view.provider.NetworkTreeContentProvider;
import org.amanzi.awe.views.network.proxy.NeoNode;
import org.amanzi.awe.views.network.proxy.Root;
import org.amanzi.neo.core.INeoConstants;
import org.amanzi.neo.core.enums.GeoNeoRelationshipTypes;
import org.amanzi.neo.core.enums.NetworkRelationshipTypes;
import org.amanzi.neo.core.service.NeoServiceProvider;
import org.amanzi.neo.core.utils.NeoUtils;
import org.neo4j.api.core.Direction;
import org.neo4j.api.core.Node;
import org.neo4j.api.core.ReturnableEvaluator;
import org.neo4j.api.core.StopEvaluator;
import org.neo4j.api.core.Transaction;
import org.neo4j.api.core.TraversalPosition;
import org.neo4j.api.core.Traverser.Order;

/**
 * <p>
 * Content provider for Drive tree
 * </p>
 * 
 * @author Cinkel_A
 * @since 1.0.0
 */
public class DriveTreeContentProvider extends NetworkTreeContentProvider {
    /**
     * Constructor
     * 
     * @param neoProvider neoServiceProvider for this ContentProvider
     */
    public DriveTreeContentProvider(NeoServiceProvider neoProvider) {
        super(neoProvider);
    }

    @Override
    public Object getParent(Object element) {
        // TODO optimize
        Transaction tx = neoServiceProvider.getService().beginTx();
        String type = "";
        try {
            if (element instanceof NeoNode) {
                Node node = ((NeoNode)element).getNode();
                type = NeoUtils.getNodeType(node, "");
            }
        } finally {
            tx.finish();
        }
        if (!INeoConstants.HEADER_M.equals(type)) {
            return super.getParent(element);
        } else {
            Node node = ((NeoNode)element).getNode();
            tx = neoServiceProvider.getService().beginTx();
            try {
                Node fileNode = node.traverse(Order.DEPTH_FIRST, StopEvaluator.END_OF_GRAPH, new ReturnableEvaluator() {

                    @Override
                    public boolean isReturnableNode(TraversalPosition currentPos) {
                        Node curNode = currentPos.currentNode();
                        return NeoUtils.isFileNode(curNode);
                    }
                }, NetworkRelationshipTypes.CHILD, Direction.INCOMING, GeoNeoRelationshipTypes.NEXT, Direction.INCOMING).iterator()
                        .next();
                return findParent(new DriveNeoNode(fileNode), node);
            } finally {
                tx.finish();
            }
        }
}

    /**
     * Finds parent node
     * 
     * @param parent parent root
     * @param node node
     * @return parent node
     */
    private NeoNode findParent(NeoNode parent, Node node) {
        NeoNode[] child = parent.getChildren();
        for (NeoNode neoNode : child) {
            if (neoNode.getNode().equals(node)) {
                return parent;
            }
            if (neoNode instanceof AggregatesNode) {
                return findParent(neoNode, node);
            }
        }
        return null;
    }

    @Override
    protected Root getRoot() {
        return new DriveRoot(neoServiceProvider);
    }
}
