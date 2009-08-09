package org.amanzi.splash.neo4j.utilities;

import java.awt.Color;
import java.awt.Font;
import java.io.BufferedReader;
import java.io.File;
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

import org.amanzi.neo.core.INeoConstants;
import org.amanzi.neo.core.service.NeoServiceProvider;
import org.amanzi.splash.neo4j.database.nodes.RootNode;
import org.amanzi.splash.neo4j.swing.Cell;
import org.amanzi.splash.neo4j.swing.SplashTableModel;
import org.amanzi.splash.neo4j.ui.SplashEditorInput;
import org.amanzi.splash.neo4j.ui.SplashPlugin;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;

public class NeoSplashUtil {

	/*
	 * Name of SplashResourceEditor
	 */
	public static final String AMANZI_SPLASH_EDITOR = "org.amanzi.splash.neo4j.editor";
	public static final String AMANZI_NEO4J_SPLASH_CHART_EDITOR = "org.amanzi.splash.editors.neo4j.SplashJFreeChartEditor";
	public static final String AMANZI_NEO4J_SPLASH_PIE_CHART_EDITOR = "org.amanzi.splash.editors.neo4j.SplashJFreePieChartEditor";
	
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
			NeoSplashUtil.logn(func + ":Null value ("+value+ ") at " + NeoSplashUtil.getCellIDfromRowColumn(row, column));
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
			System.out.print(title + ": ");

			System.out.print(c.getCellID() + "\n");
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
			System.out.print(title + ": ");

			for (int i=0;i<list.size();i++)
				System.out.print(list.get(i).getCellID()+", ");

			System.out.print("\n");
		}
	}



	public static void log(String s)
	{
		if (isDebug){
			System.out.print(s);
		}

	}

	public static void logn(String s)
	{
		if (isDebug){
			System.out.println(s);
		}

	}

	public static String getCellIDfromRowColumn(int row, int column)
	{
		String letterIndex = getColumnLetter(column);
		
		return letterIndex + Integer.toString(row+1);
	}
	
	public static String getColumnLetter(int columnIndex) {
	    //Lagutko,  4.06.2009, correct bug with columns more than 26
        String STD_HEADINGS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        StringBuilder letterIndex = new StringBuilder();        
        int iColumn = columnIndex;
        
        letterIndex.insert(0, STD_HEADINGS.charAt(columnIndex % 26));
        
        iColumn = iColumn / 26;
        
        STD_HEADINGS = "AABCDEFGHIJKLMNOPQRSTUVWXYZ";
        
        while (iColumn > 0) {
            int index = iColumn % 27;
            
            letterIndex.insert(0, STD_HEADINGS.charAt(index));
            
            iColumn = iColumn / 27;
        }     
        
        return letterIndex.toString();
	}

	public static boolean isCellInList(Cell c, ArrayList<Cell> list)
	{
		return list.contains(c);
	}

	public static int getRowIndexFromCellID(String cellID) {
		String regex = "\\d+";
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(cellID);

		int ret = 0;
		while (matcher.find()) {
			ret = Integer.parseInt(matcher.group());
		}

		return ret-1;
	}
	
	public static int getColumnIndexFromCellID(String cellID) {
		String STD_HEADINGS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

		//Lagutko, 4.06.2009, CellId can contain more than one letter, so count ColumnIndex until we have letter in CellId
		String id = cellID.toUpperCase();
		int i = 0;
		
		char c;
		int index = 0;
		
		while (!Character.isDigit(c = id.charAt(i))) {			
			index = index * 26;
			
			index = index + (STD_HEADINGS.indexOf(c) + 1);
			
			i++;
		}
		
		return index - 1;
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
	 * @param rdgProjectName name of RDT Project
	 * @return opened editor
	 * @author Lagutko_N
	 */

	public static IEditorPart openSpreadsheet(IWorkbench workbench, URL spreadsheetURL, String rdgProjectName) {
		IEditorPart result = null;
		try {
			IWorkbenchPage page = workbench.getActiveWorkbenchWindow().getActivePage();
			if (page != null) {
				//TODO: Lagutko: must be added computing for Root Node of Spreadsheet
			    //Lagutko: it's a fake because for now Root Node is a Reference Node
			    RootNode root = SplashPlugin.getDefault().getSpreadsheetService().getRootNode();
				IEditorInput fi = new SplashEditorInput(getSpreadsheetName(spreadsheetURL), root);				
				result = page.openEditor(fi, AMANZI_SPLASH_EDITOR);
			}

		} catch (PartInitException e) {
			result = null;
		}
		return result;
		
	}

	/**
	 * Returns content of script
	 * 
	 * @param scriptURI URI of script
	 * @return string with content of file
	 * @author Lagutko_N
	 */
	public static String getScriptContent(URI scriptURI) {
		if (scriptURI == null) {
			//TODO: handle this situation
			return null;
		}
		IFile[] files = ResourcesPlugin.getWorkspace().getRoot().findFilesForLocationURI(scriptURI);
		if (files.length != 1) {
			//TODO: handle this situation
			return null;
		}
		String content = null;
		try {
			content = inputStreamToString(files[0].getContents());
		}
		catch (CoreException e) {
			//TODO: handle exception
		}
		catch (IOException e) {
			//TODO: handle exception
		}

		return content;
	}
	
	/**
     * Returns content of script
     * 
     * @param scriptPath Path of script
     * @return string with content of file
     * @author Lagutko_N
     */
    public static String getScriptContent(String scriptPath) {
        if (scriptPath == null) {
            //TODO: handle this situation
            return null;
        }                
        String content = null;
        try {
            content = inputStreamToString(new FileInputStream(scriptPath));
        }        
        catch (IOException e) {
            //TODO: handle exception
        }

        return content;
    }
	
	/**
	 * Utility function that converts input stream to String
	 * 
	 * @param stream InputStream
	 * @return String
	 * @throws IOException 
	 * @author Lagutko_N
	 */

	private static String inputStreamToString(InputStream stream) throws IOException {
		StringBuffer buffer = new StringBuffer();
		BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
		String line;
		while ((line = reader.readLine()) != null) {
			buffer.append(line).append("\n");
		}
		reader.close();
		return buffer.toString();
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
	
	/**
	 * Converts name of Spreadsheet to URL
	 *
	 * @param name name of spreadsheet
	 * @return url of spreadsheet
	 */
	public static URL getSpeadsheetURL(String name) {
	    String databaseLocation = NeoServiceProvider.getProvider().getDefaultDatabaseLocation();
	    String fullPath = databaseLocation + "?" + INeoConstants.PROPERTY_NAME_NAME + "=" + name;
	    
	    try {
	        return URLUtils.constructURL(new File("."), fullPath);
	    }
	    catch (MalformedURLException e) {
	        SplashPlugin.error(null, e);
	        return null;
	    }
	}
}
