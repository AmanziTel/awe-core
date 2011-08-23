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

package org.amanzi.neo.services.networkselection;

import java.util.Iterator;

import org.amanzi.neo.services.INeoConstants;
import org.amanzi.neo.services.NeoServiceFactory;
import org.amanzi.neo.services.NetworkService;
import org.amanzi.neo.services.enums.INodeType;
import org.amanzi.neo.services.enums.NodeTypes;
import org.amanzi.neo.services.network.INetworkTraversableModel;
import org.amanzi.neo.services.utils.Utils;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.traversal.Evaluator;

/**
 * TODO Purpose of 
 * <p>
 *
 * </p>
 * @author gerzog
 * @since 1.0.0
 */
public class SelectionModel implements INetworkTraversableModel {
    
    /*
     * Name of Selection List
     */
    private String modelName;
    
    /*
     * Root node of Model
     */
    private Node rootNode;
    
    /*
     * NetworkService
     */
    private NetworkService networkService;
    
    private String indexKey;
    
    public SelectionModel(Node parentNode, String selectionListName) {
        init(parentNode, selectionListName, null);
    }
    
    public SelectionModel(Node parentNode, Node selectionRootNode) {
        init(parentNode, null, selectionRootNode);
    }
    
    private void init(Node parentNode, String selectionListName, Node selectionRootNode) {
        networkService = NeoServiceFactory.getInstance().getNetworkService();
        
        if (selectionRootNode != null) {
            this.rootNode = selectionRootNode;
            this.modelName = networkService.getNodeName(rootNode);
        }
        else if (selectionListName != null) {
            this.modelName = selectionListName;
            this.rootNode = networkService.getRootSelectionNode(parentNode, selectionListName);
        }
        
        this.indexKey = Utils.getLuceneIndexKeyByProperty(rootNode, INeoConstants.PROPERTY_NAME_NAME, NodeTypes.SECTOR);
    }
    
    public boolean addToSelection(Node sectorNode) {
        return networkService.addToSelection(rootNode, sectorNode, indexKey);
    }
    
    public Iterator<Node> getAllSelectedNodes() { 
        return networkService.getAllSelectedNodes(rootNode);
    }
    
    public String getName() {
        return modelName;
    }

    @Override
    public Iterable<Node> getAllElementsByType(Evaluator filter, INodeType ... nodeTypes) {
        return networkService.getSelectionListElementTraversal(filter, nodeTypes).traverse(rootNode).nodes();
    }
}
