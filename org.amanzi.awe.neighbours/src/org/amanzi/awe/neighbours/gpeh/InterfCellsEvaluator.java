package org.amanzi.awe.neighbours.gpeh;

import org.amanzi.neo.services.INeoConstants;
import org.amanzi.neo.services.enums.NetworkRelationshipTypes;
import org.amanzi.neo.services.enums.NodeTypes;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Path;
import org.neo4j.graphdb.traversal.Evaluation;
import org.neo4j.graphdb.traversal.Evaluator;
import org.neo4j.graphdb.traversal.Evaluators;

public class InterfCellsEvaluator extends Evaluators implements Evaluator {
	private String checkType;
	public InterfCellsEvaluator(String type) {
		checkType=type;
	}
	boolean includes;
	boolean conContinue;
	@Override
	public Evaluation evaluate(Path arg0) {
		Node node = arg0.endNode();
		includes = false;
		conContinue=true;
		String typeId = node.getProperty("statistic property type").toString();
		conContinue=node.getSingleRelationship(NetworkRelationshipTypes.NEXT, Direction.OUTGOING).getEndNode().getProperty(INeoConstants.PROPERTY_NAME_NAME).toString().indexOf("_") >-1;
		includes = typeId != null
				&& typeId.equals(checkType)
				&& node.getProperty(INeoConstants.PROPERTY_NAME_NAME).toString().indexOf("_") <0
				&& NodeTypes.M.checkNode(node);

		return Evaluation.of(includes, conContinue);
	
	}

}
