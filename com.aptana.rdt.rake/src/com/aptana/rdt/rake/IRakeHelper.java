package com.aptana.rdt.rake;

import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.debug.core.ILaunchConfiguration;

public interface IRakeHelper {

	public void runRakeTask(IProject project, String task, String arguments);
	
	public ILaunchConfiguration run(IProject project, String task, String arguments);
	
	/**
	 * Same as calling {@link #getTasks(project, false)}
	 * @param project
	 * @return
	 */
	public Map<String, String> getTasks(IProject  project);

	public Map<String, String> getTasks(IProject project, boolean force);
}
