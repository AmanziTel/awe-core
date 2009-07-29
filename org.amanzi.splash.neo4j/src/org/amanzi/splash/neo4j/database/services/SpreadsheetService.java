package org.amanzi.splash.neo4j.database.services;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.amanzi.neo.core.service.NeoServiceProvider;
import org.amanzi.splash.neo4j.database.exception.SplashDatabaseException;
import org.amanzi.splash.neo4j.database.exception.SplashDatabaseExceptionMessages;
import org.amanzi.splash.neo4j.database.nodes.CellNode;
import org.amanzi.splash.neo4j.database.nodes.ColumnNode;
import org.amanzi.splash.neo4j.database.nodes.RootNode;
import org.amanzi.splash.neo4j.database.nodes.RowNode;
import org.amanzi.splash.neo4j.database.nodes.SpreadsheetNode;
import org.amanzi.splash.neo4j.swing.Cell;
import org.amanzi.splash.neo4j.ui.SplashPlugin;
import org.amanzi.splash.neo4j.utilities.NeoSplashUtil;
import org.neo4j.api.core.NeoService;
import org.neo4j.api.core.Transaction;

import com.eteks.openjeks.format.CellFormat;

/**
 * Service class for working with Neo4j-Spreadsheet
 * 
 * @author Lagutko_N 
 */

public class SpreadsheetService {
    
    /*
     * Default value of Cell Value
     */
    private static final String DEFAULT_VALUE = "";
    
    /*
     * Default value of Cell Definition 
     */
    private static final String DEFAULT_DEFINITION = "";
    
    /*
     * NeoService Provider
     */
    private NeoServiceProvider provider;
    
    /*
     * NeoService
     */
    protected NeoService neoService;
    
    /**
     * Constructor of Service.
     * 
     * Initializes NeoService and create a Root Element
     */
    public SpreadsheetService() {
        provider = NeoServiceProvider.getProvider();
        neoService = provider.getService();
    }
    
    /**
     * Searches for Spreadsheets by given name
     *
     * @param root root node of Spreadsheet
     * @param name name of Spreadsheet
     * @return founded Spreadsheet or null if Spreadsheet was not found
     */
    public SpreadsheetNode findSpreadsheet(RootNode root, String name) {
        SpreadsheetNode result = null;
        
        Transaction tx = neoService.beginTx();
        
        try {
            Iterator<SpreadsheetNode> spreadsheetIterator = root.getSpreadsheets();
            
            while (spreadsheetIterator.hasNext()) {
                SpreadsheetNode spreadsheet = spreadsheetIterator.next();
                
                if (spreadsheet.getSpreadsheetName().equals(name)) {
                    result = spreadsheet;
                    break;
                }            
            }
            tx.success();
        }
        finally {
            tx.finish();
        }
        
        return result;
    }
    
    /**
     * Creates a Spreadsheet by given name
     *
     * @param root root node for Spreadsheet
     * @param name name of Spreadsheet
     * @return create Spreadsheet
     * @throws SplashDatabaseException if Spreadsheet with given name already exists
     */
    
    public SpreadsheetNode createSpreadsheet(RootNode root, String name) throws SplashDatabaseException {
        if (findSpreadsheet(root, name) != null) {
            String message = SplashDatabaseExceptionMessages.getFormattedString(SplashDatabaseExceptionMessages.Duplicate_Spreadsheet, name);
            throw new SplashDatabaseException(message);
        }
        else {
            Transaction tx = neoService.beginTx();
            
            try {
                SpreadsheetNode spreadsheet = new SpreadsheetNode(neoService.createNode());
                
                spreadsheet.setSpreadsheetName(name);
                
                root.addSpreadsheet(spreadsheet);
                
                tx.success();
                
                return spreadsheet;
            }
            finally {
                tx.finish();                
            }
        }
    }
    
