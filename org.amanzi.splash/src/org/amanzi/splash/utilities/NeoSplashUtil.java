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
package org.amanzi.splash.utilities;

import java.awt.Color;
import java.awt.Font;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.refractions.udig.catalog.URLUtils;

import org.amanzi.integrator.awe.AWEProjectManager;
import org.amanzi.neo.services.AweProjectService;
import org.amanzi.neo.services.INeoConstants;
import org.amanzi.neo.services.NeoServiceFactory;
import org.amanzi.neo.services.nodes.CellID;
import org.amanzi.neo.services.nodes.RubyProjectNode;
import org.amanzi.neo.services.nodes.SpreadsheetNode;
import org.amanzi.neo.services.ui.NeoServiceProviderUi;
import org.amanzi.neo.services.ui.NeoUtils;
import org.amanzi.splash.compare.SpreadsheetComparator;
import org.amanzi.splash.swing.Cell;
import org.amanzi.splash.swing.SplashTableModel;
import org.amanzi.splash.ui.SplashEditorInput;
import org.amanzi.splash.ui.SplashPlugin;
import org.amanzi.splash.views.importbuilder.ImportBuilderView;
import org.apache.log4j.Logger;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.neo4j.graphdb.Transaction;
import org.rubypeople.rdt.core.IRubyProject;
import org.rubypeople.rdt.internal.ui.wizards.NewRubyElementCreationWizard;

public class NeoSplashUtil {
    private static final Logger LOGGER = Logger.getLogger(NeoSplashUtil.class);
	/*
	 * Name of SplashResourceEditor
	 */
	public static final String AMANZI_SPLASH_EDITOR = "org.amanzi.splash.editor";
	public static final String AMANZI_NEO4J_SPLASH_CHART_EDITOR = "org.amanzi.splash.editors.SplashJFreeChartEditor";
	public static final String AMANZI_NEO4J_SPLASH_PIE_CHART_EDITOR = "org.amanzi.splash.editors.SplashJFreePieChartEditor";
	
	public static Color unselectedHeaderColor = new Color(239, 235, 231);
	public static Color selectedHeaderColor = new Color(250, 209, 132);
	public static Font unselectedHeaderFont = new Font("Tahoma", Font.PLAIN, 12);
	public static Font selectedHeaderFont = new Font("Tahoma", Font.BOLD, 12);
	public static Color selectedCellColor = new Color(232, 242, 254);
	/*
	 * Default extenstion for Spreadsheet file
	 */
	public static final String DEFAULT_SPREADSHEET_EXTENSION = ".splash";

	public static final int MAX_SPLASH_ROW_COUNT = 30;

	public static final int MAX_SPLASH_COL_COUNT = 30;

	public static final boolean enableNeo4j = true;

	public static boolean isDebug = true;

	public static boolean isTesting = false;
	

	public static void logNullAtCell(String func, String value, int row, int column){
		if (isDebug == true){
			NeoSplashUtil.logn(func + ":Null value ("+value+ ") at " + new CellID(row, column));
		}
	}

	public static void printTableModelStatus(SplashTableModel model)
	{
		if (isDebug == true){
			NeoSplashUtil.logn("Model Status:");
			for (int i=0;i<5/*model.getRowCount()*/;i++){
				for (int j=0;j<model.getColumnCount();j++)
				{
					Cell c = (Cell) model.getValueAt(i, j);
					if (c != null)
					{						
						printCell("Cell", c);
						NeoSplashUtil.logn("--------------------------------------------");
					}
					else
					{
						NeoSplashUtil.logn("NULL cell at row="+i+",column="+j);
					}
				}
			}
		}
	}

	public static int tab = 0;

	public static void logEnter(String fn)
	{
		if (isDebug == true){
			NeoSplashUtil.logn("---------------- ENTER: " + fn + "----------------");

			tab += 1;
		}
	}

	public static void logExit(String fn)
	{
		if (isDebug == true){
			NeoSplashUtil.logn("---------------- EXIT: " + fn + "----------------");

			tab -= 1;
		}
	}

	public static void printTableModelStatus(SplashTableModel model, int rowCount, int columnCount)
	{
		if (isDebug == true){
			NeoSplashUtil.logn("Model Status:");
			for (int i=0;i<rowCount;i++){
				for (int j=0;j<columnCount;j++)
				{
					Cell c = (Cell) model.getValueAt(i, j);
					if (c != null)
					{
						printCell("Cell", c);						
					}
					else
					{
						NeoSplashUtil.logn("NULL cell at row="+i+",column="+j);
					}
				}
			}
		}
	}

	public static ArrayList<String> findComplexCellIDs(String content) {
		ArrayList<String> list = new ArrayList<String>();
		String regex = "[a-zA-Z]\\d+";
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(content);

		while (matcher.find()) {
			String cellID = matcher.group();

			list.add(cellID);
		}


		//Util.displayStringList("list.add(cellID)", list);
		return list;
	}
	
