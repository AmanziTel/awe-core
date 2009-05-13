package org.amanzi.splash.swing;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.swing.event.TableModelEvent;
import javax.swing.table.DefaultTableModel;

import org.amanzi.splash.ui.SplashPlugin;
import org.amanzi.splash.utilities.Util;
import org.eclipse.core.runtime.FileLocator;

import com.eteks.openjeks.format.BorderStyle;
import com.eteks.openjeks.format.CellBorder;
import com.eteks.openjeks.format.CellFormat;
import com.eteks.openjeks.format.CellSetBorder;
import com.eteks.openjeks.format.TableFormat;

public class SplashTableModel extends DefaultTableModel
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -2315033560766233243L;
	private int    rowCount;
	private int    columnCount;
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
	public SplashTableModel (int rowCount, int columnCount)
	{
		this.rowCount     = rowCount;
		this.columnCount  = columnCount;

		initializeJRubyInterpreter();

		cellValues = new Hashtable ();
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
		ClassLoader remember = Thread.currentThread().getContextClassLoader();
		try{
			// This hack was needed so that the ruby code can find the same java classes as the current java code
			//Thread.currentThread().setContextClassLoader(this.getClass().getClassLoader());

			//rubyEngine.eval("require '/home/craig/.m2/repository/org/opengis/geoapi/2.2-SNAPSHOT/geoapi-2.2-20080605.180517-15.jar'");
			URL scriptURL = null;
			try {
				scriptURL = FileLocator.toFileURL(SplashPlugin.getDefault().getBundle().getEntry("jruby.rb"));
			} catch (IOException e) {

				e.printStackTrace();
			}

			ScriptEngineManager m = new ScriptEngineManager();

			m.registerEngineName("jruby", 
					new com.sun.script.jruby.JRubyScriptEngineFactory());

			engine = m.getEngineByName("jruby");
			ScriptContext context = engine.getContext();

			Util.log("scriptURL.getPath():" + scriptURL.getPath());
			String path = scriptURL.getPath();

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
			Util.log("input: " + input);
			engine.eval(input);

			engine.eval("$sheet = Spreadsheet.new", context);

		} catch (ScriptException e) {
			Util.log(e.toString()+": "+e.getFileName()+"["+e.getLineNumber()+":"+e.getColumnNumber()+"]: "+e.getMessage());
			e.printStackTrace();
		}finally{
			Thread.currentThread().setContextClassLoader(remember);
		}
	}

	private Object interpret_element(String cellID, String formula){
		ScriptContext ctx = engine.getContext();
		String input = "$sheet.cells." + cellID.toLowerCase() + "=" +formula;
		Util.log ("input = " + input);
		Object s = null;
		try {
			s = engine.eval(input, ctx);
		} catch (ScriptException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Util.log("JRuby1: " + s);
		return s;
	}

	public Cell interpret(String definition, int row, int column){
		
		String s0 = "";
		String cellID = Util.getCellIDfromRowColumn(row, column);
		String data0 = definition;
		Cell se = getCellByID(cellID);

		if (definition.startsWith("=") == false){
			// This is normal text entered into cell, then
			//String value = definition.replace("'", "");
			//value = definition.replace("\"", "");
			
			Object value = interpret_element(cellID,definition);
			se.setValue(value);
			se.setDefinition(definition);
			
			
		}
		else{
			List<String> list = Util.findComplexCellIDs(definition);
			data0 = data0.replace("=", "");
			for (int i=0;i<list.size();i++){
//				if (data0.contains("#{"+list.get(i)+"}") == false)
//				{
//					// this is the case of =a1
//					data0 = data0.replace(list.get(i), "#{" + list.get(i) + "}");
//				}

				data0 = data0.replace(list.get(i), "$sheet.cells." + list.get(i).toLowerCase());
				
			}

//			if (data0.contains("\"") == false)
//				s0 = "\"" + data0 + "\"";
//			else
				s0 = data0;
			
			Util.log("s0: " + s0);
			Object s = interpret_element(cellID, s0);

			se.setDefinition(definition);
			se.setValue((String)s);
		}

		this.setValueAt(se, row, column);
		return se;
	}


	/**
	 * Save model data to String buffer
	 * @param sb
	 */
	public void save(StringBuffer sb)  {
		Util.log("rowCount = " + rowCount);
		Util.log("columnCount = " + columnCount);
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
				//Util.log("line = " + line);
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
		Util.log("rowCount = " + rowCount);
		Util.log("columnCount = " + columnCount);

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



				CellFormat c = t.getFormatAt(i, j);

				if (c == null){
					c = new CellFormat();
				}

				String line = definition.replace("\n", "") + ";" + value.replace("\n", "") + ";" + Util.getFormatString(c) + ";";
				//Util.log("line = " + line);

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
					//Util.log("definition:" + definition);
					value = list.get(m++);
					//Util.log("value:" + value);
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

					BorderStyle top,bottom,left,right,internalHorizontal,internalVertical;
					top = bottom = left = right = internalHorizontal = internalVertical = new BorderStyle(1,BorderStyle.BASIC);


					Color topColor, bottomColor, leftColor, rightColor, internalHorizontalColor, internalVerticalColor;

					topColor = new Color(0,0,0);
					bottomColor = new Color(0,0,0);
					leftColor = new Color(0,0,0);
					rightColor = new Color(0,0,0);
					internalHorizontalColor = new Color(0,0,0);
					internalVerticalColor = new Color(0,0,0);

					c.setCellBorder(new CellBorder());

					// Create a new expression with value and definition
					Cell se = new Cell(rowIndex, j, definition, value, c);

					setValueAt(se, rowIndex, j);

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

	/**
	 * set model data with a certain value
	 */
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
			updateCellReferences(Util.getCellIDfromRowColumn(row, column), (String) ((Cell)value).getDefinition());
		}

		fireTableChanged (new TableModelEvent (this, row, row, column));
	}

	/**
	 * Get definition string
	 * @param row
	 * @param column
	 * @return
	 */
	private String getCellDefinition(int row, int column)
	{
		Object o = getValueAt(row, column);
		if (o instanceof Cell)
		{
			Cell ex = (Cell)(getValueAt(row, column));
			return (String) ex.getDefinition();
		}
		else
			return getValueAt(row, column).toString();
	}

	/**
	 * set definition string
	 * @param row
	 * @param column
	 * @param value
	 */
	private void setDefinitionString(int row, int column, Object value)
	{
		setValueAt(value, row, column);
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
		String cellID = Util.getCellIDfromRowColumn(row,column);

		//Util.log("oldFormula = " + oldFormula);
		//Util.log("newFormula = " + newFormula);

		if (!newFormula.equals(oldFormula) && oldFormula != null )
		{
			Cell cell ;
			ArrayList<Cell> list;
			Util.log("cellID = " + cellID);

			cell = getCellByID(cellID);

			Util.printCell("cell", cell);

			if (cell != null)
			{
				if (isInfiniteLoop(cell, newFormula) == false)
				{
					Util.log("No infinite loop found ");
					updateCellReferences (cellID,	newFormula);

					list = getAllReferringCells(cell);

					Util.printCellList("Referring Cells of Cell " + cell.getCellID(), list);

					for (int i=0;i<list.size();i++)
					{
						int r = list.get(i).getRow();
						int c = list.get(i).getColumn();

						Util.log("r = " + r);
						Util.log("c = " + c);
						Util.log("CellID = " + Util.getCellIDfromRowColumn(r, c));

						String definition = getCellDefinition(r,c);

						Util.log("definition = " + definition);
						interpret(definition, r, c);
					}
				}
			}
		}
		else
		{
			updateCellReferences (cellID,	newFormula );
		}

		//Util.printTableModelStatus(this);
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
		// Removing reference means remove A from RFD list of C, and C from RFG list of A
		C.removeRfdCell(A);
		A.removeRfgCell(C);
	}

	public Cell getCellByID(String cellID)
	{
		//Util.log("cellID = " + cellID);
		int row = Util.getRowIndexFromCellID(cellID);
		//Util.log("row = " + row);
		int column = Util.getColumnIndexFromCellID(cellID);
		//Util.log("column = " + column);

		return (Cell)getValueAt(row, column);
	}

	public boolean isInfiniteLoop(Cell c, String new_formula)
	{
		boolean ret = false;

		// find RFDs
		List<Cell> idsList = findComplexCellIDs(new_formula);

		for (int i=0;i<idsList.size();i++)
		{
			Cell c1 = idsList.get(i);
			if (Util.isCellInList(c1, getAllReferringCells(c)) == true)
			{
				ret = true;
				break;
			}
		}

		return ret;
	}

	public ArrayList<Cell> getAllReferringCells(Cell C)
	{
		ArrayList<Cell> templist = new ArrayList<Cell>();
		ArrayList<Cell> list = new ArrayList<Cell>();
		//if (C.isTraversed() == false)
		{
			//C.setTraversed(true);
			templist = C.getRfgCells();

			for (int i=0;i<templist.size();i++)
			{
				list.add(templist.get(i));

				list.addAll((ArrayList<Cell>)(getAllReferringCells(templist.get(i))));
			}
		}
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
		//Util.log("C_Content = " + newDefinition);

		Cell C = getCellByID(cellID);

		ArrayList<Cell> definitionCells = findComplexCellIDs(newDefinition);

		if (definitionCells.size() == 0) return;

		//Util.printCellList("definitionCells", definitionCells);

		//Util.printCellList("RFD Cells of " + C.getCellID(), C.getRfdCells());

		/* Now, we will scan over cells in RFD list (which form old definition of cell)
		 * if Cell exist in new definition, then it's added reference between current cell and such cell
		 * if Cell exist in old definition, not existing in new definition, then remove reference between current cell and such cell 
		 */ 

		for (int i=0;i< C.getRfdCells().size();i++)
		{
			Cell oldDefinitionCell = C.getRfdCells().get(i);

			//Util.log("checking RFD cell " + oldDefinitionCell.getCellID() + " to exist in new definition cells");
			// if existAB is not found in the new listAB, then remove reference
			if (Util.isCellInList(oldDefinitionCell, definitionCells) == false)
			{
				//Util.log("cell " + oldDefinitionCell.getCellID() + " doesn't exist, and will be removed");
				removeReferenceBetweenTwoCells(C, oldDefinitionCell);
			}
		}

		for (int i=0;i< definitionCells.size();i++)
		{
			Cell newDefinitionCell = definitionCells.get(i);
			//Util.log("checking new definition cell " + newDefinitionCell.getCellID() + " to exist in RFD cells");
			// if existAB is not found in the new listAB, then remove reference
			if (Util.isCellInList(newDefinitionCell, C.getRfdCells()) == false)
			{
				//Util.log("cell " + newDefinitionCell.getCellID() + " doesn't exist, and will be added");
				//Util.printTableModelStatus(this);
				addReferenceBetweenTwoCells(C, newDefinitionCell);
			}
		}

		//Util.printTableModelStatus(this);

	}
}
