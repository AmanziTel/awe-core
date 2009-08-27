package org.amanzi.splash.swing;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.swing.event.TableModelEvent;
import javax.swing.table.DefaultTableModel;

import org.amanzi.neo.core.NeoCorePlugin;
import org.amanzi.neo.core.database.nodes.CellID;
import org.amanzi.neo.core.database.nodes.RubyProjectNode;
import org.amanzi.neo.core.database.nodes.SpreadsheetNode;
import org.amanzi.neo.core.utils.ActionUtil;
import org.amanzi.neo.core.utils.ActionUtil.RunnableWithResult;
import org.amanzi.scripting.jruby.EclipseLoadService;
import org.amanzi.scripting.jruby.ScriptUtils;
import org.amanzi.splash.database.services.SpreadsheetService;
import org.amanzi.splash.ui.SplashPlugin;
import org.amanzi.splash.utilities.NeoSplashUtil;
import org.eclipse.core.runtime.FileLocator;
import org.jruby.Ruby;
import org.jruby.RubyArray;
import org.jruby.RubyInstanceConfig;
import org.jruby.internal.runtime.ValueAccessor;
import org.jruby.javasupport.JavaEmbedUtils;
import org.jruby.runtime.builtin.IRubyObject;
import org.jruby.runtime.load.LoadService;

import com.eteks.openjeks.format.CellFormat;

public class SplashTableModel extends DefaultTableModel {
	/*
	 * Name of 'jrubyPath' Ruby Global Variable
	 */
	private static final String JRUBY_PATH_RUBY_NAME = "jrubyPath";

	/*
	 * Name of 'tableModel' Ruby Global Variable
	 */
	private static final String TABLE_MODEL_RUBY_NAME = "tableModel";

	/*
	 * Constant for Empty String
	 */
	private static final String EMPTY_STRING = "";

	/*
	 * Script for initializing Spreadsheet
	 */
	private static final String JRUBY_SCRIPT = "jruby.rb";

	/*
	 * Arguments for IRB
	 */
	private static final String[] IRB_ARGS_LIST = { "--prompt-mode", "default",
			"--readline" };

	/*
	 * UID
	 */
	private static final long serialVersionUID = -2315033560766233243L;

	/*
	 * Row count
	 */
	private int rowCount = 0;

	/*
	 * Column Count
	 */
	private int columnCount = 0;

	/*
	 * Ruby Runtime
	 */
	private Ruby runtime;

	/*
	 * Spreadsheet for this Model
	 */
	private SpreadsheetNode spreadsheet;

	/*
	 * Spreadsheet Service
	 */
	private SpreadsheetService service;

	private RubyProjectNode rubyProjectNode;

	/**
	 * Creates a SplashTableModel by given SpreadsheetNode
	 * 
	 * @param spreadsheet
	 *            Spreadsheet Node
	 * @author Lagutko_N
	 */
	public SplashTableModel(SpreadsheetNode spreadsheet, RubyProjectNode root) throws IOException {
		this.spreadsheet = spreadsheet;

		this.service = SplashPlugin.getDefault().getSpreadsheetService();
		rubyProjectNode = root;
		this.rowCount = Short.MAX_VALUE;
		this.columnCount = Short.MAX_VALUE;

		initializeJRubyInterpreter();
	}

	/**
	 * Creates a table model with 10 rows and columns.
	 * 
	 * @param splash_name
	 *            name of Spreadsheet
	 * @param root
	 *            root node of Spreadsheet
	 */
	public SplashTableModel(String splash_name, RubyProjectNode root) throws IOException {
		this(Short.MAX_VALUE, Short.MAX_VALUE, splash_name, root);
	}