	public static ArrayList<String> findComplexCellIDsInRubyText(String content) {
		ArrayList<String> list = new ArrayList<String>();
		String regex = "[a-zA-Z]\\d+";
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(content);

		while (matcher.find()) {
			String cellID = matcher.group();
			if (content.contains("#{" + cellID + "}"))
				list.add(cellID);
		}


		//Util.displayStringList("list.add(cellID)", list);
		return list;
	}


	public static void displayStringList(String title, List<String> list)
	{
		if (isDebug == true){
			if (list == null)
			{
				//Util.log("NULL List !!!\n");
				return;
			}
			NeoSplashUtil.logn(title + ":");
			for (int i=0;i<list.size();i++)
			{
				NeoSplashUtil.logn(list.get(i) + ";");
			}

			NeoSplashUtil.logn("\n");
		}
	}
	public static void printCell(String title, Cell c)
	{
		if (isDebug == true){
			LOGGER.debug(title + ": ");

			LOGGER.debug(c.getCellID() + "\n");
			//printCellList(c.getCellID() + " RFG list:", c.getRfgCells());
			//printCellList(c.getCellID() + " RFD list:", c.getRfdCells());

			NeoSplashUtil.logn("=================================================\n");
		}
	}

	/**
	 * check if cell c in list
	 * @param list
	 * @param c
	 * @return
	 */
	public static boolean isCellinList(ArrayList<Cell> list, Cell c)
	{
		return list.contains(c);
	}

	public static void printCellList(String title, ArrayList<Cell> list)
	{
		if (isDebug == true){
			LOGGER.debug(title + ": ");

			for (int i=0;i<list.size();i++)
				LOGGER.debug(list.get(i).getCellID()+", ");

			LOGGER.debug("\n");
		}
	}



	public static void log(String s)
	{
		if (isDebug){
			LOGGER.debug(s);
		}

	}

	public static void logn(String s)
	{
		if (isDebug){
			LOGGER.debug(s);
		}

	}

	public static boolean isCellInList(Cell c, ArrayList<Cell> list)
	{
		return list.contains(c);
	}

	/**
	 * Utility function that fileName contains Spreadhseet extension or not
	 * 
	 * @param fileName name of file
	 * @return is current file is Spreadsheet file
	 * @author Lagutko_N
	 */

	public static boolean isValidSpreadsheetName(String fileName) {
		return fileName.endsWith(DEFAULT_SPREADSHEET_EXTENSION);
	}

	/**
	 * Utility function that opens file in SplashResourceEditor
	 * 
	 * @param workbench platform workbench
	 * @param spreadsheetName file to open	
	 * @param rdtProjectName name of RDT Project
	 * @return opened editor
	 * @author Lagutko_N
	 */

	public static IEditorPart openSpreadsheet(IWorkbench workbench, URL spreadsheetURL,  String rdtProjectName) {
		IEditorPart result = null;
		try {
			IWorkbenchPage page = workbench.getActiveWorkbenchWindow().getActivePage();
			if (page != null) {			    
				RubyProjectNode root = NeoServiceFactory.getInstance().getProjectService().findRubyProject(rdtProjectName);
				IEditorInput fi = new SplashEditorInput(getSpreadsheetName(spreadsheetURL), root);				
				result = page.openEditor(fi, AMANZI_SPLASH_EDITOR);
			}

		} catch (PartInitException e) {
			result = null;
		}
		return result;
		
	}
	
	public static IEditorPart openSpreadsheet(IWorkbench workbench, SpreadsheetNode node) {
        IEditorPart result = null;
        try {
            IWorkbenchPage page = workbench.getActiveWorkbenchWindow().getActivePage();
            if (!node.hasChildSpreadsheets()) {
                if (page != null) {             
                    IEditorInput fi = new SplashEditorInput(node);              
                    result = page.openEditor(fi, AMANZI_SPLASH_EDITOR);
                }
            }
            else {
                //Lagutko, 1.12.2009, if try to open parent spreadsheet, than open all child spreadsheets
                for (SpreadsheetNode childSheet : node.getAllChildSpreadsheets()) {
                    if (page != null) {             
                        IEditorInput fi = new SplashEditorInput(childSheet);              
                        result = page.openEditor(fi, AMANZI_SPLASH_EDITOR);
                    }
                }
            }

        } catch (PartInitException e) {
            result = null;
        }
        return result;
        
    }	
	
	/**
	 * Computes name of Spreadsheet by given URL
	 *
	 * @param url URL of Spreadsheet
	 * @return name of Spreadsheet
	 */
	
	public static String getSpreadsheetName(URL url) {
		String result = null;
		
		String spreadsheetPath = URLUtils.urlToString(url, true);
		
		int paramSection = spreadsheetPath.lastIndexOf("?");
		String params = spreadsheetPath.substring(paramSection + 1, spreadsheetPath.length());
		
		StringTokenizer paramTokenizer = new StringTokenizer(params, "&");
		while (paramTokenizer.hasMoreTokens()) {
			String param = paramTokenizer.nextToken();
			
			StringTokenizer valueTokenizer = new StringTokenizer(param, "=");
			while (valueTokenizer.hasMoreTokens()) {
				if (valueTokenizer.nextToken().equals(INeoConstants.PROPERTY_NAME_NAME)) {
					result = valueTokenizer.nextToken();
				}
			}
		}
		
		return result;
	}
	
