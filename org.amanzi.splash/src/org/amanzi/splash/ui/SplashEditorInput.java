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
package org.amanzi.splash.ui;

import org.amanzi.neo.services.AweProjectService;
import org.amanzi.neo.services.NeoServiceFactory;
import org.amanzi.neo.services.nodes.RubyProjectNode;
import org.amanzi.neo.services.nodes.SpreadsheetNode;
import org.amanzi.neo.services.ui.utils.ActionUtil;
import org.amanzi.neo.services.utils.RunnableWithResult;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IPersistableElement;

/**
 * Editor input for Neo4j-based Spreadsheet
 * 
 * @author Lagutko_N
 */

public class SplashEditorInput implements IEditorInput, IPersistableElement {
    
    private static final String RUBY_PROJECT = "ruby";

    private static final String SPREADSHEET_NAME = "spreadsheet";

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
        service = NeoServiceFactory.getInstance().getProjectService();
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
        service = NeoServiceFactory.getInstance().getProjectService();
        this.sheetName = spreadsheetNode.getSpreadsheetName();
        
        this.root = spreadsheetNode.getSpreadsheetRootProject();
        
        node = spreadsheetNode;
    }


	public boolean exists() {
		boolean isExist = ActionUtil.getInstance().runTaskWithResult(new RunnableWithResult<Boolean>() {

			private boolean result;

			public Boolean getValue() {
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
        return this;
	}

	public String getToolTipText() {
		return sheetName;
	}

	@SuppressWarnings("unchecked")
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

    @Override
    public String getFactoryId() {
        return SplashEditorInputFactory.getFactoryId();
    }

    @Override
    public void saveState(IMemento memento) {
        memento.putString(RUBY_PROJECT, root.getName());
        memento.putString(SPREADSHEET_NAME, getName());
    }

    /**
     * @param memento
     * @return
     */
    public static SplashEditorInput create(IMemento memento) {
        String rubyProjectName = memento.getString(RUBY_PROJECT);
        String name = memento.getString(SPREADSHEET_NAME);
        AweProjectService projectService = NeoServiceFactory.getInstance().getProjectService();
        RubyProjectNode rubyProject = projectService.findRubyProject(rubyProjectName);
        SpreadsheetNode spreadsheetNode = projectService.findSpreadsheet(rubyProject, name);
        return new SplashEditorInput(spreadsheetNode);
    }
}
