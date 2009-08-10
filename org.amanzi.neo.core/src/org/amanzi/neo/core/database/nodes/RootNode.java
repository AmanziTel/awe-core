package org.amanzi.neo.core.database.nodes;

import java.util.Iterator;

import org.amanzi.neo.core.enums.SplashRelationshipTypes;
import org.neo4j.api.core.Direction;
import org.neo4j.api.core.Node;
import org.neo4j.api.core.ReturnableEvaluator;
import org.neo4j.api.core.StopEvaluator;
import org.neo4j.api.core.Traverser;

/**
 * Wrapper root node
 * 
 * @author Cinkel_A
 * 
 */
public class RootNode extends AbstractNode {

	public RootNode(Node node) {
		super(node);
		// TODO Auto-generated constructor stub
	}

	/**
	 * Returns Iterator with all AweProjectNode
	 * 
	 * @return all AweProjectNode
	 */
	public Iterator<AweProjectNode> getAllProjects() {
		return new AllProjectIterator();
	}

	/**
	 * Iterator for searching all AweProjectNode 
	 * 
	 * 
	 */

	private class AllProjectIterator extends AbstractIterator<AweProjectNode> {

		/**
		 * Constructor. Creates a Traverser that will look for all AweProjects
		 * 
		 */
		public AllProjectIterator() {
			this.iterator = node.traverse(Traverser.Order.BREADTH_FIRST,
					StopEvaluator.DEPTH_ONE,
					ReturnableEvaluator.ALL_BUT_START_NODE,
					SplashRelationshipTypes.AWE_PROJECT, Direction.OUTGOING)
					.iterator();
		}

		@Override
		protected AweProjectNode wrapNode(Node node) {
			return new AweProjectNode(node);
		}

	}

	/**
	 * Adds project to root
	 * 
	 * @param project
	 */
	public void addProject(AweProjectNode project) {
		addRelationship(SplashRelationshipTypes.AWE_PROJECT, project);
	}

}