    /**
     * Creates a Cell in Spreadsheet by given ID 
     *
     * @param spreadsheet spreadsheet
     * @param id id of Cell
     * @return created Cell
     */
    public CellNode createCell(SpreadsheetNode spreadsheet, CellID id) {
        Transaction tx = neoService.beginTx();
        
        try {        
            RowNode rowNode = spreadsheet.getRow(id.getRowName());
        
            if (rowNode == null) {
                rowNode = new RowNode(neoService.createNode());
                rowNode.setRowIndex(id.getRowName());
                spreadsheet.addRow(rowNode);
            }
            
            ColumnNode columnNode = spreadsheet.getColumn(id.getColumnName());;
            if (columnNode == null) {
                columnNode = new ColumnNode(neoService.createNode());
                columnNode.setColumnName(id.getColumnName());
            }
            
            CellNode cell = new CellNode(neoService.createNode());
            
            rowNode.addCell(cell);
            columnNode.addCell(cell);
            
            tx.success();
            
            return cell;
        }
        catch (SplashDatabaseException e) {
            tx.failure();
            String message = SplashDatabaseExceptionMessages.getFormattedString(SplashDatabaseExceptionMessages.Service_Method_Exception, "createCell");
            SplashPlugin.error(message, e);
            return null;
        }
        finally {
            tx.finish();
        }
    }
    
    /**
     * Updates Cell and Cell's References
     *
     * @param sheet Spreadsheet
     * @param cell Cell for update
     */
    public void updateCellWithReferences(SpreadsheetNode sheet, Cell cell) {
        CellNode updatedNode = updateCell(sheet, cell);
        
        Transaction tx = neoService.beginTx();
        
        try {
            List<String> rfdCellsIDs = NeoSplashUtil.findComplexCellIDs((String) cell.getDefinition());
            
            for (int i=0;i < rfdCellsIDs.size();i++){
                String ID = rfdCellsIDs.get(i);
                
                CellID id = new CellID(ID);
                                
                CellNode node = getCellNode(sheet, id);
                
                if (node == null) {
                    node = createCell(sheet, id);
                }
                
                updatedNode.addRFDNode(node);
                node.addRFGNode(updatedNode);
            }
            
            tx.success();
        }
        finally {
            tx.finish();
        }
    }
    
    /**
     * Updates only Cell values
     *
     * @param sheet spreadsheet
     * @param cell Cell for update
     * @return updated Cell
     */
    public CellNode updateCell(SpreadsheetNode sheet, Cell cell) {
        CellID id = new CellID(cell.getRow(), cell.getColumn());
        
        CellNode node = getCellNode(sheet, id);
        
        if (node == null) {
            node = createCell(sheet, id);                
        }
        
        Transaction tx = neoService.beginTx();
        
        try {
            node.setValue((String)cell.getValue());
            node.setDefinition((String)cell.getDefinition());
            
            CellFormat format = cell.getCellFormat();
            
            if (format != null) {
                node.setBackgroundColorB(format.getBackgroundColor().getBlue());
                node.setBackgroundColorG(format.getBackgroundColor().getGreen());
                node.setBackgroundColorR(format.getBackgroundColor().getRed());
                
                node.setFontColorB(format.getFontColor().getBlue());
                node.setFontColorG(format.getFontColor().getGreen());
                node.setFontColorR(format.getFontColor().getRed());
                
                node.setFontName(format.getFontName());
                node.setFontSize(format.getFontSize());
                node.setFontStyle(format.getFontStyle());
            }
            
            tx.success();
            
            return node;
        }
        finally {
            tx.finish();
        }
    }
    
    /**
     * Returns Cell by given ID
     *
     * @param sheet spreadsheet
     * @param id cell ID
     * @return converted Cell from Database
     */
    public Cell getCell(SpreadsheetNode sheet, CellID id) {
        CellNode node = getCellNode(sheet, id);
        
        if (node != null) {
            NeoSplashUtil.logn("getCell for " + id + " returns " + node.getValue() + " in " + node.getColumn().getColumnName() + node.getRow().getRowIndex());
            
            return convertNodeToCell(node);
        }
        
        return new Cell(id.getRowIndex(), id.getColumnIndex(), DEFAULT_DEFINITION, DEFAULT_VALUE, new CellFormat());
    }
    
