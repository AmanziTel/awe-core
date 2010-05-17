package org.amanzi.awe.gps;

import java.util.ArrayList;
import java.util.Iterator;

import org.amanzi.neo.core.INeoConstants;
import org.amanzi.neo.core.enums.CorrelationRelationshipTypes;
import org.amanzi.neo.core.enums.GeoNeoRelationshipTypes;
import org.amanzi.neo.core.enums.NetworkRelationshipTypes;
import org.amanzi.neo.core.enums.NodeTypes;
import org.amanzi.neo.core.service.NeoServiceProvider;
import org.amanzi.neo.core.utils.NeoUtils;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.ReturnableEvaluator;
import org.neo4j.graphdb.StopEvaluator;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.TraversalPosition;
import org.neo4j.graphdb.Traverser.Order;
import org.neo4j.index.lucene.LuceneIndexService;

public class GPSCorrelator {
    
    private class SearchRequest {
        
        private String datasetName;
        
        private String luceneIndexName;
        
        public SearchRequest(String datasetName) {
            this.datasetName = datasetName;
            
            this.luceneIndexName = NeoUtils.getLuceneIndexKeyByProperty(datasetName, INeoConstants.SECTOR_ID_PROPERTIES, NodeTypes.M);
        }
        
        public String getDatasetName() {
            return datasetName;
        }
        
        public String getSearchIndex() {
            return luceneIndexName;
        }
    }
    
    /*
	 * Neo Service
	 */
	private GraphDatabaseService neoService;
	
	private ArrayList<SearchRequest> searchRequests;
	
	/*
	 * Node of Network to work with
	 */
	private Node networkNode;
	
	private String networkName;
	
	private LuceneIndexService luceneService;
	
	private String luceneIndexKey;
	
	private IProgressMonitor monitor;
	
	
	/**
	 * Creates a Correlator based on GPS data
	 * 
	 * @param gsmDatasetNode Dataset Node of GPS data
	 */
	public GPSCorrelator(Node networkNode, IProgressMonitor monitor) {
	    neoService = NeoServiceProvider.getProvider().getService();
	    luceneService = NeoServiceProvider.getProvider().getIndexService();
	    
	    this.networkNode = networkNode;
		this.networkName = NeoUtils.getNodeName(networkNode, neoService);
		
		luceneIndexKey = networkName + "@Correlation";
		
		if (monitor == null) {
		    this.monitor = new NullProgressMonitor();
		}
		else {
		    this.monitor = monitor;
		}
	}
	
	public void correlate(Node gsmLocationNode, Node ossCountersNode, Node gpehDataNode) {
	    String gsmLocationsName = gsmLocationNode != null ? NeoUtils.getNodeName(gsmLocationNode, neoService) : null;
	    String ossCounertsName = ossCountersNode != null ? NeoUtils.getNodeName(ossCountersNode, neoService) : null;
	    String gpehDataName = gpehDataNode != null ? NeoUtils.getNodeName(gpehDataNode, neoService) : null;
	    
	    Node rootCorrelationNode = getRootCorrelationNode();
	    
	    searchRequests = new ArrayList<SearchRequest>();
	    if (gsmLocationNode != null) {
	        searchRequests.add(new SearchRequest(gsmLocationsName));
	        updateCorrelation(rootCorrelationNode, gsmLocationNode);	        
	    }
	    if (ossCountersNode != null) {
            searchRequests.add(new SearchRequest(ossCounertsName));
            updateCorrelation(rootCorrelationNode, ossCountersNode);
        }
	    if (gpehDataNode != null) {
            searchRequests.add(new SearchRequest(gpehDataName));
            updateCorrelation(rootCorrelationNode, gpehDataNode);
        }
	    
	    if (searchRequests.isEmpty()) {
	        return;
	    }
	    
	    Transaction tx = neoService.beginTx(); 
	    
	    System.out.println("Begin correlation");
	    
	    int counter = 0;
	    
	    try {
	        Node network = networkNode.getSingleRelationship(GeoNeoRelationshipTypes.NEXT, Direction.OUTGOING).getEndNode();
	        long sectorCount = (Long)network.getProperty("sector_count");
	        
	        monitor.beginTask("Correlation", (int)sectorCount);
	        
	        for (Node sector : getNetworkIterator()) {
	        	String sectorId = null;
	        	if (sector.hasProperty(INeoConstants.PROPERTY_SECTOR_CI)) {
	        		Integer iSectorId = (Integer)sector.getProperty(INeoConstants.PROPERTY_SECTOR_CI);
	        		sectorId = iSectorId.toString();
	        	}
	        	else {
	        		sectorId = (String)sector.getProperty(INeoConstants.PROPERTY_NAME_NAME);
	        	}
	            
	            Node correlationNode = null;
	            
	            for (SearchRequest request : searchRequests) {
	                Iterator<Node> nodes = findNodesToCorrelate(request, sectorId);
	                
	                if (nodes.hasNext() && (correlationNode == null)) {
	                    correlationNode = getCorrelationNode(rootCorrelationNode, sectorId); 
	                }
	                
	                while (nodes.hasNext()) {
	                    correlateNodes(sector, correlationNode, nodes.next(), request.getDatasetName());
	                }
	            }
	            
	            counter++;
	            if (counter % 5000 == 0) {
	                tx.success();
	                tx.finish();
	            
	                tx = neoService.beginTx();
	                counter = 0;
	            }
	            
	            monitor.worked(1);
	        }
	    }
	    catch (Exception e) {
	        e.printStackTrace();
	    }
	    finally {
	        tx.success();
	        tx.finish();
	    }
	    
	    System.out.println("Finish correlation");
	}
	
