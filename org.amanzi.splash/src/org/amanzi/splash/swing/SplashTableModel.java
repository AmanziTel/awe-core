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
import java.util.Hashtable;
import java.util.List;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

import org.amanzi.scripting.jruby.EclipseLoadService;
import org.amanzi.scripting.jruby.ScriptUtils;
import org.amanzi.splash.console.SpreadsheetManager;
import org.amanzi.splash.ui.SplashPlugin;
import org.amanzi.splash.utilities.Util;
import org.eclipse.core.runtime.FileLocator;
import org.jruby.Ruby;
import org.jruby.RubyInstanceConfig;
import org.jruby.runtime.load.LoadService;

import com.eteks.openjeks.format.CellBorder;
import com.eteks.openjeks.format.CellFormat;
import com.eteks.openjeks.format.TableFormat;
public class SplashTableModel extends DefaultTableModel
{
	private static final long serialVersionUID = -2315033560766233243L;
	private int    rowCount;
	private int    columnCount;
	@SuppressWarnings("unchecked")
	private Hashtable cellValues;
	Ruby runtime;

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
		this.cellValues = new Hashtable ();

		initializeJRubyInterpreter();
		for (int i=0;i<Util.MAX_SPLASH_ROW_COUNT;i++)
			for (int j=0;j<Util.MAX_SPLASH_COL_COUNT;j++)
			{
				setValueAt(new Cell(i,j,"","",new CellFormat()),i,j);
				interpret("",i,j);
			}
		SpreadsheetManager.getInstance().setActiveModel(this);
	}


	/**
	 * This is the method that starts up the JRuby runtime that will be used for
	 * all parsing of cell contents, using ERB formula syntax.
	 */
	protected void initializeJRubyInterpreter() {
		RubyInstanceConfig config = null;
		config = new RubyInstanceConfig() {{
			//TODO: See if the following two lines are actually needed for Splash (they were copied from AWEScript console originally)
			setJRubyHome(ScriptUtils.getJRubyHome());	// this helps online help work
			//setObjectSpaceEnabled(true); // useful for code completion inside the IRB
			setLoadServiceCreator(new LoadServiceCreator() {
				public LoadService create(Ruby runtime) {
					return new EclipseLoadService(runtime);
				}
			});
		}};
		
		runtime = Ruby.newInstance(config);
		//TODO: See if this line is needed, since it passes nothing (and was copied from AWEScript console originally)
		runtime.getLoadService().init(ScriptUtils.makeLoadPath(new String[] {}));

		URL scriptURL = null;
		try {
			scriptURL = FileLocator.toFileURL(SplashPlugin.getDefault().getBundle().getEntry("jruby.rb"));
		} catch (IOException e) {
			throw new RuntimeException("Failed to find Splash initialization code",e);
		}

		StringBuffer input = new StringBuffer();
		try {
			String line;
			BufferedReader br = new BufferedReader(new FileReader(scriptURL.getPath()));
			while ((line = br.readLine()) != null) {
				input.append(line).append("\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		runtime.evalScriptlet(input.toString());
		runtime.evalScriptlet("$sheet = Spreadsheet.new");				
	}

//	/**
//	 * Constructor for class using RowCount and ColumnCount
//	 * @param rowCount
//	 * @param columnCount
//	 */
//	@SuppressWarnings("unchecked")
//	public SplashTableModel (int rowCount, int columnCount)
//	{
//		this.rowCount     = rowCount;
//		this.columnCount  = columnCount;
//		Util.isDebug = true;
//		initializeJRubyInterpreter();
//
//		cellValues = new Hashtable ();		
//	}

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

		String input = "require" + "'" + path + "'" + "\n" +
		"template = ERB.new <<-EOF" + "\n" +
		formula + "\n" +
		"EOF" + "\n" +
		"$sheet.cells." + cellID.toLowerCase() + "=" +  "template.result(binding)" + "\n" +
		"$sheet.cells." + cellID.toLowerCase();

		Util.logn("ERB Input: " + input);


		s = runtime.evalScriptlet(input);

		Util.logn("ERB Output = " + s);


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
				
				interpret_erb(cellID, definition);

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
				boolean hasReference = false;
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

				String line = definition.replace("\n", "") + ";" + value.replace("\n", "") + ";" + Util.getFormatString(((Cell)o).getCellFormat()) + ";";
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
			CellFormat cf = ((Cell)value).getCellFormat();


			((Cell)value).setCellFormat(cf);	

			

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
	public boolean updateCellsAndTableModelReferences(int row, int column, String oldFormula, String newFormula)
	{
		boolean isInfLoop = false;
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
		if (isInfiniteLoop(cell, newFormula) == false)
		{

			Util.logn("No infinite loop found ");
			if (cell != null)
			{

				if (!newFormula.equals(oldFormula) && oldFormula != null)
				{
					Util.logn("!newFormula.equals(oldFormula) && oldFormula != null");
					Util.logn("Updating cell references of Cell " + cellID + " with formula " + newFormula);
					updateCellReferences (cellID,	newFormula);
					// Get all referring cells to update
					list = getAllReferringCells(cell);
					for (int i=0;i<list.size();i++)
					{
						refreshCell(list.get(i));
					}

				}
			}
		}else{
			//cell.setValue("ERR: INF LOOP");
			//refreshCell(cell);
			Cell se = new Cell(row, column, newFormula, "ERROR", new CellFormat());
			setValueAt(se, row, column);
			isInfLoop = true;
			Util.logn("infinite loop found !!!!!");
		}

		Util.logExit("updateCellsAndTableModelReferences");
		return isInfLoop;
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
					int row = Util.getRowIndexFromCellID(cellID);
					int column = Util.getColumnIndexFromCellID(cellID);
					Cell se = new Cell(row, column, newFormula, "ERROR", new CellFormat());
					setValueAt(se, row, column);
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
		//Util.logEnter("getCellByID");
		//Util.logn("cellID = " + cellID);
		int row = Util.getRowIndexFromCellID(cellID);
		//Util.logn("row = " + row);
		int column = Util.getColumnIndexFromCellID(cellID);
		//Util.logn("column = " + column);

		//Util.logExit("getCellByID");
		return (Cell)getValueAt(row, column);

	}

	public boolean isInfiniteLoop(Cell c, String new_formula)
	{
		Util.logEnter("isInfiniteLoop");
		boolean ret = false;

		// find RFDs
		List<Cell> idsList = findComplexCellIDs(new_formula);

		if (idsList.contains(c) == true){
			ret = true;
		}else{
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
		}

		Util.logExit("isInfiniteLoop");
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



	public void addTableModelListener(TableModelListener arg0) {
		// TODO Auto-generated method stub

	}

	public void removeTableModelListener(TableModelListener arg0) {
		// TODO Auto-generated method stub

	}
}
