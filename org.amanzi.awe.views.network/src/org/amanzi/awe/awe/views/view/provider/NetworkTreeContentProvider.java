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

import org.amanzi.awe.views.network.proxy.NeoNode;
import org.amanzi.awe.views.network.proxy.Root;
import org.amanzi.neo.core.INeoConstants;
import org.amanzi.neo.core.enums.GeoNeoRelationshipTypes;
import org.amanzi.neo.core.enums.NetworkRelationshipTypes;
import org.amanzi.neo.core.service.NeoServiceProvider;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.neo4j.api.core.Direction;
import org.neo4j.api.core.Node;
import org.neo4j.api.core.Relationship;
import org.neo4j.api.core.Transaction;

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
    
    protected NeoServiceProvider neoServiceProvider;
    
    /**
     * Constructor of ContentProvider
     * 
     * @param neoProvider neoServiceProvider for this ContentProvider
     */
    
    public NetworkTreeContentProvider(NeoServiceProvider neoProvider) {
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
        if (parentElement instanceof NeoNode) {
            return ((NeoNode)parentElement).getChildren();
        }
        
        return new Object[0];
    }
   
    public Object getParent(Object element) {
        Transaction tx = neoServiceProvider.getService().beginTx();
        try {
            if (element instanceof NeoNode) {
                Node node = ((NeoNode)element).getNode();
                Node referenceNode = neoServiceProvider.getService().getReferenceNode();
                if (node.equals(referenceNode)) {
                    return null;
                } else {
                    Root root = getRoot();
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
                            return new NeoNode(parentNode);
                        }
                    }
                    relationships = node.getRelationships(GeoNeoRelationshipTypes.NEXT, Direction.INCOMING);
                    for (Relationship relation : relationships) {
                        Node parentNode = relation.getOtherNode(node);
                        if (parentNode.hasProperty(INeoConstants.PROPERTY_TYPE_NAME) || parentNode.equals(referenceNode)) {
                            return new NeoNode(parentNode);
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
