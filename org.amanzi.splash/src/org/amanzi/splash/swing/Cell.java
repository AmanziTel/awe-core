package org.amanzi.splash.swing;

import java.net.URI;

import org.amanzi.neo.core.database.nodes.CellID;

import com.eteks.openjeks.format.CellFormat;

public class Cell
{
    /*
     * Default value of Cell
     */
    public static final String DEFAULT_VALUE = "";
    
    /*
     * Default definition of Cell
     */
    public static final String DEFAULT_DEFINITION = "";
    
    public static final String CELL_CYLIC_ERROR = "ERROR:cyclic";
    
	private transient Object value;
	private Object definition;
	private Cell cellGraphInfo;
	private CellFormat cellFormat;
	
	private CellID cellID;
	private int row;
	private int column;
	
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
		
		cellFormat = new CellFormat();
		//Lagutko: Cell hasn't reference to script on creation
		hasReference = false;
	}
	
	public void renameCell(String oldCellID, String newCellID)
	{
		setCellID(new CellID(newCellID));		
		setRow(getCellID().getRowIndex());
		setColumn(getCellID().getColumnIndex());
		
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
		cellFormat = new CellFormat();
	}

	public Cell(int row, int column, String definition, String value,
			CellFormat c) {
		this.row    = row;
		this.column = column;
		
		this.definition = definition;
		//SplashJRubyInterpreter s = new SplashJRubyInterpreter();
		this.value = value;
		
		this.cellID = new CellID(row, column);
		
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
	}

	public Cell(Object value, Object definition,
			Cell cellGraphInfo) {
		super();
		this.value = value;
		this.definition = definition;
		this.cellGraphInfo = cellGraphInfo;
		cellFormat = new CellFormat();
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
	


	public CellID getCellID() {
		return cellID;
	}

	public void setCellID(CellID cellID) {
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
		hasReference = scriptURI != null;		
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
