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

package org.amanzi.awe.report.handlers;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.amanzi.awe.report.ReportPlugin;
import org.amanzi.awe.report.editor.ReportEditor;
import org.amanzi.integrator.awe.AWEProjectManager;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.part.FileEditorInput;
import org.rubypeople.rdt.core.IRubyProject;
import org.rubypeople.rdt.internal.ui.wizards.NewRubyElementCreationWizard;

/**
 * Command handler for report editor
 * 
 * @author Pechko_E
 * @since 1.0.0
 */
public class ReportCommandHandler extends AbstractHandler {

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        final IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindowChecked(event);
        String aweProjectName = AWEProjectManager.getActiveProjectName();
        IRubyProject rubyProject;
        try {
            rubyProject = NewRubyElementCreationWizard.configureRubyProject(null, aweProjectName);
        } catch (CoreException e2) {
            // TODO Handle CoreException
            throw (RuntimeException)new RuntimeException().initCause(e2);
        }

        final IProject resource = rubyProject.getProject();
        // create editor input
        IFile file;
        int i = 0;
        while ((file = resource.getFile(new Path(("report" + i) + ".r"))).exists()) {
            i++;
        }
        if (!file.exists()) {
            try {
                StringBuffer sb = new StringBuffer("report '").append("report" + i).append("' do\nauthor '").append(
                        System.getProperty("user.name")).append("'\ndate '").append(
                        new SimpleDateFormat("yyyy-MM-dd").format(new Date())).append("'\nend");
                InputStream is = new ByteArrayInputStream(sb.toString().getBytes("UTF-8"));
                file.create(is, true, null);
                is.close();
            } catch (CoreException e1) {
                // TODO Handle CoreException
                displayErrorMessage(e1);
                throw (RuntimeException)new RuntimeException().initCause(e1);
            } catch (IOException e) {
                // TODO Handle IOException
                displayErrorMessage(e);
                throw (RuntimeException)new RuntimeException().initCause(e);
            }
        }

        IFileEditorInput ei = new FileEditorInput(file);
        try {
            window.getActivePage().openEditor(ei, ReportEditor.class.getName());
        } catch (PartInitException e) {
            // TODO Handle PartInitException
            displayErrorMessage(e);
            throw (RuntimeException)new RuntimeException().initCause(e);
        }
        return null;
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
                        ReportPlugin.PLUGIN_ID, e.getClass().getName(), e));
            }

        });
    }

}
