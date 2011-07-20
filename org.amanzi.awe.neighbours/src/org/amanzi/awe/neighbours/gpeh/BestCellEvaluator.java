package org.amanzi.awe.neighbours.gpeh;

import org.amanzi.neo.services.INeoConstants;
import org.amanzi.neo.services.enums.NodeTypes;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Path;
import org.neo4j.graphdb.traversal.Evaluation;
import org.neo4j.graphdb.traversal.Evaluator;
import org.neo4j.graphdb.traversal.Evaluators;

public class BestCellEvaluator extends Evaluators implements Evaluator {
	private String checkType;

	BestCellEvaluator(String type) {
		checkType = type;
	}

	boolean includes;

	@Override
	public Evaluation evaluate(Path arg0) {
		Node node = arg0.endNode();
		includes = false;
		String typeId = node.getProperty("statistic property type").toString();

		includes = typeId != null
				&& typeId.equals(checkType)
				&& node.getProperty(INeoConstants.PROPERTY_NAME_NAME).toString().indexOf("_") > -1
				&& NodeTypes.M.checkNode(node);

		if (includes) {
			return Evaluation.INCLUDE_AND_CONTINUE;
		} else
			return Evaluation.EXCLUDE_AND_CONTINUE;
	}

}
