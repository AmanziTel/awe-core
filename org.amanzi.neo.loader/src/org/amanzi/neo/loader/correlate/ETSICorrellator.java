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

package org.amanzi.neo.loader.correlate;

import java.io.IOException;
import java.util.Iterator;
import java.util.NoSuchElementException;

import org.amanzi.neo.core.INeoConstants;
import org.amanzi.neo.core.NeoCorePlugin;
import org.amanzi.neo.core.enums.DriveTypes;
import org.amanzi.neo.core.enums.GeoNeoRelationshipTypes;
import org.amanzi.neo.core.enums.NetworkRelationshipTypes;
import org.amanzi.neo.core.enums.SplashRelationshipTypes;
import org.amanzi.neo.core.service.NeoServiceProvider;
import org.amanzi.neo.core.utils.NeoUtils;
import org.amanzi.neo.index.MultiPropertyIndex;
import org.neo4j.api.core.Direction;
import org.neo4j.api.core.NeoService;
import org.neo4j.api.core.Node;
import org.neo4j.api.core.ReturnableEvaluator;
import org.neo4j.api.core.StopEvaluator;
import org.neo4j.api.core.Transaction;
import org.neo4j.api.core.TraversalPosition;
import org.neo4j.api.core.Traverser.Order;

/**
 * Correlator that is uses for correlating ETSI data with other Drive Data.
 * 
 * @author Lagutko_N
 * @since 1.0.0
 */
public class ETSICorrellator {
	
	/**
	 * Index for Timestamps
	 */
	MultiPropertyIndex<Long> timestampIndex;
	
	/**
	 * Neo Service
	 */
	private NeoService neoService;
	
	/**
	 * Constructor.
	 * 
	 * Initializes Neo Service
	 */
	public ETSICorrellator() {
		neoService = NeoServiceProvider.getProvider().getService();
	}
	
	private MultiPropertyIndex<Double> realDatasetLocationIndex, callDatasetLocationIndex;
	
	/**
	 * Starts correlating
	 *
	 * @param firstDataset name of first dataset
	 * @param secondDataset name of second dataset
	 */
	public void correlate(String firstDataset, String secondDataset) {
		Transaction tx = neoService.beginTx();
        NeoUtils.addTransactionLog(tx, Thread.currentThread(), "correlate");
		try {
			initializeIndex(secondDataset);
			
			Node realDatasetNode = getDatasetNode(firstDataset);
			String callDatasetName = getCallDatasetName(realDatasetNode, firstDataset);
			createNewIndexes(firstDataset, callDatasetName);
		
			MNodeIterator mIterator = getMNodeIterator(realDatasetNode);
		
			while (mIterator.hasNext()) {
				Node firstM = mIterator.next();
				
				Node callNode = determineCallNode(firstM);
				
				Node mpNode = determineNode((Long)firstM.getProperty(INeoConstants.PROPERTY_TIMESTAMP_NAME));
								
				correlateNodes(firstM, callNode, mpNode);
			}
			
			realDatasetLocationIndex.finishUp();
			callDatasetLocationIndex.finishUp();
			
			tx.success();
		}
		catch (Exception e) {
			tx.failure();
			NeoCorePlugin.error(null, e);
		}
		finally {
			tx.finish();
			NeoServiceProvider.getProvider().commit();
		}
	}
	
	/**
	 * Correlates two nodes
	 *
	 * @param first first node to correlate
	 * @param second second node to correlate
	 */
	private void correlateNodes(Node mNode, Node callNode, Node orignalMNode) {
	    if (orignalMNode == null) {
	        return;
	    }
	    try {
	        //add a real child to m node
	        Node originalMpNode = getMpNode(orignalMNode);
	        
	        Node newMpNode = copyMPNode(originalMpNode);
	        newMpNode.createRelationshipTo(mNode, GeoNeoRelationshipTypes.CHILD);
	        realDatasetLocationIndex.add(newMpNode);
	        
	        if (callNode != null) {
	            newMpNode = copyMPNode(originalMpNode);
	            newMpNode.createRelationshipTo(callNode, GeoNeoRelationshipTypes.VIRTUAL_CHILD);
	            callDatasetLocationIndex.add(newMpNode);
	        }
	    }
	    catch (IOException e) {
	        NeoCorePlugin.error(null, e);	        
	    }
	}
	
