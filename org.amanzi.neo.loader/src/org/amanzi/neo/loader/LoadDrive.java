package org.amanzi.neo.loader;

import org.amanzi.neo.loader.dialogs.DriveDialog;
import org.eclipse.swt.widgets.Display;

import net.refractions.udig.project.ui.tool.AbstractActionTool;

public class LoadDrive extends AbstractActionTool {

	public LoadDrive() {
	}

	public void run() {		
		final Display display = this.getContext().getViewportPane().getControl().getDisplay();
		
		this.getContext().updateUI(new Runnable(){
	
			public void run() {
				DriveDialog dialog = new DriveDialog(display.getActiveShell());				
				dialog.open();
			}
		});
	}

	public void dispose() {
		// TODO Auto-generated method stub

	}
}