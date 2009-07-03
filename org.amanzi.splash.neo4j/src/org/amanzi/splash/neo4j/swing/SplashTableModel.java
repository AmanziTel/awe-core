package org.amanzi.splash.neo4j.swing;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.swing.event.TableModelEvent;
import javax.swing.table.DefaultTableModel;

import org.amanzi.scripting.jruby.ScriptUtils;
import org.amanzi.splash.neo4j.SplashNeoManager;
import org.amanzi.splash.neo4j.ui.SplashPlugin;
import org.amanzi.splash.neo4j.utilities.Util;
import org.eclipse.core.runtime.FileLocator;
import org.jruby.Ruby;
import org.jruby.javasupport.JavaEmbedUtils;

import com.eteks.openjeks.format.CellFormat;

public class SplashTableModel extends DefaultTableModel
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -2315033560766233243L;
	private int    rowCount = 0;
	private int    columnCount = 0;
	private ScriptEngine engine;
	private SplashNeoManager splashNeoManager;
	private String splashID = "";
	
	/**
	 * Creates a table model with <code>Short.MAX_VALUE</code> rows and columns.
	 */
	public SplashTableModel (String splash_id)
	{
		//this (Util.MAX_SPLASH_ROW_COUNT, Util.MAX_SPLASH_COL_COUNT);
		this (10, 10, splash_id);
	}
	/**
	 * Constructor for class using RowCount and ColumnCount
	 * @param rowCount
	 * @param columnCount
	 */
	@SuppressWarnings("unchecked")
	public SplashTableModel (int rows, int cols, String splash_id)
	{
		
		this.rowCount     = rows;
		this.columnCount  = cols;
		this.splashID = splash_id;
		
		
		if (engine == null)
			initializeJRubyInterpreter();

		if (splashNeoManager == null)
			splashNeoManager = new SplashNeoManager(this.splashID);
	}
	
	@SuppressWarnings("unchecked")
	public SplashTableModel (int rows, int cols, String splash_id, SplashNeoManager manager, ScriptEngine rubyengine)
	{
		
		this.rowCount     = rows;
		this.columnCount  = cols;
		this.splashID = splash_id;
		
		
		if (engine == null)
			this.engine = rubyengine;

		if (splashNeoManager == null)
			splashNeoManager = manager;
	}

	

	public void initializeJRubyInterpreter(){
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


	/**
	 * Method that update Cell that has reference to Script
	 * 
	 * @param cell Cell
	 * @author Lagutko_N
	 */

	public void updateCellFromScript(Cell cell) {
		

		updateDefinitionFromScript(cell);

		interpret((String)cell.getDefinition(), "", cell.getRow(), cell.getColumn());

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
		Object s = "";
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
			s = engine.eval(input, engine.getContext());


			Util.logn("ERB Output = " + s);


		} catch (ScriptException e) {
			//s = "";
			s = e.getMessage().replace("org.jruby.exceptions.RaiseException: ","");
			e.printStackTrace();
		}

		if (s == null) s = "ERROR";

		//Util.logExit("interpret_erb");
		return s.toString();
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

		List<String> list = null; 


		if (definition.startsWith("=") == false){
			Util.logn("Formula not starting with =, dealing as normal text");
			if (definition.startsWith("<%=") && definition.endsWith("%>")){
				Util.logn("The entered formula is already ERB");
				Util.logn("Interpreting cell using ERB...");
				Object s1 = interpret_erb(cellID, formula1);

				Util.logn("Setting cell definition: "+ definition);
				se.setDefinition(definition);

				Util.logn("Setting cell value:" + (String)s1);
				se.setValue((String)s1);
			}else{
				Util.logn("The entered formula just text, not ERB and not Ruby");
				Util.logn("Setting cell definition: "+ definition);
				se.setDefinition(definition);

				Util.logn("Setting cell value:" + definition);
				se.setValue(definition);
			}
		}
		else{
			Util.logn("definition = " + definition);

			if (definition.startsWith("='")){
				Util.logn("definition started with ='");
				list = Util.findComplexCellIDsInRubyText(definition);

				for (int i=0;i<list.size();i++){
					if (formula1.contains("$sheet.cells." + list.get(i)) == false)
						formula1 = formula1.replace(list.get(i), "$sheet.cells." + list.get(i).toLowerCase());
				}
			}else{
				Util.logn("definition NOT started with ='");
				list = Util.findComplexCellIDs(definition);

				for (int i=0;i<list.size();i++){
					if (formula1.contains("$sheet.cells." + list.get(i)) == false)
						formula1 = formula1.replace(list.get(i), "$sheet.cells." + list.get(i).toLowerCase());
				}
			}

			Util.displayStringList("list", list);

			Util.logn("Formula starts with =, Converting formula to ERB format");
			formula1 = "<%= " +formula1.replace("=", "") + " %>";


			Util.logn("Interpreting cell using ERB...");
			Object s1 = interpret_erb(cellID, formula1);

			Util.logn("Setting cell definition: "+ definition);
			se.setDefinition(definition);

			Util.logn("Setting cell value:" + (String)s1);
			se.setValue((String)s1);

		}

		setValueAt(se, row, column, oldDefinition);

		return se;
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

		return splashNeoManager.getCell(Util.getCellIDfromRowColumn(row, column));

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
		Util.logn("row = " + row + " - getRowCount () = " +getRowCount () );
		if (row >= getRowCount ())
			throw new ArrayIndexOutOfBoundsException (row);
		if (column >= getColumnCount ()){
			Util.logn("column: " + column + " - getColumnCount: " + getColumnCount());
			throw new ArrayIndexOutOfBoundsException (column);
		}

		String id = Util.getCellIDfromRowColumn(row, column);
		splashNeoManager.addCell(id, (Cell) value);
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


		String id = Util.getCellIDfromRowColumn(row, column);
		splashNeoManager.updateCell(id, (Cell) value);

		Util.logn("Cells to be updated: ");
		ArrayList<String> cellsToUpdate = splashNeoManager.findReferringCells(id);

		for (int i=0;i<cellsToUpdate.size();i++){
			Cell c = splashNeoManager.getCell(cellsToUpdate.get(i));
			refreshCell(c);
		}

		fireTableChanged (new TableModelEvent (this, row, row, column));
		//Util.logExit("setValueAt (Object value, int row, int column, String oldDefinition)");
	}
	
	public void refreshCell(String cellID) {
		//Util.logEnter("refreshCell");
		
		Util.logEnter("interpret(String definition, String oldDefinition, int row, int column)");

		//String cellID = cell.getCellID();

		Util.logn("refreshCell: cellID = " + cellID);
		Cell cell = splashNeoManager.getCell(cellID);

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



	public void refreshCell(Cell cell) {
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

	


	public Cell getCellByID(String cellID)
	{
		//Util.logEnter("getCellByID");
		//Util.logn("cellID = " + cellID);
		int row = Util.getRowIndexFromCellID(cellID);
		//Util.logn("row = " + row);
		int column = Util.getColumnIndexFromCellID(cellID);
		//Util.logn("column = " + column);

		//Util.logExit("getCellByID");
		return (Cell)getValueAt(row, column);

	}


	
	public SplashNeoManager getSplashNeoManager() {
		return splashNeoManager;
	}
	public void setSplashNeoManager(SplashNeoManager splashNeoManager) {
		this.splashNeoManager = splashNeoManager;
	}
	public ScriptEngine getEngine() {
		return engine;
	}
	public void setEngine(ScriptEngine engine) {
		this.engine = engine;
	}
}
