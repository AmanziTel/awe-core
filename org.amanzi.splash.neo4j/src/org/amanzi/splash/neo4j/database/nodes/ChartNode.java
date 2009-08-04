package org.amanzi.splash.neo4j.database.nodes;

import java.util.Iterator;

import org.amanzi.neo.core.INeoConstants;
import org.amanzi.neo.core.enums.SplashRelationshipTypes;
import org.amanzi.splash.neo4j.database.exception.SplashDatabaseException;
import org.amanzi.splash.neo4j.database.exception.SplashDatabaseExceptionMessages;

import org.neo4j.api.core.Direction;
import org.neo4j.api.core.Node;
import org.neo4j.api.core.ReturnableEvaluator;
import org.neo4j.api.core.StopEvaluator;
import org.neo4j.api.core.TraversalPosition;
import org.neo4j.api.core.Traverser;
import org.neo4j.api.core.Traverser.Order;

public class ChartNode extends AbstractNode {

	public static final String CHART_NAME = "chart_name";
	private static final String CHART_NODE_TYPE = "Spreadsheet_Chart";
	private static final String CHART_NODE_NAME = "Spreadsheet Chart";
	public static final String CHART_INDEX = "chart_index";

	public ChartNode(Node node) {
		super(node);
		// TODO Auto-generated constructor stub

		setParameter(INeoConstants.PROPERTY_TYPE_NAME, CHART_NODE_TYPE);
		setParameter(INeoConstants.PROPERTY_NAME_NAME, CHART_NODE_NAME);
	}

	/**
	 * Sets the name of Chart
	 *
	 * @param chartName name of Chart
	 */    
	public void setChartName(String chartName) {
		setParameter(CHART_NAME, chartName);
	}

	/**
	 * Adds a Cell to Column
	 *
	 * @param cell Cell
	 */    
	public void addChartItem(ChartItemNode chartItem) {
		addRelationship(SplashRelationshipTypes.CHART_ITEM, chartItem.getUnderlyingNode());
	}

	public void setChartIndex(String newChartIndex) {
		// TODO Auto-generated method stub

		setParameter(CHART_INDEX, newChartIndex);

	}

	public String getChartIndex() {
		return (String)getParameter(CHART_INDEX);
	}
	
	/**
	 * Iterator that computes all Rows in Spreadsheet
	 * 
	 * @author Lagutko_N
	 */    
	private class AllChartItemNodeIterator extends AbstractIterator<ChartItemNode> {

		public AllChartItemNodeIterator() {            
			this.iterator = node.traverse(Traverser.Order.BREADTH_FIRST, 
					StopEvaluator.DEPTH_ONE, 
					ReturnableEvaluator.ALL_BUT_START_NODE,
					SplashRelationshipTypes.CHART_ITEM,
					Direction.OUTGOING).iterator();
		}

		@Override
		protected ChartItemNode wrapNode(Node node) {            
			return new ChartItemNode(node);
		}
	}


	/**
	 * Iterator for searching Cells of this Row by given column
	 * 
	 * @author Lagutko_N 
	 */

	/**
	 * Iterator that computes Rows by given Index
	 * 
	 * @author Lagutko_N
	 */    
	private class ChartItemIterator extends AbstractIterator<ChartItemNode> {

		public ChartItemIterator(final String ChartItemIndex) {            
			this.iterator = node.traverse(Traverser.Order.BREADTH_FIRST, 
					StopEvaluator.DEPTH_ONE, 
					new ReturnableEvaluator() {

				public boolean isReturnableNode(TraversalPosition position) {
					if (position.isStartNode()) {
						return false;
					}
					return position.lastRelationshipTraversed().getEndNode().getProperty(ChartItemNode.CHART_ITEM_INDEX).equals(ChartItemIndex);
				}

			},
			SplashRelationshipTypes.CHART_ITEM,
			Direction.OUTGOING).iterator();
		}

		@Override
		protected ChartItemNode wrapNode(Node node) {            
			return new ChartItemNode(node);
		}
	}

	public ChartItemNode getChartItem(String chartItemName) throws SplashDatabaseException {
		Iterator<ChartItemNode> iterator = new ChartItemIterator(chartItemName);
        
        if (iterator.hasNext()) {
            ChartItemNode result = iterator.next();
            if (iterator.hasNext()) {
                String message = SplashDatabaseExceptionMessages.getFormattedString(SplashDatabaseExceptionMessages.Not_Single_Chart_Item_by_ID, chartItemName + getChartIndex());
                throw new SplashDatabaseException(message);
            }
            
            return result;
        }
		return null;
	}

	public Iterator<ChartItemNode> getAllChartItems() {
		// TODO Auto-generated method stub
		return new AllChartItemNodeIterator();
	}
}
