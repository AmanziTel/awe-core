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
package org.amanzi.neo.core.database.nodes;

import org.amanzi.neo.core.INeoConstants;
import org.amanzi.neo.core.enums.NodeTypes;
import org.amanzi.neo.core.enums.SplashRelationshipTypes;
import org.neo4j.api.core.Node;

/**
 * Wrapper of Ruby script node
 * 
 * @author Cinkel_A
 * 
 */
public class RubyScriptNode extends AbstractNode {
	private static final String ATTR_NAME = "ATTR_NAME";

	/**
	 * Constructor for creating a new node in the database representing this script.
	 * @param wrapped node
	 */
	public RubyScriptNode(Node node, String name) {
		super(node, name, NodeTypes.SCRIPT);
	}

   /**
     * Constructor for wrapping existing script nodes. To reduce API confusion,
     * this constructor is private, and users should use the factory method instead.
     * @param node
     */
    private RubyScriptNode(Node node) {
        super(node);
        if(!getParameter(INeoConstants.PROPERTY_TYPE_NAME).toString().equals(NodeTypes.SCRIPT.getId())) throw new RuntimeException("Expected existing Ruby Script Node, but got "+node.toString());
    }
    
    /**
     * Use factory method to ensure clear API different to normal constructor.
     *
     * @param node representing an existing Ruby project
     * @return RubyProjectNode from existing Node
     */
    public static RubyScriptNode fromNode(Node node) {
        return new RubyScriptNode(node);
    }


	/**
	 * Returns name of Ruby script
	 * 
	 * @return name of Ruby script
	 */
	@Override
    public String getName() {
		return (String) getParameter(ATTR_NAME);
	}

	/**
	 * Sets name of Ruby script
	 * 
	 * @param projectName
	 *            name of Ruby project
	 */
	public void setName(String projectName) {
		setParameter(ATTR_NAME, projectName);
	}

	/**
	 * Adds a Cell to Script
	 * 
	 * @param cellNode
	 *            wrapper
	 */
	public void addCell(CellNode cellNode) {
		addRelationship(SplashRelationshipTypes.SCRIPT_CELL, cellNode
				.getUnderlyingNode());
	}
}
