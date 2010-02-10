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
import java.util.HashMap;
import java.util.Iterator;

import org.amanzi.neo.core.INeoConstants;
import org.amanzi.neo.core.enums.NodeTypes;
import org.amanzi.neo.core.service.NeoServiceProvider;
import org.amanzi.neo.core.utils.Pair;
import org.amanzi.neo.index.PropertyIndex;
import org.amanzi.neo.index.hilbert.map.HilbertSqaud;
import org.neo4j.api.core.Direction;
import org.neo4j.api.core.NeoService;
import org.neo4j.api.core.Node;
import org.neo4j.api.core.Relationship;
import org.neo4j.api.core.RelationshipType;

/**
 * Node of Hilbert-curbes-based index
 * 
 * @author Lagutko_N
 * @since 1.0.0
 */
public class HilbertIndexNode {
    
	/*
	 * Type of Node
	 */
    public static final String HILBERT_INDEX_TYPE = NodeTypes.HILBERT_INDEX_TYPE.getId();//TODO: node types usages
    
    /*
     * Name of 'Level' property
     */
    public static final String HILBERT_INDEX_LEVEL = "hilbert_index_level";
    
    /*
     * Name of 'Order' property
     */
    public static final String HILBERT_INDEX_ORDER = "hilbert_index_order";
    
    /*
     * Name of 'Is last index?' property
     */
    public static final String IS_LAST_INDEX = "is_last_index";
    
    /*
     * NeoService
     */
    public static NeoService neoService = NeoServiceProvider.getProvider().getService();
    
    /*
     * Level of this index
     */
    private int level;
    
    /*
     * Order of index
     */
    private int order;
    
    /*
     * Name of Index
     */
    private String indexName;
    
    /*
     * Node for this Index
     */
    private Node indexNode;
    
    /*
     * Cached index nodes
     */
    private ArrayList<HashMap<String, HilbertIndexNode>> nodeList = new ArrayList<HashMap<String, HilbertIndexNode>>();
    
    /*
     * List of available RelationshipTypes
     */
    private ArrayList<RelationshipType> hilbertRelationshipTypes;
    
    /**
     * Creates a Index with level 1
     * 
     * @param referencedNode root node of this index
     * @param indexName name of index
     * @param order order of index
     */
    protected HilbertIndexNode(Node referencedNode, String indexName, int order) {
        this(referencedNode, indexName, order, 1);
        
        initialize();
    }
    
    /**
     * Creates a node of Index
     */
    private void createIndexNode(boolean isLastIndex) {
    	indexNode = neoService.createNode();
        indexNode.setProperty(INeoConstants.PROPERTY_TYPE_NAME, HILBERT_INDEX_TYPE);
        indexNode.setProperty(INeoConstants.PROPERTY_NAME_NAME, indexName);
        indexNode.setProperty(HILBERT_INDEX_LEVEL, level);
        indexNode.setProperty(HILBERT_INDEX_ORDER, order);
        indexNode.setProperty(IS_LAST_INDEX, isLastIndex);
        
        initialize();
    }
    
    /**
     * Creates a Index Node and store it in database
     * 
     * @param referencedNode root node of Index
     * @param indexName name of index
     * @param order order of index
     * @param level level of index
     */
    protected HilbertIndexNode(Node referencedNode, String indexName, int order, int level) {
        this.level = level;
        this.order = order;
        this.indexName = indexName;
        createIndexNode(true);
        
        nodeList = new ArrayList<HashMap<String, HilbertIndexNode>>(level - 1);
        for (int i = 0; i < level; i++) {
            nodeList.add(new HashMap<String, HilbertIndexNode>());
        }
        
        referencedNode.createRelationshipTo(indexNode, PropertyIndex.NeoIndexRelationshipTypes.INDEX);
    }
    
    /**
     * Creates underlying Index
     * 
     * @param highLevelNode node of Index of highly level
     * @param relationshipType type of relationship to new node
     */
    protected HilbertIndexNode(HilbertIndexNode highLevelNode, RelationshipType relationshipType) {
        this.level = highLevelNode.getLevel() - 1;
        this.order = highLevelNode.getOrder();
        this.indexName = highLevelNode.indexName;
        
        createIndexNode(false);
        
        nodeList = new ArrayList<HashMap<String, HilbertIndexNode>>(level - 1);
        for (int i = 0; i < level; i++) {
            nodeList.add(new HashMap<String, HilbertIndexNode>());
        }
        
        highLevelNode.indexNode.createRelationshipTo(indexNode, relationshipType);
    }

