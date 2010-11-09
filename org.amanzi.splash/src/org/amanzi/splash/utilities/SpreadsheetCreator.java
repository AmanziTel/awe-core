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

package org.amanzi.splash.utilities;

import java.net.MalformedURLException;

import org.amanzi.integrator.awe.AWEProjectManager;
import org.amanzi.neo.services.AweProjectService;
import org.amanzi.neo.services.NeoServiceFactory;
import org.amanzi.neo.services.nodes.AweProjectNode;
import org.amanzi.neo.services.nodes.CellNode;
import org.amanzi.neo.services.nodes.RubyProjectNode;
import org.amanzi.neo.services.nodes.SplashFormatNode;
import org.amanzi.neo.services.nodes.SpreadsheetNode;
import org.amanzi.neo.services.ui.NeoServiceProviderUi;
import org.amanzi.splash.database.services.SpreadsheetService;
import org.amanzi.splash.swing.Cell;
import org.amanzi.splash.ui.SplashPlugin;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Transaction;

/**
 * Class for creating Spreadsheets
 * 
 * @author Lagutko_N
 * @since 1.0.0
 */
public class SpreadsheetCreator {

    /*
     * Path to Project that will contain new Spreadsheet
     */
    protected IPath containerPath;
    
    /*
     * FileName of file to Import
     */
    protected String spreadsheetName;
    
    /*
     * Project Service
     */
    protected AweProjectService projectService;
    
    /*
     * Neo Service
     */
    protected GraphDatabaseService neoService;
    
    protected SpreadsheetService spreadsheetService;
    
    private IProject rubyProjectResource;
    
    /*
     * Node of created Spreadsheet
     */
    protected SpreadsheetNode spreadsheetNode;
    
    /*
     * Parent node for child Spreadsheets
     */
    protected SpreadsheetNode parentNode;
    
    /**
     * Constructor 
     * 
     * @param containerPath path to Project that will contain new Spreadsheet
     * @param spreadsheetName name of File to import
     */
    public SpreadsheetCreator(IPath containerPath, String spreadsheetName) {
        this.containerPath = containerPath;
        this.spreadsheetName = spreadsheetName;
        
        //initializing services
        projectService = NeoServiceFactory.getInstance().getProjectService();
        neoService = NeoServiceProviderUi.getProvider().getService();
        spreadsheetService = SplashPlugin.getDefault().getSpreadsheetService();
    }
    
    /**
     * Returns created Spreadsheet
     *
     * @return created spreadsheet
     */
    public SpreadsheetNode getSpreadsheet() {
        return spreadsheetNode;
    }
    
    protected RubyProjectNode getSpreadsheetRoot() {
        //computing names of Ruby and AWE projects
        rubyProjectResource = (IProject)ResourcesPlugin.getWorkspace().getRoot().findMember(containerPath);
        
        String rubyProjectName = rubyProjectResource.getName();        
        String aweProjectName = AWEProjectManager.getAWEprojectNameFromResource(rubyProjectResource);
        
        //computing nodes for Ruby and AWE projects
        AweProjectNode aweProjectNode = projectService.findOrCreateAweProject(aweProjectName);
        return projectService.findOrCreateRubyProject(aweProjectNode, rubyProjectName);
    }
    
    /**
     *  Creates a Spreadsheet 
     */
    protected void createSpreadsheet(SpreadsheetNode parent) {
        if (spreadsheetNode != null) {
            return;
        }
        RubyProjectNode rubyProjectNode = getSpreadsheetRoot();
        
        //computing name of Spreadsheet
        String spreadsheetName = getSpreadsheetName();
        
        //validate name of Spreadsheet. If for example spreadsheet with name 'sheet' already exists
        //than it will try name 'sheet1', 'sheet2' etc.
        int i = 1;        
        String oldSpreadsheetName = new String(spreadsheetName);
        String newSpreadsheetName = new String(spreadsheetName); 
        do {
            spreadsheetName = newSpreadsheetName;
            spreadsheetNode = projectService.findSpreadsheet(rubyProjectNode, spreadsheetName);
            newSpreadsheetName = oldSpreadsheetName.concat(" " + Integer.toString(i++));
        } while (spreadsheetNode != null);
        
        //create a new Spreadsheet
        spreadsheetNode = projectService.findOrCreateSpreadSheet(rubyProjectNode, spreadsheetName);
        
        if (parent != null) {
            //if parent node is not null than we should add new spreadsheet to this parent spreadsheet
            parent.addChildSpreadsheet(spreadsheetNode);
            parent.setHasChildSpreadsheets(true);
            
            //also create a Spreadsheet in AWE Project Structure
            try {
                //create child spreadsheet
                AWEProjectManager.createChildNeoSpreadsheet(rubyProjectResource, parent.getName(), spreadsheetName, NeoSplashUtil.getSpeadsheetURL(spreadsheetName));
            }
            catch (MalformedURLException e) {
                //can't happen
            }
        }
        else {
            //also create a Spreadsheet in AWE Project Structure
            try {
                //if parent node is null than it's a simple spreadsheet
                AWEProjectManager.createNeoSpreadsheet(rubyProjectResource, spreadsheetName, NeoSplashUtil.getSpeadsheetURL(spreadsheetName));
            }
            catch (MalformedURLException e) {
                //can't happen
            }
        }
    }
    
    /**
     * Convert name of File to Import to name of Spreadsheet
     *
     * @return name of Spreadsheet
     */
    private String getSpreadsheetName() {
        //if FileName contains extension than name of spreadsheet is a filename without extension
        int pointIndex = spreadsheetName.indexOf(".");
        if (pointIndex > 0) {
            return spreadsheetName.substring(0, pointIndex);
        }
        else {
            return spreadsheetName;
        }
    }
    
    /**
     * Saves a Cell
     *
     * @param cell cell to save
     */
    public CellNode saveCell(Cell cell) {
        //import Cell to database in given Row and Column
        return importCell(cell, cell.getRow() + 1, cell.getColumn() + 1);        
    }
    
    
    int count = 0;
    /**
     * Updates Transaction.
     * To prevent JavaHeapSpace exception we can call this method for example after processing 1000 rows
     *
     * @param transaction transaction to update
     * @return new transaction
     */
    protected Transaction updateTransaction(Transaction transaction) {
        transaction.success();
        transaction.finish();
        
        NeoServiceProviderUi.getProvider().commit();
        Transaction result = neoService.beginTx();
        
        return result;
    }
    
    /**
     * Import Cell to database
     *
     * @param cell cell to import
     * @param row row of Cell
     * @param column column of Cell
     * @return row that contain imported cell or null
     */
    private CellNode importCell(Cell cell, int row, int column) {
        createSpreadsheet(parentNode);
        
        //create a new cell
        CellNode cellNode = new CellNode(neoService.createNode());
        //set a value to cell
        cellNode.setValue(cell.getValue());
        cellNode.setDefinition(cell.getDefinition());
        
        cellNode.setCellColumn(column);
        cellNode.setCellRow(row);
        
        cellNode.setSpreadsheetId(spreadsheetNode.getUnderlyingNode().getId());
        
        if (cell.getCellFormat() != null) {
            SplashFormatNode formatNode = new SplashFormatNode(neoService.createNode());
            spreadsheetService.setSplashFormat(formatNode, cell.getCellFormat());
            formatNode.addCell(cellNode);
        }
        
        spreadsheetNode.addCell(cellNode);
        
        return cellNode;
    }
    
}
