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
package org.amanzi.neo.services.nodes;

import java.util.Iterator;

import org.amanzi.neo.services.INeoConstants;
import org.amanzi.neo.services.enums.NetworkRelationshipTypes;
import org.amanzi.neo.services.enums.NodeTypes;
import org.amanzi.neo.services.enums.SplashRelationshipTypes;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.ReturnableEvaluator;
import org.neo4j.graphdb.StopEvaluator;
import org.neo4j.graphdb.Traverser.Order;

/**
 * Wrapper of AweProject node
 * 
 * @author Cinkel_A
 * 
 */
public class AweProjectNode extends AbstractNode {

	public AweProjectNode(Node node, String projectName) {
		super(node);
		setParameter(INeoConstants.PROPERTY_TYPE_NAME, NodeTypes.AWE_PROJECT.getId());
		setParameter(INeoConstants.PROPERTY_NAME_NAME, projectName);
	}

    /**
     * Constructor for wrapping existing Awe project nodes. To reduce API confusion,
     * this constructor is private, and users should use the factory method instead.
     * @param node
     */
    private AweProjectNode(Node node) {
        super(node);
        if(!getParameter(INeoConstants.PROPERTY_TYPE_NAME).toString().equals(NodeTypes.AWE_PROJECT.getId())) throw new RuntimeException("Expected existing AweProject Node, but got "+node.toString());
    }
    
    /**
     * Use factory method to ensure clear API different to normal constructor.
     *
     * @param node representing an existing Awe project
     * @return AweProjectNode from existing Node
     */
    public static AweProjectNode fromNode(Node node) {
        return new AweProjectNode(node);
    }

	/**
	 * Returns name of Awe project
	 * 
	 * @return name of Awe project
	 */
	public String getName() {
		return (String) getParameter(INeoConstants.PROPERTY_NAME_NAME);
	}

	/**
	 * Sets name of Awe project
	 * 
	 * @param projectName
	 *            name of Awe project
	 */
	public void setName(String projectName) {
		setParameter(INeoConstants.PROPERTY_NAME_NAME, projectName);
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
			this.iterator = node.traverse(Order.BREADTH_FIRST, 
					StopEvaluator.DEPTH_ONE, 
					ReturnableEvaluator.ALL_BUT_START_NODE,
					SplashRelationshipTypes.RUBY_PROJECT,
					Direction.OUTGOING).iterator();
		}

		@Override
		protected RubyProjectNode wrapNode(Node node) {
			return RubyProjectNode.fromNode(node);
		}

	}

    /**
     * Adds a child node to Project. This is normally a network node or drive data node.
     * 
     * @param Node to add as child
     */
    public void addChildNode(Node child) {
        addRelationship(NetworkRelationshipTypes.CHILD, child);
    }

}
