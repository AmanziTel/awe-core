/*
 * Author: Markus Barchfeld
 * 
 * Copyright (c) 2005 RubyPeople.
 * 
 * This file is part of the Ruby Development Tools (RDT) plugin for eclipse. RDT
 * is subject to the "Common Public License (CPL) v 1.0". You may not use RDT
 * except in compliance with the License. For further information see
 * org.rubypeople.rdt/rdt.license.
 */
package org.rubypeople.rdt.internal.debug.ui;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.debug.internal.ui.DebugUIPlugin;
import org.eclipse.jface.dialogs.MessageDialog;
import org.rubypeople.rdt.internal.debug.core.model.IRubyDebugTarget;

public class CodeReloadJob extends Job {

	private String filename;
	private IRubyDebugTarget debugTarget;

	public CodeReloadJob(IRubyDebugTarget debugTarget, String filename) {
		super("Loading " + filename);
		this.filename = filename;
		this.debugTarget = debugTarget;
	}

	public IStatus run(IProgressMonitor monitor) {
		Thread runner = new Thread("LoadResult") {

			public void run() {
				final IStatus status = debugTarget.load(filename);
				if (!status.isOK()) {
					DebugUIPlugin.getStandardDisplay().syncExec(new Runnable() {
						public void run() {
							MessageDialog.openInformation(DebugUIPlugin.getStandardDisplay().getActiveShell(), "Error loading " + filename, status.getMessage());
						}
					});
				}
				done(Status.OK_STATUS);
			}
		};
		this.setThread(runner);
		runner.start();
		return ASYNC_FINISH;
	}
}