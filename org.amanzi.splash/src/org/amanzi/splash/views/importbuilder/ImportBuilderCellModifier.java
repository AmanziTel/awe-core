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

package org.amanzi.splash.views.importbuilder;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.swt.widgets.TableItem;

/**
 * This class implements an ICellModifier
 * An ICellModifier is called when the user modifes a cell in the 
 * tableViewer
 */

public class ImportBuilderCellModifier implements ICellModifier {
	private ImportBuilderTableViewer tableViewer;
	private String[] columnNames;

	/**
	 * Constructor 
	 * @param ImportBuilderTableViewer an instance of a ImportBuilderTableViewer 
	 */
	public ImportBuilderCellModifier(ImportBuilderTableViewer tableViewer) {
		super();
		this.tableViewer = tableViewer;
	}

	/**
	 * @see org.eclipse.jface.viewers.ICellModifier#canModify(java.lang.Object, java.lang.String)
	 */
	public boolean canModify(Object element, String property) {
		return true;
	}

	/**
	 * @see org.eclipse.jface.viewers.ICellModifier#getValue(java.lang.Object, java.lang.String)
	 */
	public Object getValue(Object element, String property) {

		// Find the index of the column
		int columnIndex = tableViewer.getColumnNames().indexOf(property);

		Object result = null;
		ImportBuilderFilter filter = (ImportBuilderFilter) element;

		switch (columnIndex) {
		case 0 : 
			String stringValue = filter.getFilterHeading();
			String[] choices = tableViewer.getChoices(property);
			int i = choices.length - 1;
			while (!stringValue.equals(choices[i]) && i > 0)
				--i;
			result = new Integer(i);					
			break;
		case 1 :  
			result = filter.getFilterText();
			break;

		default :
			result = "";
		}
		return result;	
	}

	/**
	 * @see org.eclipse.jface.viewers.ICellModifier#modify(java.lang.Object, java.lang.String, java.lang.Object)
	 */
	public void modify(Object element, String property, Object value) {	

		// Find the index of the column 
		int columnIndex	= tableViewer.getColumnNames().indexOf(property);

		TableItem item = (TableItem) element;
		ImportBuilderFilter filter = (ImportBuilderFilter) item.getData();
		String valueString;

		switch (columnIndex) {
		case 0 :  
			System.out.println("property: " + property);
			System.out.println("value: " + value);
			//System.out.println("valueString: " + valueString);
			System.out.println("filter: " + filter.getFilterHeading());
			
			if (((Integer) value).intValue() >= 0){
			valueString = tableViewer.getChoices(property)[((Integer) value).intValue()].trim();
				if (!filter.getFilterHeading().equals(valueString)) {
					filter.setFilterHeading(valueString);
				}
			}else{
				//tableViewer.getFiltersList().updateFilterHeading()
				filter.setFilterHeading("r");
				tableViewer.getTable().redraw();
			}
			
			
			
			break;	
		case 1 :  
			valueString = ((String) value).trim();
			filter.setFilterText(valueString);
			break;

		default :
		}
		tableViewer.getFiltersList().filterChanged(filter);
	}
}
