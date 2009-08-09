package org.amanzi.splash.neo4j.database.nodes;

import org.amanzi.neo.core.INeoConstants;
import org.neo4j.api.core.Node;

public class PieChartItemNode extends AbstractNode {

	public static final String PIE_CHART_ITEM_NAME = "pie_chart_item_name";
	private static final String PIE_CHART_ITEM_NODE_TYPE = "Spreadsheet_Pie_Chart_Item";
	private static final String PIE_CHART_ITEM_NODE_NAME = "Spreadsheet Pie Chart Item";
	
	private static final String PIE_CHART_ITEM_NODE_CATEGORY = "Spreadsheet Pie Chart Category";
	private static final String PIE_CHART_ITEM_NODE_VALUE = "0.0";
	public static final String PIE_CHART_ITEM_INDEX = "pie_chart_item_index";

	
	public PieChartItemNode(Node node) {
		super(node);
		// TODO Auto-generated constructor stub
		
		 setParameter(INeoConstants.PROPERTY_TYPE_NAME, PIE_CHART_ITEM_NODE_TYPE);
	     setParameter(INeoConstants.PROPERTY_NAME_NAME, PIE_CHART_ITEM_NODE_NAME);
	}
	
	/**
     * Sets the name of Chart
     *
     * @param chartName name of Chart
     */    
    public void setPieChartName(String chartName) {
        setParameter(PIE_CHART_ITEM_NAME, chartName);
    }
    
	public void setPieChartItemIndex(String newChartItemIndex) {
		// TODO Auto-generated method stub

		setParameter(PIE_CHART_ITEM_INDEX, newChartItemIndex);

	}

	public String getPieChartItemIndex() {
		return (String)getParameter(PIE_CHART_ITEM_INDEX);
	}


	public void setPieChartItemCategory(String chartItemCategory) {
		setParameter(PIE_CHART_ITEM_NODE_CATEGORY, chartItemCategory);
	}
	
    public String getPieChartItemCategory() {
        return (String)getParameter(PIE_CHART_ITEM_NODE_CATEGORY);
    }

    public String getPieChartItemValue() {
        return (String)getParameter(PIE_CHART_ITEM_NODE_VALUE);
    }
	

	public void setPieChartItemValue(String chartItemValue) {
		setParameter(PIE_CHART_ITEM_NODE_VALUE, chartItemValue);
	}

	
    
    
    
}
