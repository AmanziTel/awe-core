/* AWE - Amanzi Wireless Explorer
 * http://awe.amanzi.org
 * (C) 2008-2009, AmanziTel AB
 *
 * This library is provided under the terms of the Eclipse Public License
 * as described at http://www.eclipse.org/legal/epl-v10.html. Any use,
 * reproduction or distribution of the library constitutes recipient's
 * acceptance of this agreement.
 *
 * This library is distributed WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 */

package org.amanzi.splash.compare;

import java.awt.Color;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;

import org.amanzi.neo.core.database.nodes.CellNode;
import org.amanzi.neo.core.database.nodes.RowHeaderNode;
import org.amanzi.neo.core.database.nodes.SpreadsheetNode;
import org.amanzi.neo.core.enums.SplashRelationshipTypes;
import org.amanzi.neo.core.utils.Pair;
import org.amanzi.splash.swing.Cell;
import org.amanzi.splash.utilities.SpreadsheetCreator;
import org.eclipse.core.runtime.IPath;

import com.eteks.openjeks.format.CellFormat;

/**
 * Class that creates a Delta Report for Spreadsheets
 * 
 * @author Lagutko_N
 * @since 1.0.0
 */
public class SpreadsheetComparator extends SpreadsheetCreator {
    
    /*
     * Node of First Spreadsheet
     */
    private SpreadsheetNode firstSpreadsheet;
    
    /*
     * Node of Second Spreadsheet 
     */
    private SpreadsheetNode secondSpreadsheet;
    
    /*
     * CellFormat with Green background
     */
    private CellFormat greenCell;
    
    /*
     * Cell Format for Blue background
     */
    private CellFormat blueCell;

    /**
     * Creates Comparator
     * 
     * @param containerPath path to container that will contain new Spreadsheet
     * @param newSpreadsheetName name of new Spreadsheet
     * @param firstSpreadsheet node of first Spreadsheet to compare
     * @param secondSpreadsheet node of second Spreadsheet to compare
     */
    public SpreadsheetComparator(IPath containerPath, String newSpreadsheetName, SpreadsheetNode firstSpreadsheet, SpreadsheetNode secondSpreadsheet) {
        super(containerPath, newSpreadsheetName);
        
        this.firstSpreadsheet = firstSpreadsheet;
        this.secondSpreadsheet = secondSpreadsheet;
        
        greenCell = new CellFormat();
        greenCell.setBackgroundColor(new Color(0.0f, 1.0f, 0.0f));
        
        blueCell = new CellFormat();
        blueCell.setBackgroundColor(new Color(0.0f, 0.75f, 1.0f));        
    }
    
    /**
     * Start comparing
     */
    public void startComparing() {
        ArrayList<Pair<CellNode, CellNode>> headers = getHeadersList(firstSpreadsheet, secondSpreadsheet);
        
        int columnNumber = 0;
        for (Pair<CellNode, CellNode> header : headers) {
            compareCells(header.getLeft(), header.getRight(), columnNumber++);
        }
    }
    
    /**
     * Compares Column headers
     *
     * @param firstCell column header of first Spreadsheet
     * @param secondCell column header of second Spreadsheet
     * @param columnNumber number of Column
     */
    private void compareCells(CellNode firstCell, CellNode secondCell, int columnNumber) {
        //compare headers
        Cell cellToSave = null;
        CellFormat currentFormat = null;
        boolean compare = true;
        CellNode singleColumn = null;
        if (firstCell == null) {
            cellToSave = spreadsheetService.convertNodeToCell(secondCell, null, null);
            currentFormat = blueCell;
            compare = false;
            singleColumn = secondCell;
        }
        else if (secondCell == null) {
            cellToSave = spreadsheetService.convertNodeToCell(firstCell, null, null);
            currentFormat = greenCell;
            compare = false;
            singleColumn = firstCell;
        }
        else {
            cellToSave = spreadsheetService.convertNodeToCell(secondCell, null, null);
        }
        
        saveCell(cellToSave, currentFormat, columnNumber);
        
        if (!compare) {
            addSingleColumn(singleColumn, columnNumber, currentFormat, false);
        }
        else {
            addComparedCells(firstCell, secondCell, columnNumber);
        }
    }
    