	private Node copyMPNode(Node originalMPNode) {
	    Node newMpNode = neoService.createNode();	    
	    for (String propertyName : originalMPNode.getPropertyKeys()) {
	        newMpNode.setProperty(propertyName, originalMPNode.getProperty(propertyName));
	    }
	    
	    return newMpNode;
	}
	
	private Node getMpNode(Node mNode) {
        Iterator<Node> mpNodes = mNode.traverse(Order.BREADTH_FIRST, StopEvaluator.DEPTH_ONE, ReturnableEvaluator.ALL_BUT_START_NODE, GeoNeoRelationshipTypes.CHILD, Direction.INCOMING).iterator();
        if (mpNodes.hasNext()) {
            return mpNodes.next();
        }
        return null;
    }
	
	/**
	 * Searches for the node indexed by timestamp
	 *
	 * @param timestamp timestamp to search
	 * @return node by this timestamp
	 */
	private Node determineNode(long timestamp) {
		long max = timestamp + 180000;
		long min = timestamp - 180000;
		
		Node result = null;
		long delta = 180000;
		
		for (Node node : timestampIndex.find(new Long[] {min}, new Long[] {max})) {
		    long currentTimestamp = (Long)node.getProperty(INeoConstants.PROPERTY_TIMESTAMP_NAME);
			long newDelta = Math.abs(currentTimestamp - timestamp);
			if (newDelta < delta) {
				result = node;
				delta = newDelta;
			}
		}
		
		return result;
	}
	
	/**
	 * Returns dataset node by it's name
	 *
	 * @param datasetName name of dataset
	 * @return dataset node
	 */
	private Node getDatasetNode(final String datasetName) {
		Node root = neoService.getReferenceNode();
		Iterator<Node> datasetIterator = root.traverse(Order.DEPTH_FIRST, new StopEvaluator() {

            @Override
            public boolean isStopNode(TraversalPosition currentPos) {
                return currentPos.depth() > 3;
            }
        }, new ReturnableEvaluator() {

            @Override
            public boolean isReturnableNode(TraversalPosition currentPos) {
                Node node = currentPos.currentNode();
                return node.hasProperty(INeoConstants.PROPERTY_TYPE_NAME)
                        && node.getProperty(INeoConstants.PROPERTY_TYPE_NAME).equals(INeoConstants.DATASET_TYPE_NAME) &&
                       node.hasProperty(INeoConstants.PROPERTY_NAME_NAME) &&
                       node.getProperty(INeoConstants.PROPERTY_NAME_NAME).equals(datasetName);
            }
        }, SplashRelationshipTypes.AWE_PROJECT, Direction.OUTGOING, NetworkRelationshipTypes.CHILD, Direction.OUTGOING).iterator();
		
		if (datasetIterator.hasNext()) {
			return datasetIterator.next();
		}
		return null;
	}
	
	/**
	 * Returns MpNode Iterator for dataset
	 *
	 * @param datasetName dataset node
	 * @return iterator
	 */
	private MNodeIterator getMNodeIterator(Node datasetNode) {
		if (datasetNode != null) {
			return new MNodeIterator(datasetNode);
		}
		return null;
	}
	
	/**
	 * Initialize Timestamp index for dataset
	 *
	 * @param datasetName name of dataset
	 */
	private void initializeIndex(String datasetName) {
		try {
            timestampIndex = NeoUtils.getTimeIndexProperty(datasetName);
			timestampIndex.initialize(neoService, null);
		}
		catch (IOException e) {
			throw (RuntimeException)new RuntimeException().initCause(e);
		}
	}
	
	private void createNewIndexes(String datasetName, String virtualDatasetName) {
	    try {
	        NeoCorePlugin.getDefault().getProjectService().deleteMultiPropertyIndex(NeoUtils.getLocationIndexName(datasetName));
	        realDatasetLocationIndex = NeoUtils.getLocationIndexProperty(datasetName);
	        realDatasetLocationIndex.initialize(neoService, null);
	        
	        NeoCorePlugin.getDefault().getProjectService().deleteMultiPropertyIndex(NeoUtils.getLocationIndexName(virtualDatasetName));
	        callDatasetLocationIndex = NeoUtils.getLocationIndexProperty(virtualDatasetName);
	        callDatasetLocationIndex.initialize(neoService, null);
	    }
	    catch (IOException e) {
	        throw (RuntimeException)new RuntimeException().initCause(e);
	    }
	}
	
