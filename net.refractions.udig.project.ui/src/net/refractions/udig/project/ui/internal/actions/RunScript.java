package net.refractions.udig.project.ui.internal.actions;

import java.util.Iterator;

import net.refractions.udig.project.IRubyProjectElement;
import net.refractions.udig.project.internal.RubyProjectElement;

import org.amanzi.integrator.rdt.RDTProjectManager;
import org.eclipse.emf.common.ui.action.WorkbenchWindowActionDelegate;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;

/**
 * Action delegate for running scripts
 * 
 * @author Lagutko_N
 *
 */

public class RunScript extends WorkbenchWindowActionDelegate {
	
	private IStructuredSelection selection;
	
	/**
	 * Runs the action
	 * 
	 */

	public void run(IAction action) {
        for(Iterator iter = selection.iterator(); iter.hasNext(); ) {
            Object element = iter.next();

            if (element instanceof IRubyProjectElement) {
                operate((RubyProjectElement) element);
            }
        }
	}
	
	/**
	 * Hanle the action for RubyProjectElement
	 * 
	 * @param element RubyProjectElement
	 */
	
	protected void operate(RubyProjectElement element) {
		RDTProjectManager.runScript(element.getRubyProjectInternal().getName(), element.getName());
	}
	
	/**
     * @see org.eclipse.emf.common.ui.action.WorkbenchWindowActionDelegate#selectionChanged(org.eclipse.jface.action.IAction,
     *      org.eclipse.jface.viewers.ISelection)
     */
    public void selectionChanged( IAction action, ISelection selection ) {
        if (selection instanceof IStructuredSelection)
            this.selection = (IStructuredSelection) selection;
    }

}