    /**
     * Creates Index from Node
     * 
     * @param node node of index
     */
    protected HilbertIndexNode(Node node) {
        indexNode = node;
        level = (Integer)node.getProperty(HILBERT_INDEX_LEVEL);
        order = (Integer)node.getProperty(HILBERT_INDEX_ORDER);
        indexName = (String)node.getProperty(INeoConstants.PROPERTY_NAME_NAME);
        
        nodeList = new ArrayList<HashMap<String, HilbertIndexNode>>(level - 1);
        for (int i = 0; i < level; i++) {
            nodeList.add(new HashMap<String, HilbertIndexNode>());
        }
        
        initialize();
    }
    
    /**
     * Initializes Index node
     */
    private void initialize() {
    	if (hilbertRelationshipTypes == null) {
    		hilbertRelationshipTypes = new ArrayList<RelationshipType>();
    		for (int i = 0; i < Math.pow(2, 2 * order); i++) {
    			final String name = Integer.toString(i);
    			RelationshipType type = new RelationshipType(){
                
    				@Override
    				public String name() {
    					return name;
    				}
    			};
    			hilbertRelationshipTypes.add(type);
    		}
    	}
    }
    
    /**
     * Searches for an index node in database. Creates it if node cannot be found.
     * 
     * @param referencedNode root node of index
     * @param indexName name of index
     * @param order order of index
     * @return Index Node
     */
    public static HilbertIndexNode getFromReferencedNode(Node referencedNode, String indexName, int order) {
        if (referencedNode == null) {
            referencedNode = neoService.getReferenceNode();
        }
        
        for (Relationship relationship : referencedNode.getRelationships(PropertyIndex.NeoIndexRelationshipTypes.INDEX, Direction.OUTGOING)) {
            Node indexNode = relationship.getEndNode();
            
            if (indexNode.getProperty(INeoConstants.PROPERTY_TYPE_NAME).equals(HILBERT_INDEX_TYPE) &&
                indexNode.getProperty(INeoConstants.PROPERTY_NAME_NAME).equals(indexName)) {
                return new HilbertIndexNode(indexNode);
            }
        }
        
        return new HilbertIndexNode(referencedNode, indexName, order);
    }
    
    /**
     * Returns level of Index
     * 
     * @return level of index
     */
    public int getLevel() {
        return level;
    }
    
    /**
     * Returns order of Index
     * 
     * @return order of index
     */
    public int getOrder() {
        return order;
    }

    /**
     * Returns Index for given coordinates
     * 
     * @param root root index
     * @param coordinate coordinate
     * @return index 
     */
    public static HilbertIndexNode getHilbertIndex(HilbertIndexNode root, Pair<Integer, Integer> coordinate) {
        int max = (int)Math.pow((double)(1 << root.getOrder()), (double)root.getLevel());
        
        int x = coordinate.getLeft();
        int y = coordinate.getRight();
        
        if ((x < max) && (y < max)) {
            return root;
        }
        else {
            return createHighLevelIndex(root, new Pair<Integer, Integer>(x / max, y / max));
        }
    }
    
    /**
     * Creates an index for next level
     * 
     * @param currentLevelIndex 
     * @param coordinate
     * @return
     */
    private static HilbertIndexNode createHighLevelIndex(HilbertIndexNode currentLevelIndex, Pair<Integer, Integer> coordinate) {
        Relationship relationship = currentLevelIndex.indexNode.getSingleRelationship(PropertyIndex.NeoIndexRelationshipTypes.INDEX, Direction.INCOMING);
        
        HilbertIndexNode highLevel = new HilbertIndexNode(relationship.getStartNode(), 
                                                          currentLevelIndex.indexName, 
                                                          currentLevelIndex.order,
                                                          currentLevelIndex.level + 1);
        currentLevelIndex.indexNode.setProperty(IS_LAST_INDEX, false);
        highLevel.indexNode.createRelationshipTo(currentLevelIndex.indexNode, currentLevelIndex.getRelationshipType(coordinate.getLeft(), coordinate.getRight(), highLevel.level));
        relationship.delete();
        
        return getHilbertIndex(highLevel, coordinate);
    }
    
    /**
     * Searches for indexed Node
     *
     * @param x coordinate X
     * @param y coordinate Y
     * @return indexed Node or null if not exists
     */
    private Node getIndexedNode(int x, int y) {
        RelationshipType relationshipType = getRelationshipType(x, y, level);
        
        Iterator<Relationship> relationships = indexNode.getRelationships(relationshipType, Direction.OUTGOING).iterator();
        
        if (relationships.hasNext()) {
            return relationships.next().getEndNode();
        }
        
        return null;
    }
    
    /**
     * Deletes Index Node by coordinates 
     * 
     * @param x coordinate X
     * @param y coordinate Y
     */
    private void removeIndexedNode(int x, int y) {
    	RelationshipType relationshipType = getRelationshipType(x, y, level);
        
        Iterator<Relationship> relationships = indexNode.getRelationships(relationshipType, Direction.OUTGOING).iterator();
        
        if (relationships.hasNext()) {
            relationships.next().delete();
        }
    }
    
