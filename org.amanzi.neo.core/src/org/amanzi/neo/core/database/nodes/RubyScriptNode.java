package org.amanzi.neo.core.database.nodes;

import org.amanzi.neo.core.INeoConstants;
import org.amanzi.neo.core.enums.SplashRelationshipTypes;
import org.neo4j.api.core.Node;
/**
* Wrapper of Ruby node
* 
* @author Cinkel_A
* 
*/
public class RubyScriptNode extends AbstractNode {
	private static final String ATTR_NAME = "ATTR_NAME";
	private static final String NODE_TYPE = "Script";
	private static final String NODE_NAME = "Script";

	public RubyScriptNode(Node node) {
		super(node);
		setParameter(INeoConstants.PROPERTY_TYPE_NAME, NODE_TYPE);
		setParameter(INeoConstants.PROPERTY_NAME_NAME, NODE_NAME);
	}

	/**
	 * Returns name of Ruby project
	 * 
	 * @return name of Ruby project
	 */
	public String getName() {
		return (String) getParameter(ATTR_NAME);
	}

	/**
	 * Sets name of Ruby project
	 * 
	 * @param projectName
	 *            name of Ruby project
	 */
	public void setName(String projectName) {
		setParameter(ATTR_NAME, projectName);
	}
	/**
	 * Adds a Spreadsheet Project to Spreadsheet
	 *
	 * @param spreadsheetNode  wrapper
	 */ 
	public void addSpreadsheet(SpreadsheetNode spreadsheetNode) {
		addRelationship(SplashRelationshipTypes.RUBY_PROJECT, spreadsheetNode
				.getUnderlyingNode());
	}
	/**
	 * Adds a Spreadsheet Project to Spreadsheet
	 *
	 * @param scriptNode  wrapper
	 */ 
	public void addScript(RubyScriptNode scriptNode) {
		addRelationship(SplashRelationshipTypes.RUBY_PROJECT, scriptNode
				.getUnderlyingNode());
	}	
}
