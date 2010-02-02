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
import org.amanzi.neo.core.service.NeoServiceProvider;
import org.amanzi.neo.core.utils.NeoUtils;
import org.neo4j.api.core.Node;
import org.neo4j.api.core.Transaction;

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
        Transaction tx = neoServiceProvider.getService().beginTx();
        try {
            if (element instanceof NeoNode) {
                Node node = ((NeoNode)element).getNode();
                if (NeoUtils.isDatasetNode(node)) {
                    return getRoot();
                } else {
                    return findParent(new DriveNeoNode(NeoUtils.getParent(null, node)), node);
                }
            } else {
                return super.getParent(element);
            }
        } finally {
            tx.finish();
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
