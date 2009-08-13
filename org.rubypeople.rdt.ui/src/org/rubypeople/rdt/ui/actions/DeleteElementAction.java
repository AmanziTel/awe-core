/* AWE - Amanzi Wireless Explorer
 * http://awe.amanzi.org
 * (C) 2008-2009, AmanziTel AB
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation;
 * version 3.0 of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 */
package org.rubypeople.rdt.ui.actions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.amanzi.integrator.awe.AWEProjectManager;
import org.eclipse.core.resources.IProject;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.actions.DeleteResourceAction;
import org.rubypeople.rdt.core.ISpreadsheet;
import org.rubypeople.rdt.internal.core.Spreadsheet;

/**
 * Action that will provide Delete not only for Resources but also for Spreadsheets
 * 
 * @author lagutko_n
 */
public class DeleteElementAction extends DeleteResourceAction {
    
    /*
     * Is delete action was called for standard resources?
     */
    private boolean standardResources = true;
    
    /*
     * Spreadsheets to Delete
     */
    private List<Spreadsheet> spreadsheetsToDelete = new ArrayList<Spreadsheet>(0);

    /**
     * @param shell
     */
    public DeleteElementAction(Shell shell) {
        super(shell);
    }
    
    @Override
    protected boolean updateSelection(IStructuredSelection selection) {
        List<Object> selectedObject = selection.toList();
        
        boolean result = false;
        
        for (Object selected : selectedObject) {
            if (selected instanceof ISpreadsheet) {
                result = true;
                standardResources = false;
                spreadsheetsToDelete.add((Spreadsheet)selected);
            }
        }
        
        return result ^ super.updateSelection(selection);
    }
    
    @Override
    public void run() {
        //if there was standard resources than call run() from super-class
        if (standardResources) {        
            super.run();
        }
        else {            
            if (!spreadsheetsToDelete.isEmpty()) {
                //if there was Spreadsheets than delete them using uDIG functionality
                HashMap<IProject, List<String>> spreadsheetsMap = new HashMap<IProject, List<String>>(0);
                
                for (Spreadsheet spreadsheet : spreadsheetsToDelete) {
                    IProject project = spreadsheet.getRubyProject().getProject();
                    
                    if (spreadsheetsMap.containsKey(project)) {
                        spreadsheetsMap.get(project).add(spreadsheet.getElementName());
                    }
                    else {
                        ArrayList<String> names = new ArrayList<String>(1);
                        names.add(spreadsheet.getElementName());
                        spreadsheetsMap.put(project, names);
                    }
                }
                
                AWEProjectManager.deleteSpreadsheets(spreadsheetsMap);
            }
        }
    }
}