    /**
     * Searches for the Index node on the underlying level
     *
     * @param x coordinate X
     * @param y coordinate Y
     * @param createIfNotExist is need to be created?
     * @return founded Index node or null
     */
    private HilbertIndexNode getUnderlyingIndex(int x, int y, boolean createIfNotExist) {
        RelationshipType relationshipType = getRelationshipType(x, y, level);
        
        HashMap<String, HilbertIndexNode> nodeMap = nodeList.get(level - 1);
        if (nodeMap == null) {
            nodeMap = new HashMap<String, HilbertIndexNode>();
        }
        HilbertIndexNode underlyingNode = nodeMap.get(relationshipType.name());
        if (underlyingNode == null) {
            Iterator<Relationship> relationships = indexNode.getRelationships(relationshipType, Direction.OUTGOING).iterator();
            
            if (relationships.hasNext()) {
                underlyingNode = new HilbertIndexNode(relationships.next().getEndNode());
            }
            else {
                if (createIfNotExist) {
                    underlyingNode = new HilbertIndexNode(this, relationshipType);
                }
            }
            
            if (underlyingNode != null) {
                for (int i = 0; i < level; i++) {
                    if (nodeList.get(i) != null) {
                        nodeList.get(i).clear();
                    }
                }
                nodeList.get(level - 1).put(relationshipType.name(), underlyingNode);
            }
        }
        
        return underlyingNode;
    }
    
    /**
     * Returns Node by given coordinates
     * 
     * @param coordinate coordinate 
     * @return node
     */
    public Node getIndexedNode(Pair<Integer, Integer> coordinate) {
        int x = coordinate.getLeft();
        int y = coordinate.getRight();
        
        int max = (int)Math.pow((double)(1 << order), (double)level);
        
        if ((x >= max) || (y >= max)) {
            return null;
        }
        
        HilbertIndexNode currentIndexNode = this;
        
        while (currentIndexNode.getLevel() > 1) {
            currentIndexNode = currentIndexNode.getUnderlyingIndex(x, y, false);
            
            if (currentIndexNode == null) {
                return null;
            }
        }
        
        return currentIndexNode.getIndexedNode(x, y);
    }
    
    /**
     * Removes Indexed Node by coordinates 
     * 
     * @param coordinate coordinates of Index to remove
     */
    public void removeIndexNode(Pair<Integer, Integer> coordinate) {
    	int x = coordinate.getLeft();
        int y = coordinate.getRight();
        
        int max = (int)Math.pow((double)(1 << order), (double)level);
        
        if ((x >= max) || (y >= max)) {
            return;
        }
        
        HilbertIndexNode currentIndexNode = this;
        
        while (currentIndexNode.getLevel() > 1) {
            currentIndexNode = currentIndexNode.getUnderlyingIndex(x, y, false);
            
            if (currentIndexNode == null) {
                return;
            }
        }
        
        currentIndexNode.removeIndexedNode(x, y);
    }

    /**
     * Creates an index for node by given coordinates
     * 
     * @param node node to index
     * @param coordinate coordinates of node
     */
    public void index(Node node, Pair<Integer, Integer> coordinate) {
        int x = coordinate.getLeft();
        int y = coordinate.getRight();
        
        HilbertIndexNode currentIndexNode = this;
        
        while (currentIndexNode.getLevel() > 1) {
            currentIndexNode = currentIndexNode.getUnderlyingIndex(x, y, true);
        };
        
        Node indexedNode = currentIndexNode.getIndexedNode(x, y);
        RelationshipType relationshipType = getRelationshipType(x, y, currentIndexNode.getLevel());
        if (indexedNode != null) {
            //if there already exists a node than delete it
            indexedNode.getSingleRelationship(relationshipType, Direction.INCOMING).delete();
        }
        
        currentIndexNode.indexNode.createRelationshipTo(node, relationshipType);        
    }
    
    /**
     * Calculates type of Relationship
     * 
     * @param x coordinate X
     * @param y coordinate Y
     * @param currentLevel level for searching
     * @return type of Relationship
     */
    private RelationshipType getRelationshipType(int x, int y, int currentLevel) {
        final Integer position = HilbertSqaud.getHilbertPosition(x, y, currentLevel * order, (currentLevel - 1) * order);
        
        return hilbertRelationshipTypes.get(position);
    }

    /**
     * Is it high level index
     *
     * @return is it high level index
     */
    public boolean isLastIndex() {
        return (Boolean)indexNode.getProperty(IS_LAST_INDEX);
    }
}
