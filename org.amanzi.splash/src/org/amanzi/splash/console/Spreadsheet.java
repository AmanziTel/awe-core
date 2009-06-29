package org.amanzi.splash.console;

import org.amanzi.splash.swing.Cell;
import org.amanzi.splash.swing.SplashTableModel;
import org.amanzi.splash.utilities.Util;

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
	private String currentProject;
	private String currentFile;
	private boolean isSaved;
	
	
	/**
	 * Constructor for Spreadsheet
	 * 
	 * @param currentModel table model
	 * @param project name of project
	 * @param name naem of spreadsheet
	 */
	
	public Spreadsheet(SplashTableModel currentModel, String project, String name) {
		model = currentModel;
		currentProject = project;
		currentFile = name;
	}
	
	/**
	 * Return value stored in Spreadsheet Cell with cellId
	 * 
	 * @param cellID id of Cell
	 * @return value of Cell
	 */
	
	public String getValue(String cellID) {
		int column = Util.getColumnIndexFromCellID(cellID);
		int row = Util.getRowIndexFromCellID(cellID);
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
		int column = Util.getColumnIndexFromCellID(cellID);
		int row = Util.getRowIndexFromCellID(cellID);
		Cell cell = (Cell)model.getValueAt(row, column);
		
		String oldFormula = (String)cell.getDefinition();
		
		model.interpret(newFormula, oldFormula, row, column);
		
		model.updateCellsAndTableModelReferences(row, column, oldFormula, newFormula);
		
		isSaved = false;
	}
	
	/**
	 * Saves content of spreadsheet to file
	 */
	
	public void save() {	
		if (currentFile != null) {
			Util.saveTable(model, currentProject, currentFile);
		}
		isSaved = true;
	}
	
	/**
	 * Is this spreadsheet was saved?
	 * 
	 * @return
	 */
	
	public boolean isSaved() {
		return isSaved;
	}

}
