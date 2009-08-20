package org.amanzi.splash.neo4j.ui;

import org.amanzi.neo.core.NeoCorePlugin;
import org.amanzi.neo.core.database.nodes.RubyProjectNode;
import org.amanzi.neo.core.database.nodes.SpreadsheetNode;
import org.amanzi.neo.core.database.services.AweProjectService;
import org.amanzi.neo.core.utils.ActionUtil;
import org.amanzi.neo.core.utils.ActionUtil.RunnableWithResult;
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
     * Project service
     */
    private AweProjectService service;
    
    /**
     * Root node of Spreadsheet
     */
    private RubyProjectNode root;
    
    /**
     * Node of Spreadsheet
     */
    private SpreadsheetNode node;
    
    /**
     * Constructor.
     * 
     * @param sheetName name of Spreadsheet
     * @param root root node of Spreadsheet
     */
    public SplashEditorInput(String sheetName, RubyProjectNode root) {
        service = NeoCorePlugin.getDefault().getProjectService();
        this.root = root;
        
        node = service.findSpreadsheet(root, sheetName); 
        
        this.sheetName = sheetName;
    }
    
    /**
     * Constructor. 
     * 
     * @param spreadsheetNode Spreadsheet Node
     */
    public SplashEditorInput(SpreadsheetNode spreadsheetNode) {
        service = NeoCorePlugin.getDefault().getProjectService();
        this.sheetName = spreadsheetNode.getName();
        
        this.root = spreadsheetNode.getSpreadsheetRootProject();
        
        node = spreadsheetNode;
    }


	public boolean exists() {
		boolean isExist = (Boolean) ActionUtil.getInstance().runTaskWithResult(new RunnableWithResult() {

			private boolean result;

			public Object getValue() {
				return result;
			}

			public void run() {
                result = node != null;
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

	@Override
	public boolean equals(Object object) {
		if (object instanceof SplashEditorInput) {
			SplashEditorInput otherInput = (SplashEditorInput) object;
			if ((otherInput.getName() != null) && (otherInput.getRoot() != null) && (otherInput.getName().equals(getName()))
					&& (otherInput.getRoot().equals(getRoot()))) {
				return true;
			}
		}
		return false;
	}
}