	/**
	 * Constructor for class using RowCount and ColumnCount
	 * 
	 * @param rowCount
	 * @param columnCount
	 * @param splash_name
	 *            name of Spreadsheet
	 * @param root
	 *            root node of Spreadsheet
	 */
	public SplashTableModel(int rows, int cols, String splash_name,
			RubyProjectNode root) throws IOException {

		this.rowCount = rows;
		this.columnCount = cols;

		initialize(splash_name, root);
	}

	/**
	 * Constructor for class using RowCount, ColumnCount and Ruby Runtime
	 * 
	 * @param rowCount
	 * @param columnCount
	 * @param splash_name
	 *            name of Spreadsheet
	 * @param rubyengine
	 *            Ruby Runtime
	 * @param root
	 *            root node of Spreadsheet
	 */
	public SplashTableModel(int rows, int cols, String splash_name,
			Ruby rubyengine, RubyProjectNode root) {

		this.rowCount = rows;
		this.columnCount = cols;

		if (runtime == null)
			this.runtime = rubyengine;

		this.rubyProjectNode = root;
		initializeSpreadsheet(splash_name, rubyProjectNode);
	}
	
	/**
	 * Constructor for class using RowCount, ColumnCount and Ruby Runtime
	 * 
	 * @param rowCount
	 * @param columnCount
	 * @param splash_name
	 *            name of Spreadsheet
	 * @param rubyengine
	 *            Ruby Runtime
	 * @param root
	 *            root node of Spreadsheet
	 */
	public SplashTableModel(SpreadsheetNode spreadsheet, Ruby rubyengine,
			RubyProjectNode root) {

		this.spreadsheet = spreadsheet;
		this.service = SplashPlugin.getDefault().getSpreadsheetService();
		this.rowCount = Short.MAX_VALUE;
		this.columnCount = Short.MAX_VALUE;
		this.runtime = rubyengine;
	}

	/**
	 * Initializes Spreadsheet for this model
	 * 
	 * @param sheetName
	 *            name of Spreadsheet
	 * @param root
	 *            root node of Spreadsheet
	 * @author Lagutko_N
	 */
	private void initializeSpreadsheet(String sheetName, RubyProjectNode root) {
		service = SplashPlugin.getDefault().getSpreadsheetService();

		// don't need to check that spreadsheet exists because it was checked in
		// SplashEditorInput
		spreadsheet = NeoCorePlugin.getDefault().getProjectService().findSpreadsheet(root, sheetName);
	}

	/**
	 * Initializes Spreadsheet
	 * 
	 * @param splash_name
	 *            name of Spreadsheet
	 * @param root
	 *            RootNode of Spreadsheet
	 */

	private void initialize(String splash_name, RubyProjectNode root) throws IOException {

		this.rubyProjectNode = root;
		initializeSpreadsheet(splash_name, root);

		if (runtime == null)
			initializeJRubyInterpreter();
	}

	/**
	 * Initializes Ruby Runtime
	 */
	public void initializeJRubyInterpreter() throws IOException {
		RubyInstanceConfig config = null;
		config = new RubyInstanceConfig() {
			{
				setJRubyHome(ScriptUtils.getJRubyHome()); // this helps online
				// help work
				setObjectSpaceEnabled(true); // useful for code completion
				// inside the IRB
				setLoadServiceCreator(new LoadServiceCreator() {
					public LoadService create(Ruby runtime) {
						return new EclipseLoadService(runtime);
					}
				});
				
				//Lagutko, 29.08.2009, loader
				setLoader(this.getClass().getClassLoader());

				// The following modification forces IRB to ignore the fact that
				// inside eclipse
				// the STDIN.tty? returns false, and IRB must continue to use a
				// prompt
				List<String> argList = new ArrayList<String>();
				for (String arg : IRB_ARGS_LIST) {
					argList.add(arg);
				}
				setArgv(argList.toArray(new String[0]));
			}
		};

		runtime = Ruby.newInstance(config);
		runtime.getLoadService()
				.init(ScriptUtils.makeLoadPath(new String[] {}));

		String path = EMPTY_STRING;
		if (NeoSplashUtil.isTesting == false) {
			URL scriptURL = null;
			try {
				scriptURL = FileLocator.toFileURL(SplashPlugin.getDefault()
						.getBundle().getEntry(JRUBY_SCRIPT));
			} catch (IOException e) {
				e.printStackTrace();
			}

			path = scriptURL.getPath();
		} else {
			path = "D:/projects/AWE from SVN/org.amanzi.splash/jruby.rb";
		}

		String input = NeoSplashUtil.getScriptContent(path);

		HashMap<String, Object> globals = new HashMap<String, Object>();
		globals.put(TABLE_MODEL_RUBY_NAME, this);
		globals.put(JRUBY_PATH_RUBY_NAME, ScriptUtils.getJRubyHome());
		makeRubyGlobals(runtime, globals);

		runtime.evalScriptlet(input);
	}

