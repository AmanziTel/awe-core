package org.amanzi.neo.core.database.nodes;

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

	private static final String SCRIPT_NAME = "Ruby Script";
	private static final String SCRIPT_TYPE = "Ruby_Script";

	/**
	 * Constructor
	 * 
	 * @param node
	 *            wrapped node
	 */
	public RubyScriptNode(Node node) {
		super(node, SCRIPT_NAME, SCRIPT_TYPE);
	}

	/**
	 * Returns name of Ruby script
	 * 
	 * @return name of Ruby script
	 */
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
