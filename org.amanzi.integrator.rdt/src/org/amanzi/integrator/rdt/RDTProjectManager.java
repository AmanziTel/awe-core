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
package org.amanzi.integrator.rdt;

import java.net.URL;

import org.amanzi.neo.core.NeoCorePlugin;
import org.amanzi.neo.core.database.nodes.AweProjectNode;
import org.amanzi.neo.core.database.nodes.RubyProjectNode;
import org.amanzi.neo.core.database.nodes.SpreadsheetNode;
import org.amanzi.neo.core.database.services.AweProjectService;
import org.amanzi.rdt.launching.util.LaunchUtils;
import org.amanzi.splash.ui.SplashEditorInput;
import org.amanzi.splash.utilities.NeoSplashUtil;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.RenameResourceAction;
import org.rubypeople.rdt.core.IRubyElement;
import org.rubypeople.rdt.core.IRubyModel;
import org.rubypeople.rdt.core.IRubyProject;
import org.rubypeople.rdt.core.IRubyScript;
import org.rubypeople.rdt.core.ISourceFolder;
import org.rubypeople.rdt.core.RubyModelException;
import org.rubypeople.rdt.internal.core.ExternalSourceFolder;
import org.rubypeople.rdt.internal.core.ExternalSourceFolderRoot;
import org.rubypeople.rdt.internal.core.RubyElement;
import org.rubypeople.rdt.internal.core.RubyElementDelta;
import org.rubypeople.rdt.internal.core.RubyModel;
import org.rubypeople.rdt.internal.core.RubyModelManager;
import org.rubypeople.rdt.internal.core.SourceFolder;
import org.rubypeople.rdt.internal.core.Spreadsheet;
import org.rubypeople.rdt.internal.corext.util.RubyModelUtil;
import org.rubypeople.rdt.internal.ui.RubyPlugin;
import org.rubypeople.rdt.internal.ui.rubyeditor.EditorUtility;
import org.rubypeople.rdt.ui.actions.RenameElementAction;

/**
 * Class for integration functionality of RDT project to uDIG/AWE plugin
 * 
 * @author Lagutko_N
 * 
 */

public class RDTProjectManager {

	/**
	 * Delete RDT project
	 * 
	 * @param name
	 *            name of project
	 * @param deleteFiles
	 *            is need to delete files
	 */

	public static void deleteProject(String name, boolean deleteFiles) {
		IRubyProject project = RubyModelManager.getRubyModelManager().getRubyModel().getRubyProject(name);

		try {
			project.getResource().delete(deleteFiles, null);
			RubyProjectNode rubyProject = NeoCorePlugin.getDefault().getProjectService().findRubyProject(name);
			if (rubyProject != null) {
				NeoCorePlugin.getDefault().getProjectService().deleteNode(rubyProject);
			}
		} catch (CoreException e) {
			// TODO: handle this exception
		}

	}

	/**
	 * Utility function that load all Scripts of Parent RubyElement
	 * 
	 * @param parent
	 *            parent ruby Element
	 * @throws RubyModelException
	 */

	private static void loadScripts(RubyElement parent) throws RubyModelException {
		for (IRubyElement element : parent.getChildren()) {
			if (element.getElementType() == IRubyElement.SCRIPT) {
				element.getOpenable().open(null);
			} else {
				if (!(element instanceof ExternalSourceFolderRoot) && !(element instanceof ExternalSourceFolder)) {
					loadScripts((RubyElement) element);
				}
			}
		}
	}

	/**
	 * Utility function that search for Script that is child of RubyElement by
	 * name
	 * 
	 * @param parent
	 *            parent RubyElement
	 * @param name
	 *            name of Script
	 * @return RDT RubyScript
	 * @throws RubyModelException
	 */

	private static IRubyScript getScriptByName(RubyElement parent, String name) throws RubyModelException {
		IRubyScript result = null;
		for (IRubyElement element : parent.getChildren()) {
			if ((element.getElementType() == IRubyElement.SCRIPT) && (element.getResource().getName().equals(name))) {
				return (IRubyScript) element;
			} else {
				if (!(element instanceof ExternalSourceFolderRoot) && !(element instanceof ExternalSourceFolder)) {
					result = getScriptByName((RubyElement) element, name);
					if (result != null) {
						return result;
					}
				}
			}
		}
		return result;
	}

	/**
	 * Delete Script from RDT Project Structure
	 * 
	 * @param projectName
	 *            Name of Parent RubyProject
	 * @param scriptName
	 *            Name of Script
	 * @param deleteFiles
	 *            is need to delete files
	 */

	public static void deleteScript(String projectName, String scriptName, boolean deleteFiles) {
		IRubyProject parent = RubyModelManager.getRubyModelManager().getRubyModel().getRubyProject(projectName);

		try {
			IRubyScript script = getScriptByName((RubyElement) parent, scriptName);
			script.getResource().delete(deleteFiles, null);
		} catch (RubyModelException e) {
			// TODO: handle this exception
		} catch (CoreException e) {
			// TODO: handle this exception
		}
	}

	/**
	 * Loads project structure
	 * 
	 * This function needs to build project structure of RDT Ruby Projects. And
	 * when we build project structure of RDT Project we also build project
	 * structure of AWE RubyProjects.
	 * 
	 * @param name
	 */

	public static void loadProject(String name) {	    
	    IResource projectResource = ResourcesPlugin.getWorkspace().getRoot().findMember(name);
	    if (projectResource != null){
	        try {
	            IRubyProject parent = RubyModelManager.getRubyModelManager().getRubyModel().getRubyProject(projectResource);
	            parent.getOpenable().open(null);
	            loadScripts((RubyElement) parent);
	        } catch (RubyModelException e) {
	            RubyPlugin.log(e);	            
	        }
	    }
	}