	/**
	 * Utility method that creates a Ruby Global Variables from Java Objects
	 * 
	 * @param rubyRuntime
	 *            Ruby Environment
	 * @param globals
	 *            Map with Names of Variables and Java Objects
	 */

	private void makeRubyGlobals(Ruby rubyRuntime,
			HashMap<String, Object> globals) {
		for (String name : globals.keySet()) {
			IRubyObject rubyObject = JavaEmbedUtils.javaToRuby(rubyRuntime,
					globals.get(name));
			rubyRuntime.getGlobalVariables().define("$" + name,
					new ValueAccessor(rubyObject));
		}
	}

	/**
	 * Method that update Cell that has reference to Script
	 * 
	 * @param cell
	 *            Cell
	 * @author Lagutko_N
	 */

	public void updateCellFromScript(Cell cell) {
		updateDefinitionFromScript(cell);

		interpret((String) cell.getDefinition(), Cell.DEFAULT_DEFINITION, cell
				.getRow(), cell.getColumn());
	}

	/**
	 * Function that updates definition of Cell from Script
	 * 
	 * @param cell
	 *            Cell to update
	 * @author Lagutko_N
	 */

	public void updateDefinitionFromScript(Cell cell) {
		String content = NeoSplashUtil.getScriptContent(cell.getScriptURI());
		cell.setDefinition(content);
	}

	/**
	 * Interprets a definition of Cell by row and column
	 * 
	 * @param definition
	 *            definition of Cell
	 * @param row
	 *            row index of Cell
	 * @param column
	 *            column index of Cell
	 * @return Cell with updated value
	 */
	public Cell interpret(String definition, int row, int column) {
		String cellID = new CellID(row, column).getFullID();
		String formula1 = definition;
		Cell se = getCellByID(cellID);

		Object s1 = interpret_erb(cellID, formula1);

		se.setDefinition(definition);
		se.setValue((String) s1);

		this.setValueAt(se, row, column);
		return se;
	}

	/**
	 * Interprets a formula using ERB
	 * 
	 * @param cellID
	 *            id of Cell
	 * @param formula
	 *            formula of Cell
	 * @return interpreted value
	 */
	public String interpret_erb(String cellID, String formula) {
		Object s = EMPTY_STRING;
		
		NeoSplashUtil.logn("interpret_erb: formula = " + formula + " - cellID:"
				+ cellID);
		NeoSplashUtil.logn("cellID.toLowerCase():" + cellID.toLowerCase());

		String input = "update(\"" + cellID.toLowerCase() + "\", \""
				+ formula + "\")";

		NeoSplashUtil.logn("ERB Input: " + input);

		s = runtime.evalScriptlet(input);

		NeoSplashUtil.logn("ERB Output = " + s);

		if (s == null)
			s = "ERROR";

		return s.toString();
	}

