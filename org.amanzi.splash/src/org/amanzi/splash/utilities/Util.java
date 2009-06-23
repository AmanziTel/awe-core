package org.amanzi.splash.utilities;

import java.awt.Color;
import java.awt.Font;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.script.ScriptEngineFactory;
import javax.script.ScriptEngineManager;

import org.amanzi.splash.swing.Cell;
import org.amanzi.splash.swing.SplashTableModel;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.FileEditorInput;

import com.eteks.openjeks.format.CellFormat;

public class Util {

	/*
	 * Name of SplashResourceEditor
	 */
	public static final String AMANZI_SPLASH_EDITOR = "org.amanzi.splash.editor";

	public static Color unselectedHeaderColor = new Color(239, 235, 231);
	public static Color selectedHeaderColor = new Color(250, 209, 132);
	public static Font unselectedHeaderFont = new Font("Tahoma", Font.PLAIN, 12);
	public static Font selectedHeaderFont = new Font("Tahoma", Font.BOLD, 12);
	public static Color selectedCellColor = new Color(232, 242, 254);
	/*
	 * Default extenstion for Spreadsheet file
	 */
	public static final String DEFAULT_SPREADSHEET_EXTENSION = ".jrss";

	public static boolean isDebug = true;

	public static boolean isTesting = false;
	public static String ColorToString (Color c)
	{
		String s = Integer.toString(c.getRed()) + ";" + Integer.toString(c.getGreen() )
		+ ";" + Integer.toString(c.getBlue());

		return s;
	}
	
	public static void AddFileMenuItem(){
//		MenuManager menu = 
//		    new  MenuManager(ShowcasePlugin.getResourceString("menu.file"),
//		IWorkbenchActionConstants.M_FILE);
//		menu.add(new GroupMarker(IWorkbenchActionConstants.FILE_START));
//		String newId = ActionFactory.NEW.getId();
//		MenuManager newMenu =
//		    new MenuManager(ShowcasePlugin.getResourceString("menu.file.new"),
//		newId);
//		newMenu.add(new Separator(newId));
//		IContributionItem newWizards =
//		ContributionItemFactory.NEW_WIZARD_SHORTLIST.create(window);
//		newMenu.add(newWizards);
//		       newMenu.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
//		menu.add(newMenu);

	}

	public static void logNullAtCell(String func, String value, int row, int column){
		if (isDebug == true){
			Util.logn(func + ":Null value ("+value+ ") at " + Util.getCellIDfromRowColumn(row, column));
		}
	}

	public static void printTableModelStatus(SplashTableModel model)
	{
		if (isDebug == true){
			Util.logn("Model Status:");
			for (int i=0;i<5/*model.getRowCount()*/;i++){
				for (int j=0;j<model.getColumnCount();j++)
				{
					Cell c = (Cell) model.getValueAt(i, j);
					if (c != null)
					{						
						printCell("Cell", c);
						printCellList("RFD List of Cell " + c.getCellID(), c.getRfdCells());
						printCellList("RFG List of Cell " + c.getCellID(), c.getRfgCells());
						Util.logn("--------------------------------------------");
					}
					else
					{
						Util.logn("NULL cell at row="+i+",column="+j);
					}
				}
			}
		}
	}

	public static int tab = 0;

	public static void addtab()
	{
//		for (int i=0;i<tab;i++)	System.out.print(" ");
//		if (tab == 0)
//		System.out.print("\n<><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><>\n");
	}

	public static void logEnter(String fn)
	{
		if (isDebug == true){
			addtab();

			Util.logn("---------------- ENTER: " + fn + "----------------");

			tab += 1;
		}
	}

	public static void logExit(String fn)
	{
		if (isDebug == true){
			addtab();
			Util.logn("---------------- EXIT: " + fn + "----------------");

			tab -= 1;
		}
	}

	public static void printTableModelStatus(SplashTableModel model, int rowCount, int columnCount)
	{
		if (isDebug == true){
			Util.logn("Model Status:");
			for (int i=0;i<rowCount;i++){
				for (int j=0;j<columnCount;j++)
				{
					Cell c = (Cell) model.getValueAt(i, j);
					if (c != null)
					{
						printCell("Cell", c);
						//printCellList("RFD List of Cell " + c.getCellID(), c.getRfdCells());
						//printCellList("RFG List of Cell " + c.getCellID(), c.getRfgCells());
						//Util.logn("--------------------------------------------");
					}
					else
					{
						Util.logn("NULL cell at row="+i+",column="+j);
					}
				}
			}
		}
	}

