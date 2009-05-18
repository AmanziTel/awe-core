package org.amanzi.splash.swing;

import java.net.URI;
import java.util.ArrayList;
import javax.swing.table.TableModel;

import org.amanzi.splash.jruby.SplashJRubyInterpreter;
import org.amanzi.splash.utilities.Util;

import com.eteks.openjeks.format.CellFormat;

public class Cell
{
	private transient Object value;
	private Object definition;
	private Cell cellGraphInfo;
	private CellFormat cellFormat;
	
	private String cellID;
	private int row;
	private int column;
	private ArrayList<Cell> rfdCells;
	private ArrayList<Cell> rfgCells;
	
	//Lagutko: new attributes 
	private URI scriptURI;
	private boolean hasReference;
	
	/**
	 * Another constructor (create an expression using definition and value), used with 
	 * file loading mechanism
	 * @param definition
	 * @param value
	 */
	public Cell (String definition, String value)
	{
		this.definition = definition;
		this.value = value;
		rfdCells = new ArrayList<Cell>();
		rfgCells = new ArrayList<Cell>();
		
		cellFormat = new CellFormat();
		//Lagutko: Cell hasn't reference to script on creation
		hasReference = false;
	}
	
	public void renameCell(String oldCellID, String newCellID)
	{
		//Cell c = getCellByID(oldCellID);
		setCellID(newCellID);
		setRow(Util.getRowIndexFromCellID(newCellID));
		setColumn(Util.getColumnIndexFromCellID(newCellID));
		
		//Util.printTableModelStatus(model)
		Util.printCellList("RFG List", rfgCells);
		for (int i=0;i<rfgCells.size();i++)
		{
			
			Cell c = rfgCells.get(i);
			Util.printCell("processing RFG cell ", c);
			ArrayList<Cell> rfd = c.getRfdCells();
			Util.printCellList("RFD cells of cell " + c.getCellID(), rfd);
			for (int j=0;j<rfd.size();j++)
			{
				Cell c1 = rfd.get(j);
				Util.printCell("processing RFD cell ", c1);
				
				if (c1.getCellID().equals(newCellID))
				{
					String definition = (String) c.getDefinition();
					Util.log("old definition: definition");
					definition = definition.replace(oldCellID, newCellID);
					Util.log("new definition: definition");
					c.setDefinition(definition);
				}
			}
		}
	}
	
	/**
	 * Constructor using row and column
	 * @param row
	 * @param column
	 */
	public Cell (int    row,int    column)
	{
		this.row    = row;
		this.column = column;
		rfdCells = new ArrayList<Cell>();
		rfgCells = new ArrayList<Cell>();
		cellFormat = new CellFormat();
	}

	public Cell(int row, int column, String definition, String value,
			CellFormat c) {
		this.row    = row;
		this.column = column;
		
		this.definition = definition;
		//SplashJRubyInterpreter s = new SplashJRubyInterpreter();
		this.value = value;
		
		this.cellID = Util.getCellIDfromRowColumn(row, column);
		
		this.rfdCells = new ArrayList<Cell>();
		this.rfgCells = new ArrayList<Cell>();
		
		cellFormat = c;
	}
	
	public Cell(Object value, Object definition,
			Cell cellGraphInfo, CellFormat cellFormat) {
		super();
		this.value = value;
		this.definition = definition;
		this.cellGraphInfo = cellGraphInfo;
		
		cellFormat = new CellFormat();
		
		this.cellFormat = cellFormat;
		
		rfdCells = new ArrayList<Cell>();
		rfgCells = new ArrayList<Cell>();
	}

	public Cell(Object value, Object definition,
			Cell cellGraphInfo) {
		super();
		this.value = value;
		this.definition = definition;
		this.cellGraphInfo = cellGraphInfo;
		cellFormat = new CellFormat();
		rfdCells = new ArrayList<Cell>();
		rfgCells = new ArrayList<Cell>();
	}

	/**
	 * Get value
	 * @return
	 */
	public Object getValue ()
	{
		String s = (String) value;
		return s.replace("\n", "");
	}

	/**
	 * Set value to null
	 */
	public void invalidateValue ()
	{
		value = null;
	}

	public Object getDefinition() {
		return definition;
	}

	public void setDefinition(Object definition) {
		this.definition = definition;
	}



	

