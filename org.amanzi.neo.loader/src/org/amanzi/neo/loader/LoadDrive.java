/* AWE - Amanzi Wireless Explorer
 * http://awe.amanzi.org
 * (C) 2008-2009, AmanziTel AB
 *
 * This library is provided under the terms of the Eclipse Public License
 * as described at http://www.eclipse.org/legal/epl-v10.html. Any use,
 * reproduction or distribution of the library constitutes recipient's
 * acceptance of this agreement.
 *
 * This library is distributed WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 */
package org.amanzi.neo.loader;

import net.refractions.udig.project.ui.tool.AbstractActionTool;

import org.amanzi.neo.loader.dialogs.DriveDialog;
import org.eclipse.swt.widgets.Display;

@Deprecated
// TODO remove candidate
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
