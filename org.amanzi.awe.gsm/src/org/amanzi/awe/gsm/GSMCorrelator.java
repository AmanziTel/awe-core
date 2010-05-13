package org.amanzi.awe.gsm;

import org.amanzi.neo.core.service.NeoServiceProvider;
import org.amanzi.neo.core.utils.NeoUtils;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;

public class GSMCorrelator {
	
	private GraphDatabaseService neoService;
	
	private Node gsmDatasetNode;
	
	public GSMCorrelator(Node gsmDatasetNode) {
		neoService = NeoServiceProvider.getProvider().getService();
		
		this.gsmDatasetNode = gsmDatasetNode;
	}
	
	public void correlateWithNetwork(Node networkNode) {
		
	}
	
	public void correlateWithPerformance(Node countersDataNode) {
		
	}
}
