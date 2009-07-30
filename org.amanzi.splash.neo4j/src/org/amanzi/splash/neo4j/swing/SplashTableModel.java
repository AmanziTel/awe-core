package org.amanzi.splash.neo4j.swing;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.swing.event.TableModelEvent;
import javax.swing.table.DefaultTableModel;

import org.amanzi.scripting.jruby.EclipseLoadService;
import org.amanzi.scripting.jruby.ScriptUtils;
import org.amanzi.splash.neo4j.database.nodes.RootNode;
import org.amanzi.splash.neo4j.database.nodes.SpreadsheetNode;
import org.amanzi.splash.neo4j.database.services.CellID;
import org.amanzi.splash.neo4j.database.services.SpreadsheetService;
import org.amanzi.splash.neo4j.ui.SplashPlugin;
import org.amanzi.splash.neo4j.utilities.ActionUtil;
import org.amanzi.splash.neo4j.utilities.ActionUtil.RunnableWithResult;
import org.amanzi.splash.neo4j.utilities.NeoSplashUtil;
import org.eclipse.core.runtime.FileLocator;
import org.jruby.Ruby;
import org.jruby.RubyInstanceConfig;
import org.jruby.runtime.load.LoadService;

import com.eteks.openjeks.format.CellFormat;

public class SplashTableModel extends DefaultTableModel
{
    private static final String ERB_PATH = "/lib/ruby/1.8/erb";

    private static final String EMPTY_STRING = "";

    private static final String JRUBY_SCRIPT = "jruby.rb";

    /*
     * Arguments for IRB 
     */
	private static final String[] IRB_ARGS_LIST = {"--prompt-mode",  "default", "--readline"};

    /*
	 * UID
	 */
	private static final long serialVersionUID = -2315033560766233243L;
	
	/*
	 * Row count
	 */
	private int    rowCount = 0;
	
	/*
	 * Column Count
	 */
	private int    columnCount = 0;
	
	/*
	 * Ruby Runtime
	 */
	Ruby runtime;
	
	/*
	 * Spreadsheet for this Model 
	 */
	private SpreadsheetNode spreadsheet;
	
	/*
	 * Spreadsheet Service
	 */
	private SpreadsheetService service;
	
	/**
	 * Creates a SplashTableModel by given SpreadsheetNode
	 * 
	 * @param spreadsheet Spreadsheet Node
	 * @author Lagutko_N
	 */
	public SplashTableModel(SpreadsheetNode spreadsheet) {
	    this.spreadsheet = spreadsheet;
	    
	    this.service = SplashPlugin.getDefault().getSpreadsheetService();
	    
	    this.rowCount = Short.MAX_VALUE;
	    this.columnCount = Short.MAX_VALUE;
	    
	    initializeJRubyInterpreter();
	}
		
	/**
	 * Creates a table model with 10 rows and columns.
	 *
	 * @param splash_name name of Spreadsheet
	 * @param root root node of Spreadsheet
	 */
	public SplashTableModel (String splash_name, RootNode root)
	{
		this (10, 10, splash_name, root);
	}
	/**
	 * Constructor for class using RowCount and ColumnCount
	 * @param rowCount
	 * @param columnCount
	 * @param splash_name name of Spreadsheet
     * @param root root node of Spreadsheet
	 */
	@SuppressWarnings("unchecked")
	public SplashTableModel (int rows, int cols, String splash_name, RootNode root)
	{
		
		this.rowCount     = rows;
		this.columnCount  = cols;
		
		if (runtime == null)
			initializeJRubyInterpreter();	
		
		initializeSpreadsheet(splash_name, root);
	}
	
	/**
     * Constructor for class using RowCount, ColumnCount and Ruby Runtime
     * @param rowCount
     * @param columnCount
     * @param splash_name name of Spreadsheet
     * @param rubyengine Ruby Runtime
     * @param root root node of Spreadsheet
     */
	@SuppressWarnings("unchecked")
	public SplashTableModel (int rows, int cols, String splash_id, Ruby rubyengine, RootNode root)
	{
		
		this.rowCount     = rows;
		this.columnCount  = cols;		
		
		if (runtime == null)
			this.runtime = rubyengine;

		initializeSpreadsheet(splash_id, root);
	}
	
