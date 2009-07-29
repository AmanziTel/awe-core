package org.amanzi.splash.neo4j.ui;

import org.amanzi.splash.neo4j.database.nodes.RootNode;
import org.amanzi.splash.neo4j.database.services.SpreadsheetService;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPersistableElement;

/**
 * Editor input for Neo4j-based Spreadsheet
 * 
 * @author Lagutko_N
 */

public class SplashEditorInput implements IEditorInput {
    
    /**
     * Name of Spreadsheet
     */
    private String sheetName;
    
    /**
     * Spreadsheet service
     */
    private SpreadsheetService service;
    
    /**
     * Root node of Spreadsheet
     */
    private RootNode root;
    
    /**
     * Constructor.
     * 
     * @param sheetName name of Spreadsheet
     * @param root root node of Spreadsheet
     */
    public SplashEditorInput(String sheetName, RootNode root) {
        service = SplashPlugin.getDefault().getSpreadsheetService();
        this.root = root;
        
        this.sheetName = sheetName;
    }

    public boolean exists() {
        return service.findSpreadsheet(root, sheetName) != null;
    }

    public ImageDescriptor getImageDescriptor() {
        return null;
    }

    public String getName() {
        return sheetName;
    }

    public IPersistableElement getPersistable() {
        return null;
    }

    public String getToolTipText() {
        return sheetName;
    }

    public Object getAdapter(Class adapter) {
        return null;
    }

    /**
     * Returns Root Node of Spreadsheet
     *
     * @return root Node
     */
    public RootNode getRoot() {
        return root;
    }
}
