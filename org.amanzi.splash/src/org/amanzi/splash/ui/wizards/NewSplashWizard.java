package org.amanzi.splash.ui.wizards;

/*
 * "The Java Developer's Guide to Eclipse"
 *   by D'Anjou, Fairbrother, Kehn, Kellerman, McCarthy
 * 
 * (C) Copyright International Business Machines Corporation, 2003, 2004. 
 * All Rights Reserved.
 * 
 * Code or samples provided herein are provided without warranty of any kind.
 */

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import org.eclipse.core.filesystem.URIUtil;
import org.amanzi.splash.utilities.Util;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.wizards.newresource.BasicNewProjectResourceWizard;
import org.eclipse.ui.wizards.newresource.BasicNewResourceWizard;
import org.rubypeople.rdt.core.ILoadpathEntry;
import org.rubypeople.rdt.core.IRubyProject;
import org.rubypeople.rdt.core.RubyCore;
import org.rubypeople.rdt.internal.ui.wizards.NewWizardMessages;
import org.rubypeople.rdt.internal.ui.wizards.buildpaths.BuildPathsBlock;
import org.rubypeople.rdt.internal.ui.wizards.buildpaths.CPListElement;
import org.rubypeople.rdt.launching.IVMInstall;
import org.rubypeople.rdt.launching.RubyRuntime;
import org.rubypeople.rdt.ui.PreferenceConstants;

/**
 * Specialization of the new file creation processing that adds
 * fields to initialize the number of rows and columns in the new
 * mini-spreadsheet.
 */
public class NewSplashWizard extends Wizard implements INewWizard {
	IStructuredSelection selection;
	SplashWizardNewFileCreationPage fileCreationPage;
	private SpreadsheetProjectWizardPage fFirstPage;
	IWorkbench workbench;
	private IConfigurationElement fConfigElement;
    private URI fCurrProjectLocation;
	/**
	 * Constructor for NewSplashWizard.
	 */
	public NewSplashWizard() {
		super();
		
		
	}
	protected void selectAndReveal(IResource newResource) {		
		BasicNewResourceWizard.selectAndReveal(newResource, workbench.getActiveWorkbenchWindow());
	}
	private URI getProjectLocationURI() throws CoreException {
		if (fFirstPage.isInWorkspace()) {
			return null;
		}
		return URIUtil.toURI(fFirstPage.getLocationPath());
	}
	/**
	 * Adds the Ruby nature to the project (if not set yet) and configures the build loadpath.
	 * 
	 * @param monitor a progress monitor to report progress or <code>null</code> if
	 * progress reporting is not desired
	 * @throws CoreException Thrown when the configuring the Ruby project failed.
	 * @throws InterruptedException Thrown when the operation has been canceled.
	 */
	public void configureRubyProject(IProgressMonitor monitor) throws CoreException, InterruptedException {
		if (monitor == null) {
			monitor= new NullProgressMonitor();
		}
		
		int nSteps= 8;			
		monitor.beginTask(NewWizardMessages.RubyCapabilityConfigurationPage_op_desc_ruby, nSteps); 
		
		try {
			IProject project= fFirstPage.getProjectHandle();
			fCurrProjectLocation= getProjectLocationURI();
			
			URI realLocation= fCurrProjectLocation;
			if (fCurrProjectLocation == null) {  // inside workspace
				try {
					URI rootLocation= ResourcesPlugin.getWorkspace().getRoot().getLocationURI();
					realLocation= new URI(rootLocation.getScheme(), null,
						Path.fromPortableString(rootLocation.getPath()).append(project.getName()).toString(),
						null);
				} catch (URISyntaxException e) {
					Assert.isTrue(false, "Can't happen"); //$NON-NLS-1$
				}
			}
			
			BuildPathsBlock.createProject(project, fCurrProjectLocation, monitor);
			IRubyProject rubyProject = RubyCore.create(project, fFirstPage.getAWEProjectName());
			List<ILoadpathEntry> cpEntries= new ArrayList<ILoadpathEntry>();
			IPath projectPath= project.getFullPath();
			cpEntries.add(RubyCore.newSourceEntry(projectPath));
			cpEntries.addAll(Arrays.asList(getDefaultLoadpathEntry()));
			
			List<CPListElement> newClassPath= new ArrayList<CPListElement>();
			for (ILoadpathEntry entry : cpEntries) {
				newClassPath.add(CPListElement.createFromExisting(entry, rubyProject));
			}			
			
			monitor.worked(2);			
			BuildPathsBlock.addRubyNature(project, new SubProgressMonitor(monitor, 1));
			BuildPathsBlock.flush(newClassPath, rubyProject, new SubProgressMonitor(monitor, 5));
		} catch (OperationCanceledException e) {
			throw new InterruptedException();
		} finally {
			monitor.done();
		}			
	}
       
	private ILoadpathEntry[] getDefaultLoadpathEntry() {
		ILoadpathEntry[] defaultJRELibrary= PreferenceConstants.getDefaultRubyVMLibrary();
		String compliance= fFirstPage.getCompilerCompliance();
		IPath jreContainerPath= new Path(RubyRuntime.RUBY_CONTAINER);
		if (compliance == null || defaultJRELibrary.length > 1 || !jreContainerPath.isPrefixOf(defaultJRELibrary[0].getPath())) {
			// use default
			return defaultJRELibrary;
		}
		IVMInstall inst= fFirstPage.getJVM();
		if (inst != null) {
			IPath newPath= jreContainerPath.append(inst.getVMInstallType().getId()).append(inst.getName());
			return new ILoadpathEntry[] { RubyCore.newContainerEntry(newPath) };
		}
		return defaultJRELibrary;
	}
	
	/**
	 * @see org.eclipse.jface.wizard.IWizard#performFinish()
	 */
	public boolean performFinish() {
		
		if (selection.toString().equals("<empty selection>") == true){
			Util.logn("Empty Selection: Finish button clicked !!!");
			//return spreadsheetPropertiesPage.finish();
			boolean res= true;//super.performFinish();
			if (res) {
				BasicNewProjectResourceWizard.updatePerspective(fConfigElement);			
		 		selectAndReveal(fFirstPage.getProjectHandle());
//				try {
//					configureRubyProject(null);
//				} catch (CoreException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				} catch (InterruptedException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
			}
			return res;
		}
		else{
			Util.logn("Selection: Finish button clicked !!!");
			return fileCreationPage.finish();
		}
	}

	/**
	 * @see org.eclipse.ui.IWorkbenchWizard#init(IWorkbench, IStructuredSelection)
	 */
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		this.workbench = workbench;
		this.selection = selection;
		Util.logn("selection = " + selection);
		setWindowTitle("New JRuby Spreadsheet");
	}

	/**
	 * @see org.eclipse.jface.wizard.IWizard#addPages()
	 */
	public void addPages() {
		
		if (selection.toString().equals("<empty selection>") == true){
			fFirstPage = new SpreadsheetProjectWizardPage(workbench, "project");
			addPage(fFirstPage);
		}
		else{
			fileCreationPage =
				new SplashWizardNewFileCreationPage(workbench, selection);
			addPage(fileCreationPage);
		}
	}
}
