package org.amanzi.splash.jruby;

import org.amanzi.splash.utilities.Util;





public class JRubyJavaInterface {
	public static void UpdateCellValueInSpreadsheet(String cell_id, String cell_value)
	{
		String CellID = cell_id;
		String CellValue = cell_value;
		
		// TODO: SplashCell(cell_id) should be updated with cell_value
		Util.log("Updating spreadsheet cell: " + CellID + " with value: " + CellValue);
		
		
	}
	
	public static String UpdateCellValueFromSpreadsheet(String cell_id)
	{
		// TODO: This should be replaced by function to get content of cell(cell_id)
		//System.out.println("Updating spreadsheet cell: " + CellID + " with value: " + CellValue);
		return "Hello World!";
	}
}
