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
import org.neo4j.api.core.Direction;
import org.neo4j.api.core.Node;

public class ChartItemNode extends AbstractNode implements Comparable<ChartItemNode>{

	/** String DEFAULT_CHART_ITEM_VALUE field */
    private static final String DEFAULT_CHART_ITEM_VALUE = "0";
    
    public static final String CHART_ITEM_NAME = "chart_item_name";
	private static final String CHART_ITEM_NODE_NAME = "Spreadsheet Chart Item";
	
	private static final String CHART_ITEM_NODE_CATEGORY = "Spreadsheet Chart Category";
	private static final String CHART_ITEM_NODE_SERIES = "Spreadsheet Chart Series";
	private static final String CHART_ITEM_NODE_VALUE = "0.0";
	public static final String CHART_ITEM_INDEX = "chart_item_index";

	
	
	
	
	
	public ChartItemNode(Node node) {
		super(node);
		// TODO Auto-generated constructor stub
		
		 setParameter(INeoConstants.PROPERTY_TYPE_NAME, NodeTypes.CHART_ITEM.getId());
	     setParameter(INeoConstants.PROPERTY_NAME_NAME, CHART_ITEM_NODE_NAME);
	}
	
	/**
     * Sets the name of Chart
     *
     * @param chartName name of Chart
     */    
    public void setChartName(String chartName) {
        setParameter(CHART_ITEM_NAME, chartName);
    }
    
	public void setChartItemIndex(Integer newChartItemIndex) {
		setParameter(CHART_ITEM_INDEX, newChartItemIndex);

	}

	public Integer getChartItemIndex() {
		return (Integer)getParameter(CHART_ITEM_INDEX);
	}


	public void setChartItemCategory(String chartItemCategory) {
		setParameter(CHART_ITEM_NODE_CATEGORY, chartItemCategory);
	}
	
    public String getChartItemCategory() {
        return (String)getParameter(CHART_ITEM_NODE_CATEGORY);
    }

    public String getChartItemValue() {
        //Lagutko, 2.11.2009, if chartItemValue is null or empty string than we should set it to default
        //to have no exception on parsing of this value
        String itemValue = (String)getParameter(CHART_ITEM_NODE_VALUE);
        if ((itemValue == null) || (itemValue.length() == 0)) {
            itemValue = DEFAULT_CHART_ITEM_VALUE;
        }
        return (String)getParameter(CHART_ITEM_NODE_VALUE);
    }
	

	public void setChartItemValue(String chartItemValue) {
		setParameter(CHART_ITEM_NODE_VALUE, chartItemValue);
	}

    public void setChartItemSeries(String series) {
        setParameter(CHART_ITEM_NODE_SERIES, series);
    }

    public String getChartItemSeries() {
        return (String)getParameter(CHART_ITEM_NODE_SERIES);
    }
    
    /**
     * Adds given cell node to this chart item node as category
     *
     * @param cell Cell
     */    
    public void addCategoryNode(CellNode category) {
        addRelationship(SplashRelationshipTypes.CHART_CATEGORY, category.getUnderlyingNode());
    }
    /**
     * Adds given cell node to this chart item node as value
     *
     * @param cell Cell
     */    
    public void addValueNode(CellNode value) {
        addRelationship(SplashRelationshipTypes.CHART_VALUE, value.getUnderlyingNode());
    }
    /**
     * Getter for category node for current chart item node
     *
     * @return cell node representing category node
     */
    public CellNode getCategoryNode(){
        return CellNode.fromNode(node.getSingleRelationship(SplashRelationshipTypes.CHART_CATEGORY, Direction.OUTGOING).getEndNode());
    }
    /**
     * Getter for value node for current chart item node
     *
     * @return cell node representing value node
     */
    public CellNode getValueNode(){
        return CellNode.fromNode(node.getSingleRelationship(SplashRelationshipTypes.CHART_VALUE, Direction.OUTGOING).getEndNode());
    }

    @Override
    public int compareTo(ChartItemNode o) {
        return getChartItemIndex().compareTo(o.getChartItemIndex());
    }

   
}
