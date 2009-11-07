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

	private static final Object RUBY_PROJECT_NODE_TYPE = "ruby_project";

	/**
	 * Constructor. Wraps a Node that will be parent node for Spreadsheets
	 * 
	 * @param node
	 */
	public RubyProjectNode(Node node, String projectName) {
		super(node);
		setParameter(INeoConstants.PROPERTY_TYPE_NAME, RUBY_PROJECT_NODE_TYPE);
		setParameter(INeoConstants.PROPERTY_NAME_NAME, projectName);
	}

	/**
	 * Constructor for wrapping existing Ruby project nodes. To reduce API confusion,
	 * this constructor is private, and users should use the factory method instead.
	 * @param node
	 */
	private RubyProjectNode(Node node) {
	    super(node);
	    if(!getParameter(INeoConstants.PROPERTY_TYPE_NAME).toString().equals(RUBY_PROJECT_NODE_TYPE)) throw new RuntimeException("Expected existing RubyProject Node, but got "+node.toString());
	}
	
	/**
	 * Use factory method to ensure clear API different to normal constructor.
	 *
	 * @param node representing an existing Ruby project
	 * @return RubyProjectNode from existing Node
	 */
	public static RubyProjectNode fromNode(Node node) {
	    return new RubyProjectNode(node);
	}

    /**
     * Returns name of RUBY project
     * 
     * @return name of RUBY project
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
     * Returns all Scripts for this Node
     * 
     * @return RubyScriptNode
     */

    public Iterator<ChartNode> getCharts() {
        return new ChartIterator();
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
			return SpreadsheetNode.fromNode(node);
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
			return RubyScriptNode.fromNode(node);
		}

	}
	/**
	 * 
	 * Iterator for charts of this project
	 * 
	 * @author Pechko E.
	 * @since 1.0.0
	 */
	private class ChartIterator extends AbstractIterator<ChartNode> {
        /**
         * Creates Iterator for all charts
         */
        public ChartIterator() {
            super(node, SplashRelationshipTypes.CHART);
        }

        @Override
        protected ChartNode wrapNode(Node node) {
            return ChartNode.fromNode(node);
        }
	    
	}

	@Override
	public boolean equals(Object object) {
	    if (object instanceof RubyProjectNode) {
	        return ((RubyProjectNode)object).getUnderlyingNode().equals(getUnderlyingNode());
	    }
	    return false;
	}

	/**
     * Adds chart to ruby project
     * 
     * @param chart
     */
    public void addChart(ChartNode chart) {
        addRelationship(SplashRelationshipTypes.CHART, chart
                .getUnderlyingNode());
    }

}
