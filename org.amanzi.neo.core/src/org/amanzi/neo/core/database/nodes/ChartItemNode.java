package org.amanzi.neo.core.database.nodes;

import org.amanzi.neo.core.INeoConstants;
import org.neo4j.api.core.Node;

public class ChartItemNode extends AbstractNode {

	public static final String CHART_ITEM_NAME = "chart_item_name";
	private static final String CHART_ITEM_NODE_TYPE = "Spreadsheet_Chart_Item";
	private static final String CHART_ITEM_NODE_NAME = "Spreadsheet Chart Item";
	
	private static final String CHART_ITEM_NODE_CATEGORY = "Spreadsheet Chart Category";
	private static final String CHART_ITEM_NODE_VALUE = "0.0";
	public static final String CHART_ITEM_INDEX = "chart_item_index";

	
	
	
	
	
	public ChartItemNode(Node node) {
		super(node);
		// TODO Auto-generated constructor stub
		
		 setParameter(INeoConstants.PROPERTY_TYPE_NAME, CHART_ITEM_NODE_TYPE);
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
    
	public void setChartItemIndex(String newChartItemIndex) {
		// TODO Auto-generated method stub

		setParameter(CHART_ITEM_INDEX, newChartItemIndex);

	}

	public String getChartItemIndex() {
		return (String)getParameter(CHART_ITEM_INDEX);
	}


	public void setChartItemCategory(String chartItemCategory) {
		setParameter(CHART_ITEM_NODE_CATEGORY, chartItemCategory);
	}
	
    public String getChartItemCategory() {
        return (String)getParameter(CHART_ITEM_NODE_CATEGORY);
    }

    public String getChartItemValue() {
        return (String)getParameter(CHART_ITEM_NODE_VALUE);
    }
	

	public void setChartItemValue(String chartItemValue) {
		setParameter(CHART_ITEM_NODE_VALUE, chartItemValue);
	}

	
    
    
    
}