    /**
     * Adds column of Compared Cells
     *
     * @param firstColumn Column header of first Spreadsheet
     * @param secondColumn Column header of second Spreadsheet
     * @param columnNumber number of column
     */
    private void addComparedCells(CellNode firstColumn, CellNode secondColumn, int columnNumber) {
        CellNode currentCell = firstColumn.getNextCellInColumn();
        CellNode cellToCompare = secondColumn.getNextCellInColumn();
        
        while (currentCell != null) {
            if (cellToCompare == null) {
                addSingleColumn(currentCell, columnNumber, blueCell, true);
                break;
            }
            
            //check that cells from one row
            while (cellToCompare.getCellRow() > currentCell.getCellRow()) {
                Cell cellToSave = spreadsheetService.convertNodeToCell(currentCell, null, null);
                saveCell(cellToSave, greenCell, columnNumber);
                currentCell = currentCell.getNextCellInColumn();   
                if (currentCell == null) {
                    break;
                }
            }
            while (cellToCompare.getCellRow() < currentCell.getCellRow()) {
                Cell cellToSave = spreadsheetService.convertNodeToCell(cellToCompare, null, null);
                saveCell(cellToSave, blueCell, columnNumber);
                cellToCompare = cellToCompare.getNextCellInColumn();
                if (cellToCompare == null) {
                    continue;
                }
            }
            
            //compare cells
            Cell oldCell = spreadsheetService.convertNodeToCell(cellToCompare, null, null);
            Cell newCell = spreadsheetService.convertNodeToCell(currentCell, null, null);
            
            //check types
            boolean saved = false;
            if ((oldCell.getValue() instanceof String) &&
                (newCell.getValue() instanceof String)) {
                try {
                    int oldValue = Integer.parseInt(oldCell.getValue().toString());
                    int newValue = Integer.parseInt(newCell.getValue().toString());
                    
                    oldCell.setValue(Integer.toString(newValue - oldValue));
                    saveCell(oldCell, null, columnNumber);
                    saved = true;
                }
                catch (NumberFormatException e) {
                    //it's not a Integer, try Float
                    try {
                        float oldValue = Float.parseFloat(oldCell.getValue().toString());
                        float newValue = Float.parseFloat(newCell.getValue().toString());
                        oldCell.setValue(Float.toString(newValue - oldValue));
                        saveCell(oldCell, null, columnNumber);
                        saved = true;
                    }
                    catch (NumberFormatException ex) {
                        //it's not a Float
                    }
                }
            }
            if ((oldCell.getValue() instanceof Number) &&
                (newCell.getValue() instanceof Number)) {
                BigDecimal oldValue = new BigDecimal(oldCell.getValue().toString());
                BigDecimal newValue = new BigDecimal(newCell.getValue().toString());
                
                oldCell.setValue(newValue.add(oldValue.negate()).toString());
                saveCell(oldCell, null, columnNumber);
                saved = true;
            }
            
            if (!saved) {
                String oldValue = oldCell.getValue().toString();
                String newValue = newCell.getValue().toString();
                
                oldCell.setValue(oldValue + " -> " + newValue);
                saveCell(oldCell, null, columnNumber);                
            }
            
            currentCell = currentCell.getNextCellInColumn();
            cellToCompare = cellToCompare.getNextCellInColumn();
        }
        
        if (cellToCompare != null) {
            addSingleColumn(cellToCompare, columnNumber, greenCell, true);
        }
    }
    
    /**
     * Saves a new Cell 
     *
     * @param cellToSave cell to Save
     * @param newFormat format of Cell
     * @param columnNumber number of Column
     * @return node of saved cell
     */
    private CellNode saveCell(Cell cellToSave, CellFormat newFormat, int columnNumber) {
        cellToSave.setColumn(columnNumber);
        
        if (newFormat != null) {
            if (cellToSave.getCellFormat() == null) {
                cellToSave.setCellFormat(newFormat);
            }
            else {
                cellToSave.getCellFormat().setBackgroundColor(newFormat.getBackgroundColor());
            }
        }
        
        return saveCell(cellToSave);
    }
    
    /**
     * Add unchanged column from existing Spreadsheet
     *
     * @param cell first cell in column
     * @param columnNumber number of column
     * @param color color of new cells
     * @param returnThis should first cell be added
     */
    private void addSingleColumn(CellNode cell, int columnNumber, CellFormat color, boolean returnThis) {
        ArrayList<CellNode> cells = cell.getAllCellsFromThis(SplashRelationshipTypes.NEXT_CELL_IN_COLUMN, returnThis);
        
        for (CellNode singleCell : cells) {
            Cell cellToSave = spreadsheetService.convertNodeToCell(singleCell, null, null);
            
            saveCell(cellToSave, color, columnNumber);
        }
    }

    /**
     * Returns list of columns for new Spreadsheet
     *
     * @param first first Spreadsheet
     * @param second second Spreadsheet
     * @return
     */
    private ArrayList<Pair<CellNode, CellNode>> getHeadersList(SpreadsheetNode first, SpreadsheetNode second) {
        HashMap<String, CellNode> firstSpreadsheetHeader = getHeaderCells(first);
        
        HashMap<String, CellNode> secondSpreadsheetHeader = getHeaderCells(second);
        
        ArrayList<Pair<CellNode, CellNode>> result = new ArrayList<Pair<CellNode,CellNode>>();
        
        for (Object header : secondSpreadsheetHeader.keySet()) {
            CellNode cell = null;
            if (firstSpreadsheetHeader.containsKey(header)) {
                cell = firstSpreadsheetHeader.remove(header);
            }
            
            result.add(new Pair<CellNode, CellNode>(secondSpreadsheetHeader.get(header), cell));            
        }
        
        for (CellNode node : firstSpreadsheetHeader.values()) {
            result.add(new Pair<CellNode, CellNode>(null, node));
        }
        
        return result;
    }
    
    /**
     * Returns Column Headers of Spreadsheet
     *
     * @param spreadsheet spreadsheet
     * @return map that contains name of Column and node of this Column
     */
    private HashMap<String, CellNode> getHeaderCells(SpreadsheetNode spreadsheet) {
        RowHeaderNode row = spreadsheet.getRowHeader(1);
        
        HashMap<String, CellNode> result = new HashMap<String, CellNode>();
        
        for (CellNode headerNode : row.getAllCellsFromThis(false)) {
            result.put(headerNode.getValue().toString(), headerNode);
        }
        
        return result;
    }
}
