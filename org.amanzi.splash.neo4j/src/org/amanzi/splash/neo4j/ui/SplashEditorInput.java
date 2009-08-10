package org.amanzi.splash.neo4j.ui;

import org.amanzi.neo.core.database.nodes.RubyProjectNode;
import org.amanzi.splash.neo4j.database.services.SpreadsheetService;
import org.amanzi.splash.neo4j.utilities.ActionUtil;
import org.amanzi.splash.neo4j.utilities.ActionUtil.RunnableWithResult;
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
    private RubyProjectNode root;
    
    /**
     * Constructor.
     * 
     * @param sheetName name of Spreadsheet
     * @param root root node of Spreadsheet
     */
    public SplashEditorInput(String sheetName, RubyProjectNode root) {
        service = SplashPlugin.getDefault().getSpreadsheetService();
        this.root = root;
        
        this.sheetName = sheetName;
    }

    public boolean exists() {
        boolean isExist = (Boolean)ActionUtil.getInstance().runTaskWithResult(new RunnableWithResult() {

            private boolean result;
            
            public Object getValue() {
                return result;
            }

            public void run() {
                result = service.findSpreadsheet(root, sheetName) != null;
            }
            
        });
        return isExist;
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
    public RubyProjectNode getRoot() {
        return root;
    }
}
