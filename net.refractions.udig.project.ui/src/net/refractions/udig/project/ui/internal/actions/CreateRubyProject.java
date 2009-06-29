package net.refractions.udig.project.ui.internal.actions;

import net.refractions.udig.project.internal.Project;

import org.eclipse.emf.common.ui.action.WorkbenchWindowActionDelegate;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.rubypeople.rdt.internal.ui.wizards.OpenRubyProjectWizardToolbarAction;

/**
 * Class for CreateRubyProject action
 * 
 * @author Lagutko_N
 *
 */

public class CreateRubyProject extends WorkbenchWindowActionDelegate {
	
	/**
	 * Run this action
	 * 
	 */

	public void run(IAction action) {
		ISelection selection = getSelection();
		
		Project project = null;
		
		if (selection instanceof IStructuredSelection) {
			IStructuredSelection elements = (IStructuredSelection)selection;
			
			Object maybeProject = elements.getFirstElement();
			
			if (maybeProject instanceof Project) {
				project = (Project)maybeProject;
				
				runCreateAction(project.getName(), action);				
			}
		}
	}
	
	/**
	 * Runs Action for creating RubyProject
	 * 
	 * @param projectName name of AWE Project
	 * @param action action
	 */
	
	private void runCreateAction(String projectName, IAction action) {
		StructuredSelection newSelection = new StructuredSelection(projectName);
		
		OpenRubyProjectWizardToolbarAction createAction = new OpenRubyProjectWizardToolbarAction();		
		createAction.setSelection(newSelection);
		createAction.run(action);
	}

}