    /**
     * Returns CellNode by given ID
     *
     * @param sheet spreadsheet
     * @param id id of Cell
     * @return CellNode by ID or null if Cell doesn't exists
     */
    private CellNode getCellNode(SpreadsheetNode sheet, CellID id) {
        Transaction tx = neoService.beginTx();
        
        try {            
            CellNode result = sheet.getCell(id.getRowName(), id.getColumnName());
            
            tx.success();
            
            return result;
        }
        catch (SplashDatabaseException e) {
            String message = SplashDatabaseExceptionMessages.getFormattedString(SplashDatabaseExceptionMessages.Service_Method_Exception, "getCellNode");
            SplashPlugin.error(message, e);            
        }
        finally {
            tx.finish();
        }
        
        return null;
    }
    
    /**
     * Converts CellNode to Cell
     *
     * @param node CellNode 
     * @return Cell
     */
    private Cell convertNodeToCell(CellNode node) {
        RowNode row = node.getRow();
        ColumnNode column = node.getColumn();
        
        CellID id = new CellID(row.getRowIndex(), column.getColumnName());
        
        CellFormat cellFormat = new CellFormat();
        
        Integer bgColorB = node.getBackgroundColorB();
        Integer bgColorG = node.getBackgroundColorG();
        Integer bgColorR = node.getBackgroundColorR();
        
        if ((bgColorB != null) && (bgColorG != null) && (bgColorR != null)) {
            Color color = new Color(bgColorR, bgColorR, bgColorB);
            cellFormat.setBackgroundColor(color);
        }
        
        Integer fontColorB = node.getFontColorB();
        Integer fontColorG = node.getFontColorG();
        Integer fontColorR = node.getFontColorR();
        
        if ((fontColorB != null) && (fontColorG != null) && (fontColorR != null)) {
            Color color = new Color(fontColorR, fontColorG, fontColorB);
            cellFormat.setFontColor(color);
        }
        
        cellFormat.setFontName(node.getFontName());
        cellFormat.setFontSize(node.getFontSize());
        cellFormat.setFontStyle(node.getFontStyle());
        cellFormat.setHorizontalAlignment(node.getHorizontalAlignment());
        cellFormat.setVerticalAlignment(node.getVerticalAlignment());
        
        String value = node.getValue();
        if (value == null) {
            value = DEFAULT_VALUE;
        }
        
        String definition = node.getDefinition();
        if (definition == null) {
            definition = DEFAULT_DEFINITION;
        }
        
        return new Cell(id.getRowIndex(), id.getColumnIndex(), definition, value, cellFormat);
    }
    
    /**
     * Returns RFD Cells of Cell by given ID
     *
     * @param sheet Spreadsheet
     * @param cellID id of Cell
     * @return RFD cells of Cell
     */
    public ArrayList<Cell> getRFDCells(SpreadsheetNode sheet, CellID cellID) {
        CellNode currentNode = getCellNode(sheet, cellID);
        
        Iterator<CellNode> rfdNodes = currentNode.getRFDNodes();
        
        ArrayList<Cell> result = new ArrayList<Cell>(0);
        
        while (rfdNodes.hasNext()) {            
            result.add(convertNodeToCell(rfdNodes.next()));
        }
        
        return result;
    }
    
    /**
     * Returns RootNode for Spreadsheets
     *
     * @return root node
     */
    //TODO: this method must be rewritten to support specification from comments in #564 to support
    //Spreadsheets with same name in different Ruby and AWE projects
    public RootNode getRootNode() {
        Transaction tx = neoService.beginTx();
        try {
            
            RootNode root = new RootNode(neoService.getReferenceNode());
            tx.success();
            
            return root;
        }
        finally {
            tx.finish();
        }
    }
}
