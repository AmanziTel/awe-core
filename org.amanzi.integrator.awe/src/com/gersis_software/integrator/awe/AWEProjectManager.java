package com.gersis_software.integrator.awe;

import java.util.ArrayList;

import net.refractions.udig.project.internal.Project;
import net.refractions.udig.project.internal.ProjectElement;
import net.refractions.udig.project.internal.ProjectPlugin;
import net.refractions.udig.project.internal.RubyFile;
import net.refractions.udig.project.internal.RubyProjectElement;
import net.refractions.udig.project.internal.RubyProject;
import net.refractions.udig.project.internal.impl.ProjectFactoryImpl;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;

import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.emf.ecore.resource.Resource;

/**
 * Class for integration functionality of AWE project to RDT plugin
 * 
 * Note: 
 * It's only initial implementation of integration. 
 * 
 * @author Lagutko_N
 *
 */

public class AWEProjectManager {
	
	/*
	 * Constant for Qualified name of PersistencePreferences that will contain
	 * name of AWE Project
	 */
	
	public static final QualifiedName AWE_PROJECT_NAME = new QualifiedName("awe_project", "name");
	
	/**
	 * Returns all Ruby projects that are referenced by AWE projects
	 * 
	 * @return array of RubyProjects
	 */
	
	public static IProject[] getAllRubyProjects() {
		ArrayList<IProject> rubyProjects = new ArrayList<IProject>();
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		
		for (Project aweProject : ProjectPlugin.getPlugin().getProjectRegistry().getProjects()) {
			for (RubyProject rubyProject : aweProject.getElements(RubyProject.class)) {
				IProject resourceProject = root.getProject(rubyProject.getName());
				try {					
					resourceProject.setPersistentProperty(AWE_PROJECT_NAME, aweProject.getName());
				}
				catch (CoreException e) {
					//TODO: handle this exception
				}
			
				rubyProjects.add(resourceProject);		
			}
		}
		
		return rubyProjects.toArray(new IProject[]{});
	}
	
	/**
	 * Returns all AWE projects
	 * 
	 * @return array of AWE Projects
	 */
	
	public static IProject[] getAWEProjects() {
		ArrayList<IProject> aweProjects = new ArrayList<IProject>();
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		
		for (Project project : ProjectPlugin.getPlugin().getProjectRegistry().getProjects()) {
			IProject aweProject = root.getProject(project.getName());
			aweProjects.add(aweProject);
		}
		
		return aweProjects.toArray(new IProject[]{});
	}
	
	/**
	 * Utility function that search for AWE Project by it's name
	 * 
	 * @param name name of AWE Project
	 * @return AWE project
	 */
	
	private static Project findProjects(String name) {
		for (Project project : ProjectPlugin.getPlugin().getProjectRegistry().getProjects()) {
			if (project.getName().equals(name)) {
				return project;
			}
		}
		return null;
	}
	
	/**
	 * Creates RubyScript for RubyProject if RubyProject doesn't contain script with this name
	 * 
	 * This method calls not only when RubyScript creates from RDT functionality, but also 
	 * when we create a project structure. We must check if project structure already contains
	 * this Script because RDT plug-in creates project structure more than one time. 
	 * 
	 * @param rubyProject 
	 * @param scriptName
	 * @param scriptResource
	 */
	
	private static void createScriptIfNotExist(RubyProject rubyProject, String scriptName, IResource scriptResource) {
		boolean exist = false;
		
		RubyProjectElement element = findRubyScript(rubyProject, scriptName);
		if (element != null) {
			exist = true;
		}
		
		if (!exist) {
			RubyFile rubyFile = ProjectFactoryImpl.eINSTANCE.createRubyFile();
			rubyFile.setName(scriptName);
			rubyFile.setResource(scriptResource);
			rubyProject.addRubyElementInternal(rubyFile);
			rubyFile.setRubyProjectInternal(rubyProject);
		}
	}
	
	/**
	 * Creates RubyProject for AWEProject if AWEProject doesn't contain project with this name
	 * 
	 * This method calls not only when RubyProject creates from RDT functionality, but also 
	 * when we create a project structure. We must check if project structure already contains
	 * this Project because RDT plug-in creates project structure more than one time. 
	 * 
	 * @param aweProject 
	 * @param rubyProjectName
	 */
	
	private static void createProjectIfNotExist(Project aweProject, String rubyProjectName) {
		boolean exist = false;
		
		for (RubyProject rubyProject : aweProject.getElements(RubyProject.class)) {
			if (rubyProject.getName().equals(rubyProjectName)) {
				exist = true;
			}
		}
		
		if (!exist) {
			RubyProject ruby = ProjectFactoryImpl.eINSTANCE.createRubyProject();
			ruby.setName(rubyProjectName);
			ruby.setProjectInternal(aweProject);			
		}
	}
	