	/**
	 * Initializes Spreadsheet for this model
	 *
	 * @param sheetName name of Spreadsheet
	 * @param root root node of Spreadsheet
	 * @author Lagutko_N
	 */
	private void initializeSpreadsheet(String sheetName, RootNode root) {
	    service = SplashPlugin.getDefault().getSpreadsheetService();
        
	    //don't need to check that spreadsheet exists because it was checked in SplashEditorInput
	    spreadsheet = service.findSpreadsheet(root, sheetName);
	}

	/**
	 * Initializes Ruby Runtime
	 */
	public void initializeJRubyInterpreter(){
		RubyInstanceConfig config = null;
		config = new RubyInstanceConfig() {{
			setJRubyHome(ScriptUtils.getJRubyHome());	// this helps online help work
			setObjectSpaceEnabled(true); // useful for code completion inside the IRB
			setLoadServiceCreator(new LoadServiceCreator() {
				public LoadService create(Ruby runtime) {
					return new EclipseLoadService(runtime);
				}
			});

			// The following modification forces IRB to ignore the fact that inside eclipse
			// the STDIN.tty? returns false, and IRB must continue to use a prompt
			List<String> argList = new ArrayList<String>();
			for (String arg : IRB_ARGS_LIST) {
			    argList.add(arg);
			}
			setArgv(argList.toArray(new String[0]));
		}};
		
		runtime = Ruby.newInstance(config);		
		runtime.getLoadService().init(ScriptUtils.makeLoadPath(new String[] {}));

		String path = EMPTY_STRING;
		if (NeoSplashUtil.isTesting == false){
			URL scriptURL = null;
			try {
				scriptURL = FileLocator.toFileURL(SplashPlugin.getDefault().getBundle().getEntry(JRUBY_SCRIPT));
			} catch (IOException e) {
				e.printStackTrace();
			}

			path = scriptURL.getPath();
		}
		else{
			path ="D:/projects/AWE from SVN/org.amanzi.splash/jruby.rb";	
		}

		String input = NeoSplashUtil.getScriptContent(path);

		runtime.evalScriptlet(input);
		//TODO: Lagutko: extract scripts to files
		runtime.evalScriptlet("$sheet = Spreadsheet.new");
	}


	/**
	 * Method that update Cell that has reference to Script
	 * 
	 * @param cell Cell
	 * @author Lagutko_N
	 */

	public void updateCellFromScript(Cell cell) {
		updateDefinitionFromScript(cell);

		interpret((String)cell.getDefinition(), Cell.DEFAULT_DEFINITION, cell.getRow(), cell.getColumn());

	}

	

	/**
	 * Function that updates definition of Cell from Script
	 * 
	 * @param cell Cell to update
	 * @author Lagutko_N
	 */

	public void updateDefinitionFromScript(Cell cell) {
		String content = NeoSplashUtil.getScriptContent(cell.getScriptURI());
		cell.setDefinition(content);
	}

	/**
	 * Interprets a definition of Cell by row and column 
	 *
	 * @param definition definition of Cell
	 * @param row row index of Cell
	 * @param column column index of Cell
	 * @return Cell with updated value
	 */
	public Cell interpret(String definition, int row, int column){
		String cellID = NeoSplashUtil.getCellIDfromRowColumn(row, column);
		String formula1 = definition;
		Cell se = getCellByID(cellID);
		
		List<String> list = NeoSplashUtil.findComplexCellIDs(definition);

		//TODO: Lagutko: extract scripts to files
		for (int i=0;i<list.size();i++){
			if (formula1.contains("$sheet.cells." + list.get(i)) == false)
				formula1 = formula1.replace(list.get(i), "$sheet.cells." + list.get(i).toLowerCase());
		}

		if (definition.startsWith("=") == false){
		}
		else{
			formula1 = "<%= " +formula1.replace("=", EMPTY_STRING) + " %>";
		}

		Object s1 = interpret_erb(cellID, formula1);

		se.setDefinition(definition);
		se.setValue((String)s1);

		this.setValueAt(se, row, column);
		return se;
	}
	
