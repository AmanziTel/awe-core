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
import org.neo4j.api.core.Node;

public class PieChartItemNode extends AbstractNode {

	public static final String PIE_CHART_ITEM_NAME = "pie_chart_item_name";
	private static final String PIE_CHART_ITEM_NODE_TYPE = NodeTypes.PIE_CHART_ITEM_NODE_TYPE.getId();//TODO: node types usages (Leave constant!)
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
