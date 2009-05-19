package org.amanzi.splash.swing;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;

import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

import org.amanzi.scripting.jruby.ScriptUtils;
import org.amanzi.splash.ui.SplashPlugin;
import org.amanzi.splash.utilities.Util;
import org.eclipse.core.runtime.FileLocator;


import com.eteks.openjeks.format.BorderStyle;
import com.eteks.openjeks.format.CellBorder;
import com.eteks.openjeks.format.CellFormat;
import com.eteks.openjeks.format.CellSetBorder;
import com.eteks.openjeks.format.TableFormat;
import org.jruby.Ruby;
import org.jruby.javasupport.Java;
import org.jruby.javasupport.JavaEmbedUtils;

public class SplashTableModel extends DefaultTableModel
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -2315033560766233243L;
	private int    rowCount;
	private int    columnCount;
	@SuppressWarnings("unchecked")
	private Hashtable cellValues;
	public TableFormat tableFormat = new TableFormat();
	private ScriptEngine engine;

	/**
	 * Creates a table model with <code>Short.MAX_VALUE</code> rows and columns.
	 */
	public SplashTableModel ()
	{
		this (Short.MAX_VALUE, Short.MAX_VALUE);


	}
	/**
	 * Constructor for class using RowCount and ColumnCount
	 * @param rowCount
	 * @param columnCount
	 */
	@SuppressWarnings("unchecked")
	public SplashTableModel (int rowCount, int columnCount)
	{
		this.rowCount     = rowCount;
		this.columnCount  = columnCount;

		initializeJRubyInterpreter();

		cellValues = new Hashtable ();
	}
	/**
	 * Constructor for class using RowCount and ColumnCount
	 * @param rowCount
	 * @param columnCount
	 */
	@SuppressWarnings("unchecked")
	public SplashTableModel (int rowCount, int columnCount, boolean isTesting)
	{
		this.rowCount     = rowCount;
		this.columnCount  = columnCount;
		Util.isTesting = isTesting;
		Util.isDebug = true;
		initializeJRubyInterpreter();

		cellValues = new Hashtable ();
		

		if (Util.isTesting) {
			for (int i=0;i<rowCount;i++) {
				for (int j=0;j<columnCount;j++) {
					setValueAt(new Cell(i,j,"","",new CellFormat()),i,j);
				}
			}
		}
	}




	/**
	 * Override for constructor to accept input stream
	 * @param is
	 */
	public SplashTableModel (InputStream is)
	{
		this (Short.MAX_VALUE, Short.MAX_VALUE);

		//initializeJRubyInterpreter();

		load(is);
	}

	public void initializeJRubyInterpreter(){
//		ClassLoader remember = Thread.currentThread().getContextClassLoader();
//		try{
		// This hack was needed so that the ruby code can find the same java classes as the current java code
		//Thread.currentThread().setContextClassLoader(this.getClass().getClassLoader());

		//rubyEngine.eval("require '/home/craig/.m2/repository/org/opengis/geoapi/2.2-SNAPSHOT/geoapi-2.2-20080605.180517-15.jar'");
		
		String path = "";
		if (Util.isTesting == false){
			URL scriptURL = null;
			try {
				scriptURL = FileLocator.toFileURL(SplashPlugin.getDefault().getBundle().getEntry("jruby.rb"));
			} catch (IOException e) {
				e.printStackTrace();
			}

			path = scriptURL.getPath();
		}
		else{
			path ="/home/amabdelsalam/Desktop/amanzi/jrss/org.amanzi.splash/jruby.rb";	
		}

		//if (Util.isTesting == false){
			ScriptEngineManager m = new ScriptEngineManager(getClass().getClassLoader());

			m.registerEngineName("jruby", 
					new com.sun.script.jruby.JRubyScriptEngineFactory());

			engine = m.getEngineByName("jruby");

			ScriptContext context = engine.getContext();


			String input = "";
			FileReader fr;

			String line;
			try {
				fr = new FileReader(path);
				BufferedReader br = new BufferedReader(fr);
				line = br.readLine();
				while (line != null)
				{
					input += line + "\n";
					line = br.readLine();
				}
			} catch (IOException e) {

				e.printStackTrace();
			}

			try {
				engine.eval(input, context);
				engine.eval("$sheet = Spreadsheet.new", context);
			} catch (ScriptException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	//}

	/**
	 * Method that update Cell that has reference to Script
	 * 
	 * @param cell Cell
	 * @author Lagutko_N
	 */
	
	public void updateCellFromScript(Cell cell) {
		String oldFormula = (String)cell.getDefinition();
		
		updateDefinitionFromScript(cell);
		
		interpret((String)cell.getDefinition(), cell.getRow(), cell.getColumn());
		updateCellsAndTableModelReferences(cell.getRow(), cell.getColumn(), oldFormula, (String)cell.getDefinition());
	}

	public Object execJRubyCommand(String input){
		Object ret = "";
		if (Util.isTesting == true){
			Ruby runtime = JavaEmbedUtils.initialize(Collections.EMPTY_LIST);
			runtime.evalScriptlet(input);
		}else{
			try {
				ret = engine.eval(input);
			} catch (ScriptException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return ret;
	}

	/**
	 * Function that updates definitiona of Cell from Script
	 * 
	 * @param cell Cell to update
	 * @author Lagutko_N
	 */

	public void updateDefinitionFromScript(Cell cell) {
		String content = Util.getScriptContent(cell.getScriptURI());
		cell.setDefinition(content);
	}

	public Cell interpret(String definition, int row, int column){
		//Util.logEnter("interpret");

		String cellID = Util.getCellIDfromRowColumn(row, column);
		String formula1 = definition;
		Cell se = getCellByID(cellID);
		////Util.logn("Interpreting formula: " + formula1 + " at Cell " + cellID);
		List<String> list = Util.findComplexCellIDs(definition);

		for (int i=0;i<list.size();i++){
			if (formula1.contains("$sheet.cells." + list.get(i)) == false)
				formula1 = formula1.replace(list.get(i), "$sheet.cells." + list.get(i).toLowerCase());
		}

		////Util.logn("Interpreting formula: " + formula1 + " at Cell " + cellID);

		if (definition.startsWith("=") == false){
			// This is normal text entered into cell, then
			////Util.logn("CASE 1: Formula not starting with =");
		}
		else{
			////Util.logn("CASE 2: Formula starting with =, performing ERB Wrapping");
			formula1 = "<%= " +formula1.replace("=", "") + " %>";
		}

		Object s1 = interpret_erb(cellID, formula1);
		//Util.log("definition = " + definition);

		se.setDefinition(definition);
		se.setValue((String)s1);

		this.setValueAt(se, row, column);
		//Util.logExit("interpret");
		return se;
	}

	public String interpret_erb(String cellID, String formula) {
		String s = "";
		//Util.logEnter("interpret_erb");
		String path = ScriptUtils.getJRubyHome() + "/lib/ruby/1.8" + "/erb";

		Util.logn("interpret_erb: formula = " + formula + " - cellID:" + cellID);
		Util.logn("path = " + path);
		Util.logn("cellID.toLowerCase():" + cellID.toLowerCase());

		try {
			String input = "require" + "'" + path + "'" + "\n" +
			"template = ERB.new <<-EOF" + "\n" +
			formula + "\n" +
			"EOF" + "\n" +
			"$sheet.cells." + cellID.toLowerCase() + "=" +  "template.result(binding)" + "\n" + 
			"return " + "$sheet.cells." + cellID.toLowerCase();

			Util.logn("ERB Input: " + input);
			s = (String) engine.eval(input, engine.getContext());
			

			Util.logn("ERB Output = " + s);


		} catch (ScriptException e) {
			s = "";
			e.printStackTrace();
		}

		if (s == null) s = "";

		//Util.logExit("interpret_erb");
		return s;
	}

	public Cell interpret(String definition, String oldDefinition, int row, int column){
		//Util.logEnter("interpret(String definition, String oldDefinition, int row, int column)");

		String cellID = Util.getCellIDfromRowColumn(row, column);
		String formula1 = definition;
		Util.logn("<><><><><><><><><><><><><><><><><><><><><><><><><><><><><><>");
		Util.logn("Start interpreting a cell...");
		Util.logn("CellID = " + cellID);

		Cell se = getCellByID(cellID);

		if (se == null)
		{
			Util.logn("WARNING: se = null");
			se = new Cell(row, column, "","", new CellFormat());
		}

		List<String> list = Util.findComplexCellIDs(definition);

		for (int i=0;i<list.size();i++){
			if (formula1.contains("$sheet.cells." + list.get(i)) == false)
				formula1 = formula1.replace(list.get(i), "$sheet.cells." + list.get(i).toLowerCase());
		}


		if (definition.startsWith("=") == false){
			Util.logn("Formula not starting with =, dealing as normal text");
		}
		else{
			Util.logn("Formula starts with =, Converting formula to ERB format");
			formula1 = "<%= " +formula1.replace("=", "") + " %>";
		}
		
		Util.logn("Interpreting cell using ERB...");
		Object s1 = interpret_erb(cellID, formula1);
		
		Util.logn("Setting cell definition: "+ definition);
		se.setDefinition(definition);

		Util.logn("Setting cell value:" + (String)s1);
		se.setValue((String)s1);
		
		Util.logn("setValueAt " + row +"," +column);
		Util.printCell("se", se);
		this.setValueAt(se, row, column, oldDefinition);

		Util.logn("finish interpreting a cell...");
		Util.logn("<><><><><><><><><><><><><><><><><><><><><><><><><><><><><><>");
		
		return se;
	}


	/**
	 * Save model data to String buffer
	 * @param sb
	 */
	public void save(StringBuffer sb)  {
		////Util.log("rowCount = " + rowCount);
		////Util.log("columnCount = " + columnCount);
		for (int i = 0; i < rowCount; i++) {
			for (int j = 0; j < columnCount; j++) {
				String definition = "";
				String value = "";
				Object o = getValueAt(i, j);
				if (o instanceof Cell && o != null)
				{
					definition = (String) ((Cell)o).getDefinition();
					value = (String) ((Cell)o).getValue();
				}

				if (o instanceof String && o != null)
				{
					definition = (String) o;
				}

				String line = definition.replace("\n", "") + ";" + value.replace("\n", "") + ";" + Util.getFormatString(new CellFormat()) + ";";
				////Util.log("line = " + line);
				sb.append(line);
			}
			sb.append("\n");
		}
		sb.append("\n");
	}


	/**
	 * Save model data and formatting information to String Buffer
	 * @param sb
	 * @param t
	 */
	public void save(StringBuffer sb, TableFormat t)  {
		////Util.log("rowCount = " + rowCount);
		////Util.log("columnCount = " + columnCount);

		for (int i = 0; i < rowCount; i++) {
			for (int j = 0; j < columnCount; j++) {
				String definition = "";
				String value = "";
				boolean hasReference = false;
				Object o = getValueAt(i, j);
				if (o instanceof Cell && o != null)
				{
					//Lagutko: if Cell has reference to script than we store URI of script in definition
					Cell cell = (Cell)o;
					if (cell.hasReference()) {
						definition = cell.getScriptURI().toString();
						hasReference = true;
					}
					else {
						definition = (String) ((Cell)o).getDefinition();
						value = (String) ((Cell)o).getValue();
					}					
				}

				if (o instanceof String && o != null)
				{
					definition = (String) o;
				}


				CellFormat c = t.getFormatAt(i, j);

				if (c == null){
					c = new CellFormat();
				}

				String line = definition.replace("\n", "") + ";" + value.replace("\n", "") + ";" + Util.getFormatString(c) + ";";
				////Util.log("line = " + line);

				sb.append(line);

			}
			sb.append("\n");
		}
		sb.append("\n");
	}



	/**
	 * Count columns in a semi-colon separated line words
	 * @param line
	 * @return
	 */
	private int countColumns(String line)
	{
		int c = 0;
		for (int i=0;i<line.length();i++)
		{
			char s = ';';
			if (line.charAt(i) == s) c++;
		}

		return c;
	}

	/**
	 * Read a line and extract elements separated by semi colon character
	 * @param line
	 * @return
	 */
	private ArrayList<String> readLine(String line)
	{
		ArrayList<String> list = new ArrayList<String>();
		String s = "";
		for (int i=0;i<line.length();i++)
		{
			if (line.charAt(i) == ';')
			{
				list.add(s);
				s = "";
			}
			else
			{
				s += line.charAt(i);
			}
		}
		return list;
	}

	/**
	 * Load spreadsheet data and formatting information from Input Stream
	 * Also parses both definitions, values and create inter-cell relationships
	 * @param is
	 */
	public void load(InputStream is) {
		int m=0;
		int j=0;
		String definition = "";
		String value = "";
		boolean hasReference = false;
		LineNumberReader lnr = new LineNumberReader(new InputStreamReader(is));
		int rowIndex = 0;
		try
		{
			String line;
			line = lnr.readLine();
			columnCount = countColumns(line)/13;
			while (line != null && line.lastIndexOf(";") > 0) {
				ArrayList<String> list = readLine(line);
				m = 0;
				j = 0;
				for (j=0;j<columnCount;j++)
				{
					definition = list.get(m++);
					////Util.log("definition:" + definition);
					value = list.get(m++);
					////Util.log("value:" + value);
					// Load cell formatting
					CellFormat c = new CellFormat();
					String o1= list.get(m++);
					String o2= list.get(m++);
					String o3= list.get(m++);

					if (!"".equals(o1) && !"".equals(o2) && !"".equals(o3))
					{
						Color bgColor = new Color(
								Integer.parseInt(o1),
								Integer.parseInt(o2),
								Integer.parseInt(o3)
						);

						c.setBackgroundColor(bgColor);
					}

					String o4= list.get(m++);

					String o5= list.get(m++);

					String o6= list.get(m++);

					if (!"".equals(o4) && !"".equals(o5) && !"".equals(o6))
					{
						Color fontColor = new Color(
								Integer.parseInt(o4),
								Integer.parseInt(o5),
								Integer.parseInt(o6)
						);
						c.setFontColor(fontColor);
					}

					String str = list.get(m++);

					if (!"".equals(str))
						c.setFontName(str);

					str = list.get(m++);

					if (!"".equals(str))
						c.setFontSize(Integer.parseInt(str));

					str = list.get(m++);

					if (!"".equals(str))
						c.setFontStyle(Integer.parseInt(str));

					str = list.get(m++);

					if (!"".equals(str))
						c.setHorizontalAlignment(Integer.parseInt(str));

					str = list.get(m++);

					if (!"".equals(str))
						c.setVerticalAlignment(Integer.parseInt(str));

					c.setCellBorder(new CellBorder());
					// Create a new expression with value and definition
					Cell se = new Cell(rowIndex, j, definition, value, c);
					//Lagutko: if Cell has reference to script than we must
					//set ScriptURI and read Definition from Script
					if (hasReference) {
						try {
							se.setScriptURI(new URI(definition));							
						}
						catch (URISyntaxException e) {
							
						}
						updateDefinitionFromScript(se);						
					}
					setValueAt(se, rowIndex, j);
					//Lagutko: if definition is not empty than we must compute value of Cell
					if (definition.length() > 0) {
						interpret((String)se.getDefinition(), rowIndex, j);
					}
					
					
					//initCellReferences(rowIndex, j, definition);

					//tableFormat.setFormatAt(c, rowIndex, j, rowIndex, j);
				}

				rowIndex++;
				line = lnr.readLine();


			}

			rowCount = rowIndex;

		}
		catch (FileNotFoundException e) {
		} catch (IOException e) {

			e.printStackTrace();
		}
	}

	/**
	 * Get number of rows
	 */
	public int getRowCount ()
	{
		return rowCount;
	}

	/**
	 * Get number of columns
	 */
	public int getColumnCount ()
	{
		return columnCount;
	}

	/**
	 * return model value at certain location
	 */
	public Object getValueAt (int row, int column)
	{
		// row and column index are checked but storing in a Hashtable
		// won't cause real problems
		if (row >= getRowCount ())
			throw new ArrayIndexOutOfBoundsException (row);
		if (column >= getColumnCount ())
			throw new ArrayIndexOutOfBoundsException (column);
		return cellValues.get (new Cell (row, column));
	}

	/**
	 * check if cell editable
	 */
	public boolean isCellEditable (int row, int column)
	{
		return true;
	}

	public String getPlainText(){
		return "PLAINTEXT";
	}


	/**
	 * set model data with a certain value
	 */
	@SuppressWarnings("unchecked")
	public void setValueAt (Object value, int row, int column)
	{
		// row and column index are checked but storing in a Hashtable
		// won't cause real problems
		if (row >= getRowCount ())
			throw new ArrayIndexOutOfBoundsException (row);
		if (column >= getColumnCount ())
			throw new ArrayIndexOutOfBoundsException (column);

		Cell cell = new Cell (row, column);


		if (   value == null
				|| "".equals (value))
			cellValues.remove (cell);
		else
			cellValues.put (cell, value);

		if (value != null)
		{
			tableFormat.setFormatAt(((Cell)value).getCellFormat(), row, column, row, column);
		}

		fireTableChanged (new TableModelEvent (this, row, row, column));
	}

	@SuppressWarnings("unchecked")
	public void setValueAt (Object value, int row, int column, String oldDefinition)
	{
		//Util.logEnter("setValueAt (Object value, int row, int column, String oldDefinition)");
		// row and column index are checked but storing in a Hashtable
		// won't cause real problems
		if (row >= getRowCount ())
			throw new ArrayIndexOutOfBoundsException (row);
		if (column >= getColumnCount ())
			throw new ArrayIndexOutOfBoundsException (column);
		Cell cell = new Cell (row, column);

		if (   value == null
				|| "".equals (value))
			cellValues.remove (cell);
		else
			cellValues.put (cell, value);

		if (value != null)
		{
			tableFormat.setFormatAt(((Cell)value).getCellFormat(), row, column, row, column);
			
			updateCellsAndTableModelReferences(((Cell)value), oldDefinition, (String) ((Cell)value).getDefinition());
		}

		fireTableChanged (new TableModelEvent (this, row, row, column));
		//Util.logExit("setValueAt (Object value, int row, int column, String oldDefinition)");
	}



	/**
	 * Initialize inter-cell relationships with new formula
	 * @param row
	 * @param column
	 * @param new_formula
	 */
	public void initCellReferences(int row, int column, String new_formula)
	{
		Cell cell;
		String cellID = Util.getCellIDfromRowColumn(row,column);
		cell = getCellByID(cellID);

		if (isInfiniteLoop(cell, new_formula) == false)
		{
			updateCellReferences (cellID,	new_formula );
		}
	}

	/**
	 * Update cell references and update table model with such references.
	 * @param row
	 * @param column
	 * @param oldFormula
	 * @param newFormula
	 */
	public void updateCellsAndTableModelReferences(int row, int column, String oldFormula, String newFormula)
	{
		Util.logEnter("updateCellsAndTableModelReferences");
		String cellID = Util.getCellIDfromRowColumn(row,column);
		Cell cell ;
		ArrayList<Cell> list;
		////Util.logn("updateCellsAndTableModelReferences");
		Util.logn("oldFormula = " + oldFormula);
		Util.logn("newFormula = " + newFormula);

		Util.logn("finding cell with cellID = " + cellID);
		cell = getCellByID(cellID);

		Util.printCell("Cell found", cell);
		Util.logn("checking formulas...");
		if (!newFormula.equals(oldFormula) && oldFormula != null)
		{
			Util.logn("!newFormula.equals(oldFormula) && oldFormula != null");
			if (cell != null)
			{
				if (isInfiniteLoop(cell, newFormula) == false)
				{
					Util.logn("No infinite loop found ");
					Util.logn("Updating cell references of Cell " + cellID + " with formula " + newFormula);

					updateCellReferences (cellID,	newFormula);
					Util.logn("After updateCellReferences");

					// Get all referring cells to update
					list = getAllReferringCells(cell);

					Util.logn("After getAllReferringCells(cell)");

					Util.printCellList("Referring Cells of Cell " + cell.getCellID(), list);

					for (int i=0;i<list.size();i++)
					{
						refreshCell(list.get(i));
					}
					Util.logn("After loop");
				}
				else
				{
					Util.logn("infinite loop found !!!!!");
				}
			}
		}

		Util.logExit("updateCellsAndTableModelReferences");
	}
	
	/**
	 * Update cell references and update table model with such references.
	 * @param row
	 * @param column
	 * @param oldFormula
	 * @param newFormula
	 */
	public void updateCellsAndTableModelReferences(Cell cell, String oldFormula, String newFormula)
	{
		Util.logEnter("updateCellsAndTableModelReferences");
		String cellID = cell.getCellID();
		//Cell cell ;
		ArrayList<Cell> list;
		////Util.logn("updateCellsAndTableModelReferences");
		Util.logn("oldFormula = " + oldFormula);
		Util.logn("newFormula = " + newFormula);

		Util.logn("finding cell with cellID = " + cellID);
		//cell = getCellByID(cellID);

		Util.printCell("Cell found", cell);
		Util.logn("checking formulas...");
		if (!newFormula.equals(oldFormula) && oldFormula != null)
		{
			Util.logn("!newFormula.equals(oldFormula) && oldFormula != null");
			if (cell != null)
			{
				if (isInfiniteLoop(cell, newFormula) == false)
				{
					Util.logn("No infinite loop found ");
					Util.logn("Updating cell references of Cell " + cellID + " with formula " + newFormula);

					updateCellReferences (cellID,	newFormula);
					Util.logn("After updateCellReferences");

					// Get all referring cells to update
					list = getAllReferringCells(cell);

					Util.logn("After getAllReferringCells(cell)");

					Util.printCellList("Referring Cells of Cell " + cell.getCellID(), list);

					for (int i=0;i<list.size();i++)
					{
						refreshCell(list.get(i));
					}
					Util.logn("After loop");
				}
				else
				{
					Util.logn("infinite loop found !!!!!");
				}
			}
		}

		Util.logExit("updateCellsAndTableModelReferences");
	}

	private void refreshCell(Cell cell) {
		//Util.logEnter("refreshCell");
		Util.printCell("Refreshing Cell", cell);
		Util.logEnter("interpret(String definition, String oldDefinition, int row, int column)");

		String cellID = cell.getCellID();

		Util.logn("refreshCell: cellID = " + cellID);

		String definition = (String) cell.getDefinition();
		String formula1 = (String) cell.getDefinition();
		Cell se = cell;

		if (se == null){
			Util.logn("WARNING: se = null");
		}

		List<String> list = Util.findComplexCellIDs(definition);
		for (int i=0;i<list.size();i++){
			formula1 = formula1.replace(list.get(i), "$sheet.cells." + list.get(i).toLowerCase());
		}

		Util.logn("Interpreting formula: " + formula1 + " at Cell " + cellID);

		if (definition.startsWith("=") == false){
			// This is normal text entered into cell, then
			Util.logn("CASE 1: Formula not starting with =");
		}
		else{
			Util.logn("CASE 2: Formula starting with =, performing ERB Wrapping");
			formula1 = "<%= " +formula1.replace("=", "") + " %>";
		}

		Util.logn("formula1 =" + formula1);

		Object s1 = interpret_erb(cellID, formula1);
		se.setDefinition(definition);
		se.setValue((String)s1);

		this.setValueAt(se, cell.getRow(), cell.getColumn());
		//Util.logExit("refreshCell");
	}

	private CellFormat getCellFormatAt(int row, int column)
	{
		return ((Cell)getValueAt(row, column)).getCellFormat();
	}

	public void addReferenceBetweenTwoCells(Cell C, Cell A)
	{
		// Adding Reference means to add A in RFD list of C, and C in RFG list of A
		C.addRfdCell(A);
		A.addRfgCell(C);
	}

	public void removeReferenceBetweenTwoCells(Cell C, Cell A)
	{
		//Util.logEnter("removeReferenceBetweenTwoCells");
		// Removing reference means remove A from RFD list of C, and C from RFG list of A
		C.removeRfdCell(A);
		A.removeRfgCell(C);
		//Util.logExit("removeReferenceBetweenTwoCells");
	}

	public Cell getCellByID(String cellID)
	{
		Util.logEnter("getCellByID");
		Util.logn("cellID = " + cellID);
		int row = Util.getRowIndexFromCellID(cellID);
		Util.logn("row = " + row);
		int column = Util.getColumnIndexFromCellID(cellID);
		Util.logn("column = " + column);
		
		Util.logExit("getCellByID");
		return (Cell)getValueAt(row, column);
		
	}

	public boolean isInfiniteLoop(Cell c, String new_formula)
	{
		//Util.logEnter("isInfiniteLoop");
		boolean ret = false;

		// find RFDs
		List<Cell> idsList = findComplexCellIDs(new_formula);

		//Util.printCell("isInfiniteLoop check of Cell " + c.getCellID(), c);
		for (int i=0;i<idsList.size();i++)
		{
			Cell c1 = idsList.get(i);
			//Util.printCell("isInfiniteLoop check of Cell " + c1.getCellID(), c1);
			if (Util.isCellInList(c1, getAllReferringCells(c)) == true)
			{
				ret = true;
				break;
			}
		}

		//Util.logExit("isInfiniteLoop");
		return ret;
	}

	public ArrayList<Cell> getAllReferringCells(Cell C)
	{
		//Util.logEnter("getAllReferringCells");
		ArrayList<Cell> templist = new ArrayList<Cell>();
		ArrayList<Cell> list = new ArrayList<Cell>();
		//if (C.isTraversed() == false)
		{
			//C.setTraversed(true);
			templist = C.getRfgCells();

			for (int i=0;i<templist.size();i++)
			{
				Util.printCell("getAllReferringCells: templist.get(i):", templist.get(i));
				list.add(templist.get(i));

				list.addAll((ArrayList<Cell>)(getAllReferringCells(templist.get(i))));
			}
		}
		//Util.logExit("getAllReferringCells");
		return list;
	}

	private ArrayList<Cell> findComplexCellIDs(String str)
	{
		ArrayList<String> tempList = Util.findComplexCellIDs(str);
		ArrayList<Cell> retList = new ArrayList<Cell>();
		for (int i=0;i<tempList.size();i++){
			retList.add(getCellByID(tempList.get(i)));
		}

		return retList;
	}



	/*
	 * This function is used to update cell references:
	 * 1- for cell with cellID, add it to CellsManager managed cells
	 * 2- For each of RfdCellsIDs, add them to CellsManager managed cells
	 * 3- Perform matching between the existing RFD cells and the new RFD cells to get cells to add/remove
	 */
	public void updateCellReferences(String cellID, String newDefinition)
	{
		//Util.logEnter("updateCellReferences");
		////Util.log("C_Content = " + newDefinition);

		Cell C = getCellByID(cellID);

		//Util.printCell("updateCellReferences: Input Cell", C);

		//Util.log("newDefinition: " + newDefinition);
		//Util.log("oldDefinition: " + C.getDefinition());

		ArrayList<Cell> newDefinitionCells = findComplexCellIDs(newDefinition);
		//Util.printCellList("updateCellReferences: Input Definition Cells", newDefinitionCells);

		if (newDefinitionCells.size() == 0){
			//Util.log("new definition contains no cells...");
		}

		/* Now, we will scan over cells in RFD list (which form old definition of cell)
		 * if Cell exist in new definition, then it's added reference between current cell and such cell
		 * if Cell exist in old definition, not existing in new definition, then remove reference between current cell and such cell 
		 */ 

		ArrayList<Cell> oldDefinitionRfdCells = C.getRfdCells();


		for (int i=0;i< oldDefinitionRfdCells.size();i++)
		{
			Cell rfdCell = oldDefinitionRfdCells.get(i);
			Util.logn("processing RFD cell " + rfdCell.getCellID());

			//Util.log("checking RFD cell " + oldDefinitionCell.getCellID() + " to exist in new definition cells");
			// if existAB is not found in the new listAB, then remove reference
			if (Util.isCellInList(rfdCell, newDefinitionCells) == false)
			{
				Util.logn("Cell" + rfdCell.getCellID() + "not in input list, removing references");
				removeReferenceBetweenTwoCells(C, rfdCell);
			}
		}

		for (int i=0;i< newDefinitionCells.size();i++)
		{
			Cell newDefinitionCell = newDefinitionCells.get(i);
			Util.logn("processing new input cell " + newDefinitionCell.getCellID());

			// if existAB is not found in the new listAB, then remove reference
			if (Util.isCellInList(newDefinitionCell, oldDefinitionRfdCells) == false)
			{
				Util.logn("Cell " + newDefinitionCell.getCellID() + "not in RFD cells, adding RFD cells");
				addReferenceBetweenTwoCells(C, newDefinitionCell);
			}
		}

		//Util.printCell("updateCellReferences: Cell After processing: ", C);
		//Util.logExit("updateCellReferences");
	}
}
class RowModel implements TableModel
{
	private TableModel source;

	RowModel(TableModel source)
	{
		this.source = source;
	}

	public boolean isCellEditable(int rowIndex, int columnIndex)
	{
		return false;
	}

	@SuppressWarnings("unchecked")
	public Class getColumnClass(int columnIndex)
	{
		return Object.class;
	}

	public int getColumnCount()
	{
		return 1;
	}

	public String getColumnName(int columnIndex)
	{
		return null;
	}

	public int getRowCount()
	{
		return source.getRowCount();
	}

	public void setValueAt(Object aValue, int rowIndex, int columnIndex)
	{
	}

	public Object getValueAt(int rowIndex, int columnIndex)
	{
		return null;
	}
	
	

	@Override
	public void addTableModelListener(TableModelListener arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void removeTableModelListener(TableModelListener arg0) {
		// TODO Auto-generated method stub
		
	}
}
