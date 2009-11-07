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
package org.amanzi.splash.editors;

import java.awt.Color;
import java.awt.GradientPaint;
import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;


import org.amanzi.neo.core.NeoCorePlugin;
import org.amanzi.neo.core.database.exception.SplashDatabaseException;
import org.amanzi.neo.core.database.listener.IUpdateDatabaseListener;
import org.amanzi.neo.core.database.nodes.ChartItemNode;
import org.amanzi.neo.core.database.nodes.ChartNode;
import org.amanzi.neo.core.database.nodes.RubyProjectNode;
import org.amanzi.neo.core.database.nodes.SpreadsheetNode;
import org.amanzi.neo.core.database.services.AweProjectService;
import org.amanzi.neo.core.database.services.UpdateDatabaseEvent;
import org.amanzi.neo.core.database.services.UpdateDatabaseEventType;
import org.amanzi.splash.chart.Charts;
import org.amanzi.splash.database.services.SpreadsheetService;
import org.amanzi.splash.swing.Cell;
import org.amanzi.splash.swing.SplashTable;
import org.amanzi.splash.swing.SplashTableModel;
import org.amanzi.splash.ui.AbstractSplashEditor;
import org.amanzi.splash.ui.ChartEditorInput;
import org.amanzi.splash.ui.SplashPlugin;
import org.amanzi.splash.utilities.NeoSplashUtil;
import org.eclipse.albireo.core.SwingControl;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IStorageEditorInput;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.SaveAsDialog;
import org.eclipse.ui.part.EditorPart;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.part.IShowInSource;
import org.eclipse.ui.part.IShowInTargetList;
import org.eclipse.ui.part.ShowInContext;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.experimental.chart.swt.ChartComposite;

import com.eteks.openjeks.format.CellBorder;
import com.eteks.openjeks.format.CellFormat;
import com.eteks.openjeks.format.CellFormatPanel;


