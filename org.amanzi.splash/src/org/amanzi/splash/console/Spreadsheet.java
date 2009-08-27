package org.amanzi.splash.console;

import org.amanzi.neo.core.database.nodes.CellID;
import org.amanzi.splash.swing.Cell;
import org.amanzi.splash.swing.SplashTableModel;

/**
 * Spreadsheet class
 * 
 * Provides access to Cells from AWE Script Console and Ruby Scripts
 * 
 * @author Lagutko_N
 */

public class Spreadsheet {
	
	private SplashTableModel  model;
	
	/**
	 * Constructor for Spreadsheet
	 * 
	 * @param currentModel table model
	 * @param project name of project
	 * @param name naem of spreadsheet
	 */	
	public Spreadsheet(SplashTableModel currentModel, String project, String name) {
		model = currentModel;
	}
	
	/**
	 * Return value stored in Spreadsheet Cell with cellId
	 * 
	 * @param cellID id of Cell
	 * @return value of Cell
	 */
	
	public String getValue(String cellID) {
		CellID id = new CellID(cellID);
		Cell cell = (Cell)model.getValueAt(id.getRowIndex(), id.getColumnIndex());	
		return (String)cell.getValue();
	}
	
	/**
	 * Set the value to Cell of Spreadsheet
	 * 
	 * @param cellID id of Cell
	 * @param newFormula new value of Cell
	 */
	
	public void setValue(String cellID, String newFormula) {
	    CellID id = new CellID(cellID);
        Cell cell = (Cell)model.getValueAt(id.getRowIndex(), id.getColumnIndex());
		
		String oldFormula = (String)cell.getDefinition();
		
		model.interpret(newFormula, oldFormula, id.getRowIndex(), id.getColumnIndex());
		
		//TODO: Lagutko: is it needs?
		//model.updateCellsAndTableModelReferences(row, column, oldFormula, newFormula);
	}
}
