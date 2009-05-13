package org.amanzi.splash.utilities;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.script.ScriptEngineFactory;
import javax.script.ScriptEngineManager;
import javax.swing.JTable;


import org.amanzi.splash.swing.Cell;
import org.amanzi.splash.swing.SplashTableModel;

import com.eteks.openjeks.format.CellFormat;

public class Util {
	
	private static boolean isDebug = true;

	public static String ColorToString (Color c)
	{
		String s = Integer.toString(c.getRed()) + ";" + Integer.toString(c.getGreen() )
		+ ";" + Integer.toString(c.getBlue());

		return s;
	}
	
	public static void logNullAtCell(String func, String value, int row, int column){
		Util.log(func + ":Null value ("+value+ ") at " + Util.getCellIDfromRowColumn(row, column));
	}

	public static void printTableModelStatus(SplashTableModel model)
	{
		Util.log("Model Status:");
		for (int i=0;i<5/*model.getRowCount()*/;i++){
			for (int j=0;j<model.getColumnCount();j++)
			{
				Cell c = (Cell) model.getValueAt(i, j);
				if (c != null)
				{
					printCell("Cell", c);
					printCellList("RFD List of Cell " + c.getCellID(), c.getRfdCells());
					printCellList("RFG List of Cell " + c.getCellID(), c.getRfgCells());
					Util.log("--------------------------------------------");
				}
				else
				{
					Util.log("NULL cell at row="+i+",column="+j);
				}
			}
		}
	}
	public static void listScriptingEngines() {
		ScriptEngineManager mgr = new ScriptEngineManager();
		for (ScriptEngineFactory factory : mgr.getEngineFactories()) {
			Util.log("ScriptEngineFactory Info");
			//Util.log("\tScript Engine: %s (%s)\n", factory.getEngineName(), factory.getEngineVersion());
			//Util.log("\tLanguage: %s (%s)\n", factory.getLanguageName(), factory.getLanguageVersion());
//			for (String name : factory.getNames()) {
//				Util.logf("\tEngine Alias: %s\n", name);
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
		if (list == null)
		{
			//Util.log("NULL List !!!\n");
			return;
		}
		Util.log(title + ":");
		for (int i=0;i<list.size();i++)
		{
			Util.log(list.get(i) + ";");
		}

		Util.log("\n");
	}
	public static void printCell(String title, Cell c)
	{
		Util.log(title + ": ");

		Util.log(c.getCellID());
		printCellList(c.getCellID() + " RFG list:", c.getRfgCells());
		printCellList(c.getCellID() + " RFD list:", c.getRfdCells());

		Util.log("=================================================\n");
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
		Util.log(title + ": ");

		for (int i=0;i<list.size();i++)
			Util.log(list.get(i).getCellID()+", ");

		Util.log("\n");
	}
	
	

	public static void log(String s)
	{
		if (isDebug)
			System.out.println(s);
	}

	public static String getCellIDfromRowColumn(int row, int column)
	{
		//JTable t = new JTable(row+1,column+1);
		//return t.getColumnName(column);

		//String cc = (String) t.getColumnModel().getColumn(column).getHeaderValue();
		// TODO: here, there is a bug with columns more than 26
		String STD_HEADINGS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
		String cc = new Character(STD_HEADINGS.charAt(column%26)).toString();
		return cc + Integer.toString(row+1);
	}

	public static boolean isCellInList(Cell c, ArrayList<Cell> list)
	{
		boolean ret = false;
		if (c == null || list == null) return false;
		for (int i=0;i<list.size();i++)
		{
			if (list.get(i).getCellID().equals(c.getCellID()));
			{
				ret = true;
				break;
			}
		}

		return ret;
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

		// TODO: if cell column ID is more than one letter, this method will fail
		String cc = new Character(cellID.toUpperCase().charAt(0)).toString();
		return STD_HEADINGS.indexOf(cc);
	}

}
