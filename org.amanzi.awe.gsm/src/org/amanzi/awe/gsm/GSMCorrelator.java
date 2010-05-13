package org.amanzi.awe.gsm;

import org.amanzi.neo.core.INeoConstants;
import org.amanzi.neo.core.service.NeoServiceProvider;
import org.amanzi.neo.core.utils.NeoUtils;
import org.apache.log4j.Logger;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;

public class GSMCorrelator {
	
	/*
	 * Logger for this class
	 */
	private static Logger LOGGER = Logger.getLogger(GSMCorrelator.class);
	
	/*
	 * Neo Service
	 */
	private GraphDatabaseService neoService;
	
	/*
	 * GSM Dataset Node
	 */
	private Node gsmDatasetNode;
	/*
	 * Name of GSM dataset
	 */
	private String gsmDatasetName;
	
	/*
	 * Name of correlated Network
	 */
	private String networkName;
	
	/*
	 * Name of correlated Counters
	 */
	private String ossCountersName;
	
	/**
	 * Creates a Correlator based on GPS data
	 * 
	 * @param gsmDatasetNode Dataset Node of GPS data
	 */
	public GSMCorrelator(Node gsmDatasetNode) {
		neoService = NeoServiceProvider.getProvider().getService();
		
		this.gsmDatasetNode = gsmDatasetNode;
		this.gsmDatasetName = NeoUtils.getNodeName(gsmDatasetNode, neoService);		
	}
	
	/**
	 * Correlates GPS data with Network Data
	 * 
	 * @param networkNode Network Node for correlation
	 */
	public void correlateWithNetwork(Node networkNode) {
		this.networkName = NeoUtils.getNodeName(networkNode, neoService);
		
		LOGGER.info("Correlation between GSM Data <" + gsmDatasetName + "> and Network <" + networkName + ">");
	}
	
	/**
	 * Correlates GPS data with OSS Counters/Performance Data
	 * 
	 * @param countersDataNode Root OSS Node for correlation
	 */
	public void correlateWithPerformance(Node countersDataNode) {
		this.ossCountersName = NeoUtils.getNodeName(countersDataNode, neoService);
		
		LOGGER.info("Correlation between GSM Data <" + gsmDatasetName + "> and OSS Counters Data <" + ossCountersName + ">");
	}
}
