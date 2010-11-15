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

import org.amanzi.awe.ui.AweUiPlugin;
import org.amanzi.awe.views.network.node_handlers.INeoNode;
import org.amanzi.awe.views.network.node_handlers.INodeHandler;
import org.amanzi.neo.services.DatasetService;
import org.amanzi.neo.services.NeoServiceFactory;
import org.amanzi.neo.services.enums.NodeTypes;
import org.amanzi.neo.services.utils.Utils;
import org.amanzi.neo.services.utils.Utils.FilterAND;
import org.neo4j.graphdb.Path;
import org.neo4j.graphdb.traversal.TraversalDescription;
import org.neo4j.helpers.Predicate;

/**
 * <p>
 * Network node handler
 * </p>
 * @author tsinkel_a
 * @since 1.0.0
 */
public class RootNetworkHandler implements INodeHandler {
    DatasetService service=NeoServiceFactory.getInstance().getDatasetService();
    @Override
    public boolean isParent(INeoNode node) {
        return service.isReferenceNode(node.getNode());
    }

    @Override
    public Iterable<INeoNode> getChildrens(INeoNode parent) {
        TraversalDescription td = Utils.getTDRootNodesOfProject(AweUiPlugin.getDefault().getUiService().getActiveProjectName(), getNetworkPredicate());
        return new TraverseWrapper(td.traverse(parent.getNode()).nodes());
    }


    /**
     * Gets the network predicate.
     *
     * @return the network predicate
     */
    private Predicate<Path> getNetworkPredicate() {
        return new Predicate<Path>() {
            
            @Override
            public boolean accept(Path paramT) {
                return NodeTypes.NETWORK.checkNode(paramT.endNode());
            }
        };
    }

    @Override
    public Iterable<INeoNode> getChildrensAfter(final INeoNode childNode) {
        FilterAND pred = new Utils.FilterAND();
        pred.addFilter(new Predicate<Path>(){

            @Override
            public boolean accept(Path paramT) {
                return paramT.endNode().getId()>childNode.getNode().getId();
            }
            
        });
        TraversalDescription td = Utils.getTDRootNodesOfProject(AweUiPlugin.getDefault().getUiService().getActiveProjectName(), pred);
        return new TraverseWrapper(td.traverse(childNode.getNode()).nodes());
    }
}
