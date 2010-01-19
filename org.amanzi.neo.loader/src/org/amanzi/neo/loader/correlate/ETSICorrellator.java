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
import org.amanzi.neo.core.enums.GeoNeoRelationshipTypes;
import org.amanzi.neo.core.enums.NetworkRelationshipTypes;
import org.amanzi.neo.core.enums.SplashRelationshipTypes;
import org.amanzi.neo.core.service.NeoServiceProvider;
import org.amanzi.neo.core.utils.NeoUtils;
import org.amanzi.neo.index.MultiPropertyIndex;
import org.amanzi.neo.index.MultiPropertyIndex.MultiTimeIndexConverter;
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
		
			MpNodeIterator mpIterator = getMpNodeIterator(firstDataset);
		
			while (mpIterator.hasNext()) {
				Node firstMp = mpIterator.next();
				
				Node secondMp = determineNode((Long)firstMp.getProperty(INeoConstants.PROPERTY_TIMESTAMP_NAME));
				
				correlateNodes(firstMp, secondMp);
			}
			
			tx.success();
		}
		catch (Exception e) {
			tx.failure();
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
	private void correlateNodes(Node first, Node second) {
		if ((first == null) || (second == null)) {
			return;
		}
		
		Node correlationNode = getCorrelationNode(first);
		if (correlationNode == null) {
			correlationNode = getCorrelationNode(second);
		}
		else {
			second.createRelationshipTo(correlationNode, GeoNeoRelationshipTypes.CORRELATE_LEFT);
		}
		if (correlationNode == null) {		
			correlationNode = neoService.createNode();
			correlationNode.setProperty(INeoConstants.PROPERTY_TYPE_NAME, "correlation_node");
			
			first.createRelationshipTo(correlationNode, GeoNeoRelationshipTypes.CORRELATE_RIGHT);
			second.createRelationshipTo(correlationNode, GeoNeoRelationshipTypes.CORRELATE_LEFT);
		}
		else {
			first.createRelationshipTo(correlationNode, GeoNeoRelationshipTypes.CORRELATE_RIGHT);
		}
		
	}
	
	/**
	 * Tries to find already created correlation node
	 *
	 * @param rootNode root node
	 * @return correlation node
	 */
	private Node getCorrelationNode(Node rootNode) {
		Iterator<Node> correlationNodeIterator = rootNode.traverse(Order.BREADTH_FIRST, StopEvaluator.DEPTH_ONE, ReturnableEvaluator.ALL_BUT_START_NODE, GeoNeoRelationshipTypes.CORRELATE_RIGHT, Direction.OUTGOING, GeoNeoRelationshipTypes.CORRELATE_LEFT, Direction.OUTGOING).iterator();
		if (correlationNodeIterator.hasNext()) {
			return correlationNodeIterator.next();
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
	 * @param datasetName dataset name
	 * @return iterator
	 */
	private MpNodeIterator getMpNodeIterator(String datasetName) {
		Node datasetNode = getDatasetNode(datasetName);
		if (datasetNode != null) {
			return new MpNodeIterator(datasetNode);
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
            timestampIndex = new MultiPropertyIndex<Long>(NeoUtils.getTimeIndexName(datasetName),
                    new String[] {INeoConstants.PROPERTY_TIMESTAMP_NAME}, new MultiTimeIndexConverter(), 10);
			timestampIndex.initialize(neoService, null);
		}
		catch (IOException e) {
			throw (RuntimeException)new RuntimeException().initCause(e);
		}
	}
	
	/**
	 * Class that provides iteration through all mp nodes of dataset
	 * 
	 * @author Lagutko_N
	 * @since 1.0.0
	 */
	private class MpNodeIterator implements Iterator<Node> {
		
		/*
		 * Iterator for file nodes
		 */
		Iterator<Node> fileIterator;
		/*
		 * Iterator for mp nodes
		 */
		Iterator<Node> mpIterator;
		
		/**
		 * Constructor
		 * 
		 * @param datasetNode name of dataset
		 */
		public MpNodeIterator(Node datasetNode) {
			fileIterator = datasetNode.traverse(Order.BREADTH_FIRST, StopEvaluator.END_OF_GRAPH, ReturnableEvaluator.ALL_BUT_START_NODE, GeoNeoRelationshipTypes.NEXT, Direction.OUTGOING).iterator();
			
			if (fileIterator.hasNext()) {
				Node fileNode = fileIterator.next();
				updateMpIterator(fileNode);
			}
		}
		
		/**
		 * Update iterator of mp nodes with new file node
		 *
		 * @param fileNode
		 */
		private void updateMpIterator(Node fileNode) {
			mpIterator = fileNode.traverse(Order.DEPTH_FIRST, StopEvaluator.END_OF_GRAPH, ReturnableEvaluator.ALL_BUT_START_NODE, GeoNeoRelationshipTypes.NEXT, Direction.OUTGOING).iterator();
		}

		@Override
		public boolean hasNext() {
			boolean hasNext = false;
			
			if (mpIterator != null) {
				hasNext = mpIterator.hasNext();
			}		
			if (!hasNext) {
				if (fileIterator.hasNext()) {
					updateMpIterator(fileIterator.next());
					return mpIterator.hasNext();
				}
			}
			return hasNext;
		}

		@Override
		public Node next() {
			if (hasNext()) {
				return mpIterator.next();
			}
			throw new NoSuchElementException();
		}

		@Override
		public void remove() {
		}
		
	}

}
