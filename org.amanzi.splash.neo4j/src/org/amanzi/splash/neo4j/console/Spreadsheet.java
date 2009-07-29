package org.amanzi.splash.neo4j.console;

import org.amanzi.splash.neo4j.swing.Cell;
import org.amanzi.splash.neo4j.swing.SplashTableModel;
import org.amanzi.splash.neo4j.utilities.NeoSplashUtil;

/**
 * Spreadsheet class
 * 
 * Provides access to Cells
 * 
 * @author Lagutko_N
 *
 */

public class Spreadsheet {
	
	private SplashTableModel model;
	private String currentFile;
	
	/**
	 * Constructor for Spreadsheet
	 * 
	 * @param currentModel table model
	 * @param project name of project
	 * @param name naem of spreadsheet
	 */
	
	public Spreadsheet(SplashTableModel currentModel, String project, String name) {
		model = currentModel;
		currentFile = name;
	}
	
	/**
	 * Return value stored in Spreadsheet Cell with cellId
	 * 
	 * @param cellID id of Cell
	 * @return value of Cell
	 */
	
	public String getValue(String cellID) {
		int column = NeoSplashUtil.getColumnIndexFromCellID(cellID);
		int row = NeoSplashUtil.getRowIndexFromCellID(cellID);
		Cell cell = (Cell)model.getValueAt(row, column);		
		return (String)cell.getValue();
	}
	
	/**
	 * Set the value to Cell of Spreadsheet
	 * 
	 * @param cellID id of Cell
	 * @param newFormula new value of Cell
	 */
	
	public void setValue(String cellID, String newFormula) {
		int column = NeoSplashUtil.getColumnIndexFromCellID(cellID);
		int row = NeoSplashUtil.getRowIndexFromCellID(cellID);
		Cell cell = (Cell)model.getValueAt(row, column);
		
		String oldFormula = (String)cell.getDefinition();
		
		model.interpret(newFormula, oldFormula, row, column);
		
		
	}
	
	/**
	 * Saves content of spreadsheet to file
	 */
	
	public void save() {	
		if (currentFile != null) {
			//Util.saveTable(model, currentProject, currentFile);
		}
	}

}
