package org.amanzi.splash.neo4j.console;

import org.amanzi.integrator.awe.AWEProjectManager;
import org.amanzi.neo.core.NeoCorePlugin;
import org.amanzi.neo.core.database.nodes.RubyProjectNode;
import org.amanzi.neo.core.database.nodes.SpreadsheetNode;
import org.amanzi.splash.neo4j.swing.SplashTableModel;
import org.amanzi.splash.neo4j.ui.SplashPlugin;
import org.amanzi.splash.neo4j.utilities.ActionUtil;
import org.amanzi.splash.neo4j.utilities.ActionUtil.RunnableWithResult;


/**
 * Spreadsheet Manager class
 * 
 * Provides access to Spreadsheets
 * 
 * @author Lagutko_N
 *
 */

public class NeoSplashManager {
	
	private SplashTableModel activeModel;
	
	private Spreadsheet currentSpreadsheet;	
	
	private static NeoSplashManager instance = null;
	
	/**
	 * Returns the instance of SpreadsheetManager
	 * 
	 * @return
	 */
	
	public static NeoSplashManager getInstance() {
		if (instance == null) {
			instance = new NeoSplashManager();
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
	
	public Spreadsheet getSpreadsheet(final String name,  String rdtName, String udigName) throws SpreadsheetManagerException {
		final String realUdigName = resolveUDIGProjectName(udigName);
		final String realRdtName = resolveRDTProjectName(realUdigName, rdtName);
		
		final SpreadsheetNode spreadsheet = (SpreadsheetNode)ActionUtil.getInstance().runTaskWithResult(new RunnableWithResult() {
		    
		    private SpreadsheetNode result;

            public Object getValue() {
                return result;
            }

            public void run() {
                RubyProjectNode rootNode = NeoCorePlugin.getDefault().getProjectService().findRubyProject(realUdigName);//getSpreadsheetRoot(realUdigName, realRdtName);
                result = SplashPlugin.getDefault().getSpreadsheetService().findSpreadsheet(rootNode, name);
            }		    
		});
		
		if (spreadsheet == null) {
		    throw new SpreadsheetManagerException(realRdtName, realUdigName, name);
		}
		
		SplashTableModel model = new SplashTableModel(spreadsheet);
		
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
		activeModel = model;
	}

}