	private Node getCorrelationNode(Node rootCorrelationNode, String sectorId) {
	    Node node = luceneService.getSingleNode(luceneIndexKey, sectorId);
	    
	    if (node == null) {
	        node = neoService.createNode();
	        
	        node.setProperty(INeoConstants.SECTOR_ID_PROPERTIES, sectorId);
	        luceneService.index(node, luceneIndexKey, sectorId);
	        rootCorrelationNode.createRelationshipTo(node, GeoNeoRelationshipTypes.CHILD);
	    }
	    
	    return node;
	}
	
	private Iterator<Node> findNodesToCorrelate(SearchRequest request, String sectorId) {
	    return luceneService.getNodes(request.getSearchIndex(), sectorId).iterator();
	}
	
	private void correlateNodes(Node sectorNode, Node correlationNode, Node correlatedNode, String correlationType) {
		boolean create = !correlationNode.hasRelationship(CorrelationRelationshipTypes.CORRELATION, Direction.OUTGOING);
		
		Relationship link;
		if (create) {
			link = correlationNode.createRelationshipTo(sectorNode, CorrelationRelationshipTypes.CORRELATION);
			link.setProperty(INeoConstants.NETWORK_GIS_NAME, networkName);
		}
		link = correlationNode.createRelationshipTo(sectorNode, NetworkRelationshipTypes.SECTOR);
		link.setProperty(INeoConstants.NETWORK_GIS_NAME, networkName);
	    
	    Relationship locationLink = correlatedNode.getSingleRelationship(GeoNeoRelationshipTypes.LOCATION, Direction.OUTGOING);
	    if (locationLink != null) {
	    	correlatedNode = locationLink.getEndNode();
	    }
	    
	    link = correlationNode.createRelationshipTo(correlatedNode, CorrelationRelationshipTypes.CORRELATED);
	    link.setProperty(INeoConstants.NETWORK_GIS_NAME, correlationType);
	    
	    link = correlationNode.createRelationshipTo(correlatedNode, NetworkRelationshipTypes.DRIVE);
	    link.setProperty(INeoConstants.NETWORK_GIS_NAME, correlationType);
	}
	
	private Iterable<Node> getNetworkIterator() {
	    return networkNode.traverse(Order.DEPTH_FIRST, StopEvaluator.END_OF_GRAPH, 
	                                new ReturnableEvaluator() {
                                        
                                        @Override
                                        public boolean isReturnableNode(TraversalPosition currentPos) {
                                            return NeoUtils.getNodeType(currentPos.currentNode()).equals(NodeTypes.SECTOR.getId());
                                        }
                                    },
                                    GeoNeoRelationshipTypes.CHILD, Direction.OUTGOING,
                                    GeoNeoRelationshipTypes.NEXT, Direction.OUTGOING);
	}
	
	private Node getRootCorrelationNode() {
	    Transaction tx = neoService.beginTx();
	    
	    try {	    
	        Relationship link = networkNode.getSingleRelationship(CorrelationRelationshipTypes.CORRELATION, Direction.OUTGOING);
	    
	        if (link != null) {
	            return link.getEndNode(); 
	        }
	    
	        Node result = neoService.createNode();
	        result.setProperty(INeoConstants.PROPERTY_TYPE_NAME, NodeTypes.ROOT_SECTOR_DRIVE.getId());
	    
	        networkNode.createRelationshipTo(result, CorrelationRelationshipTypes.CORRELATION);
	    
	        return result;
	    }
	    catch (Exception e) {
	        e.printStackTrace();
	        return null;
	    }
	    finally {
	        tx.success();
	        tx.finish();
	    }
	}
	
	private void updateCorrelation(Node rootCorrelationNode, Node datasetNode) {
	    Transaction tx = neoService.beginTx();
	    
	    try {
	        Iterable<Relationship> links = rootCorrelationNode.getRelationships(CorrelationRelationshipTypes.CORRELATED, Direction.INCOMING);
	    
	        boolean createNew = true;
	        for (Relationship link : links) {
	            if (link.getStartNode().equals(datasetNode)) {
	                createNew = false;
	                break;
	            }
	        }
	    
	        if (createNew) {
	            datasetNode.createRelationshipTo(rootCorrelationNode, CorrelationRelationshipTypes.CORRELATED);
	        }
	    }
	    catch (Exception e) {
	        e.printStackTrace();
	    }
	    finally {
	        tx.success();
	        tx.finish();
	    }
	}
	
}