public class SplashJFreeChartEditor extends EditorPart implements
        IResourceChangeListener, IShowInSource, IShowInTargetList, IUpdateDatabaseListener {
    private static final Collection<UpdateDatabaseEventType> handedTypes;
    static {
        Collection<UpdateDatabaseEventType> spr = new HashSet<UpdateDatabaseEventType>();
        spr.add(UpdateDatabaseEventType.Spreadsheet);
        handedTypes = Collections.unmodifiableCollection(spr);
    }
	private boolean isDirty = false;
	CellFormatPanel cellFormatPanel = null;
	CellFormat cellFormat = null;


	/**
	 * Class constructor
	 */
	public SplashJFreeChartEditor() {
	}

	/**
	 * @see org.eclipse.ui.IWorkbenchPart#createPartControl(Composite)
	 */
	public void createPartControl(final Composite parent) {
        ChartNode chartNode = getChartNode();
        JFreeChart chart = Charts.createBarChart(Charts.createBarChartDataset(chartNode));
        final ChartComposite frame = new ChartComposite(parent, SWT.NONE, chart, true, true, true, true, true);
        parent.layout();

    }

    private ChartNode getChartNode() {
        AweProjectService projectService = NeoCorePlugin.getDefault().getProjectService();
        ChartEditorInput chartEI = (ChartEditorInput)getEditorInput();
        RubyProjectNode rubyProject = projectService.findRubyProject(chartEI.getProjectName());
        ChartNode chartNode = projectService.getChartByName(rubyProject, chartEI.getChartName());
        return chartNode;
    }


	/**
	 * @see org.eclipse.ui.IEditorPart#doSave(IProgressMonitor)
	 */
	public void doSave(IProgressMonitor monitor) {
		try {
			if (validateEditorInput(getEditorInput()) != null) {
				if (getEditorInput().exists())
					saveContents();
				else
					doSaveAs(MessageFormat.format(
							"The original input ''{0}'' has been deleted.",
							new Object[] { getEditorInput().getName() }));
			} else {
				doSaveAs();
			}
		} catch (CoreException e) {
			monitor.setCanceled(true);
			MessageDialog.openError(null, "Unable to Save Changes", e
					.getLocalizedMessage());
			return;
		}
	}

	/**
	 * @see org.eclipse.ui.IEditorPart#isDirty()
	 */
	public boolean isDirty() {
		return isDirty;
	}

	/**
	 * @see org.eclipse.ui.IEditorPart#isSaveAsAllowed()
	 */
	public boolean isSaveAsAllowed() {
		return true;
	}


	/**
	 * Flag the mini-spreadsheet as dirty,
	 * enable the <b>Save</b> options, an update the editor's modification
	 * indicator (*).
	 */
	protected void setIsDirty(final boolean is_dirty) {
		PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
			public void run() {
				isDirty = is_dirty;
				firePropertyChange(PROP_DIRTY);
			}
		});
	}


	/**
	 * @see org.eclipse.ui.IEditorPart#doSaveAs()
	 */
	public void doSaveAs() {
		doSaveAs("Save As");
	}

	private void doSaveAs(String message) {
	}


	@Override
	public void setFocus() {
		// TODO Auto-generated method stub
		
	}
	
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
            if (ext == null || !ext.equalsIgnoreCase("jrss")) {
                throw new CoreException(new Status(IStatus.ERROR, SplashPlugin
                        .getId(), 0, "File extension must be 'jrss'.", null));
            }
            IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(path);
            if (!file.exists())
                file.create(new ByteArrayInputStream(new byte[] {}), false,
                        null);
            return file;
        }
    }

    
    public void setContents(IEditorInput editorInput) throws CoreException {
//
//        IStorageEditorInput sei = (IStorageEditorInput) getEditorInput();
//        InputStream is;
//
//        is = sei.getStorage().getContents();
        
        
        setIsDirty(false);
    }
    
    public boolean saveContents() throws CoreException {
        boolean saved = false;
        ResourcesPlugin.getWorkspace().removeResourceChangeListener(this);
        try {
            StringBuffer sb = new StringBuffer();
            //((SplashTableModel)table.getModel()).save(sb, getTable().tableFormat);
            IFile file = ((IFileEditorInput) getEditorInput()).getFile();
            file.setContents(
                    new ByteArrayInputStream(sb.toString().getBytes()),
                    IResource.KEEP_HISTORY, null);
            setIsDirty(false);
            saved = true;
        } finally {
            ResourcesPlugin.getWorkspace().addResourceChangeListener(this);
        }
        return saved;
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
        NeoCorePlugin.getDefault().getUpdateDatabaseManager().removeListener(this);
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

    public void init(IEditorSite site, IEditorInput editorInput)
            throws PartInitException {
        //super.init(site, editorInput);
		if (!editorInput.exists())
			throw new PartInitException(editorInput.getName()
					+ "does not exist.");

		IEditorInput ei = validateEditorInput(editorInput);
		NeoCorePlugin.getDefault().getUpdateDatabaseManager().addListener(this);
		// This message includes class names to help
		// the programmer / reader; production code would instead
		// log an error and provide a helpful, friendly message.
		if (ei == null)
			throw new PartInitException(MessageFormat.format("Invalid input.\n\n({0} is not a valid input for {1})",
					new String[] {editorInput.getClass().getName(),
					this.getClass().getName()
			}));

		try {

			NeoSplashUtil.logn("ei: " + ei.toString());
			setInput(ei);
			setContents(ei);
			setSite(site);
			setPartName(editorInput.getName());
		} catch (CoreException e) {
			throw new PartInitException(e.getMessage());
		}

		
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
                        	NeoSplashUtil.logn("spreadsheet has been deleted !!!");
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
                           
                        }
                    });
                }
            }
        }
    }
   

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
     * @see org.amanzi.splash.ui.AbstractSplashEditor#validateEditorInput(IEditorInput)
     */
    public IEditorInput validateEditorInput(IEditorInput editorInput) {
        if (editorInput instanceof ChartEditorInput)
            return editorInput;
        if (editorInput instanceof IStorageEditorInput)
            return editorInput;
        if (editorInput instanceof IFileEditorInput)
            return editorInput;
        
        return null;
    }

	@Override
	public String[] getShowInTargetIds() {
		// TODO Auto-generated method stub
		return null;
	}

    @Override
    public void databaseUpdated(UpdateDatabaseEvent event) {
        NeoSplashUtil.logn("firePropertyChange(PROP_INPUT)");
        PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
            public void run() {
                firePropertyChange(PROP_INPUT);
            }
        });
        
    }

    @Override
    public Collection<UpdateDatabaseEventType> getType() {
        return handedTypes;
    }

}
