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

package org.amanzi.neo.index.hilbert;

import java.util.ArrayList;

import org.amanzi.neo.core.service.NeoServiceProvider;
import org.amanzi.neo.core.utils.Pair;
import org.neo4j.api.core.NeoService;
import org.neo4j.api.core.Node;


/**
 * Index that based on Hilbert curves
 * 
 * 
 * @author Lagutko_N
 * @since 1.0.0
 */
public class HilbertIndex {
    
	/*
	 * Order of Index
	 */
    private int hilbertSquadOrder;
    
    /*
     * Name of Node Property for X coordinate
     */
    private String xPropertyName;
    
    /*
     * Name of Node property for Y coordinate 
     */
    private String yPropertyName;
    
    /*
     * Name of Index
     */
    private String indexName;
    
    /*
     * Root Index Node 
     */
    private HilbertIndexNode root;
    
    /*
     * Nodes that should be indexed
     */
    private ArrayList<Node> nodesToIndex = new ArrayList<Node>();
    
    /**
     * Constructor
     * 
     * @param indexName name of index
     * @param hilbertSquadOrder order of index
     * @param xPropertyName name of property X
     * @param yPropertyName name of property Y
     */
    public HilbertIndex(String indexName, int hilbertSquadOrder, String xPropertyName, String yPropertyName) {
        this.hilbertSquadOrder = hilbertSquadOrder;
        this.indexName = indexName;
        this.xPropertyName = xPropertyName;
        this.yPropertyName = yPropertyName;
        
                
    }
    
    /**
     * Initialize an Index with given referenced Node
     * 
     * @param reference root node of indexes (if null than it's a referenced node of database)
     */
    public void initialize(Node reference) {
        NeoService service = NeoServiceProvider.getProvider().getService();
        if (reference == null) {
            reference = service.getReferenceNode();
        }
        
        root = HilbertIndexNode.getFromReferencedNode(reference, indexName, hilbertSquadOrder);        
    }
    
    /**
     * Add node for indexing
     * 
     * @param node node for indexing
     */
    public void addNode(Node node) {
        nodesToIndex.add(node);
    }
    
    /**
     * Index nodes
     * 
     */
    public void finishUp() {
        for (Node node : nodesToIndex) {
        	index(node);
        }
        nodesToIndex.clear();
    }
    
    /**
     * Index single node
     * 
     * @param node node for indexing
     */
    private void index(Node node) {
        Pair<Integer, Integer> coordinate = getCoordinate(node);
        
        boolean valid = true;
        if ((coordinate.getLeft() == null) ||
            (coordinate.getRight() == null)) {
            valid = false;
        }
        
        if (valid) {
            HilbertIndexNode index = HilbertIndexNode.getHilbertIndex(root, coordinate);
            if (!index.equals(root)) {
                root = index;
            }
            index.index(node, coordinate);            
        }
        
    }
    
    /**
     * Creates a Pair of coordinate for given node
     * 
     * @param node node for indexing
     * @return coordinates of this node
     */
    private Pair<Integer, Integer> getCoordinate(Node node) {
        Integer x = null;
        if (node.hasProperty(xPropertyName)) {
            x = (Integer)node.getProperty(xPropertyName);
        }
        
        Integer y = null;
        if (node.hasProperty(yPropertyName)) {
            y = (Integer)node.getProperty(yPropertyName);
        }
        
        return new Pair<Integer, Integer>(x, y);
    }
    
    /**
     * Searches for Node by given coordinates 
     * 
     * @param x x coordinate of Node
     * @param y y coordinate of Node
     * @return node by this coordinates
     */
    public Node find(int x, int y) {
        Pair<Integer, Integer> coordinate = new Pair<Integer, Integer>(x, y);
        
        return root.getIndexedNode(coordinate);
    }
	
}
