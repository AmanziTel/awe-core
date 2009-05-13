package com.gersis_software.integrator.rdt;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.ui.PartInitException;

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
}
