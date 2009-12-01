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

package net.refractions.udig.project.ui.internal.actions;

import java.util.Iterator;

import net.refractions.udig.project.internal.Spreadsheet;

import org.amanzi.integrator.rdt.RDTProjectManager;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

/**
 * Action for Delta Report for Spreadsheet
 * 
 * @author Lagutko_N
 * @since 1.0.0
 */
public class CompareSpreadsheets implements IViewActionDelegate, IWorkbenchWindowActionDelegate {
    
    /**
     * Selected Spreadsheets
     */
    private IStructuredSelection selection;

    @Override
    public void init(IViewPart view) {
    }

    @Override
    public void run(IAction action) {
        Iterator iterator = selection.iterator();
        Spreadsheet firstSpreadsheet = (Spreadsheet)iterator.next();
        Spreadsheet secondSpreadsheet = (Spreadsheet)iterator.next();
        String aweProjectName = null;
        String rubyProjectName = null;
        
        String firstParent = null;
        if (firstSpreadsheet.getParentSpreadsheet() != null) {
            firstParent = firstSpreadsheet.getParentSpreadsheet().getName();
            rubyProjectName = firstSpreadsheet.getParentSpreadsheet().getRubyProjectInternal().getName();
            aweProjectName = firstSpreadsheet.getParentSpreadsheet().getRubyProjectInternal().getProject().getName();
        }
        else if (!firstSpreadsheet.getChildSpreadsheets().isEmpty()) {
            return;
        }
        else {
            rubyProjectName = firstSpreadsheet.getRubyProjectInternal().getName();
            aweProjectName = firstSpreadsheet.getRubyProjectInternal().getProject().getName();
        }
        
        String secondParent = null;
        if (secondSpreadsheet.getParentSpreadsheet() != null) {
            secondParent = secondSpreadsheet.getParentSpreadsheet().getName();
        }
        else if (!secondSpreadsheet.getChildSpreadsheets().isEmpty()) {
            return;
        }
        
        RDTProjectManager.compareSpreadsheets(aweProjectName, rubyProjectName, 
                                              firstSpreadsheet.getName(), secondSpreadsheet.getName(),
                                              firstParent, secondParent);
                                                          
        
    }

    @Override
    public void selectionChanged(IAction action, ISelection selection) {
        if (selection instanceof IStructuredSelection) {
            this.selection = (IStructuredSelection)selection;
        }
        else {
            this.selection = StructuredSelection.EMPTY;
        }
    }

    @Override
    public void dispose() {
    }

    @Override
    public void init(IWorkbenchWindow window) {
    }

}
