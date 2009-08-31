package org.amanzi.neo.core.database.nodes;

import java.util.Iterator;

import org.amanzi.neo.core.INeoConstants;
import org.amanzi.neo.core.enums.NetworkRelationshipTypes;
import org.amanzi.neo.core.enums.SplashRelationshipTypes;
import org.neo4j.api.core.Direction;
import org.neo4j.api.core.Node;
import org.neo4j.api.core.ReturnableEvaluator;
import org.neo4j.api.core.StopEvaluator;
import org.neo4j.api.core.Traverser;

/**
 * Wrapper of AweProject node
 * 
 * @author Cinkel_A
 * 
 */
public class AweProjectNode extends AbstractNode {

	private static final String ATTR_PROJECT_NAME = "Project name";
	private static final String AWE_PROJECT_NODE_TYPE = "awe_project";
	private static final String AWE_PROJECT_NODE_NAME = "Awe Project";

	public AweProjectNode(Node node) {
		super(node);
		setParameter(INeoConstants.PROPERTY_TYPE_NAME, AWE_PROJECT_NODE_TYPE);
		setParameter(INeoConstants.PROPERTY_NAME_NAME, AWE_PROJECT_NODE_NAME);
	}

	/**
	 * Returns name of Awe project
	 * 
	 * @return name of Awe project
	 */
	public String getName() {
		return (String) getParameter(ATTR_PROJECT_NAME);
	}

	/**
	 * Sets name of Awe project
	 * 
	 * @param projectName
	 *            name of Awe project
	 */
	public void setName(String projectName) {
		setParameter(ATTR_PROJECT_NAME, projectName);
	}

	/**
	 * Adds a Ruby Project 
	 *
	 * @param rubyProjectNode  wrapper
	 */ 
	public void addRubyProject(RubyProjectNode rubyProjectNode) {
		addRelationship(SplashRelationshipTypes.RUBY_PROJECT, rubyProjectNode
				.getUnderlyingNode());
	}
	/**
	 * Returns Iterator with all RubyProject
	 * 
	 * @return all RubyProject
	 */
	public Iterator<RubyProjectNode> getAllProjects() {
		return new AllProjectIterator();
	}

	/**
	 * Iterator for searching all AweProjectNode 
	 * 
	 * 
	 */

	private class AllProjectIterator extends AbstractIterator<RubyProjectNode> {

		/**
		 * Constructor. Creates a Traverser that will look for all RubyProject
		 * 
		 */
		public AllProjectIterator() {
			this.iterator = node.traverse(Traverser.Order.BREADTH_FIRST, 
					StopEvaluator.DEPTH_ONE, 
					ReturnableEvaluator.ALL_BUT_START_NODE,
					SplashRelationshipTypes.RUBY_PROJECT,
					Direction.OUTGOING).iterator();
		}

		@Override
		protected RubyProjectNode wrapNode(Node node) {
			return new RubyProjectNode(node);
		}

	}

    /**
     *Adds a Network to Project
     * 
     * @param network network node
     */
    public void addNetworkNode(Node network) {
        addRelationship(NetworkRelationshipTypes.CHILD, network);
    }

}
