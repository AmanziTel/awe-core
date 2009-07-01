package org.amanzi.splash.neo4j.console;

import org.amanzi.splash.neo4j.swing.SplashTableModel;

/**
 * Spreadsheet Manager class
 * 
 * Provides access to Spreadsheets
 * 
 * @author Lagutko_N
 *
 */

public class SpreadsheetManager {
	
	private SplashTableModel activeModel;
	
	private Spreadsheet currentSpreadsheet;	
	
	private static SpreadsheetManager instance = null;
	
	/**
	 * Returns the instance of SpreadsheetManager
	 * 
	 * @return
	 */
	
	public static SpreadsheetManager getInstance() {
		if (instance == null) {
			instance = new SpreadsheetManager();
		}	
				
		return instance;		
	}
	
	/**
	 * Search for spreadsheet by it's name and name of project
	 * 
	 * @param project name of project
	 * @param name name of spreadsheet
	 * @return Spreadsheet
	 */
	
	public Spreadsheet getSpreadsheet(String project, String name) {
		if (currentSpreadsheet != null) {
			currentSpreadsheet.save();
		}
		
		//SplashTableModel model = Util.loadTable(project, name);
		
		//currentSpreadsheet = new Spreadsheet(model, project, name);
		
		return currentSpreadsheet;
	}
	
	/**
	 * Returns Spreadsheet that is currently opened
	 * 
	 * @return
	 */
	
	public Spreadsheet getActiveSpreadsheet() {		
		return new Spreadsheet(activeModel, null, null);
	}
	
	public void setActiveModel(SplashTableModel model) {
		if (currentSpreadsheet != null) {
			currentSpreadsheet.save();
		}
		
		activeModel = model;
	}

}
