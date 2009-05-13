package com.aptana.rdt.internal.rake.actions;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.action.Action;

import com.aptana.rdt.rake.IRakeHelper;
import com.aptana.rdt.rake.RakePlugin;


public class RunRakeAction extends Action {

	private IProject project;
	private String task;
	private String description;

	public RunRakeAction(IProject project, String task, String description) {
		this.project = project;
		this.task = task;
		this.description = description;
	}

	@Override
	public void run() {
		getRakeHelper().runRakeTask(project, task, "");
	}

	protected IRakeHelper getRakeHelper() {
		return RakePlugin.getDefault().getRakeHelper();
	}
	
	@Override
	public String getText() {
		String[] parts = task.split(":");
		return parts[parts.length - 1];
	}
	
	@Override
	public String getToolTipText() {
		return description;
	}
}