	/**
	 * Interprets a Definition of cell by row and column
	 * 
	 * @param definition
	 *            new formula of cell
	 * @param oldDefinition
	 *            old formula of cell
	 * @param row
	 *            row of Cell
	 * @param column
	 *            column of Cell
	 * @return Cell with interpeted values
	 */
	// TODO: Lagutko: do we need oldDefinition param?
	public Cell interpret(String definition, String oldDefinition, int row,
			int column) {
		String cellID = new CellID(row, column).getFullID();
		String formula1 = definition;
		NeoSplashUtil
				.logn("<><><><><><><><><><><><><><><><><><><><><><><><><><><><><><>");
		NeoSplashUtil.logn("Start interpreting a cell...");
		NeoSplashUtil.logn("CellID = " + cellID);

		Cell se = getCellByID(cellID);

		if (se == null) {
			NeoSplashUtil.logn("WARNING: se = null");
			se = new Cell(row, column, Cell.DEFAULT_VALUE,
					Cell.DEFAULT_DEFINITION, new CellFormat());
		}

		Object s1 = interpret_erb(cellID, formula1);
		
		NeoSplashUtil.logn("Setting cell definition: " + definition);
        se.setDefinition(definition);

        NeoSplashUtil.logn("Setting cell value:" + (String) s1);
        se.setValue((String) s1);

		setValueAt(se, row, column, oldDefinition);

		return se;
	}

	/**
	 * Get number of rows
	 */
	public int getRowCount() {
		return rowCount;
	}

	/**
	 * Get number of columns
	 */
	public int getColumnCount() {
		return columnCount;
	}

	/**
	 * return model value at certain location
	 * 
	 */
	public Object getValueAt(final int row, final int column) {
		// row and column index are checked but storing in a Hashtable
		// won't cause real problems
		if (row >= getRowCount())
			throw new ArrayIndexOutOfBoundsException(row);
		if (column >= getColumnCount())
			throw new ArrayIndexOutOfBoundsException(column);

		Cell result = (Cell) ActionUtil.getInstance().runTaskWithResult(
				new RunnableWithResult() {
					private Cell result = null;

					public Object getValue() {
						return result;
					}

					public void run() {
						result = service.getCell(spreadsheet, new CellID(row,
								column));
					}
				});

		return result;

	}

	/**
	 * check if cell editable
	 */
	public boolean isCellEditable(int row, int column) {
		return true;
	}

	/**
	 * set model data with a certain value
	 */
	public void setValueAt(final Object value, int row, int column) {
		// row and column index are checked but storing in a Hashtable
		// won't cause real problems
//		NeoSplashUtil.logn("row = " + row + " - getRowCount () = "
//				+ getRowCount() + " - column: " + column + " - getColumnCount: ");
		if (row >= getRowCount())
			throw new ArrayIndexOutOfBoundsException(row);
		if (column >= getColumnCount()) {
			throw new ArrayIndexOutOfBoundsException(column);
		}

		ActionUtil.getInstance().runTask(new Runnable() {
			public void run() {
				service.updateCell(spreadsheet, (Cell) value);
			}
		}, true);
		
		
		

		fireTableChanged(new TableModelEvent(this, row, row, column));
	}

	/**
	 * Sets the Cell to given row and column
	 * 
	 * @param value
	 *            Cell
	 * @param row
	 *            row index
	 * @param column
	 *            column index
	 * @param oldDefinition
	 *            oldDefinition
	 */
	public void setValueAt(final Object value, final int row, final int column,
			String oldDefinition) {
		// row and column index are checked but storing in a Hashtable
		// won't cause real problems
		if (row >= getRowCount())
			throw new ArrayIndexOutOfBoundsException(row);
		if (column >= getColumnCount())
			throw new ArrayIndexOutOfBoundsException(column);

		ActionUtil.getInstance().runTask(new Runnable() {
			public void run() {
				updateCellWithDependencies((Cell) value);
			}
		}, false);

		fireTableChanged(new TableModelEvent(this, row, row, column));
	}

