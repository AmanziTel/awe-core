package net.refractions.udig.project;

import org.eclipse.core.resources.IResource;

public interface IRubyProjectElement extends IProjectElement {
	
	/**
	 * Setter for Resource of RubyProjectElement
	 * 
	 * @param resource Resource of Ruby Element
	 * @author Lagutko_N
	 */
	
	public void setResource(IResource resource);
	
	/**
	 * Getter for Resource of RubyElement
	 * 
	 * @return resource of RubyElement
	 * @author Lagutko_N
	 */
	
	public IResource getResource();

}
