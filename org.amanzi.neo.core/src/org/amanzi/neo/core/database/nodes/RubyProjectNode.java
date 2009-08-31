package org.amanzi.neo.core.database.nodes;

import java.util.Iterator;

import org.amanzi.neo.core.INeoConstants;
import org.amanzi.neo.core.enums.SplashRelationshipTypes;
import org.neo4j.api.core.Direction;
import org.neo4j.api.core.Node;
import org.neo4j.api.core.ReturnableEvaluator;
import org.neo4j.api.core.StopEvaluator;
import org.neo4j.api.core.Traverser;

/**
 * Root Node of Spreadsheets
 * 
 * @author Lagutko_N
 */

public class RubyProjectNode extends AbstractNode {

	private static final String ATTR_PROJECT_NAME = "Project name";
	private static final Object RUBY_PROJECT_NODE_TYPE = "ruby_project";
	private static final Object RUBY_PROJECT_NODE_NAME = "Ruby project";

	/**
	 * Constructor. Wraps a Node that will be parent node for Spreadsheets
	 * 
	 * @param node
	 */
	public RubyProjectNode(Node node) {
		super(node);
		setParameter(INeoConstants.PROPERTY_TYPE_NAME, RUBY_PROJECT_NODE_TYPE);
		setParameter(INeoConstants.PROPERTY_NAME_NAME, RUBY_PROJECT_NODE_NAME);
	}

    /**
     * Returns name of RUBY project
     * 
     * @return name of RUBY project
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
	 * Returns all Spreadsheets for this Node
	 * 
	 * @return all Spreadsheets
	 */

	public Iterator<SpreadsheetNode> getSpreadsheets() {
		return new SpreadsheetIterator();
	}

	/**
	 * Returns all Scripts for this Node
	 * 
	 * @return RubyScriptNode
	 */

	public Iterator<RubyScriptNode> getScripts() {
		return new ScriptIterator();
	}

	/**
	 * Adds a Spreadsheets
	 * 
	 * @param spreadsheet
	 *            wrapper of Spreadsheet
	 */

	public void addSpreadsheet(SpreadsheetNode spreadsheet) {
		addRelationship(SplashRelationshipTypes.SPREADSHEET, spreadsheet
				.getUnderlyingNode());
	}

	/**
	 * Adds a script
	 * 
	 * @param scriptNode
	 *            wrapper of script
	 */
	public void addScript(RubyScriptNode scriptNode) {
		addRelationship(SplashRelationshipTypes.SCRIPT, scriptNode
				.getUnderlyingNode());

	}

	/**
	 * Iterator for Spreadsheets in the Root
	 * 
	 * @author Tsinkel_A
	 */

	private class SpreadsheetIterator extends AbstractIterator<SpreadsheetNode> {

		/**
		 * Constructor. Creates a Traverser that will look for all Spreadsheets
		 * 
		 */
		public SpreadsheetIterator() {
			this.iterator = node.traverse(Traverser.Order.BREADTH_FIRST,
					StopEvaluator.DEPTH_ONE,
					ReturnableEvaluator.ALL_BUT_START_NODE,
					SplashRelationshipTypes.SPREADSHEET, Direction.OUTGOING)
					.iterator();
		}

		@Override
		protected SpreadsheetNode wrapNode(Node node) {
			return new SpreadsheetNode(node);
		}

	}

	/**
	 * Iterator for Script in the Node
	 * 
	 * @author Tsinkel_A
	 */

	private class ScriptIterator extends AbstractIterator<RubyScriptNode> {

		/**
		 * Constructor. Creates a Traverser that will look for all Spreadsheets
		 * 
		 */
		public ScriptIterator() {
			super(node, SplashRelationshipTypes.SCRIPT);
		}

		@Override
		protected RubyScriptNode wrapNode(Node node) {
			return new RubyScriptNode(node);
		}

	}
	
	@Override
	public boolean equals(Object object) {
	    if (object instanceof RubyProjectNode) {
	        return ((RubyProjectNode)object).getUnderlyingNode().equals(getUnderlyingNode());
	    }
	    return false;
	}

}
