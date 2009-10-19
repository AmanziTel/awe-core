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

package org.amanzi.splash.ui.importWizards;

import java.lang.reflect.InvocationTargetException;

import org.amanzi.splash.database.services.Messages;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.IExportWizard;
import org.eclipse.ui.IWorkbench;
import org.rubypeople.rdt.core.IRubyProject;
import org.rubypeople.rdt.core.ISourceFolder;
import org.rubypeople.rdt.internal.core.CreateRubyScriptOperation;
import org.rubypeople.rdt.internal.core.RubyModelManager;
import org.rubypeople.rdt.internal.corext.util.RubyModelUtil;

/**
 * Wizard for exporting script from ImportBuilder
 * 
 * @author Lagutko_N
 * @since 1.0.0
 */
public class ExportBuilderScriptWizard extends Wizard implements IExportWizard {
    
    /*
     * Page of this Wizard
     */
    private ExportBuilderScriptWizardPage mainPage;
    
    /*
     * Content of Script
     */
    private String source;
    
    /*
     * Exception of CreateScript operation
     */
    private Throwable exception;
    
    /**
     * Constructor
     * 
     * @param source content of new script
     */
    public ExportBuilderScriptWizard(String source) {
        mainPage = new ExportBuilderScriptWizardPage("Export", StructuredSelection.EMPTY);
        this.source = source;
    }

    @Override
    public boolean performFinish() {
        final CreateRubyScriptOperation operation = new CreateRubyScriptOperation(getSourceFolder(), 
                                                                            getScriptName(), 
                                                                            source,
                                                                            true);
        try {
            getContainer().run(true, false, new IRunnableWithProgress(){
            
                @Override
                public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
                    try {
                        operation.run(monitor);
                    }
                    catch (CoreException e) {
                        exception = e;
                    }
                }
            });
        }
        catch (InterruptedException e) {
            return false;
        }
        catch (InvocationTargetException e) {
            Throwable realException = e.getTargetException();
            MessageDialog.openError(getShell(), Messages.Wizard_Error_Message, realException.getMessage());
            return false;
        }
        if (exception != null) {
            MessageDialog.openError(getShell(), Messages.Wizard_Error_Message, exception.getMessage());
            return false;
        }
        
        return true;
    }
    
    /**
     * Computes SourceFolder for new Script
     *
     * @return source folder
     */
    private ISourceFolder getSourceFolder() {
        IProject rubyProject = (IProject)ResourcesPlugin.getWorkspace().getRoot().findMember(mainPage.getContainerFullPath());
        
        IRubyProject parent = RubyModelManager.getRubyModelManager().getRubyModel().getRubyProject(rubyProject.getName());
        
        ISourceFolder folder = RubyModelUtil.getSourceFolder(parent);
        
        return folder;
    }
    
    /**
     * Computes name for Script
     *
     * @return
     */
    private String getScriptName() {
        String scriptName = mainPage.getFileName();
        if (!scriptName.contains(".rb")) {
            scriptName = scriptName + ".rb";
        }
        
        return scriptName;
    }

    @Override
    public void init(IWorkbench workbench, IStructuredSelection selection) {
        mainPage = new ExportBuilderScriptWizardPage("Export", selection);
    }
    
    @Override
    public void addPages() {
        super.addPages(); 
        addPage(mainPage);        
    }

}
