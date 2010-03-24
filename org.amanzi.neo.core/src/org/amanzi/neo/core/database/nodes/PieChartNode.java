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
import org.amanzi.neo.core.database.exception.SplashDatabaseException;
import org.amanzi.neo.core.database.exception.SplashDatabaseExceptionMessages;
import org.amanzi.neo.core.enums.NodeTypes;
import org.amanzi.neo.core.enums.SplashRelationshipTypes;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.ReturnableEvaluator;
import org.neo4j.graphdb.StopEvaluator;
import org.neo4j.graphdb.TraversalPosition;
import org.neo4j.graphdb.Traverser.Order;

public class PieChartNode extends AbstractNode {

	public static final String PIE_CHART_NAME = "chart_name";
	private static final String PIE_CHART_NODE_NAME = "Spreadsheet Chart";
	public static final String PIE_CHART_INDEX = "chart_index";

	public PieChartNode(Node node) {
		super(node);
		// TODO Auto-generated constructor stub

		setParameter(INeoConstants.PROPERTY_TYPE_NAME, NodeTypes.PIE_CHART.getId());
		setParameter(INeoConstants.PROPERTY_NAME_NAME, PIE_CHART_NODE_NAME);
	}

	/**
	 * Sets the name of Chart
	 *
	 * @param chartName name of Chart
	 */    
	public void setPieChartName(String chartName) {
		setParameter(PIE_CHART_NAME, chartName);
	}

	/**
	 * Adds a Cell to Column
	 *
	 * @param cell Cell
	 */    
	public void addPieChartItem(PieChartItemNode chartItem) {
		addRelationship(SplashRelationshipTypes.PIE_CHART_ITEM, chartItem.getUnderlyingNode());
	}

	public void setPieChartIndex(String newChartIndex) {
		// TODO Auto-generated method stub

		setParameter(PIE_CHART_INDEX, newChartIndex);

	}

	public String getPieChartIndex() {
		return (String)getParameter(PIE_CHART_INDEX);
	}
	
	/**
	 * Iterator that computes all Charts in Spreadsheet
	 * 
	 * @author amabdelsalam
	 */    
	private class AllPieChartItemNodeIterator extends AbstractIterator<PieChartItemNode> {

		public AllPieChartItemNodeIterator() {            
			this.iterator = node.traverse(Order.BREADTH_FIRST, 
					StopEvaluator.DEPTH_ONE, 
					ReturnableEvaluator.ALL_BUT_START_NODE,
					SplashRelationshipTypes.PIE_CHART_ITEM,
					Direction.OUTGOING).iterator();
		}

		@Override
		protected PieChartItemNode wrapNode(Node node) {            
			return new PieChartItemNode(node);
		}
	}


	

	/**
	 * Iterator that computes Charts by given Index
	 * 
	 * @author amabdelsalam
	 */    
	private class PieChartItemIterator extends AbstractIterator<PieChartItemNode> {

		public PieChartItemIterator(final String PieChartItemIndex) {            
			this.iterator = node.traverse(Order.BREADTH_FIRST, 
					StopEvaluator.DEPTH_ONE, 
					new ReturnableEvaluator() {

				public boolean isReturnableNode(TraversalPosition position) {
					if (position.isStartNode()) {
						return false;
					}
					return position.lastRelationshipTraversed().getEndNode().getProperty(PieChartItemNode.PIE_CHART_ITEM_INDEX).equals(PieChartItemIndex);
				}
			},
			SplashRelationshipTypes.PIE_CHART_ITEM,
			Direction.OUTGOING).iterator();
		}

		@Override
		protected PieChartItemNode wrapNode(Node node) {            
			return new PieChartItemNode(node);
		}
	}

	public PieChartItemNode getPieChartItem(String chartItemName) throws SplashDatabaseException {
		Iterator<PieChartItemNode> iterator = new PieChartItemIterator(chartItemName);
        
        if (iterator.hasNext()) {
            PieChartItemNode result = iterator.next();
            if (iterator.hasNext()) {
                String message = SplashDatabaseExceptionMessages.getFormattedString(SplashDatabaseExceptionMessages.Not_Single_Pie_Chart_Item_by_ID, chartItemName + getPieChartIndex());
                throw new SplashDatabaseException(message);
            }
            
            return result;
        }
		return null;
	}

	public Iterator<PieChartItemNode> getAllPieChartItems() {
		// TODO Auto-generated method stub
		return new AllPieChartItemNodeIterator();
	}
}
