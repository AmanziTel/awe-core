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
package org.amanzi.awe.awe.views.view.provider;

import java.util.ArrayList;
import java.util.List;

import org.amanzi.awe.views.network.proxy.NeoNode;
import org.amanzi.awe.views.network.proxy.Root;
import org.amanzi.neo.services.INeoConstants;
import org.amanzi.neo.services.NeoServiceFactory;
import org.amanzi.neo.services.enums.GeoNeoRelationshipTypes;
import org.amanzi.neo.services.enums.NetworkRelationshipTypes;
import org.amanzi.neo.services.model.IDataElement;
import org.amanzi.neo.services.model.INetworkModel;
import org.amanzi.neo.services.model.impl.DataElement;
import org.amanzi.neo.services.model.impl.NetworkModel;
import org.amanzi.neo.services.ui.NeoServiceProviderUi;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.Transaction;

/**
 * Content provider for NetworkTree
 * 
 * @author Lagutko_N
 * @since 1.0.0
 */

public class NetworkTreeContentProvider implements IStructuredContentProvider, ITreeContentProvider {
    
    /*
     * NeoServiceProvider
     */
    
    protected NeoServiceProviderUi neoServiceProvider;
    
    /**
     * Constructor of ContentProvider
     * 
     * @param neoProvider neoServiceProvider for this ContentProvider
     */
    
    public NetworkTreeContentProvider(NeoServiceProviderUi neoProvider) {
        this.neoServiceProvider = neoProvider;
    }
    
    public Object[] getElements(Object inputElement) {
        return new Object[] {getRoot()};
    }

    /**
     *
     * @return
     */
    protected Root getRoot() {
        return new Root(neoServiceProvider);
    }

    public void dispose() {
    }

    public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
    }

    public Object[] getChildren(Object parentElement) {
//        List<INetworkModel> networkModels = NetworkModel.findAllNetworkModels();
//        List<Node> nodes = new ArrayList<Node>();
//        for (INetworkModel nModel : networkModels) {
//        	nodes.add(nModel.getRootNode());
//        }
//        Iterable<IDataElement> childrens =
//        		networkModels.get(0).getChildren(new DataElement(((NeoNode)parentElement).getNode()));
//        ArrayList<NeoNode> neoNodes = new ArrayList<NeoNode>();
//        int number = 0;
//        for (IDataElement element : childrens) {
//        	neoNodes.add(new NeoNode(((DataElement)element).getNode(), number++));
//        }
//        
//        NeoNode[] neoNodesArray = new NeoNode[neoNodes.size()];
//        number = 0;
//        for (NeoNode neoNode : neoNodes) {
//        	neoNodesArray[number++] = neoNode;
//        }
//        return neoNodesArray;
        if (parentElement instanceof NeoNode) {
            return ((NeoNode)parentElement).getChildren();
        }
        
        return new Object[0];       
    }
   
    public Object getParent(Object element) {
        Transaction tx = neoServiceProvider.getService().beginTx();
        try {
            if (element instanceof NeoNode) {
                NeoNode nodeElem = (NeoNode)element;
                int curNumber = nodeElem.getNumber();
                if(curNumber>NeoNode.MAX_CHILDREN_COUNT){
                    return null;
                }
                Node node = nodeElem.getNode();                
                Node referenceNode = neoServiceProvider.getService().getReferenceNode();
                if (node.equals(referenceNode)) {
                    return null;
                } else {
                    Root root = getRoot();
//                    NetworkModel.findAllNetworkNodes();
                    NeoNode[] rootChildren = root.getChildren();
                    for (NeoNode neoNode : rootChildren) {
                        if (neoNode.getNode().equals(node)) {
                            return root;
                        }
                    }
                    Iterable<Relationship> relationships = node
                            .getRelationships(NetworkRelationshipTypes.CHILD, Direction.INCOMING);
                    for (Relationship relation : relationships) {
                        Node parentNode = relation.getOtherNode(node);
                        if (parentNode.hasProperty(INeoConstants.PROPERTY_TYPE_NAME) || parentNode.equals(referenceNode)) {
                            return new NeoNode(parentNode,curNumber+1);
                        }
                    }
                    relationships = node.getRelationships(GeoNeoRelationshipTypes.NEXT, Direction.INCOMING);
                    for (Relationship relation : relationships) {
                        Node parentNode = relation.getOtherNode(node);
                        if (parentNode.hasProperty(INeoConstants.PROPERTY_TYPE_NAME) || parentNode.equals(referenceNode)) {
                            return new NeoNode(parentNode,curNumber+1);
                        }
                    }
                }
        }
        return null;
        } finally {
            tx.success();
            tx.finish();
        }
    }

    public boolean hasChildren(Object element) {        
        if (element instanceof NeoNode) {
            return ((NeoNode)element).hasChildren();
        }
        return false;
    }

}