	public Cell getCellGraphInfo() {
		return cellGraphInfo;
	}

	public void setCellGraphInfo(Cell cellGraphInfo) {
		this.cellGraphInfo = cellGraphInfo;
	}

	public CellFormat getCellFormat() {
		return cellFormat;
	}

	public void setCellFormat(CellFormat cellFormat) {
		this.cellFormat = cellFormat;
	}

	public void setValue(Object value) {
		this.value = value;
	}
	
	/**
	 * Add argument cell to referred cells
	 * @param c
	 */	
	public void addRfdCell(Cell c)
	{
		//if (Util.isCellInList(c, rfdCells) == false)
			rfdCells.add(c);
	}
	
	/**
	 * Add argument cell to referring cells
	 * @param c
	 */
	public void addRfgCell(Cell c)
	{
		//if (Util.isCellInList(c, rfgCells) == false)
			rfgCells.add(c);
	}
	
	/**
	 * Remove cell from referred cells
	 * @param c
	 */
	public void removeRfdCell(Cell c)
	{
		for (int i=0;i<rfdCells.size();i++)
		{
			if (rfdCells.get(i).getCellID().equals(c.getCellID())  == true)
			{
				rfdCells.remove(i);
				break;
			}
		}
	}
	
	/**
	 * remove cell from referring cells
	 * @param c
	 */
	public void removeRfgCell(Cell c)
	{
		for (int i=0;i<rfgCells.size();i++)
		{
			if (rfgCells.get(i).getCellID().equals(c.getCellID())  == true)
			{
				rfgCells.remove(i);
				break;
			}
		}
	}

	public String getCellID() {
		return cellID;
	}

	public void setCellID(String cellID) {
		this.cellID = cellID;
	}

	public int getRow() {
		return row;
	}

	public void setRow(int row) {
		this.row = row;
	}

	public int getColumn() {
		return column;
	}

	public void setColumn(int column) {
		this.column = column;
	}

	public ArrayList<Cell> getRfdCells() {
		return rfdCells;
	}

	public void setRfdCells(ArrayList<Cell> rfdCells) {
		this.rfdCells = rfdCells;
	}

	public ArrayList<Cell> getRfgCells() {
		return rfgCells;
	}

	public void setRfgCells(ArrayList<Cell> rfgCells) {
		this.rfgCells = rfgCells;
	}
	
	/**
	 * Update referred list
	 * @param newList
	 */
	public void updateRfdCells(ArrayList<Cell> newList)
	{
		// first on new RFD list, add new entries to existing
		for (int i=0;i<newList.size();i++)
		{
			if (rfdCells.contains(newList.get(i)) == false)
				rfdCells.add(newList.get(i));
		}
		
		// second, scan of existing, remove cells not found in the new list
		for (int i=0;i<rfdCells.size();i++)
		{
			Cell c  = rfdCells.get(i);
			if (newList.contains(c) == false)
				rfdCells.remove(c);
		}
	}
	
	public void emptyRfdCells()
	{
		rfdCells.clear();
	}
	
	public void emptyRfgCells()
	{
		rfgCells.clear();
	}
	
	

	/**
	 * Check equality
	 */
	public boolean equals (Object object)
	{
		return    object instanceof Cell
		&& ((Cell)object).row == row
		&& ((Cell)object).column == column;
	}

	/**
	 * return hash code
	 */
	public int hashCode ()
	{
		return (row % 0xFFFF) | ((column % 0xFFFF) << 16);
	}

	/**
	 * convert to string
	 */
	public String toString ()
	{
		return row + " " + column;
	}
	
	//Lagutko: getter and setter for script name
	
	/**
	 * Set name of script and sets that cell has reference to script
	 * @param newScriptName name of scipt
	 * @author Lagutko_N
	 */
	public void setScriptURI(URI newScriptName) {
		scriptURI = newScriptName;
		hasReference = true;		
	}
	
	/**
	 * Returns name of script
	 * 
	 * @return name of script
	 * @author Lagutko_N
	 */
	public URI getScriptURI() {
		return scriptURI;
	}
	
	/**
	 * Is this cell has reference to script?
	 * 
	 * @return Is this cell has reference to script?
	 * @author Lagutko_N
	 */
	public boolean hasReference() {
		return hasReference;
	}
}
