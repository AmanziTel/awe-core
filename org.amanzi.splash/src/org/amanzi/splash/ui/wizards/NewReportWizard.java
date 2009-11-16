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

package org.amanzi.splash.ui.wizards;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.amanzi.integrator.awe.AWEProjectManager;
import org.amanzi.neo.core.NeoCorePlugin;
import org.amanzi.neo.core.database.nodes.AweProjectNode;
import org.amanzi.neo.core.database.nodes.RubyProjectNode;
import org.amanzi.neo.core.database.services.AweProjectService;
import org.amanzi.splash.editors.ReportEditor;
import org.amanzi.splash.ui.SplashPlugin;
import org.amanzi.splash.utilities.NeoSplashUtil;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.FileEditorInput;
import org.rubypeople.rdt.core.IRubyElement;
import org.rubypeople.rdt.core.RubyModelException;
import org.rubypeople.rdt.internal.ui.wizards.NewRubyElementCreationWizard;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Pechko_E
 * @since 1.0.0
 */
public class NewReportWizard extends NewRubyElementCreationWizard implements INewWizard {

    private NewReportWizardPage page;

    @Override
    public void addPages() {
        page = new NewReportWizardPage(getSelection());
        addPage(page);
    }

    @Override
    protected void finishPage(IProgressMonitor monitor) throws InterruptedException, CoreException {
        System.out.println("--------> finishPage");
    }

    /**
     * This method is called when 'Finish' button is pressed in the wizard. We will create an
     * operation and run it using wizard as execution context.
     */
    public boolean performFinish() {
        System.out.println("--------> performFinish");
        final String containerName = page.getContainerText().getText();

        NeoSplashUtil.logn("containerName: " + containerName);

        final String fileName = page.getReportText().getText();
        IRunnableWithProgress op = new IRunnableWithProgress() {
            public void run(IProgressMonitor monitor) throws InvocationTargetException {
                try {
                    doFinish(containerName, fileName, monitor);
                } catch (CoreException e) {
                    throw new InvocationTargetException(e);
                } finally {
                    monitor.done();
                }
            }

        };
        try {
            getContainer().run(true, false, op);
        } catch (InterruptedException e) {
            return false;
        } catch (InvocationTargetException e) {
            Throwable realException = e.getTargetException();
            MessageDialog.openError(getShell(), "Error", realException.getMessage());
            return false;
        }
        return true;
    }

    @Override
    public IRubyElement getCreatedElement() {
        return null;
    }

    private void doFinish(String containerName, final String fileName, IProgressMonitor monitor) throws CoreException {
        // create a sample file
        monitor.beginTask("Creating " + fileName, 2);
        IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
        final IResource resource = root.findMember(new Path(containerName));

        if (!resource.exists() || !(resource instanceof IContainer)) {
            throwCoreException("Container \"" + containerName + "\" does not exist.");
        }
        final AweProjectService projectService = NeoCorePlugin.getDefault().getProjectService();
        final AweProjectNode aweProject = projectService.findOrCreateAweProject(containerName);
        final RubyProjectNode rubyProject = projectService.findOrCreateRubyProject(aweProject, resource.getProject().getName());
        projectService.findOrCreateReport(rubyProject, fileName.replaceAll("\\.r", ""));
        
        monitor.worked(1);
        monitor.setTaskName("Opening report for editing...");

        getShell().getDisplay().asyncExec(new Runnable() {
            public void run() {
                openEditor(resource.getProject(), fileName);
            }
        });
        monitor.worked(1);
    }

    private void throwCoreException(String message) throws CoreException {
        IStatus status = new Status(IStatus.ERROR, "org.amanzi.splash", IStatus.OK, message, null);
        throw new CoreException(status);
    }

    private void openEditor(IProject resource, String fileName) {
        final IFile file;;
        try {
            StringBuffer sb = new StringBuffer("report '").append(fileName).append("' do\nauthor '").append(
                    System.getProperty("user.name")).append("'\ndate '").append(
                    new SimpleDateFormat("yyyy-MM-dd").format(new Date())).append("'\nend");
            InputStream is = new ByteArrayInputStream(sb.toString().getBytes());
           
            if (!fileName.matches("\\.r$")){
                fileName=new StringBuffer(fileName).append(".r").toString();
            }
            file = resource.getFile(new Path(fileName));
            file.create(is, true, null);
            is.close();
        } catch (CoreException e1) {
            displayErrorMessage(e1);
            throw (RuntimeException)new RuntimeException().initCause(e1);
        } catch (IOException e) {
            displayErrorMessage(e);
            throw (RuntimeException)new RuntimeException().initCause(e);
        }
        IWorkbench workbench = PlatformUI.getWorkbench();
        final IWorkbenchPage page = workbench.getWorkbenchWindows()[0].getActivePage();
        IFileEditorInput ei = new FileEditorInput(file);
        try {
            page.openEditor(ei, ReportEditor.class.getName());
        } catch (PartInitException e) {
            displayErrorMessage(e);
            throw (RuntimeException)new RuntimeException().initCause(e);
        }
    }

    /**
     * Displays error message instead of throwing an exception
     * 
     * @param e exception thrown
     */
    private void displayErrorMessage(final Exception e) {
        final Display display = PlatformUI.getWorkbench().getDisplay();
        display.asyncExec(new Runnable() {

            @Override
            public void run() {
                ErrorDialog.openError(display.getActiveShell(), "Error", "An exception occured", new Status(Status.ERROR,
                        SplashPlugin.getId(), e.getClass().getName(), e));
            }

        });
    }

}
