package org.amanzi.splash.console;

import org.amanzi.integrator.awe.AWEProjectManager;
import org.amanzi.splash.swing.SplashTableModel;
import org.amanzi.splash.utilities.Util;

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
	 * Search for spreadsheet by it's name, name of Ruby Project and name of AWE project
	 * 
	 * @param name name of spreadsheet
	 * @param rdtName name of Ruby Project
	 * @param udigName name of uDIG project
	 * @return Spreadsheet
	 */
	
	public Spreadsheet getSpreadsheet(String name, String rdtName, String udigName) throws SpreadsheetManagerException {
		if ((currentSpreadsheet != null) && !currentSpreadsheet.isSaved()) {
			currentSpreadsheet.save();
		}
		
		String realUdigName = resolveUDIGProjectName(udigName);
		String realRdtName = resolveRDTProjectName(realUdigName, rdtName);
		
		SplashTableModel model = Util.loadTable(realRdtName, name);
		
		currentSpreadsheet = new Spreadsheet(model, realRdtName, name);
		
		return currentSpreadsheet;
	}
	
	/**
	 * Resolves name of AWE project. 
	 * 
	 * @param projectName name of AWE project, if null than method computes name of default project
	 * @return name of AWE project 
	 * @throws SpreadsheetManagerException if project doesn't found 
	 */
	
	private String resolveUDIGProjectName(String projectName) throws SpreadsheetManagerException {
		String realName = AWEProjectManager.findAWEProjectName(projectName);
		
		if (realName == null) {
			throw new SpreadsheetManagerException(projectName);
		}
		
		return realName;
	}
	
	/**
	 * Resolves name of Ruby project
	 * 
	 * @param udigName name of AWE project
	 * @param rdtName name of Ruby project, if null than method computes name of default project
	 * @return name of Ruby project
	 * @throws SpreadsheetManagerException if Ruby project doesnt' found or AWE project doesn't contain Ruby projects
	 */
	
	private String resolveRDTProjectName(String udigName, String rdtName) throws SpreadsheetManagerException {
		String realName = AWEProjectManager.findRubyProjectName(udigName, rdtName);
		
		if (realName == null) {
			throw new SpreadsheetManagerException(udigName, rdtName);
		}
		
		return realName;
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
