package org.amanzi.neo.loader;

import org.amanzi.neo.loader.dialogs.TEMSDialog;
import org.eclipse.swt.widgets.Display;

import net.refractions.udig.project.ui.tool.AbstractActionTool;

public class LoadTEMS extends AbstractActionTool {

	public LoadTEMS() {
	}

	public void run() {		
		final Display display = this.getContext().getViewportPane().getControl().getDisplay();
		
		this.getContext().updateUI(new Runnable(){
	
			public void run() {
				TEMSDialog dialog = new TEMSDialog(display.getActiveShell());				
				dialog.open();
			}
		});
	}

	public void dispose() {
		// TODO Auto-generated method stub

	}
}