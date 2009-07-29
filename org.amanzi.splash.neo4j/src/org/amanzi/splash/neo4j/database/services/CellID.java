package org.amanzi.splash.neo4j.database.services;

import org.amanzi.splash.neo4j.utilities.Util;

/**
 * Utility class that wraps ID of Cell and provides it in different forms
 * 
 * @author Lagutko_N
 */

public class CellID {
    
    /*
     * Name of Column
     */
    private String columnName;
    
    /*
     * Name of Row
     */
    private String rowName;
    
    /*
     * Index of Column
     */
    private Integer columnIndex;
    
    /*
     * Index of Row
     */
    private Integer rowIndex;
    
    /*
     * Id of Column
     */
    private String fullID;
    
    /**
     * Constructor for ID from Strings
     * 
     * @param row name of Row
     * @param column name of Column
     */
    public CellID(String row, String column) {
        columnName = column;
        rowName = row;
        
        fullID = column + row;
        
        columnIndex = Util.getColumnIndexFromCellID(fullID);
        rowIndex = Util.getRowIndexFromCellID(fullID);
    }
    
    /**
     * Constructor for ID from indexes
     * 
     * @param row index of Row
     * @param column index of Column
     */
    public CellID(Integer row, Integer column) {
        columnIndex = column;
        rowIndex = row;
        
        fullID = Util.getCellIDfromRowColumn(row, column);
        
        columnName = Util.getColumnLetter(column);
        rowName = Integer.toString(row + 1);
    }
    
    /**
     * Constructor for ID from String ID
     * 
     * @param fullId Cell ID
     */
    public CellID(String fullId) {
        fullID = fullId;
        
        columnIndex = Util.getColumnIndexFromCellID(fullId);
        rowIndex = Util.getRowIndexFromCellID(fullId);
        
        columnName = Util.getColumnLetter(columnIndex);
        rowName = Integer.toString(rowIndex + 1);
    }
    
    /**
     * Returns name of Column
     *
     * @return name of Column
     */
    public String getColumnName() {
        return columnName;
    }

    /**
     * Returns name of Row
     *
     * @return name of Row
     */
    public String getRowName() {
        return rowName;
    }

    /**
     * Returns index of Column
     *
     * @return index of Column
     */
    public Integer getColumnIndex() {
        return columnIndex;
    }

    /**
     * Returns index of Row
     *
     * @return index of Row
     */
    public Integer getRowIndex() {
        return rowIndex;
    }

    /**
     * Returns Full ID
     *
     * @return full ID
     */
    public String getFullID() {
        return fullID;
    }

    public String toString() {
        return fullID;
    }
}