	public static void listScriptingEngines() {
		ScriptEngineManager mgr = new ScriptEngineManager();
		for (ScriptEngineFactory factory : mgr.getEngineFactories()) {
			Util.logn("ScriptEngineFactory Info");
			//Util.log("\tScript Engine: %s (%s)\n", factory.getEngineName(), factory.getEngineVersion());
			//Util.log("\tLanguage: %s (%s)\n", factory.getLanguageName(), factory.getLanguageVersion());
//			for (String name : factory.getNames()) {
//			Util.logf("\tEngine Alias: %s\n", name);
//			}
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


	public static String getFormatString(CellFormat c)
	{
		Color bgColor = c.getBackgroundColor();

		Color fontColor = c.getFontColor();
		String fontName = c.getFontName();
		int fontSize = c.getFontSize();
		int fontStyle = c.getFontStyle();
		int horizontalAlignment = c.getHorizontalAlignment();
		int verticalAlignment = c.getVerticalAlignment();

		return 
		ColorToString(bgColor) + ";" + 
		ColorToString(fontColor) + ";" +
		fontName + ";" +
		Integer.toString(fontSize) + ";" +
		Integer.toString(fontStyle) + ";" +
		Integer.toString(horizontalAlignment) + ";" +
		Integer.toString(verticalAlignment);
	}

	public static void displayStringList(String title, List<String> list)
	{
		if (isDebug == true){
			if (list == null)
			{
				//Util.log("NULL List !!!\n");
				return;
			}
			Util.logn(title + ":");
			for (int i=0;i<list.size();i++)
			{
				Util.logn(list.get(i) + ";");
			}

			Util.logn("\n");
		}
	}
	public static void printCell(String title, Cell c)
	{
		if (isDebug == true){
			addtab();
			System.out.print(title + ": ");

			System.out.print(c.getCellID() + "\n");
			//printCellList(c.getCellID() + " RFG list:", c.getRfgCells());
			//printCellList(c.getCellID() + " RFD list:", c.getRfdCells());

			Util.logn("=================================================\n");
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
			addtab();
			System.out.print(title + ": ");

			for (int i=0;i<list.size();i++)
				System.out.print(list.get(i).getCellID()+", ");

			System.out.print("\n");
		}
	}



	public static void log(String s)
	{
		if (isDebug){
			addtab();
			System.out.print(s);
		}

	}

	public static void logn(String s)
	{
		if (isDebug){
			addtab();
			System.out.println(s);
		}

	}

	public static String getCellIDfromRowColumn(int row, int column)
	{
		//JTable t = new JTable(row+1,column+1);
		//return t.getColumnName(column);

		//String cc = (String) t.getColumnModel().getColumn(column).getHeaderValue();
		
		//Lagutko,  4.06.2009, correct bug with columns more than 26
		String STD_HEADINGS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
		StringBuilder letterIndex = new StringBuilder();		
		int iColumn = column;
		
		letterIndex.insert(0, STD_HEADINGS.charAt(column % 26));
		
		iColumn = iColumn / 26;
		
		STD_HEADINGS = "AABCDEFGHIJKLMNOPQRSTUVWXYZ";
		
		while (iColumn > 0) {
			int index = iColumn % 27;
			
			letterIndex.insert(0, STD_HEADINGS.charAt(index));
			
			iColumn = iColumn / 27;
		}		
		
		return letterIndex + Integer.toString(row+1);
	}

	public static boolean isCellInList(Cell c, ArrayList<Cell> list)
	{
//		boolean ret = false;

//		Util.logn("isCellInList: Cell " + c.getCellID());
//		Util.printCellList("isCellInList: ", list);

//		if (c == null || list == null) return false;

//		for (int i=0;i<list.size();i++)
//		{
//		String s1 = list.get(i).getCellID();
//		Util.logn("isCellInList: s1 = " + s1);
//		String s2 = c.getCellID();
//		Util.logn("isCellInList: s2 = " + s2);
//		if (s1.equals(s2));
//		{
//		return true;
//		}
//		}

//		return false;

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
	 * @param file file to open	
	 * @return opened editor
	 * @author Lagutko_N
	 */

	public static IEditorPart openSpreadsheet(IWorkbench workbench, IFile file) {
		IEditorPart result = null;
		try {
			IWorkbenchPage page = workbench.getActiveWorkbenchWindow().getActivePage();
			if (page != null) {
				IFileEditorInput fi = new FileEditorInput(file);				
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
	 * Utility function that saves TableModel to file
	 * 
	 * @param spreadsheet
	 * @param model
	 */
	
	public static void saveTable(SplashTableModel model, String project, String name) {
		IFile file = ResourcesPlugin.getWorkspace().getRoot().getProject(project).getFile(name + DEFAULT_SPREADSHEET_EXTENSION);
		
		try {
			StringBuffer sb = new StringBuffer();
			model.save(sb);			
			file.setContents(
							new ByteArrayInputStream(sb.toString().getBytes()),
							IResource.KEEP_HISTORY, null);            
        } catch (CoreException e) {
        	//TODO: handle this exception
            e.printStackTrace();
        }
	}
	
	/**
	 * Utility function that load TableModel from file
	 * 
	 * @param project name of project
	 * @param name name of file
	 * @return model of table
	 */
	
	public static SplashTableModel loadTable(String project, String name) {		
		IFile file = ResourcesPlugin.getWorkspace().getRoot().getProject(project).getFile(name + DEFAULT_SPREADSHEET_EXTENSION);
		
		SplashTableModel model = null;
		
		try {
			model = new SplashTableModel(file.getContents());
		}
		catch (CoreException e) {
			//TODO: handle this exception
			e.printStackTrace();
		}
		
		return model;
	}
}