	/**
	 * Interprets a formula using ERB
	 *
	 * @param cellID id of Cell
	 * @param formula formula of Cell
	 * @return interpreted value
	 */
	public String interpret_erb(String cellID, String formula) {
		Object s = EMPTY_STRING;
		String path = ScriptUtils.getJRubyHome() + ERB_PATH;

		NeoSplashUtil.logn("interpret_erb: formula = " + formula + " - cellID:" + cellID);
		NeoSplashUtil.logn("path = " + path);
		NeoSplashUtil.logn("cellID.toLowerCase():" + cellID.toLowerCase());

		//TODO: Lagutko: extract script to file
		String input = "require" + "'" + path + "'" + "\n" +
		"template = ERB.new <<-EOF" + "\n" +
		formula + "\n" +
		"EOF" + "\n" +
		"$sheet.cells." + cellID.toLowerCase() + "=" +  "template.result(binding)" + "\n" +
		"$sheet.cells." + cellID.toLowerCase();

		NeoSplashUtil.logn("ERB Input: " + input);


		s = runtime.evalScriptlet(input);

		NeoSplashUtil.logn("ERB Output = " + s);

		if (s == null) s = "ERROR";

		return s.toString();
	}

	/**
	 * Interprets a Definition of cell by row and column
	 *
	 * @param definition new formula of cell
	 * @param oldDefinition old formula of cell
	 * @param row row of Cell
	 * @param column column of Cell
	 * @return Cell with interpeted values
	 */
	//TODO: Lagutko: do we need oldDefinition param?
	public Cell interpret(String definition, String oldDefinition, int row, int column){
		String cellID = NeoSplashUtil.getCellIDfromRowColumn(row, column);
		String formula1 = definition;
		NeoSplashUtil.logn("<><><><><><><><><><><><><><><><><><><><><><><><><><><><><><>");
		NeoSplashUtil.logn("Start interpreting a cell...");
		NeoSplashUtil.logn("CellID = " + cellID);

		Cell se = getCellByID(cellID);

		if (se == null)
		{
			NeoSplashUtil.logn("WARNING: se = null");
			se = new Cell(row, column, Cell.DEFAULT_VALUE, Cell.DEFAULT_DEFINITION, new CellFormat());
		}

		List<String> list = null; 


		if (definition.startsWith("=") == false){
			NeoSplashUtil.logn("Formula not starting with =, dealing as normal text");
			if (definition.startsWith("<%=") && definition.endsWith("%>")){
				NeoSplashUtil.logn("The entered formula is already ERB");
				NeoSplashUtil.logn("Interpreting cell using ERB...");
				Object s1 = interpret_erb(cellID, formula1);

				NeoSplashUtil.logn("Setting cell definition: "+ definition);
				se.setDefinition(definition);

				NeoSplashUtil.logn("Setting cell value:" + (String)s1);
				se.setValue((String)s1);
			}else{
				NeoSplashUtil.logn("The entered formula just text, not ERB and not Ruby");
				NeoSplashUtil.logn("Setting cell definition: "+ definition);
				se.setDefinition(definition);
				
				interpret_erb(cellID, definition);

				NeoSplashUtil.logn("Setting cell value:" + definition);
				se.setValue(definition);
			}
		}
		else{
			NeoSplashUtil.logn("definition = " + definition);

			if (definition.startsWith("='")){
				NeoSplashUtil.logn("definition started with ='");
				list = NeoSplashUtil.findComplexCellIDsInRubyText(definition);

				for (int i=0;i<list.size();i++){
					if (formula1.contains("$sheet.cells." + list.get(i)) == false)
						formula1 = formula1.replace(list.get(i), "$sheet.cells." + list.get(i).toLowerCase());
				}
			}else{
				NeoSplashUtil.logn("definition NOT started with ='");
				list = NeoSplashUtil.findComplexCellIDs(definition);

				for (int i=0;i<list.size();i++){
					if (formula1.contains("$sheet.cells." + list.get(i)) == false)
						formula1 = formula1.replace(list.get(i), "$sheet.cells." + list.get(i).toLowerCase());
				}
			}

			NeoSplashUtil.displayStringList("list", list);

			NeoSplashUtil.logn("Formula starts with =, Converting formula to ERB format");
			formula1 = "<%= " +formula1.replace("=", EMPTY_STRING) + " %>";


			NeoSplashUtil.logn("Interpreting cell using ERB...");
			Object s1 = interpret_erb(cellID, formula1);

			NeoSplashUtil.logn("Setting cell definition: "+ definition);
			se.setDefinition(definition);

			NeoSplashUtil.logn("Setting cell value:" + (String)s1);
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
	 * 
	 */
	public Object getValueAt (final int row, final int column)
	{
		// row and column index are checked but storing in a Hashtable
		// won't cause real problems
		if (row >= getRowCount ())
			throw new ArrayIndexOutOfBoundsException (row);
		if (column >= getColumnCount ())
			throw new ArrayIndexOutOfBoundsException (column);

		Cell result = (Cell)ActionUtil.getInstance().runTaskWithResult(new RunnableWithResult() {
		    private Cell result = null;
		    
		    public Object getValue() {
		        return result;
		    }
		    
		    public void run() {
		        result = service.getCell(spreadsheet, new CellID(row, column));
		    }
		});
		
		return result;

	}

	/**
	 * check if cell editable
	 */
	public boolean isCellEditable (int row, int column)
	{
		return true;
	}

	/**
	 * set model data with a certain value
	 */
	@SuppressWarnings("unchecked")
	public void setValueAt (final Object value, int row, int column)
	{
		// row and column index are checked but storing in a Hashtable
		// won't cause real problems
		NeoSplashUtil.logn("row = " + row + " - getRowCount () = " +getRowCount () );
		if (row >= getRowCount ())
			throw new ArrayIndexOutOfBoundsException (row);
		if (column >= getColumnCount ()){
			NeoSplashUtil.logn("column: " + column + " - getColumnCount: " + getColumnCount());
			throw new ArrayIndexOutOfBoundsException (column);
		}		
		
		ActionUtil.getInstance().runTask(new Runnable() {
            public void run() {
                service.updateCell(spreadsheet, (Cell)value);
            }
        }, false);
		
		fireTableChanged (new TableModelEvent (this, row, row, column));
	}

	/**
	 * Sets the Cell to given row and column
	 *
	 * @param value Cell
	 * @param row row index
	 * @param column column index
	 * @param oldDefinition oldDefinition
	 */
	@SuppressWarnings("unchecked")
	//TODO: Lagutko: do we need oldDefinition param?
	public void setValueAt (final Object value, final int row, final int column, String oldDefinition)
	{
		// row and column index are checked but storing in a Hashtable
		// won't cause real problems
		if (row >= getRowCount ())
			throw new ArrayIndexOutOfBoundsException (row);
		if (column >= getColumnCount ())
			throw new ArrayIndexOutOfBoundsException (column);

		ActionUtil.getInstance().runTask(new Runnable() {
		    public void run() {
		        updateCellWithReferences((Cell)value);
		    }
		}, false);

		fireTableChanged (new TableModelEvent (this, row, row, column));
	}
	
	/**
	 * Recursively updates Cell Values by References
	 *
	 * @param rootCell Cell for update
	 */
	
	private void updateCellWithReferences(Cell rootCell) {
	    service.updateCellWithReferences(spreadsheet, rootCell);
	    
	    for (Cell c : service.getDependentCells(spreadsheet, new CellID(rootCell.getCellID()))) {
	        refreshCell(c);
	        updateCellWithReferences(c);
	    }
	}
	
	/**
	 * Refreshes Cell with given ID
	 *
	 * @param cellID id of Cell to refresh
	 */
	public void refreshCell(String cellID) {
		NeoSplashUtil.logn("refreshCell: cellID = " + cellID);
		Cell cell = service.getCell(spreadsheet, new CellID(cellID));

		String definition = (String) cell.getDefinition();
		String formula1 = (String) cell.getDefinition();
		Cell se = cell;

		if (se == null){
			NeoSplashUtil.logn("WARNING: se = null");
		}

		List<String> list = NeoSplashUtil.findComplexCellIDs(definition);
		for (int i=0;i<list.size();i++){
		    //TODO: Lagutko, extract script
			formula1 = formula1.replace(list.get(i), "$sheet.cells." + list.get(i).toLowerCase());
		}

		NeoSplashUtil.logn("Interpreting formula: " + formula1 + " at Cell " + cellID);

		if (definition.startsWith("=") == false){
			// This is normal text entered into cell, then
			NeoSplashUtil.logn("CASE 1: Formula not starting with =");
		}
		else{
			NeoSplashUtil.logn("CASE 2: Formula starting with =, performing ERB Wrapping");
			formula1 = "<%= " +formula1.replace("=", EMPTY_STRING) + " %>";
		}

		NeoSplashUtil.logn("formula1 =" + formula1);

		Object s1 = interpret_erb(cellID, formula1);
		se.setDefinition(definition);
		se.setValue((String)s1);

		this.setValueAt(se, cell.getRow(), cell.getColumn());
	}

	/**
	 * Refreshes given Cell
	 *
	 * @param cell Cell
	 */

	public void refreshCell(Cell cell) {
		NeoSplashUtil.printCell("Refreshing Cell", cell);
		
		String cellID = cell.getCellID();

		NeoSplashUtil.logn("refreshCell: cellID = " + cellID);

		String definition = (String) cell.getDefinition();
		String formula1 = (String) cell.getDefinition();
		Cell se = cell;

		if (se == null){
			NeoSplashUtil.logn("WARNING: se = null");
		}

		List<String> list = NeoSplashUtil.findComplexCellIDs(definition);
		for (int i=0;i<list.size();i++){
		    //TODO: Lagutko, extract script
			formula1 = formula1.replace(list.get(i), "$sheet.cells." + list.get(i).toLowerCase());
		}

		NeoSplashUtil.logn("Interpreting formula: " + formula1 + " at Cell " + cellID);

		if (definition.startsWith("=") == false){
			// This is normal text entered into cell, then
			NeoSplashUtil.logn("CASE 1: Formula not starting with =");
		}
		else{
			NeoSplashUtil.logn("CASE 2: Formula starting with =, performing ERB Wrapping");
			formula1 = "<%= " +formula1.replace("=", EMPTY_STRING) + " %>";
		}

		NeoSplashUtil.logn("formula1 =" + formula1);

		Object s1 = interpret_erb(cellID, formula1);
		se.setDefinition(definition);
		se.setValue((String)s1);

		this.setValueAt(se, cell.getRow(), cell.getColumn());
	}

	/**
	 * Returns the Cell by ID
	 *
	 * @param cellID ID of Cell
	 * @return Cell
	 */
	public Cell getCellByID(String cellID)
	{
		int row = NeoSplashUtil.getRowIndexFromCellID(cellID);
		int column = NeoSplashUtil.getColumnIndexFromCellID(cellID);
	
		return (Cell)getValueAt(row, column);
	}
	
	/**
	 * Returns Ruby Engine of this Model
	 *
	 * @return Ruby Engine
	 */
	public Ruby getEngine() {
		return runtime;
	}
	
	/**
	 * Updates Format of Cell
	 *
	 * @param cell cell
	 * @author Lagutko_N
	 */
	public void updateCellFormat(Cell cell) {
	    service.updateCell(spreadsheet, cell);
	}
}