	public static String getFreeSpreadsheetName(String startName, String containerName){
		AweProjectService projectService = NeoServiceFactory.getInstance().getProjectService();
		if(projectService == null){
			return startName;
		}
		RubyProjectNode root = projectService.findRubyProject(containerName);
		if(root == null){
			return startName;
		}
		int i = 1;        
        String oldSpreadsheetName = new String(startName);
        String newSpreadsheetName = new String(startName); 
        String spreadsheetName;
        SpreadsheetNode spreadsheetNode = null;
        do {
            spreadsheetName = newSpreadsheetName;
            spreadsheetNode = projectService.findSpreadsheet(root, spreadsheetName);
            newSpreadsheetName = oldSpreadsheetName.concat(Integer.toString(i++));
        } while (spreadsheetNode != null);
        return spreadsheetName;
	}
	
	/**
	 * Converts name of Spreadsheet to URL
	 *
	 * @param name name of spreadsheet
	 * @return url of spreadsheet
	 */
	public static URL getSpeadsheetURL(String name) throws MalformedURLException {
	    String databaseLocation = NeoServiceProviderUi.getProvider().getDefaultDatabaseLocation();
	    String fullPath = "file://" + databaseLocation + "?" + INeoConstants.PROPERTY_NAME_NAME + "=" + name;
	    
	    return new URL(fullPath);	    
	}
	
	/**
	 * Return ImportBuilder view
	 *
	 * @return view part of ImoprtBuilder
	 */
	public static IViewPart getImportBuilderView() {
	    IViewPart result = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().findView(ImportBuilderView.IMPORT_BUILDER_VIEW_ID);
	    
	    if (result == null) {
	        //if ImportBuilder is not shown in active page than open it
	        try {
	            result = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView(ImportBuilderView.IMPORT_BUILDER_VIEW_ID);
	        }
	        catch (PartInitException e) {
	            SplashPlugin.error(null, e);
	        }
	    }
	    
	    return result;
	}
	
	/**
	 * Runs comparing of Spreadsheet
	 *
	 * @param rubyProject Ruby Project resource that will contain new Spreadsheet
	 * @param firstSpreadsheet first Spreadsheet to compare
	 * @param secondSpreadsheet second Spreadsheet to compare
	 */
	public static void compareSpreadsheets(IProject rubyProject, SpreadsheetNode firstSpreadsheet, SpreadsheetNode secondSpreadsheet) {
	    String newSpreadsheetName = "Delta Report (" + firstSpreadsheet.getName() + " - " + secondSpreadsheet.getName() + ")";
	    
	    SpreadsheetComparator comparator = new SpreadsheetComparator(rubyProject.getFullPath(), newSpreadsheetName, firstSpreadsheet, secondSpreadsheet);
	    
	    Transaction transaction = NeoUtils.beginTransaction();
	    try {
	        comparator.compare();
	    }
	    finally {
	        transaction.success();
	        transaction.finish();
	        NeoServiceProviderUi.getProvider().commit();
	        
	        //Lagutko, 3.12.2009, open a Spreadsheet
	        openSpreadsheet(PlatformUI.getWorkbench(), comparator.getSpreadsheet());
	    }
	}
	   /**
     * configure rubyproject
     *
     * @param aweProjectName - awe project name
     * @param rubyProjectName - ruby project name
     * @return IPath of ruby project
     */
       public static IPath configureRubyPath(String rubyProjectName) {
           String aweProjectName=AWEProjectManager.getActiveProjectName();
           if (rubyProjectName!=null){
               if (!rubyProjectName.startsWith(aweProjectName)){
                   rubyProjectName=aweProjectName+"."+rubyProjectName;
               }
           }
           IRubyProject rubyProject = null;
           try {
               rubyProject = NewRubyElementCreationWizard.configureRubyProject(rubyProjectName, aweProjectName);
           }
           catch (CoreException e) {
               throw (RuntimeException) new RuntimeException( ).initCause( e );
           }
           
           return  rubyProject.getProject().getFullPath();
       }

    /**
     * configure rubyproject
     * 
     * @param aweProjectName - awe project name
     * @param rubyProjectName - ruby project name
     * @return IPath of ruby project
     */
    public static IPath configureRubyPath(String aweProjectName, String rubyProjectName) {
        IRubyProject rubyProject = null;
        try {
            rubyProject = NewRubyElementCreationWizard.configureRubyProject(rubyProjectName, aweProjectName);
        } catch (CoreException e) {
            throw (RuntimeException)new RuntimeException().initCause(e);
        }

        return rubyProject.getProject().getFullPath();
    }

    /**
     * Obtains the current project.
     * 
     * @return The current active project name
     */
    public static String getActiveProjectName() {
        return AWEProjectManager.getActiveProjectName();
    }

    /**
     *open Spreadsheet
     * @param spreadsheet - Spreadsheet node
     */
    public static void openSpreadsheet(SpreadsheetNode spreadsheet) {
        openSpreadsheet(PlatformUI.getWorkbench(),spreadsheet);
    }
}