	/**
	 * Method that updates references of given Cell
	 * 
	 * @param cellID
	 *            ID of Cell to update
	 * @param referencedIDs
	 *            IDs of referenced cells
	 */
	public void updateCellReferences(final String cellID,
			final RubyArray referencedIDs) {
		ActionUtil.getInstance().runTask(new Runnable() {
			public void run() {
				service.updateCellReferences(spreadsheet, cellID,
								referencedIDs);
			}
		}, false);
	}

	/**
	 * Recursively updates Cell Values by References
	 * 
	 * @param rootCell
	 *            Cell for update
	 */

	private void updateCellWithDependencies(Cell rootCell) {
		service.updateCell(spreadsheet, rootCell);

		for (Cell c : service.getDependentCells(spreadsheet, rootCell
				.getCellID())) {
			refreshCell(c);
			updateCellWithDependencies(c);
		}
	}

	/**
	 * Refreshes Cell with given ID
	 * 
	 * @param cellID
	 *            id of Cell to refresh
	 */
	public void refreshCell(String cellID) {
		NeoSplashUtil.logn("refreshCell: cellID = " + cellID);
		Cell cell = service.getCell(spreadsheet, new CellID(cellID));

		String definition = (String) cell.getDefinition();
		String formula1 = (String) cell.getDefinition();
		Cell se = cell;

		if (se == null) {
			NeoSplashUtil.logn("WARNING: se = null");
		}

		Object s1 = interpret_erb(cellID, formula1);
		se.setDefinition(definition);
		se.setValue((String) s1);

		this.setValueAt(se, cell.getRow(), cell.getColumn());
	}

	/**
	 * Refreshes given Cell
	 * 
	 * @param cell
	 *            Cell
	 */

	public void refreshCell(Cell cell) {
		NeoSplashUtil.printCell("Refreshing Cell", cell);

		String cellID = cell.getCellID().getFullID();

		NeoSplashUtil.logn("refreshCell: cellID = " + cellID);

		String definition = (String) cell.getDefinition();
		String formula1 = (String) cell.getDefinition();
		Cell se = cell;

		if (se == null) {
			NeoSplashUtil.logn("WARNING: se = null");
		}

		Object s1 = interpret_erb(cellID, formula1);
		se.setDefinition(definition);
		se.setValue((String) s1);

		this.setValueAt(se, cell.getRow(), cell.getColumn());
	}

	/**
	 * Returns the Cell by ID
	 * 
	 * @param cellID
	 *            ID of Cell
	 * @return Cell
	 */
	public Cell getCellByID(String cellID) {
		CellID id = new CellID(cellID);

		return (Cell) getValueAt(id.getRowIndex(), id.getColumnIndex());
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
	 * @param cell
	 *            cell
	 * @author Lagutko_N
	 */
	public void updateCellFormat(Cell cell) {
		service.updateCell(spreadsheet, cell);
	}

	/**
	 * Returns Spreadsheet Service
	 * 
	 * @return spreadsheet service
	 */
	public SpreadsheetService getService() {
		return service;
	}

	/**
	 * Sets Spreadsheet Service
	 * 
	 * @param service
	 *            spreadsheet service
	 */
	public void setService(SpreadsheetService service) {
		this.service = service;
	}

	/**
	 * Returns current Spreadsheet
	 * 
	 * @return spreadsheet node
	 */
	public SpreadsheetNode getSpreadsheet() {
		return spreadsheet;
	}

	/**
	 * Sets current Spreadsheet
	 * 
	 * @param spreadsheet
	 *            spreadsheet node
	 */
	public void setSpreadsheet(SpreadsheetNode spreadsheet) {
		this.spreadsheet = spreadsheet;
	}

	/**
	 * @return the rubyProjectNode
	 */
	public RubyProjectNode getRubyProjectNode() {
		return rubyProjectNode;
	}

	/**
	 * @return the runtime
	 */
	public Ruby getRubyRuntime() {
		return runtime;
	}
}
