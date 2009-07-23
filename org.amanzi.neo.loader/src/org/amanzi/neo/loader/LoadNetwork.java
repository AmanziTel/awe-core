package org.amanzi.neo.loader;

import java.io.IOException;

import org.amanzi.neo.core.INeoConstants;
import org.amanzi.neo.core.NeoCorePlugin;
import org.amanzi.neo.loader.dialogs.TEMSDialog;
import org.amanzi.neo.loader.internal.NeoLoaderPluginMessages;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;

import net.refractions.udig.project.ui.tool.AbstractActionTool;

public class LoadNetwork extends AbstractActionTool {
	
	private static String directory = null;

	public LoadNetwork() {
	}
	
	public static String getDirectory(){
		//LN, 9.07.2009, if directory in LoadNetwork is null than get DefaultDirectory from TEMSDialog
		if (directory == null) {
			if (TEMSDialog.hasDefaultDirectory()) {
				directory = TEMSDialog.getDefaultDirectory();
			}
		}
		return directory;
	}
	
	/**
	 * Sets Default Directory path for file dialogs in TEMSLoad and NetworkLoad
	 * 
	 * @param newDirectory new default directory
	 * @author Lagutko_N
	 */
	
	public static void setDirectory(String newDirectory) {
		if (!newDirectory.equals(directory)) {
			directory = newDirectory;		
			TEMSDialog.setDefaultDirectory(newDirectory);
		}
	}
	
	/**
	 * Is DefaultDirectored set
	 * 
	 * @return 
	 * @author Lagutko_N
	 */
	
	public static boolean hasDirectory() {
		return directory != null;
	}

	public void run() {
		final Display display = this.getContext().getViewportPane().getControl().getDisplay();
		this.getContext().updateUI(new Runnable(){
			
			public void run() {
				FileDialog dlg = new FileDialog(display.getActiveShell(), SWT.OPEN);
				dlg.setText(NeoLoaderPluginMessages.NetworkDialog_DialogTitle);
				dlg.setFilterNames(INeoConstants.NETWORK_FILE_NAMES);
				dlg.setFilterExtensions(INeoConstants.NETWORK_FILE_EXTENSIONS);
				dlg.setFilterPath(getDirectory());
				final String filename = dlg.open();
				if (filename != null) {
					setDirectory(dlg.getFilterPath());
					display.asyncExec(new Runnable() {
						public void run() {
							NetworkLoader networkLoader;
							try {
								networkLoader = new NetworkLoader(filename);
								networkLoader.run();
								networkLoader.printStats();
							} catch (IOException e) {								
								NeoCorePlugin.error("Error loading Network file", e);
							}
						}
					});
				}
			}
		});
	}

	public void dispose() {
	}

}
