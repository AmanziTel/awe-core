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
import org.amanzi.neo.services.ui.NeoServiceProviderUi;
import org.amanzi.neo.services.ui.NeoUtils;
import org.amanzi.neo.services.ui.utils.DistributionSelectionNode;
import org.amanzi.neo.services.ui.utils.StatisticSelectionNode;
import org.amanzi.neo.services.utils.Pair;
import org.eclipse.core.runtime.IAdaptable;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;

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
    public DriveTreeContentProvider(NeoServiceProviderUi neoProvider) {
        super(neoProvider);
    }

    @Override
    public Object getParent(Object element) {
        Transaction tx = neoServiceProvider.getService().beginTx();
        try {
            if (element instanceof Node) {
                element = new DriveNeoNode((Node)element, 0);
            }
            if (element instanceof IAdaptable) {
                if (element instanceof StatisticSelectionNode) {
                    IAdaptable adapter = (IAdaptable)element;
                    Pair< ? , ? > pair = (Pair< ? , ? >)adapter.getAdapter(Pair.class);
                    if (pair.getLeft() instanceof Node && pair.getRight() instanceof Node) {
                        StatisticsNeoNode elem = new StatisticsNeoNode((Node)pair.getLeft(), 0);
                        return elem.getParent();
                    }
                } else if(element instanceof DistributionSelectionNode){
                    Node selected = ((DistributionSelectionNode)element).getSelected();
                    DistributeNeoNode neoNode = new DistributeNeoNode(selected, 0);
                    return neoNode.getParent();
                }
            }

            if (element instanceof NeoNode) {
                NeoNode neoNode = (NeoNode)element;
                int curNum = neoNode.getNumber();
                if(curNum>NeoNode.MAX_CHILDREN_COUNT){
                    return null;
                }
                Node node = neoNode.getNode();
                for (NeoNode child : getRoot().getChildren()) {
                    if (child.getNode().equals(node)) {
                        return getRoot();
                    }
                }
                if (element instanceof CallAnalyzisNeoNode) {
                    return ((CallAnalyzisNeoNode)element).getParent();
                }
                if (element instanceof DistributeNeoNode) {
                    return ((DistributeNeoNode)element).getParent();
                }
                if (element instanceof StatisticsNeoNode) {
                    return ((StatisticsNeoNode)element).getParent();
                }
                return findParent(new DriveNeoNode(NeoUtils.getParent(null, node),curNum+1), node);
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