	/**
	 * Utility function that get name of AWE Project from RubyProject 
	 * 
	 * @param rubyProject resource of RubyProject
	 * @return name of AWE project
	 */
	
	private static String getAWEprojectName(IProject rubyProject) {
		String name = null;
		try {
			name = rubyProject.getPersistentProperty(AWE_PROJECT_NAME);
		}
		catch (CoreException e) {
			
		}
		return name;
	}
	
	/**
	 * Creates RubyProject
	 * 
	 * @param rubyProject RDT Ruby Project
	 */
	
	public static void createRubyProject(IProject rubyProject) {
		String name = getAWEprojectName(rubyProject);
		if (name != null) {
			Project project = findProjects(name);
			if (project != null) {
				createProjectIfNotExist(project, rubyProject.getName());
			}
		}
	}
	
	/**
	 * Utility function that deletes ProjectElement from AWE Project
	 * 
	 * This functionality copied from uDIG plugins.
	 * 
	 * @param element AWE ProjectElement to delete
	 */
	
	private static void deleteElement(ProjectElement element) {
		Project projectInternal = element.getProjectInternal();
        projectInternal.getElementsInternal().remove(element);
                    
        deleteResource(element);   
	}
	
	/**
	 * Utility function that deletes RubyProjectElement from AWE Project
	 * 
	 * This functionality copied from uDIG plugins.
	 * 
	 * @param element AWE RubyProjectElement to delete
	 */
	
	private static void deleteElement(RubyProjectElement element) {
		RubyProject projectInternal = element.getRubyProjectInternal();
        projectInternal.removeRubyElementInternal(element);
                    
        deleteResource(element);   
	}
	
	/**
	 * Utility function that delete EMF resource of AWE ProjectElement
	 * 
	 * @param element AWE ProjectElement to delete
	 */
	
	private static void deleteResource(ProjectElement element) {
		Resource resource = element.eResource();
		if (resource != null) {
            resource.getContents().remove(element);
            resource.unload();
        }   
	}
	
	/**
	 * Delete RubyProject
	 * 
	 * @param rubyProject RDT RubyProject to delete
	 */
	
	public static void deleteRubyProject(IProject rubyProject) {
		String name = getAWEprojectName(rubyProject);
		
		Project project = findProjects(name);	
		RubyProject ruby = findRubyProject(project, rubyProject.getName());
		if (ruby != null) {
			deleteElement(ruby);
		}
	}
	
	/**
	 * Search for RubyProject inside an AWE Project by Name
	 * 
	 * @param aweProject AWE Project
	 * @param rubyProjectName name of RubyProject
	 * @return AWE RubyProject or null if there are no such projects
	 */
	
	private static RubyProject findRubyProject(Project aweProject, String rubyProjectName) {
		for (RubyProject ruby : aweProject.getElements(RubyProject.class)) {
			if (ruby.getName().equals(rubyProjectName)) {
				return ruby;
			}
		}
		return null;
	}
	
	/**
	 * Search for RubyScript inside a AWE RubyProject by name
	 * 
	 * @param rubyProject AWE Ruby Project
	 * @param rubyScriptName name of RubyScript
	 * @return AWE RubyScript or null if there are no such scripts
	 */
	
	private static RubyFile findRubyScript(RubyProject rubyProject, String rubyScriptName) {
		for (RubyProjectElement rubyElement : rubyProject.getRubyElementsInternal()) {
			if (rubyElement.getFileExtension().equals("urf")) {
				if (rubyElement.getName().equals(rubyScriptName)) {
					return (RubyFile)rubyElement;
				}
			}
		}
		return null;
	}
	
	/**
	 * Delete RubyScript from AWE Project
	 * 
	 * @param scriptName name of Script
	 * @param rubyProject RDT Ruby Project
	 */
	
	public static void deleteRubyScript(String scriptName, IProject rubyProject) {
		String aweProjectName = getAWEprojectName(rubyProject);
		
		Project project = findProjects(aweProjectName);
		if (project != null) {
			RubyProject ruby = findRubyProject(project, rubyProject.getName());
			if (ruby != null) {
				RubyProjectElement element = findRubyScript(ruby, scriptName);
				if (element != null) {
					deleteElement(element);
				}
			}
		}		
	}
	
	/**
	 * Creates RubyScript in AWE project structure
	 * 
	 * @param rubyProject parent RDT Ruby Project
	 * @param scriptName name of Script
	 * @param resource resource of Script
	 */
	
	public static void createRubyScript(IProject rubyProject, String scriptName, IResource resource) {
		String name = getAWEprojectName(rubyProject);
		Project project = findProjects(name);
		RubyProject ruby = findRubyProject(project, rubyProject.getName());
		if (ruby != null) {
			createScriptIfNotExist(ruby, scriptName, resource);
		}		
	}
}