	/**
	 * Open RubyScript in editor
	 * 
	 * @param resource
	 *            resource of script
	 */

	public static void openScript(IResource resource) {
		try {
			EditorUtility.openInEditor(resource);
		}
		catch (PartInitException e) {
			RubyPlugin.log(e);
		}
		catch (RubyModelException e) {
		    RubyPlugin.log(e);
		}
	}

	/**
	 * Runs RubyScript
	 * 
	 * @param projectName
	 *            name of project of RubyScript
	 * @param scriptName
	 *            name of script
	 */

	public static void runScript(String projectName, String scriptName) {
		try {
			IRubyProject parent = RubyModelManager.getRubyModelManager().getRubyModel().getRubyProject(projectName);

			IRubyScript script = getScriptByName((RubyElement) parent, scriptName);

			LaunchUtils.launchRubyScript(script);
		} catch (RubyModelException e) {

		}
	}

	/**
	 * Opens Neo4j-based Spreadsheet in Editor
	 * 
	 * @param spreadsheetURL
	 *            URL of Neo4j Spreadsheet
	 */

	public static void openSpreadsheet(URL spreadsheetURL, String rubyProjectName) {
		NeoSplashUtil.openSpreadsheet(PlatformUI.getWorkbench(), spreadsheetURL, rubyProjectName);
	}

	/**
	 * Deletes the Spreadsheet from Ruby Project Tree and Database
	 * 
	 * @param aweProjectName
	 *            name of AWE Project that contain Spreadsheet
	 * @param rubyProjectName
	 *            name of Ruby Project that contain Spreadsheet
	 * @param spreadsheetName
	 *            name of Spreadsheet
	 */
	public static void deleteSpreadsheet(String aweProjectName, String rubyProjectName, String spreadsheetName) {
	    IRubyProject parent = RubyModelManager.getRubyModelManager().getRubyModel().getRubyProject(rubyProjectName);
	    
	    RubyModel model = RubyModelManager.getRubyModelManager().getRubyModel();
	    RubyElementDelta delta = new RubyElementDelta(model);
	    ISourceFolder folder = RubyModelUtil.getSourceFolder(parent);
	    delta.removed(new Spreadsheet((SourceFolder)folder, spreadsheetName));
	    RubyModelManager.getRubyModelManager().getDeltaProcessor().fire(delta, 0);
	    
	    AweProjectService service = NeoCorePlugin.getDefault().getProjectService();	   
        SpreadsheetNode node = service.findOrCreateSpreadsheet(aweProjectName, rubyProjectName, spreadsheetName);
        
        IEditorReference[] editors = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().findEditors(new SplashEditorInput(node), NeoSplashUtil.AMANZI_SPLASH_EDITOR, IWorkbenchPage.MATCH_INPUT);
        if (editors != null) {            
            PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().closeEditors(editors, false);
        }
        service.deleteNode(node);
	}
	
	/**
	 * Calls action for renaming script
	 *
	 * @param rubyProjectName Ruby Project of script for Renaming
	 * @param oldScriptName current name of Script
	 * @author Lagutko_N
	 */
	public static void renameRubyScript(String rubyProjectName, String oldScriptName) {	    
	    IRubyProject parent = RubyModelManager.getRubyModelManager().getRubyModel().getRubyProject(rubyProjectName);
	    
	    try {
            IRubyScript script = getScriptByName((RubyElement)parent, oldScriptName);
            RenameResourceAction action = new RenameResourceAction(PlatformUI.getWorkbench().getDisplay().getActiveShell());
            action.selectionChanged(new StructuredSelection(script.getResource()));
            action.run();
            
            //TODO: script can be exported from Spreadsheet and in this case we must update references and properties of Cell and Script in database
        }
        catch (RubyModelException e) {
            RubyPlugin.log(e);
        }
	}
	
	/**
	 * //TODO:
	 *
	 * @param aweProjectName
	 * @param rubyProjectName
	 * @param oldSpreadsheetName
	 * @param newSpreadsheetName
	 */
	public static void renameSpreadsheet(String aweProjectName, String rubyProjectName, String oldSpreadsheetName, String newSpreadsheetName) {
	    AweProjectService service = NeoCorePlugin.getDefault().getProjectService();
	    
	    AweProjectNode aweNode = service.findAweProject(aweProjectName);
	    RubyProjectNode rubyNode = service.findRubyProject(aweNode, rubyProjectName);
	    
	    service.renameSpreadsheet(rubyNode, oldSpreadsheetName, newSpreadsheetName);
	    
	    IRubyModel model = RubyModelManager.getRubyModelManager().getRubyModel();
	    IRubyProject project = model.getRubyProject(rubyProjectName);
	    
	    try {
	        model.refreshSpreadsheets(new IRubyElement[] {project}, null);
	    }
	    catch (RubyModelException e) {
	        RubyPlugin.log(e);
	    }
	}

	/**
	 * Renames Ruby Project in RDT Project Structure
	 *
	 * @param aweProjectName name of parent AWE Project
	 * @param rubyProjectName old name of Ruby Project
	 * @param newRubyProjectName new name of Ruby Project
	 * @author Lagutko_N
	 */
	public static void renameRubyProject(String aweProjectName, String rubyProjectName, String newRubyProjectName) {
	    if (!rubyProjectName.equals(newRubyProjectName)) {
	        IRubyProject project = RubyModelManager.getRubyModelManager().getRubyModel().getRubyProject(rubyProjectName);
	    
	        RenameElementAction action = new RenameElementAction(PlatformUI.getWorkbench().getDisplay().getActiveShell(), newRubyProjectName);
	        action.selectionChanged(new StructuredSelection(project));	    
	        action.run();
	    }
	}
}
