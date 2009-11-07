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

import java.util.ArrayList;
import java.util.Iterator;

import org.amanzi.neo.core.INeoConstants;
import org.amanzi.neo.core.enums.SplashRelationshipTypes;
import org.amanzi.neo.core.database.exception.SplashDatabaseException;
import org.amanzi.neo.core.database.exception.SplashDatabaseExceptionMessages;

import org.neo4j.api.core.Direction;
import org.neo4j.api.core.Node;
import org.neo4j.api.core.ReturnableEvaluator;
import org.neo4j.api.core.StopEvaluator;
import org.neo4j.api.core.TraversalPosition;
import org.neo4j.api.core.Traverser;

public class ChartNode extends AbstractNode {

	public static final String CHART_NAME = "chart_name";
	private static final String CHART_NODE_TYPE = "spreadsheet_chart";
	private static final String CHART_NODE_NAME = "Spreadsheet Chart";
	public static final String CHART_INDEX = "chart_index";

	public ChartNode(Node node) {
		super(node);
		// TODO Auto-generated constructor stub

//		setParameter(INeoConstants.PROPERTY_TYPE_NAME, CHART_NODE_TYPE);
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
     * Sets the type of a Chart
     *
     * @param chartType type of a Chart
     */    
    public void setChartType(String chartType) {
        setParameter(CHART_NODE_TYPE, chartType);
        setParameter(INeoConstants.PROPERTY_TYPE_NAME, CHART_NODE_TYPE+"_"+chartType.toLowerCase());
    }
    /**
     * Getter for chart type
     * 
     * @return chart type
     */
    public String getChartType() {
        return (String)getParameter(CHART_NODE_TYPE);
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
	 * Iterator that computes all Charts in Spreadsheet
	 * 
	 * @author amabdelsalam
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
	 * Iterator that computes Charts by given Index
	 * 
	 * @author amabdelsalam
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

    /**
     * Method to get all chart items
     * 
     * @return all chart items as list
     */
    public ArrayList<ChartItemNode> getAllChartItems() {
        ArrayList<ChartItemNode> chartItemsList = new ArrayList<ChartItemNode>(0);

        Iterator<ChartItemNode> chartItems = new AllChartItemNodeIterator();

        while (chartItems.hasNext()) {
            chartItemsList.add(chartItems.next());
        }

        return chartItemsList;
    }
	/**
     * Use factory method to ensure clear API different to normal constructor.
     *
     * @param node representing an existing Spreadsheet
     * @return SpreadsheetNode from existing Node
     */
    public static ChartNode fromNode(Node node) {
        return new ChartNode(node);
    }
}
