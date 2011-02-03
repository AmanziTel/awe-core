package org.amanzi.neo.services.node2node.testing;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.HashMap;

import org.amanzi.neo.db.manager.DatabaseManager;
import org.amanzi.neo.services.INeoConstants;
import org.amanzi.neo.services.enums.GeoNeoRelationshipTypes;
import org.amanzi.neo.services.node2node.NodeToNodeRelationModel;
import org.amanzi.neo.services.node2node.NodeToNodeTypes;
import org.amanzi.neo.services.ui.NeoServiceProviderUi;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Path;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.traversal.Evaluation;
import org.neo4j.graphdb.traversal.Evaluator;
import org.neo4j.graphdb.traversal.Traverser;
import org.neo4j.kernel.EmbeddedGraphDatabase;

public class NodeToNodeRelationModelTesting {
    private static final String PATH_TO_BASE = "c://database";
	HashMap<String, Object> hashMap = null;
	Node servingNode = null, 
	    dependentNode = null,
	    rootModelNode = null;
	NodeToNodeRelationModel model = null;
	GraphDatabaseService graphDatabaseService = null;
	
	@Before
	public void createDatabase() {
	    graphDatabaseService = new EmbeddedGraphDatabase(PATH_TO_BASE);
        NeoServiceProviderUi.initProvider(graphDatabaseService, PATH_TO_BASE);
        DatabaseManager.setDatabaseAndIndexServices(graphDatabaseService, NeoServiceProviderUi.getProvider().getIndexService());
        
        Transaction tx = graphDatabaseService.beginTx();
        try{
    	    rootModelNode = graphDatabaseService.createNode();
    	    rootModelNode.setProperty("node2node", NodeToNodeTypes.NEIGHBOURS.toString());
    	    rootModelNode.setProperty(INeoConstants.PROPERTY_NAME_NAME, "rootNode");
    	    
            servingNode = graphDatabaseService.createNode();
            servingNode.setProperty(INeoConstants.PROPERTY_NAME_NAME, "servingNodeValue");
            dependentNode = graphDatabaseService.createNode();
            dependentNode.setProperty(INeoConstants.PROPERTY_NAME_NAME, "dependentNodeValue");
    	    
            model = new NodeToNodeRelationModel(rootModelNode);
            
            tx.success();
        }
        finally{
            tx.finish();
        }
	}
	
	@Test
	public void getRelationIsFixedRelationTest() {
	    Relationship relation = null;
	    
        Transaction tx = graphDatabaseService.beginTx();
        try{        
            servingNode.createRelationshipTo(dependentNode, GeoNeoRelationshipTypes.CHILD);
            relation = model.getRelation(servingNode, dependentNode);
            
            tx.success();
        }finally{
            tx.finish();
        }

		assertTrue(relation.getType().toString().equals("PROXYS"));
		assertTrue(model.getRelationCount() == 1);
	}
	
	@Test
    public void clear1IsProxyCountIsZeroTest() {
	    Transaction tx = graphDatabaseService.beginTx();
        try{ 
            model.clear(true);
            tx.success();
        }finally{
            tx.finish();
        }
	    
	    assertTrue(model.getProxyCount() == 0);
    }
	
	@Test
    public void clear2IsProxyCountIsZeroTest() {
        Transaction tx = graphDatabaseService.beginTx();
        try{ 
            model.clear(false);
            tx.success();
        }finally{
            tx.finish();
        }
        
        assertTrue(model.getProxyCount() == 0);
    }

	@Test
    public void getProxyCountIsFixedCountTest() {	    
        Transaction tx = graphDatabaseService.beginTx();
        try{        
            servingNode.createRelationshipTo(dependentNode, GeoNeoRelationshipTypes.CHILD);
            model.getRelation(servingNode, dependentNode);
            
            tx.success();
        }finally{
            tx.finish();
        }
        assertTrue(model.getProxyCount() == 2);
    }

	@Test
    public void getNameIsDefaultNameAndGetNetworkNodeIsNullTest() {
	    assertTrue(model.getName() != null);
	    assertTrue(model.getName().equals("rootNode"));
	    
	    Node networkNode = model.getNetworkNode();    
	    assertTrue(networkNode == null);
    }

	@Test
    public void getTypeNeighboursTypeTest() {
	    
	    assertTrue(model.getType() != null);
	    assertTrue(model.getType().toString().equals(NodeToNodeTypes.NEIGHBOURS.toString()));
    }

	@Test
    public void getNeighTraverserFixedCountNodesAndRelationsTestTest() {
	    Node servingNode2 = null, dependentNode2 = null;
        Transaction tx = graphDatabaseService.beginTx();
        try{        
            servingNode.createRelationshipTo(dependentNode, GeoNeoRelationshipTypes.CHILD);
            model.getRelation(servingNode, dependentNode);
            
            servingNode2 = graphDatabaseService.createNode();
            servingNode2.setProperty(INeoConstants.PROPERTY_NAME_NAME, "servingNodeValue2");
            dependentNode2 = graphDatabaseService.createNode();
            dependentNode2.setProperty(INeoConstants.PROPERTY_NAME_NAME, "dependentNodeValue2");
            
            model.getRelation(servingNode2, dependentNode2);
            tx.success();
        }finally{
            tx.finish();
        }

        int countOfNodes = 0, countOfRelationships = 0;
        Traverser traverser = model.getNeighTraverser(null);
        for (Node node : traverser.nodes()) {
            countOfNodes++;
        }
        for (Relationship rel : traverser.relationships()) {
            countOfRelationships++;
        }
        
        assertTrue(countOfNodes == 2);
        assertTrue(countOfRelationships == 2);
    }

	@Test
    public void getServTraverserFixedCountNodesAndRelationsTest() {
	    Node servingNode2 = null, dependentNode2 = null;
	    Transaction tx = graphDatabaseService.beginTx();
	    try{        
	        servingNode.createRelationshipTo(dependentNode, GeoNeoRelationshipTypes.CHILD);
	        model.getRelation(servingNode, dependentNode);
            
	        tx.success();
	    }finally{
	        tx.finish();
	    }

	    int countOfNodes = 0, countOfRelationships = 0;
	    Traverser traverser = model.getServTraverser(null);
	    for (Node node : traverser.nodes()) {
	        countOfNodes++;
	    }
        for (Relationship rel : traverser.relationships()) {
            countOfRelationships++;
        }

        assertTrue(countOfNodes == 1);
        assertTrue(countOfRelationships == 1);
    }	
	
	@After
	public void shutdownDatabase() {
	    NeoServiceProviderUi.getProvider().getIndexService().shutdown();
        graphDatabaseService.shutdown();
        
	    File file = new File(PATH_TO_BASE);
	    deleteFolder(file);
	}
	
	/**
	 * Delete all folder
	 *
	 * @param file File with path to delete
	 */
	private void deleteFolder(File file)
	{
	    if(!file.exists())
	        return;
	    if(file.isDirectory())
	    {
	        for(File f : file.listFiles())
	            deleteFolder(f);
	        file.delete();
	    }
	    else
	    {
	        file.delete();
	    }
	}
}
