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

import java.text.MessageFormat;


import org.amanzi.neo.core.NeoCorePlugin;
import org.amanzi.neo.core.database.nodes.ChartNode;
import org.amanzi.neo.core.database.nodes.RubyProjectNode;
import org.amanzi.neo.core.database.services.AweProjectService;
import org.amanzi.splash.chart.Charts;
import org.amanzi.splash.ui.ChartEditorInput;
import org.amanzi.splash.utilities.NeoSplashUtil;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.EditorPart;
import org.jfree.chart.JFreeChart;
import org.jfree.experimental.chart.swt.ChartComposite;

import com.eteks.openjeks.format.CellFormat;
import com.eteks.openjeks.format.CellFormatPanel;


public class SplashJFreePieChartEditor extends EditorPart {
	private boolean isDirty = false;
	CellFormatPanel cellFormatPanel = null;
	CellFormat cellFormat = null;


	/**
	 * Class constructor
	 */
	public SplashJFreePieChartEditor() {
	}

	/**
     * @see org.eclipse.ui.IWorkbenchPart#createPartControl(Composite)
     */
    public void createPartControl(final Composite parent) {
        JFreeChart chart = Charts.createPieChart(Charts.createPieChartDataset(getChartNode()));
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
                if (getEditorInput().exists()){}
//                  saveContents();
                else{
                    doSaveAs(MessageFormat.format(
                            "The original input ''{0}'' has been deleted.",
                            new Object[] { getEditorInput().getName() }));}
            } else {
                doSaveAs();
            }
        } catch (Exception e) {
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
    
    
    
    public void setContents(IEditorInput editorInput) throws CoreException {
        setIsDirty(false);
    }
    
    

    /**
     * @see org.eclipse.ui.IWorkbenchPart#dispose()
     */
    public void dispose() {
        super.dispose();
    }

    public void init(IEditorSite site, IEditorInput editorInput)
            throws PartInitException {
        //super.init(site, editorInput);
        if (!editorInput.exists())
            throw new PartInitException(editorInput.getName()
                    + " does not exist.");

        IEditorInput ei = validateEditorInput(editorInput);

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
    }

    

    /**
     * Returns the given editor input if it valid without attempting to adapt it.
     * 
     * @see org.amanzi.splash.ui.AbstractSplashEditor#validateEditorInput(IEditorInput)
     */
    public IEditorInput validateEditorInput(IEditorInput editorInput) {
        if (editorInput instanceof ChartEditorInput)
            return editorInput;
        return null;
    }

}
