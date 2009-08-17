package org.rubypeople.rdt.ui.actions;

import org.amanzi.integrator.awe.AWEProjectManager;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.views.navigator.ResourceNavigatorRenameAction;
import org.rubypeople.rdt.internal.core.RubyModelManager;

/**
 * Action that Renames Resource, EMF and Database Project Elements.
 * 
 * Renames elements in RDT Project Tree.
 * 
 * @author Lagutko_N
 */
public class RenameElementInNavigatorAction extends ResourceNavigatorRenameAction {

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

}
