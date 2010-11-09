/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2004, Refractions Research Inc.
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 *
 */
package net.refractions.udig.project.ui.internal.actions;

import net.refractions.udig.project.IRubyFile;
import net.refractions.udig.project.ISpreadsheet;
import net.refractions.udig.project.internal.Layer;
import net.refractions.udig.project.internal.Project;
import net.refractions.udig.project.internal.ProjectElement;
import net.refractions.udig.project.internal.RubyProject;
import net.refractions.udig.project.internal.RubyProjectElement;
import net.refractions.udig.project.ui.UDIGGenericAction;
import net.refractions.udig.project.ui.internal.Messages;

import org.amanzi.integrator.rdt.RDTProjectManager;
import org.amanzi.neo.services.AweProjectService;
import org.amanzi.neo.services.NeoServiceFactory;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.swt.widgets.Display;

/**
 * An action for renaming objects in UDIG
 * 
 * @author jeichar
 * @since 0.6.0
 */
public class Rename extends UDIGGenericAction {
    /**
     * Construct <code>Rename</code>.
     */
    public Rename() {
        super();
    }

    protected void operate( Layer layer ) {
        layer.setName(getNewName(layer.getName()));
        layer.getContextModel().getMap().getProjectInternal().eResource().setModified(true);
    }

    @Override
    protected void operate( Layer[] layers ) {
        if (layers != null)
            operate(layers[0]);
    }

    protected void operate( ProjectElement element ) {
        //Lagutko, 14.08.2009, additional operate for RubyProjectElements
        if (element instanceof RubyProjectElement) {
            operate((RubyProjectElement)element);
        }
        //Lagutko, 17.08.2009, renaming of RubyProject
        else if (element instanceof RubyProject) {
            String oldName = element.getName();
            element.setName(getNewName(oldName));
            element.eResource().setModified(true);            
            RDTProjectManager.renameRubyProject(element.getProjectInternal().getName(), oldName, element.getName());
        }
        else {
            element.setName(getNewName(element.getName()));
            element.getProjectInternal().eResource().setModified(true);
        }
    }

    protected void operate( Project project ) {
        String oldName = project.getName();
        String newName = getNewName(oldName);
        project.setName(newName);
        project.eResource().setModified(true);
        
        //Lagutko, 17.08.2009, rename AWE Project in Database
        AweProjectService service = NeoServiceFactory.getInstance().getProjectService();
        service.renameAweProject(oldName, newName);
    }
    
    /**
     * Operates with RubyProjectElement
     *
     * @param element RubyProjectElement
     */
    protected void operate(RubyProjectElement element) {      
        if (element instanceof IRubyFile) {
            RDTProjectManager.renameRubyScript(element.getRubyProjectInternal().getName(), element.getName());
        }
        //Lagutko, 18.08.2009, renaming of Spreadsheet
        else if (element instanceof ISpreadsheet) {
            String aweProjectName = element.getRubyProjectInternal().getProjectInternal().getName();
            String rubyProjectName = element.getRubyProjectInternal().getName();
            String oldName = element.getName();
            String newName = getNewName(oldName);
            RDTProjectManager.renameSpreadsheet(aweProjectName, rubyProjectName, oldName, newName);
            element.setName(newName);
        }
    }

    /**
     * Opens a dialog asking the user for a new name.
     * 
     * @return The new name of the element.
     */
    private String getNewName( String oldName ) {
        InputDialog dialog = new InputDialog(Display.getDefault().getActiveShell(),
                Messages.Rename_enterNewName, "", oldName, null); //$NON-NLS-1$
        int result = dialog.open();
        if (result == Dialog.CANCEL)
            return oldName;
        return dialog.getValue();
    }

}
