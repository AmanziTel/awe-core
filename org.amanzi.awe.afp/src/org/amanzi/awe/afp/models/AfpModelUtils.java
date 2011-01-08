package org.amanzi.awe.afp.models;

import org.amanzi.neo.services.INeoConstants;
import org.amanzi.neo.services.enums.NetworkRelationshipTypes;
import org.amanzi.neo.services.enums.NodeTypes;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.ReturnableEvaluator;
import org.neo4j.graphdb.StopEvaluator;
import org.neo4j.graphdb.TraversalPosition;
import org.neo4j.graphdb.Traverser;
import org.neo4j.graphdb.Traverser.Order;

public class AfpModelUtils {
	
	public static Traverser getTrxTraverser(Node sectorNode) {
		Traverser trxTraverser = sectorNode.traverse(Order.DEPTH_FIRST, StopEvaluator.DEPTH_ONE, new ReturnableEvaluator(){
			@Override
			public boolean isReturnableNode(TraversalPosition currentPos) {
				if (currentPos.currentNode().getProperty(INeoConstants.PROPERTY_TYPE_NAME,"").equals(NodeTypes.TRX.getId())){
					return true;
				}
					
				return false;
			}
    		
    	}, NetworkRelationshipTypes.CHILD, Direction.OUTGOING);
		
		return trxTraverser;
	}

}
