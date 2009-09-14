package org.amanzi.awe.awe.views.view.provider;

import java.util.Iterator;

import org.amanzi.awe.views.network.proxy.NeoNode;
import org.amanzi.awe.views.network.proxy.Root;
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
 * @since 1.1.0
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
            if (node.equals(neoServiceProvider.getService().getReferenceNode())) {
                return null;
            } else {
                Root root = getRoot();
                NeoNode[] rootChildren = root.getChildren();
                for (NeoNode neoNode : rootChildren) {
                    if (neoNode.getNode().equals(node)) {
                        return root;
                    }
                }
                Iterator<Relationship> relationships = node.getRelationships(NetworkRelationshipTypes.CHILD, Direction.INCOMING)
                        .iterator();
                if (!relationships.hasNext()) {
                    relationships = node.getRelationships(GeoNeoRelationshipTypes.NEXT, Direction.INCOMING).iterator();
                    if (!relationships.hasNext()) {
                        return null;
                    }
                }
                return new NeoNode(relationships.next().getStartNode());
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
