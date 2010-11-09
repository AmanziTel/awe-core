package org.rubypeople.rdt.ui.actions;

import org.amanzi.integrator.awe.AWEProjectManager;
import org.amanzi.neo.services.AweProjectService;
import org.amanzi.neo.services.NeoServiceFactory;
import org.amanzi.neo.services.nodes.AweProjectNode;
import org.amanzi.neo.services.nodes.RubyProjectNode;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.internal.ide.IDEWorkbenchMessages;
import org.eclipse.ui.internal.ide.IDEWorkbenchPlugin;
import org.eclipse.ui.views.navigator.ResourceNavigatorRenameAction;
import org.rubypeople.rdt.core.IRubyElement;
import org.rubypeople.rdt.core.ISpreadsheet;
import org.rubypeople.rdt.core.RubyModelException;
import org.rubypeople.rdt.internal.core.RubyElement;
import org.rubypeople.rdt.internal.core.RubyModelManager;
import org.rubypeople.rdt.internal.ui.RubyPlugin;

/**
 * Action that Renames Resource, EMF and Database Project and Spreadsheet Elements.
 * 
 * Renames elements in RDT Project Tree.
 * 
 * @author Lagutko_N
 */
public class RenameElementInNavigatorAction extends ResourceNavigatorRenameAction {
    
    /*
     * Is rename calls for Spreadsheet
     */
    private boolean isSpreadsheet = false;
    
    /*
     * Project Service
     */
    private AweProjectService projectService;

    public RenameElementInNavigatorAction(Shell shell, TreeViewer treeViewer) {        
        super(shell, treeViewer);
    }
    
    @Override
    protected void runWithNewPath(IPath path, IResource resource) {
        if (resourceIsType(resource, IResource.PROJECT)) {
            resource = RubyModelManager.getRubyModelManager().getRubyModel().findRubyProject((IProject)resource).getProject();
            AWEProjectManager.renameRubyProject((IProject)resource, path.lastSegment());
            super.runWithNewPath(path, resource);
        }
        else {
            super.runWithNewPath(path, resource);
        }        
    }
    
    @Override
    public void run() {
        if (isSpreadsheet) {
            //if trying to rename Spreadsheet than didn't use standard way
            PlatformUI.getWorkbench().getDisplay().syncExec(new Runnable() {
                public void run() {
                    renameSpreadsheet();
                }
            });            
        }
        else {
            super.run();
        }
    }
    
    /**
     * Renames Spreadsheet in RDT Project Structure and Database
     * 
     * @author Lagutko_N
     */
    protected void renameSpreadsheet() {
        ISpreadsheet spreadsheet = (ISpreadsheet)getStructuredSelection().getFirstElement();
        
        projectService = NeoServiceFactory.getInstance().getProjectService();
        String aweProjectName = AWEProjectManager.getAWEprojectNameFromResource(spreadsheet.getRubyProject().getProject());
        
        AweProjectNode aweNode = projectService.findAweProject(aweProjectName);
        final RubyProjectNode rubyNode = projectService.findRubyProject(aweNode, spreadsheet.getRubyProject().getElementName());
        
        String newName = queryNewSpreadsheetName(spreadsheet, rubyNode);
        
        if (newName != null) {
            projectService.renameSpreadsheet(rubyNode, spreadsheet.getElementName(), newName);
            
            try {
                RubyModelManager.getRubyModelManager().getRubyModel().refreshSpreadsheets(new IRubyElement[] {spreadsheet.getRubyProject()}, null);
            }
            catch (RubyModelException e) {
                RubyPlugin.log(e);
            }
            
            AWEProjectManager.renameSpreadsheet(spreadsheet.getRubyProject().getProject(), spreadsheet.getElementName(), newName);
        }
    }
    
    /**
     * Creates a Dialog for new Spreadsheet Name input
     *
     * @param spreadsheet Spreadsheet to rename
     * @param rubyNode parent Node of this Spreadsheet
     * @return new Name of Spreasheet
     * @author Lagutko_N
     */
    private String queryNewSpreadsheetName(ISpreadsheet spreadsheet, final RubyProjectNode rubyNode) {
        if ((projectService == null) || (rubyNode == null)) {
            return null;
        }
        
        final IWorkspace workspace = IDEWorkbenchPlugin.getPluginWorkspace();
        
        final String oldName = spreadsheet.getElementName();
        
        IInputValidator validator = new IInputValidator() {
            public String isValid(String string) {
                if (oldName.equals(string)) {
                    return IDEWorkbenchMessages.RenameResourceAction_nameMustBeDifferent;
                }
                
                if (projectService.findSpreadsheet(rubyNode, string) != null) {
                    return IDEWorkbenchMessages.RenameResourceAction_nameExists;
                }
                return null;
            }
        };

        InputDialog dialog = new InputDialog(PlatformUI.getWorkbench().getDisplay().getActiveShell(),
                IDEWorkbenchMessages.RenameResourceAction_inputDialogTitle,
                IDEWorkbenchMessages.RenameResourceAction_inputDialogMessage,
                oldName, validator);
        dialog.setBlockOnOpen(true);
        int result = dialog.open();
        if (result == Window.OK)
            return dialog.getValue();
        return null;
    }
    
    @Override
    protected boolean updateSelection(IStructuredSelection selection) {
        //Lagutko, 18.08.2009, this method was overriden to support also Spreadsheet in Rename Action
        if (selection.size() > 1) {
            return false;
        }
        
        isSpreadsheet = false;
        
        Object element = selection.getFirstElement();
        if (element instanceof RubyElement) {
            if (((RubyElement)element).getElementType() == IRubyElement.SPREADSHEET) {
                isSpreadsheet = true;
                return true;
            }
        }
        return super.updateSelection(selection);
    }
}
