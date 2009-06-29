package org.amanzi.integrator.rdt;

import java.io.File;
import java.net.URI;
import java.net.URL;

import org.amanzi.rdt.internal.launching.launcher.RubyLaunchShortcut;
import org.amanzi.rdt.launching.util.LaunchUtils;
import org.amanzi.splash.utilities.Util;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import org.rubypeople.rdt.core.IRubyElement;
import org.rubypeople.rdt.core.IRubyProject;
import org.rubypeople.rdt.core.IRubyScript;
import org.rubypeople.rdt.core.RubyModelException;
import org.rubypeople.rdt.internal.core.ExternalSourceFolder;
import org.rubypeople.rdt.internal.core.ExternalSourceFolderRoot;
import org.rubypeople.rdt.internal.core.RubyElement;
import org.rubypeople.rdt.internal.core.RubyModelManager;

import org.rubypeople.rdt.internal.ui.rubyeditor.EditorUtility;

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
	 * @param name name of project 
	 * @param deleteFiles is need to delete files
	 */
	
	public static void deleteProject(String name, boolean deleteFiles) {
		IRubyProject project = RubyModelManager.getRubyModelManager().getRubyModel().getRubyProject(name);
		
		try {			
			project.getResource().delete(deleteFiles, null);
		}
		catch (CoreException e) {
			//TODO: handle this exception
		}
	}
	
	/**
	 * Utility function that load all Scripts of Parent RubyElement
	 * 
	 * @param parent parent ruby Element
	 * @throws RubyModelException
	 */
	
	private static void loadScripts(RubyElement parent) throws RubyModelException {
		for (IRubyElement element : parent.getChildren()) {
			if (element.getElementType() == IRubyElement.SCRIPT) {
				element.getOpenable().open(null);				
			}
			else {				
				if (!(element instanceof ExternalSourceFolderRoot) &&
					!(element instanceof ExternalSourceFolder)) {
					loadScripts((RubyElement)element);
				}
			}
		}
	}
	
	/**
	 * Utility function that search for Script that is child of RubyElement by name
	 * 
	 * @param parent parent RubyElement
	 * @param name name of Script
	 * @return RDT RubyScript
	 * @throws RubyModelException
	 */
	
	private static IRubyScript getScriptByName(RubyElement parent, String name) throws RubyModelException {
		IRubyScript result = null;
		for (IRubyElement element : parent.getChildren()) {
			if ((element.getElementType() == IRubyElement.SCRIPT) &&
				(element.getResource().getName().equals(name))) {
				return (IRubyScript)element;
			}
			else {				
				if (!(element instanceof ExternalSourceFolderRoot) &&
					!(element instanceof ExternalSourceFolder)) {
					result = getScriptByName((RubyElement)element, name);
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
	 * @param projectName Name of Parent RubyProject
	 * @param scriptName Name of Script
	 * @param deleteFiles is need to delete files
	 */
	
	public static void deleteScript(String projectName, String scriptName, boolean deleteFiles) {
		IRubyProject parent = RubyModelManager.getRubyModelManager().getRubyModel().getRubyProject(projectName);
		
		try {
			IRubyScript script = getScriptByName((RubyElement)parent, scriptName);
			script.getResource().delete(deleteFiles, null);
		}
		catch (RubyModelException e) {
			//TODO: handle this exception
		}
		catch (CoreException e) {
			//TODO: handle this exception
		}
	}
	
	/**
	 * Loads project structure 
	 * 
	 * This function needs to build project structure of RDT Ruby Projects. And when we 
	 * build project structure of RDT Project we also build project structure of AWE RubyProjects. 
	 * 
	 * @param name
	 */
	
	public static void loadProject(String name) {
		IRubyProject parent = RubyModelManager.getRubyModelManager().getRubyModel().getRubyProject(name);
		
		try {
			parent.getOpenable().open(null);
			loadScripts((RubyElement)parent);
		}
		catch (RubyModelException e) {
			//TODO: handle this exception
		}
	}
	
	/**
	 * Open RubyScript in editor
	 * 
	 * @param resource resource of script
	 */
	
	public static void openScript(IResource resource) {
		try {			
			EditorUtility.openInEditor(resource);
		}
		catch (PartInitException e) {
			//TODO: handle this exception
		}
		catch (RubyModelException e) {
			//TODO: handle this exception
		}
	}
	
	/**
	 * Runs RubyScript
	 * 
	 * @param projectName name of project of RubyScript
	 * @param scriptName name of script
	 */
	
	public static void runScript(String projectName, String scriptName) {
		try {
			IRubyProject parent = RubyModelManager.getRubyModelManager().getRubyModel().getRubyProject(projectName);
		
			IRubyScript script = getScriptByName((RubyElement)parent, scriptName);
			
			LaunchUtils.launchRubyScript(script);
		}
		catch (RubyModelException e) {
			
		}
	}
	
	/**
	 * Opens Spreadsheet in Editor
	 * 
	 * @param resource resource of Spreadsheet to open
	 */
	
	public static void openSpreadsheet(IResource resource) {
		Util.openSpreadsheet(PlatformUI.getWorkbench(), (IFile)resource);
	}
}
