package org.amanzi.splash.neo4j.ui;

/*
 * "The Java Developer's Guide to Eclipse"
 *   by D'Anjou, Fairbrother, Kehn, Kellerman, McCarthy
 * 
 * (C) Copyright International Business Machines Corporation, 2003, 2004. 
 * All Rights Reserved.
 * 
 * Code or samples provided herein are provided without warranty of any kind.
 */

import java.io.ByteArrayInputStream;

import org.amanzi.splash.neo4j.utilities.Util;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.SaveAsDialog;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.part.IShowInSource;
import org.eclipse.ui.part.IShowInTargetList;
import org.eclipse.ui.part.ShowInContext;



/**
 * Defines a sample "mini-spreadsheet" editor that demonstrates how to create an
 * editor whose input is based on resources from the workspace.
 * <p>
 * The mini-spreadsheet editor defines its own toolbar button, main menu bar
 * pull-down labeled "Calculations," and context menu. It will also allow others
 * to extend it with their own actions; for example, the
 * <code>org.amanzi.splash.extras</code> plug-in contributes toolbar
 * buttons, menu choices, and editor pop-up menu choices (denoted in the UI with
 * an asterisk).
 */

public class SplashResourceEditor extends AbstractSplashEditor implements
        IResourceChangeListener, IShowInSource, IShowInTargetList {

    private IFile createNewFile(String message) throws CoreException {
        SaveAsDialog dialog = new SaveAsDialog(getEditorSite().getShell());
        dialog.setTitle("Save Mini-Spreadsheet As");
        if (getEditorInput() instanceof FileEditorInput)
            dialog.setOriginalFile(((FileEditorInput) getEditorInput())
                    .getFile());
        dialog.create();
        if (message != null)
            dialog.setMessage(message, IMessageProvider.WARNING);
        else
            dialog.setMessage("Save file to another location.");
        dialog.open();
        IPath path = dialog.getResult();

        if (path == null) {
            return null;
        } else {
            String ext = path.getFileExtension();
            if (ext == null || !ext.equalsIgnoreCase("splash")) {
                throw new CoreException(new Status(IStatus.ERROR, SplashPlugin
                        .getId(), 0, "File extension must be 'splash'.", null));
            }
            IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(path);
            if (!file.exists())
                file.create(new ByteArrayInputStream(new byte[] {}), false,
                        null);
            return file;
        }
    }

    
    public void setContents(IEditorInput editorInput) throws CoreException {
    }
    
    public boolean saveContents() throws CoreException {
        
        return true;
    }
	/**
	 * Create a new valid <code>IEditorInput</code> for this concrete
	 * implementation, an editor input of <code>IFile</code>.
	 */
    public IEditorInput createNewInput(String message) throws CoreException {
        IFile file = createNewFile(message);

        if (file != null)
            return new FileEditorInput(file);
        else
            return null;
    }

    /**
     * @see org.eclipse.ui.IWorkbenchPart#dispose()
     */
    public void dispose() {
        super.dispose();
        ResourcesPlugin.getWorkspace().removeResourceChangeListener(this);
        Util.logn("Closing the spreadsheet");
    }

	/*
     * (non-Javadoc)
     * 
     * @see org.eclipse.ui.part.IShowInSource#getShowInContext()
     */
    public ShowInContext getShowInContext() {
        FileEditorInput fei = (FileEditorInput) getEditorInput();
        return new ShowInContext(fei.getFile(), null);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.ui.part.IShowInTargetList#getShowInTargetIds()
     */
    public String[] getShowInTargetIds() {
        return new String[] { IPageLayout.ID_RES_NAV };
    }

    public void init(IEditorSite site, IEditorInput editorInput)
            throws PartInitException {
        super.init(site, editorInput);
        ResourcesPlugin.getWorkspace().addResourceChangeListener(this,
                IResourceChangeEvent.POST_CHANGE);
    }

    /**
     * This editor is a resource change listener in order to detect "special"
     * situations. Specifically:
     * 
     * <ul>
     * <li>Resource is deleted while editor is open (action: mark as dirty,
     * permit only "Save As...")
     * <li>Resource is replaced by local history, modified by another means
     * (view), or modified outside Eclipse and then user selects "Refresh"
     * (action: update contents of editor)
     * </ul>
     * 
     * This editor supports both file-based and stream-based inputs. Note that
     * the editor input can become file-based if the user chooses "Save As...".
     * 
     * @see org.eclipse.core.resources.IResourceChangeListener#resourceChanged(IResourceChangeEvent)
     */
    public void resourceChanged(IResourceChangeEvent event) {
        // If the editor input is not a file, no point in worrying about
        // resource changes.
        if (!(getEditorInput() instanceof FileEditorInput))
            return;
        if (event.getType() == IResourceChangeEvent.POST_CHANGE) {
            final IFile file = ((FileEditorInput) getEditorInput()).getFile();
            IResourceDelta delta = event.getDelta().findMember(
                    file.getFullPath());
            if (delta != null) {
                if (delta.getKind() == IResourceDelta.REMOVED) {
                    // Editor's underlying resource was deleted. Mark editor
                    // as dirty and only allow "Save As..." (see doSave method
                    // for more details).
                    Display.getDefault().syncExec(new Runnable() {
                        public void run() {
                            Util.logn("spreadsheet has been deleted !!!");
                        	//setIsDirty(true);
                            IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
                            
                            IEditorPart editor = page.findEditor(getEditorInput());
                            
                            page.closeEditor(editor, false);
                        }
                    });
                }
                if (delta.getKind() == IResourceDelta.CHANGED
                        && (delta.getFlags() & (IResourceDelta.CONTENT | IResourceDelta.REPLACED)) != 0) {
                    // Editor's underlying resource has changed, perhaps by
                    // update in local history, refresh, etc. 
                	// Note that this update cannot be
                    // because of a change initiated by the editor, since the
                    // editor removes its RCL during updates (see saveContents
                    // for more details).
                    Display.getDefault().syncExec(new Runnable() {
                        public void run() {
                            //getTableModel().load(file.getContents());
							setIsDirty(false);
                        }
                    });
                }
            }
        }
    }
    
   
    

	/**
	 * Persist the mini-spreadsheet as a workspace resource, allowing 
	 * for the <b>Restore from Local History</b> options.
	 */
    

	/**
	 * Set the mini-spreadsheet's contents using the given <code>IEditorInput</code>,
	 * knowing that it has already been validated.
	 * 
	 * @see #validateEditorInput(IEditorInput)
	 */
    
    

    /**
     * Return the given editor input if it valid without attempting to adapt it.
     * 
     * <p><b>Editor Note:</b> A fuller implementation might accept
     * any input that is adaptable to the <code>ILocationProvider</code>
     * protocol. As coded, this implementation will only allow
     * workspace resources, not arbitrary files, such as those
     * that are accessible from the <b>File &gt; Open External File...</b> menu choice. 
     * Moving the <code>MiniSSEditorInput</code> from the 
     * <code>org.amanzi.spreadsheet.jrss.editor.miniwp</code> project to the
     * <code>org.amanzi.spreadsheet.jrss.editor.common</code> project  
     * would accomplish much of this goal, if desired.  We decided against
     * this approach because it would blur the distinction between workspace-centric 
     * and file system-centric implementations.  Or to put it another way, allowing
     * arbitrary file system access in a workspace-oriented editor may serve the
     * interests of the user, but would complicate the points our book wishes to convey.
     * 
     * <p>Also see related bug <a href="https://bugs.eclipse.org/bugs/show_bug.cgi?id=58179">58179</a>
     * ("Problems with opening external files").
     * 
     * @see org.eclipse.ui.editors.text.ILocationProvider 
     * @see org.amanzi.splash.neo4j.ui.AbstractSplashEditor#validateEditorInput(IEditorInput)
     */
    public IEditorInput validateEditorInput(IEditorInput editorInput) {
        //Lagutko, 28.07.2009, SplashResourceEditor supports only SplashEditorInput
        if (editorInput instanceof SplashEditorInput)
            return editorInput;
        
        return null;
    }

	
}
