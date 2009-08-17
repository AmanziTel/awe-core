package org.rubypeople.rdt.ui.actions;

import org.amanzi.integrator.awe.AWEProjectManager;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.actions.RenameResourceAction;
import org.rubypeople.rdt.internal.core.RubyModelManager;

/**
 * Action that Renames Resource, EMF and Database Project Elements.
 * 
 * Calls Dialog to get new Name of Project.
 * 
 * @author Lagutko_N
 */
public class RenameElementAction extends RenameResourceAction {

    private String newElementName;
    
    public RenameElementAction(Shell shell, String newElementName) {
        super(shell);        
        this.newElementName = newElementName; 
    }
    
    @Override
    protected String queryNewResourceName(final IResource resource) {
        String name = null;
        if (newElementName != null) {
            name = newElementName;
        }
        else {
            name = super.queryNewResourceName(resource);
        }
        
        IProject projectResource = RubyModelManager.getRubyModelManager().getRubyModel().findRubyProject((IProject)resource).getProject();
        AWEProjectManager.renameRubyProject(projectResource, name);
        
        return name;
    }
}
