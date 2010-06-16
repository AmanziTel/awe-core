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

package org.amanzi.awe.gps;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.amanzi.neo.core.INeoConstants;
import org.amanzi.neo.core.enums.CorrelationRelationshipTypes;
import org.amanzi.neo.core.enums.GeoNeoRelationshipTypes;
import org.amanzi.neo.core.enums.NetworkRelationshipTypes;
import org.amanzi.neo.core.enums.NodeTypes;
import org.amanzi.neo.core.enums.SectorIdentificationType;
import org.amanzi.neo.core.service.NeoServiceProvider;
import org.amanzi.neo.core.utils.NeoUtils;
import org.apache.log4j.Logger;
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

/**
 * Class that creates correlation between Network Sectors and other data
 * 
 * @author Lagutko_N
 * @since 1.0.0
 */
public class GPSCorrelator {
    
    private class SearchRequest {
        
        private String datasetName;
        
        private String luceneIndexName;
        
        private SectorIdentificationType searchType;
        
        public SearchRequest(Node dataNode) {
            Transaction tx = neoService.beginTx();
            try {
                this.datasetName = NeoUtils.getNodeName(dataNode, neoService);
                this.searchType = SectorIdentificationType.valueOf((String)dataNode.getProperty(INeoConstants.SECTOR_ID_TYPE));
                this.luceneIndexName = NeoUtils.getLuceneIndexKeyByProperty(datasetName, INeoConstants.SECTOR_ID_PROPERTIES, NodeTypes.M);
            }
            catch (Exception e) {
                LOGGER.error(e);
            }
            finally {
                tx.success();
                tx.finish();
            }
        }
        
        public SectorIdentificationType getSearchType() {
            return searchType;
        }
        
        public String getDatasetName() {
            return datasetName;
        }
        
        public String getSearchIndex() {
            return luceneIndexName;
        }
    }
    
    private static final Logger LOGGER = Logger.getLogger(GPSCorrelator.class); 
    
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
	
	public void correlate(Set<Node> nodesForCorrelation) {
	    Node rootCorrelationNode = getRootCorrelationNode();
	    searchRequests = new ArrayList<SearchRequest>();
	    
	    for (Node dataNode : nodesForCorrelation) {
	        searchRequests.add(new SearchRequest(dataNode));
	        updateCorrelation(rootCorrelationNode, dataNode);
	    }
	    
	    Transaction tx = neoService.beginTx(); 
        
        int counter = 0;
        
        try {
            long sectorCount = (Long)networkNode.getProperty(INeoConstants.SECTOR_COUNT);
            
            monitor.beginTask("Correlation", (int)sectorCount);
            
            for (Node sector : getNetworkIterator()) {
                Node correlationNode = null;
                
                HashMap<SectorIdentificationType, String> searchValues = new HashMap<SectorIdentificationType, String>();
                
                for (SearchRequest request : searchRequests) {
                    String sectorId = searchValues.get(request.getSearchType());
                    if (sectorId == null) {
                        sectorId = sector.getProperty(request.getSearchType().getProperty()).toString();
                        searchValues.put(request.getSearchType(), sectorId);
                    }
                    
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
            LOGGER.error(e);
        }
        finally {
            tx.success();
            tx.finish();
        }
	}
	
	private Node getCorrelationNode(Node rootCorrelationNode, String sectorId) {
	    Node node = luceneService.getSingleNode(luceneIndexKey, sectorId);
	    
	    if (node == null) {
	        node = neoService.createNode();
	        
	        node.setProperty(INeoConstants.SECTOR_ID_PROPERTIES, sectorId);
	        node.setProperty(INeoConstants.PROPERTY_NAME_NAME, networkName);
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

