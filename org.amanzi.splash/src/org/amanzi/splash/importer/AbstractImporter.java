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

package org.amanzi.splash.importer;

import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;

import org.amanzi.integrator.awe.AWEProjectManager;
import org.amanzi.neo.core.NeoCorePlugin;
import org.amanzi.neo.core.database.nodes.AweProjectNode;
import org.amanzi.neo.core.database.nodes.CellNode;
import org.amanzi.neo.core.database.nodes.RubyProjectNode;
import org.amanzi.neo.core.database.nodes.SpreadsheetNode;
import org.amanzi.neo.core.database.services.AweProjectService;
import org.amanzi.neo.core.service.NeoServiceProvider;
import org.amanzi.splash.swing.Cell;
import org.amanzi.splash.utilities.NeoSplashUtil;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.neo4j.api.core.NeoService;
import org.neo4j.api.core.Transaction;


/**
 * Abstract class that provides basic functionality for importing data into Splash
 * 
 * @author Lagutko_N
 * @since 1.0.0
 */
public abstract class AbstractImporter implements IRunnableWithProgress {
    
    /*
     * Path to Project that will contain new Spreadsheet
     */
    protected IPath containerPath;
    
    /*
     * FileName of file to Import
     */
    protected String fileName;
    
    /*
     * Content of file to import
     */
    protected InputStream fileContent;
    
    /*
     * Project Service
     */
    private AweProjectService projectService;
    
    /*
     * Neo Service
     */
    private NeoService neoService;
    
    /*
     * Node of created Spreadsheet
     */
    protected SpreadsheetNode spreadsheetNode;
    
    /*
     * Size of File
     */
    protected long fileSize;
    
    /**
     * Constructor 
     * 
     * @param containerPath path to Project that will contain new Spreadsheet
     * @param fileName name of File to import
     * @param stream content of File to import
     * @param fileSize size of File to import
     */
    public AbstractImporter(IPath containerPath, String fileName, InputStream stream, long fileSize) {
        this.containerPath = containerPath;
        this.fileName = fileName;
        this.fileContent = stream;
        this.fileSize = fileSize;
        
        //initializing services
        projectService = NeoCorePlugin.getDefault().getProjectService();
        neoService = NeoServiceProvider.getProvider().getService();
    }
    
    @Override
    public abstract void run(IProgressMonitor monitor) throws InvocationTargetException;
    
    /**
     * Returns created Spreadsheet
     *
     * @return created spreadsheet
     */
    public SpreadsheetNode getSpreadsheet() {
        return spreadsheetNode;
    }
    
    /**
     *  Creates a Spreadsheet 
     */
    protected void createSpreadsheet() {
        if (spreadsheetNode != null) {
            return;
        }
        //computing names of Ruby and AWE projects
        IProject rubyProject = (IProject)ResourcesPlugin.getWorkspace().getRoot().findMember(containerPath);
        
        String rubyProjectName = rubyProject.getName();        
        String aweProjectName = AWEProjectManager.getAWEprojectNameFromResource(rubyProject);
        
        //computing nodes for Ruby and AWE projects
        AweProjectNode aweProjectNode = projectService.findOrCreateAweProject(aweProjectName);
        RubyProjectNode rubyProjectNode = projectService.findOrCreateRubyProject(aweProjectNode, rubyProjectName);
        
        //computing name of Spreadsheet
        String spreadsheetName = getSpreadsheetName();
        
        //validate name of Spreadsheet. If for example spreadsheet with name 'sheet' already exists
        //than it will try name 'sheet1', 'sheet2' etc.
        int i = 1;        
        String newSpreadsheetName = spreadsheetName;
        do {            
            spreadsheetName = newSpreadsheetName;
            spreadsheetNode = projectService.findSpreadsheet(rubyProjectNode, spreadsheetName);
            newSpreadsheetName = spreadsheetName.concat(Integer.toString(i++));
        } while (spreadsheetNode != null);
        
        //create a new Spreadsheet
        spreadsheetNode = projectService.findOrCreateSpreadSheet(rubyProjectNode, spreadsheetName);
        
        //also create a Spreadsheet in AWE Project Structure
        try {
            AWEProjectManager.createNeoSpreadsheet(rubyProject, spreadsheetName, NeoSplashUtil.getSpeadsheetURL(spreadsheetName));
        }
        catch (MalformedURLException e) {
            //can't happen
        }
    }
    
    /**
     * Convert name of File to Import to name of Spreadsheet
     *
     * @return name of Spreadsheet
     */
    private String getSpreadsheetName() {
        //if FileName contains extension than name of spreadsheet is a filename without extension
        int pointIndex = fileName.indexOf(".");
        if (pointIndex > 0) {
            return fileName.substring(0, pointIndex);
        }
        else {
            return fileName;
        }
    }
    
    /**
     * Saves a Cell
     *
     * @param cell cell to save
     */
    protected void saveCell(Cell cell) {
        //import Cell to database in given Row and Column
        importCell(cell, cell.getRow() + 1, cell.getColumn() + 1);        
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
        
        NeoServiceProvider.getProvider().commit();
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
    private void importCell(Cell cell, int row, int column) {
        createSpreadsheet();
        
        //create a new cell
        CellNode cellNode = new CellNode(neoService.createNode());
        //set a value to cell
        cellNode.setValue(cell.getValue());
        cellNode.setDefinition(cell.getDefinition());
        
        cellNode.setCellColumn(column);
        cellNode.setCellRow(row);
        
        spreadsheetNode.addCell(cellNode);
    }
}