	private String getCallDatasetName(Node realDataset, String realDatasetName) {
	    Node virtualDataset = NeoUtils.findOrCreateVirtualDatasetNode(realDataset, DriveTypes.AMS_CALLS, neoService);
	    
	    if (virtualDataset != null) {
	        return NeoUtils.getNodeName(virtualDataset);
	    }
	    else {
	        return null;
	    }
	}
	
	private Node determineCallNode(Node mNode) {
	    Iterator<Node> mNodesIterator = mNode.traverse(Order.BREADTH_FIRST, StopEvaluator.DEPTH_ONE, new ReturnableEvaluator() {
            
            @Override
            public boolean isReturnableNode(TraversalPosition currentPos) { 
                return (currentPos.depth() == 1) && (NeoUtils.isCallNode(currentPos.currentNode()));
            }
        }, GeoNeoRelationshipTypes.CHILD, Direction.INCOMING).iterator();
	    
	    if (mNodesIterator.hasNext()) {
	        Node callNode = mNodesIterator.next();
	        
	        //check that this Call node is not correlated yet, e.g. didn't have VIRTUAL_CHILD relationship to mp node
	        Iterator<Node> mpNodesIterator = callNode.traverse(Order.BREADTH_FIRST, StopEvaluator.DEPTH_ONE, new ReturnableEvaluator() {
                
                @Override
                public boolean isReturnableNode(TraversalPosition currentPos) {
                    return (currentPos.depth() == 1) && (NeoUtils.isDrivePointNode(currentPos.currentNode()));
                }
            }, GeoNeoRelationshipTypes.VIRTUAL_CHILD, Direction.OUTGOING).iterator();
	        
	        if (mpNodesIterator.hasNext()) {
	            return null;
	        }
	        else {
	            return callNode;
	        }
	    }
	    else {
	        return null;
	    }
	}
	
	/**
	 * Class that provides iteration through all m nodes of dataset
	 * 
	 * @author Lagutko_N
	 * @since 1.0.0
	 */
	private class MNodeIterator implements Iterator<Node> {
		
		/*
		 * Iterator for file nodes
		 */
		Iterator<Node> fileIterator;
		/*
		 * Iterator for mp nodes
		 */
		Iterator<Node> mIterator;
		
		/**
		 * Constructor
		 * 
		 * @param datasetNode name of dataset
		 */
		public MNodeIterator(Node datasetNode) {
			fileIterator = datasetNode.traverse(Order.BREADTH_FIRST, StopEvaluator.END_OF_GRAPH, new ReturnableEvaluator() {
				
				@Override
				public boolean isReturnableNode(TraversalPosition currentPos) {
					if (currentPos.depth() > 0) {
						return NeoUtils.isFileNode(currentPos.currentNode());
					}
					return false;
				}
			}, GeoNeoRelationshipTypes.NEXT, Direction.OUTGOING, GeoNeoRelationshipTypes.NEXT, Direction.OUTGOING).iterator();
			
			if (fileIterator.hasNext()) {
				Node fileNode = fileIterator.next();
				updateMIterator(fileNode);
			}
		}
		
		/**
		 * Update iterator of mp nodes with new file node
		 *
		 * @param fileNode
		 */
		private void updateMIterator(Node fileNode) {
			mIterator = fileNode.traverse(Order.DEPTH_FIRST, StopEvaluator.END_OF_GRAPH, ReturnableEvaluator.ALL_BUT_START_NODE, GeoNeoRelationshipTypes.NEXT, Direction.OUTGOING).iterator();
		}

		@Override
		public boolean hasNext() {
			boolean hasNext = false;
			
			if (mIterator != null) {
				hasNext = mIterator.hasNext();
			}		
			if (!hasNext) {
				if (fileIterator.hasNext()) {
					updateMIterator(fileIterator.next());
					return mIterator.hasNext();
				}
			}
			return hasNext;
		}

		@Override
		public Node next() {
			if (hasNext()) {
				return mIterator.next();
			}
			throw new NoSuchElementException();
		}

		@Override
		public void remove() {
		}
		
	}

}
