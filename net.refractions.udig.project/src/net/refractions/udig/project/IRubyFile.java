package net.refractions.udig.project;

import org.eclipse.core.resources.IResource;

public interface IRubyFile extends IProjectElement, IRubyProjectElement {
	
	/**
	 * Setter for Resource of RubyFile
	 * 
	 * @param resource Resource of File
	 * @author Lagutko_N
	 */
	
	public void setResource(IResource resource);
	
	/**
	 * Getter for Resource of RubyFile
	 * 
	 * @return resource of File
	 * @author Lagutko_N
	 */
	
	public IResource getResource();

}
